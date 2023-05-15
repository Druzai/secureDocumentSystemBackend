package ru.mirea.secureapp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import ru.mirea.secureapp.data.WSMessage;
import ru.mirea.secureapp.services.DocumentService;

@Controller
public class WebSocketController {
    @Autowired
    private DocumentService documentService;

    @MessageMapping("/send")
    @SendTo("/topic/messages")
    public WSMessage greeting(WSMessage message) {
        // TODO: decrypt
        documentService.editDocument(message);
        // TODO: encrypt
        return message;
    }
}
