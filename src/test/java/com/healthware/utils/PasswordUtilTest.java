package com.healthware.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordUtilTest {

    @Test
    void encrypt_Success() {
        String password = "test123";
        String encrypted = PasswordUtil.encrypt(password);

        assertNotNull(encrypted);
        assertFalse(encrypted.isEmpty());
        assertEquals(32, encrypted.length()); // MD5 produces 32 hex characters
    }

    @Test
    void encrypt_SamePasswordSameResult() {
        String password = "test123";
        String encrypted1 = PasswordUtil.encrypt(password);
        String encrypted2 = PasswordUtil.encrypt(password);

        assertEquals(encrypted1, encrypted2);
    }

    @Test
    void encrypt_DifferentPasswordDifferentResult() {
        String encrypted1 = PasswordUtil.encrypt("password1");
        String encrypted2 = PasswordUtil.encrypt("password2");

        assertNotEquals(encrypted1, encrypted2);
    }

    @Test
    void verify_CorrectPassword() {
        String password = "test123";
        String encrypted = PasswordUtil.encrypt(password);

        assertTrue(PasswordUtil.verify(password, encrypted));
    }

    @Test
    void verify_WrongPassword() {
        String password = "test123";
        String encrypted = PasswordUtil.encrypt(password);

        assertFalse(PasswordUtil.verify("wrongpassword", encrypted));
    }

    @Test
    void verify_EmptyPassword() {
        String encrypted = PasswordUtil.encrypt("");

        assertTrue(PasswordUtil.verify("", encrypted));
        assertFalse(PasswordUtil.verify("notempty", encrypted));
    }
}
