package ru.mirea.secureapp.services;

import org.springframework.stereotype.Service;
import ru.mirea.secureapp.components.aes.Cipher;
import ru.mirea.secureapp.components.aes.Type;
import ru.mirea.secureapp.models.Document;
import ru.mirea.secureapp.models.User;

import java.util.HashMap;

@Service
public class CipherService {
    private final HashMap<Long, Cipher> userCipherHashMap = new HashMap<>();
    private final HashMap<Long, Cipher> documentCipherHashMap = new HashMap<>();

    public String encode(String text, User user) {
        return getCipher(user).encrypt(text);
    }

    public String encode(String text, Document document) {
        return getCipher(document).encrypt(text);
    }

    public String decode(String encodedText, User user) {
        return getCipher(user).decrypt(encodedText);
    }

    public String decode(String encodedText, Document document) {
        return getCipher(document).decrypt(encodedText);
    }

    public String getBase64Key() {
        Cipher cipher = new Cipher(Type.AES_256);
        return cipher.getKeyBase64();
    }

    public String getBase64Key(User user) {
        return getCipher(user).getKeyBase64();
    }

    public String getBase64Key(Document document) {
        return getCipher(document).getKeyBase64();
    }

    public void updateKey(Long id, String key){
        userCipherHashMap.put(id, new Cipher(key));
    }

    private Cipher getCipher(User user) {
        if (!userCipherHashMap.containsKey(user.getId())) {
            userCipherHashMap.put(user.getId(), new Cipher(user.getCryptKey()));
        }
        return userCipherHashMap.get(user.getId());
    }

    private Cipher getCipher(Document document) {
        if (!documentCipherHashMap.containsKey(document.getId())) {
            documentCipherHashMap.put(document.getId(), new Cipher(document.getCryptKey()));
        }
        return documentCipherHashMap.get(document.getId());
    }
}
