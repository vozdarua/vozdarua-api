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

    // Keeps only the local part of the email (before @) followed by "...", so the full
    // address never leaves the server for a comment/ranking display. Mirrors the format
    // chosen for the frontend's own (now-redundant, idempotent) src/utils/email.js.
    public static String maskEmail(String email) {
        if (email == null || email.isBlank()) return email;
        int at = email.indexOf('@');
        return (at == -1 ? email : email.substring(0, at)) + "...";
    }

    // Great-circle distance in km between two lat/lng points. Same formula the frontend
    // already had client-side (stores/cidade.js), moved here because Geoapify's nearby-places
    // response doesn't include a ready-made distance.
    public static double haversineKm(double lat1, double lon1, double lat2, double lon2) {
        double earthRadiusKm = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
            + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
            * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return earthRadiusKm * c;
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
