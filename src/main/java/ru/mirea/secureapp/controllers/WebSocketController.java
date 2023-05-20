package ru.mirea.secureapp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import ru.mirea.secureapp.data.WSContent;
import ru.mirea.secureapp.data.WSMessage;
import ru.mirea.secureapp.services.CipherService;
import ru.mirea.secureapp.services.DocumentService;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Controller
public class WebSocketController {
    @Autowired
    private DocumentService documentService;

    @Autowired
    private CipherService cipherService;

    @MessageMapping("/send")
    @SendTo("/topic/messages")
    public WSMessage greeting(WSMessage message) {
        var document = documentService.getDocument((long) message.getDocumentId());
        WSMessage decodedMessage = new WSMessage(
                message.getDocumentId(),
                cipherService.decode(message.getFromUser(), document),
                cipherService.decode(message.getCommand(), document),
                (ArrayList<WSContent>)
                        message.getContent().stream()
                                .map(c -> new WSContent(
                                        c.getNumber(),
                                        cipherService.decode(c.getData(), document),
                                        cipherService.decode(c.getAlign(), document)
                                ))
                                .collect(Collectors.toList())

        );
        documentService.editDocument(decodedMessage);
        return message;
    }
}
