package ru.mirea.secureapp.services;

import org.springframework.stereotype.Service;
import ru.mirea.secureapp.components.aes.Cipher;
import ru.mirea.secureapp.components.aes.Type;

@Service
public class CipherService {
    private final Cipher cipher = new Cipher(Type.AES_256);

    public String encode(String text) {
        return cipher.encrypt(text);
    }

    public String decode(String encodedText) {
        return cipher.decrypt(encodedText);
    }

    public String getBase64Key() {
        return cipher.getKeyBase64();
    }
}
