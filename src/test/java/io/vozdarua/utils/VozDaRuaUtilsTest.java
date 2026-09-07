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
}
