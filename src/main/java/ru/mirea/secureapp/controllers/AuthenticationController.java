package ru.mirea.secureapp.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.mirea.secureapp.components.AuthenticationRequest;
import ru.mirea.secureapp.components.jwt.JwtTokenProvider;
import ru.mirea.secureapp.data.AnswerBase;
import ru.mirea.secureapp.services.CipherService;
import ru.mirea.secureapp.services.UserService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(path = "/api/auth", produces = "application/json")
@RequiredArgsConstructor
public class AuthenticationController {
    @Autowired
    private final AuthenticationManager authenticationManager;
    @Autowired
    private final JwtTokenProvider jwtTokenProvider;
    @Autowired
    private final UserService userService;
    @Autowired
    private final CipherService cipherService;

    @PostMapping("/register")
    public AnswerBase register(@RequestBody AuthenticationRequest data) {
        String username = data.getUsername().trim();
        String password = data.getPassword().trim();
        userService.validate(username, password);
        userService.save(username, password);
        Map<Object, Object> model = new HashMap<>();
        model.put("username", username);
        var answer = new AnswerBase();
        answer.setResult(model);
        return answer;
    }

    @PostMapping("/signin")
    public AnswerBase signin(@RequestBody AuthenticationRequest data) {
        try {
            String username = data.getUsername().trim();
            var authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, data.getPassword().trim()));
            userService.updateUserKey(username);
            return getResponseEntity(
                    username,
                    jwtTokenProvider.createToken(authentication, false),
                    jwtTokenProvider.createToken(authentication, true)
            );
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid username/password supplied");
        }
    }

    @GetMapping("/refresh")
    public AnswerBase refreshToken(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            String username = userDetails.getUsername().trim();
            var authentication = new UsernamePasswordAuthenticationToken(username, null, userDetails.getAuthorities());
            return getResponseEntity(
                    username,
                    jwtTokenProvider.createToken(authentication, false),
                    jwtTokenProvider.createToken(authentication, true)
            );
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid username/password supplied");
        }
    }

    private AnswerBase getResponseEntity(
            String username,
            String accessToken,
            String refreshToken
    ) {
        Map<Object, Object> model = new HashMap<>();
        model.put("username", username);
        model.put("accessToken", accessToken);
        model.put("refreshToken", refreshToken);
        var answer = new AnswerBase();
        answer.setResult(model);
        return answer;
    }
}
