package io.vozdarua.utils;

import io.vertx.core.http.HttpServerRequest;

public class VozDaRuaUtils {
    public static <T> T verifyNull(T oldObj, T newObj) {
        return newObj != null ? newObj : oldObj;
    }

    // ponytail: trusts X-Forwarded-For blindly (no reverse-proxy allowlist check yet);
    // fine while the only proxy in front is Cloudflare, revisit if that assumption changes.
    public static String clientIp(HttpServerRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.remoteAddress() != null ? request.remoteAddress().host() : null;
    }
}
