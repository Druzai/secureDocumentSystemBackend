package ru.mirea.secureapp.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.mirea.secureapp.SecureDocumentSystemApplicationTests;
import ru.mirea.secureapp.components.aes.Cipher;
import ru.mirea.secureapp.components.aes.Type;
import java.nio.charset.StandardCharsets;

import java.util.Arrays;

public class CryptTest extends SecureDocumentSystemApplicationTests {
    private Cipher cipher;

    @BeforeEach
    public void setUp() {
        cipher = new Cipher(Type.AES_256);
    }

    @Test
    public void TextShouldBeDecryptedEnglish() {
        String msg = "Testing cipher 123456789";

        String encodedString = cipher.encrypt(msg);
        String decodedString = cipher.decrypt(encodedString);

        Assertions.assertEquals(msg, decodedString);
    }

    @Test
    public void TextShouldBeDecryptedRussian() {
        String msg = "Тестирование шифратора cipher 123456789";

        String encodedString = cipher.encrypt(msg);
        String decodedString = cipher.decrypt(encodedString);

        Assertions.assertEquals(msg, decodedString);
    }
}
