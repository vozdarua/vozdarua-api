package io.vozdarua.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VozDaRuaUtilsTest {

    @Test
    void keepsSafeFileNameUnchanged() {
        assertEquals("foto-1.jpg", VozDaRuaUtils.sanitizeFileName("foto-1.jpg"));
    }

    @Test
    void stripsPathTraversalAttempt() {
        assertEquals("passwd", VozDaRuaUtils.sanitizeFileName("../../etc/passwd"));
    }

    @Test
    void replacesUnsafeCharacters() {
        assertEquals("foto_com_espa_os_.jpg", VozDaRuaUtils.sanitizeFileName("foto com espaços!.jpg"));
    }

    @Test
    void fallsBackToDefaultForBlankOrNull() {
        assertEquals("file", VozDaRuaUtils.sanitizeFileName(null));
        assertEquals("file", VozDaRuaUtils.sanitizeFileName("   "));
    }

    @Test
    void stripsLeadingDots() {
        assertEquals("bashrc", VozDaRuaUtils.sanitizeFileName("..bashrc"));
    }

    @Test
    void masksEmailKeepingOnlyLocalPart() {
        assertEquals("pedrosilva...", VozDaRuaUtils.maskEmail("pedrosilva@gmail.com"));
    }

    @Test
    void masksEmailWithoutAtSign() {
        assertEquals("notanemail...", VozDaRuaUtils.maskEmail("notanemail"));
    }

    @Test
    void maskEmailPassesThroughBlankOrNull() {
        assertEquals(null, VozDaRuaUtils.maskEmail(null));
        assertEquals("   ", VozDaRuaUtils.maskEmail("   "));
    }

    @Test
    void haversineIsZeroForSamePoint() {
        assertEquals(0.0, VozDaRuaUtils.haversineKm(-23.2237, -45.9009, -23.2237, -45.9009), 0.001);
    }

    @Test
    void haversineMatchesKnownDistanceBetweenCities() {
        // São José dos Campos -> Jacareí is ~11km in a straight line
        double km = VozDaRuaUtils.haversineKm(-23.2237, -45.9009, -23.3053, -45.9658);
        assertEquals(11.0, km, 1.0);
    }
}
