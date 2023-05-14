package ru.mirea.secureapp.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.mirea.secureapp.data.InputMessage;
import ru.mirea.secureapp.services.CipherService;

import java.text.MessageFormat;

@RestController
@RequestMapping(path = "/api/aes", produces = "application/json")
@Slf4j
public class CipherController {
    @Autowired
    private CipherService cipherService;

    @PostMapping("/text")
    public String processText(@RequestBody InputMessage message) {
        log.info("Received message");
        log.info(MessageFormat.format("Message: {0}", message.getTextMessage()));
        log.info(MessageFormat.format("Encode: {0}", message.isToEncode()));
        String result = message.isToEncode() ? cipherService.encode(message.getTextMessage()) : cipherService.decode(message.getTextMessage());
        log.info(MessageFormat.format("Result: {0}", result));
        return result;
    }

    @GetMapping("/key")
    public String getKey() {
        return cipherService.getBase64Key();
    }
}
