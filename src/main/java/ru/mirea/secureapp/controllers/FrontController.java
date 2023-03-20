package ru.mirea.secureapp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.mirea.secureapp.data.InputMessage;
import ru.mirea.secureapp.services.FrontService;

import java.text.MessageFormat;

@RestController
@RequestMapping(path = "/api", produces = "application/json")
public class FrontController {
    @Autowired
    private FrontService frontService;

    @PostMapping("/text")
    public String processText(@RequestBody InputMessage message) {
        System.out.println("Recieved:");
        System.out.println(MessageFormat.format("Message: {0}", message.getTextMessage()));
        System.out.println(MessageFormat.format("Encode: {0}", message.isToEncode()));
        String result = message.isToEncode() ? frontService.encode(message.getTextMessage()) : frontService.decode(message.getTextMessage());
        System.out.println(MessageFormat.format("Result: {0}", result));
        return result;
    }

    @GetMapping("/key")
    public String getKey(){
        return frontService.getBase64Key();
    }
}
