package ru.mirea.secureapp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.mirea.secureapp.data.AnswerBase;
import ru.mirea.secureapp.data.DocumentInfo;
import ru.mirea.secureapp.data.DocumentRight;
import ru.mirea.secureapp.data.UserInfo;
import ru.mirea.secureapp.services.DocumentService;
import ru.mirea.secureapp.services.RoleService;
import ru.mirea.secureapp.services.UserService;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController()
@RequestMapping(path = "/api/users", produces = "application/json")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private DocumentService documentService;

    @GetMapping("/me")
    public AnswerBase currentUser(@AuthenticationPrincipal UserDetails userDetails) {
        var user = userService.findByUsername(userDetails.getUsername());
        Map<Object, Object> model = new HashMap<>();
        model.put("username", userDetails.getUsername());
        model.put("myRoles", user.getRoles());
        model.put("allRoles", roleService.getRoles());
        var answer = new AnswerBase();
        answer.setResult(model);
        return answer;
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnswerBase> getUser(@PathVariable int id, @AuthenticationPrincipal UserDetails userDetails) {
        var user = userService.findById((long) id);
        Map<Object, Object> model = new HashMap<>();
        var answer = new AnswerBase();
        if (user.isEmpty()) {
            answer.setError("User not found!");
            return ResponseEntity.status(HttpStatusCode.valueOf(404)).body(answer);
        } else {
            model.put("username", user.get().getUsername());
            model.put("allRoles", roleService.getRoles());
            if (userDetails.getUsername().equals(user.get().getUsername()))
                model.put("me", true);
            else {
                model.put("me", false);
                model.put("documents",
                        documentService.getDocumentsOfCurrentUser(userService.findByUsername(userDetails.getUsername()))
                                .stream().map(d-> new DocumentInfo(d.getId(), d.getName(), d.getLastEditBy(), d.getOwner()))
                                .collect(Collectors.toList())
                );
                model.put("allRoles", roleService.getRolesRights());
            }
            answer.setResult(model);
            return ResponseEntity.ok(answer);
        }
    }

    @PostMapping("/changeRights")
    public ResponseEntity<AnswerBase> addUserRight(@RequestBody DocumentRight documentRight) {
        var user = userService.findById(documentRight.getUserId());
        Map<Object, Object> model = new HashMap<>();
        var answer = new AnswerBase();
        if (user.isPresent()) {
            documentService.checkDocumentRights(documentRight.getDocumentId(), user.get(), documentRight.getRoleId());
            model.put("documentRight", documentRight);
            answer.setResult(model);
            return ResponseEntity.ok(answer);
        } else {
            answer.setError("User not found!");
            return ResponseEntity.status(HttpStatusCode.valueOf(404)).body(answer);
        }
    }

    @PostMapping("/updateRoles")
    public ResponseEntity<AnswerBase> setUserRole(@AuthenticationPrincipal UserDetails userDetails, @RequestBody UserInfo userInfo) {
        var user = userService.findByUsername(userDetails.getUsername());
        Map<Object, Object> model = new HashMap<>();
        var answer = new AnswerBase();
        if (user.getUsername().equals(userInfo.getUsername()) ||
//                userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))
                user.getRoles().stream().anyMatch(r -> r.getName().equals("ROLE_ADMIN"))) {
            var changingUser = user.getUsername().equals(userInfo.getUsername())
                    ? user
                    : userService.findByUsername(userInfo.getUsername());
            changingUser.setRoles(userInfo.getRoles());
            userService.update(changingUser);
            model.put("username", userDetails.getUsername());
            model.put("changedRoles", user.getRoles());
            answer.setResult(model);
            return ResponseEntity.ok(answer);
        } else {
            answer.setError("You don't have permissions!");
            return ResponseEntity.status(HttpStatusCode.valueOf(403)).body(answer);
        }
    }

    @GetMapping("/all")
    public AnswerBase getUsers() {
        Map<Object, Object> model = new HashMap<>();
        model.put("users", userService.getUserList());
        var answer = new AnswerBase();
        answer.setResult(model);
        return answer;
    }
}
