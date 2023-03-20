package ru.mirea.secureapp.services;

import org.springframework.stereotype.Service;
import ru.mirea.secureapp.aes.Cipher;
import ru.mirea.secureapp.aes.Type;

@Service
public class FrontService {
    private final Cipher cipher = new Cipher(Type.AES_128);

    public String encode(String text) {
        return cipher.encrypt(text.getBytes());
    }

    public String decode(String encodedText) {
        return cipher.decrypt(encodedText);
    }

    public String getBase64Key() {
        return cipher.getKeyBase64();
    }
}
