package ru.mirea.secureapp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.mirea.secureapp.data.AnswerBase;
import ru.mirea.secureapp.data.DocumentInfo;
import ru.mirea.secureapp.models.Document;
import ru.mirea.secureapp.models.Paragraph;
import ru.mirea.secureapp.models.User;
import ru.mirea.secureapp.services.CipherService;
import ru.mirea.secureapp.services.DocumentService;
import ru.mirea.secureapp.services.UserService;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController()
@RequestMapping(path = "/api/document", produces = "application/json")
public class DocumentController {
    @Autowired
    private DocumentService documentService;

    @Autowired
    private UserService userService;

    @Autowired
    private CipherService cipherService;

    @GetMapping("/allByUser")
    public AnswerBase getDocuments(@AuthenticationPrincipal UserDetails userDetails) {
        Map<Object, Object> model = new HashMap<>();
        model.put("documents",
                documentService.getUserDocuments(userService.findByUsername(userDetails.getUsername()))
                        .stream().map(DocumentInfo::new).collect(Collectors.toList())
        );
        var answer = new AnswerBase();
        answer.setResult(model);
        return answer;
//        return "documents";
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnswerBase> getDocument(@PathVariable int id, @AuthenticationPrincipal UserDetails userDetails) {
        var document = documentService.getDocument((long) id);
        Map<Object, Object> model = new HashMap<>();
        var answer = new AnswerBase();
        if (document == null) {
            answer.setError("User not found!");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(answer);
        }
        var user = userService.findByUsername(userDetails.getUsername());
        var editor = false;
        var owner = false;
        if (document.getOwner() != user) {
            for (var docRights : document.getDocumentUserRightsList()) {
                if (docRights.getUser().equals(user) && docRights.getRole().getName().equals("ROLE_EDITOR")) {
                    editor = true;
                    break;
                }
            }
        } else {
            editor = true;
            owner = true;
        }
        model.put("editor", editor);
        model.put("document", new DocumentInfo(document));
        model.put("owner", owner);
//        model.put("lastEditedBy", document.getLastEditBy());
        if (!document.getParagraphList().isEmpty())
            document.setParagraphList(document.getParagraphList().stream()
                    .sorted(Comparator.comparingInt(Paragraph::getNumber)).toList());
        model.put("documentParagraphs", document.getParagraphList());
        answer.setResult(model);
        return ResponseEntity.ok(answer);
//        return "editor";
    }

    @PostMapping("/new")
    public ResponseEntity<AnswerBase> addDocument(
            @RequestBody DocumentInfo documentInfo,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Map<Object, Object> model = new HashMap<>();
        var answer = new AnswerBase();
        if (documentInfo.getName() == null || documentInfo.getName().trim().isEmpty()) {
            answer.setError("Wrong name!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(answer);
//            return "new_document";
        }
        var document = documentService.createDocument(userService.findByUsername(userDetails.getUsername()), documentInfo.getName());
        model.put("document", new DocumentInfo(document));
        answer.setResult(model);
        return ResponseEntity.ok(answer);
//        return "redirect:/doc/" + doc.getId();
    }

    @GetMapping("/{id}/wsKey")
    public ResponseEntity<AnswerBase> getDocuments(@PathVariable int id, @AuthenticationPrincipal UserDetails userDetails) {
        Document document = documentService.getDocument((long) id);
        Map<Object, Object> model = new HashMap<>();
        var answer = new AnswerBase();
        if (document == null) {
            answer.setError("User not found!");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(answer);
        }
        User user = userService.findByUsername(userDetails.getUsername());
        model.put("documentWsKey", cipherService.encode(document.getCryptKey(), user)); // Encrypted
        answer.setResult(model);
        return ResponseEntity.ok(answer);
    }
}
