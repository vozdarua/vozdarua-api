package io.vozdarua.ratelimit;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.vertx.core.http.HttpServerRequest;
import io.vozdarua.config.RequestLocale;
import io.vozdarua.model.dto.MessageResponse;
import io.vozdarua.model.messages.AppMessages;
import io.vozdarua.utils.VozDaRuaUtils;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

@RateLimited(limit = 0, windowSeconds = 0) // placeholder values, real config comes from the intercepted method's annotation
@Interceptor
@Priority(Interceptor.Priority.PLATFORM_BEFORE + 200)
public class RateLimitInterceptor {

    // ponytail: buckets live in this process's memory, keyed per (IP, method). Fine for the
    // current single-instance deploy; move to Bucket4j's distributed/Redis backend if this ever
    // scales to multiple instances.
    private static final ConcurrentHashMap<String, Bucket> BUCKETS = new ConcurrentHashMap<>();

    @Inject
    HttpServerRequest request;

    @Inject
    @RequestLocale
    AppMessages appMessages;

    // Off by default in tests (see %test.app.rate-limit.enabled=false) so the many rapid
    // calls test setups make to shared endpoints like /auth/login don't trip each other's buckets.
    @ConfigProperty(name = "app.rate-limit.enabled", defaultValue = "true")
    boolean enabled;

    @AroundInvoke
    public Object checkLimit(InvocationContext context) throws Exception {
        RateLimited config = context.getMethod().getAnnotation(RateLimited.class);
        if (config == null || !enabled) {
            return context.proceed();
        }

        String ip = VozDaRuaUtils.clientIp(request);
        String key = context.getMethod().getDeclaringClass().getName() + "#" + context.getMethod().getName() + "|" + ip;

        Bucket bucket = BUCKETS.computeIfAbsent(key, k -> newBucket(config));
        if (!bucket.tryConsume(1)) {
            throw new WebApplicationException(
                    Response.status(429).entity(new MessageResponse(appMessages.rate_limit_exceeded())).build());
        }

        return context.proceed();
    }

    private static Bucket newBucket(RateLimited config) {
        Bandwidth limit = Bandwidth.builder()
                .capacity(config.limit())
                .refillIntervally(config.limit(), Duration.ofSeconds(config.windowSeconds()))
                .build();
        return Bucket.builder().addLimit(limit).build();
    }
}
