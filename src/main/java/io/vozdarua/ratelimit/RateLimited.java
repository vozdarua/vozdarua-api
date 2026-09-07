package io.vozdarua.ratelimit;

import jakarta.enterprise.util.Nonbinding;
import jakarta.interceptor.InterceptorBinding;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Limits how often a JAX-RS resource method can be called by the same client IP.
 * Backed by an in-memory Bucket4j bucket per (IP, method) — see RateLimitInterceptor.
 */
@InterceptorBinding
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimited {

    /** Max requests allowed within the window. Nonbinding: only the annotation's presence matters to CDI. */
    @Nonbinding
    int limit();

    /** Window size, in seconds, over which the limit refills. Nonbinding, same reason. */
    @Nonbinding
    int windowSeconds();
}
