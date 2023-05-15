package ru.mirea.secureapp.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.mirea.secureapp.data.AnswerBase;
import ru.mirea.secureapp.data.InputMessage;
import ru.mirea.secureapp.services.CipherService;
import ru.mirea.secureapp.services.UserService;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(path = "/api/aes", produces = "application/json")
@Slf4j
public class CipherController {
    @Autowired
    private UserService userService;
    @Autowired
    private CipherService cipherService;

    @PostMapping("/text")
    public AnswerBase processText(@AuthenticationPrincipal UserDetails userDetails, @RequestBody InputMessage message) {
        log.info("Received message");
        log.info(MessageFormat.format("Message: {0}", message.getTextMessage()));
        log.info(MessageFormat.format("Encode: {0}", message.isToEncode()));
        String result = message.isToEncode()
                ? cipherService.encode(message.getTextMessage(), userService.findByUsername(userDetails.getUsername()))
                : cipherService.decode(message.getTextMessage(), userService.findByUsername(userDetails.getUsername()));
        log.info(MessageFormat.format("Result: {0}", result));
        Map<Object, Object> model = new HashMap<>();
        model.put("message", result);
        model.put("toEncode", message.isToEncode());
        var answer = new AnswerBase();
        answer.setResult(model);
        return answer;
    }

    @GetMapping("/key")
    public AnswerBase getKey(@AuthenticationPrincipal UserDetails userDetails) {
        Map<Object, Object> model = new HashMap<>();
        model.put("key", cipherService.getBase64Key(userService.findByUsername(userDetails.getUsername())));
        var answer = new AnswerBase();
        answer.setResult(model);
        return answer;
    }
}
