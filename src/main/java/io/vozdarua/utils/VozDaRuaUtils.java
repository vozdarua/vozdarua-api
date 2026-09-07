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

    // Strips any path segments and keeps only safe characters, so a client-supplied
    // filename can't escape the intended S3/R2 key prefix (e.g. "../../etc/passwd").
    public static String sanitizeFileName(String fileName) {
        if (fileName == null || fileName.isBlank()) return "file";
        String base = fileName.replace("\\", "/");
        int lastSlash = base.lastIndexOf('/');
        if (lastSlash >= 0) base = base.substring(lastSlash + 1);
        base = base.replaceAll("[^a-zA-Z0-9._-]", "_");
        base = base.replaceAll("^\\.+", "");
        if (base.isBlank()) base = "file";
        return base.length() > 100 ? base.substring(base.length() - 100) : base;
    }
}
