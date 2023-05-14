package ru.mirea.secureapp.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.mirea.secureapp.components.AuthenticationRequest;
import ru.mirea.secureapp.components.jwt.JwtTokenProvider;
import ru.mirea.secureapp.services.UserService;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping(path = "/api/auth", produces = "application/json")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;

    private final JwtTokenProvider jwtTokenProvider;

    private final UserService userService;

    @SuppressWarnings("rawtypes")
    @PostMapping("/register")
    public ResponseEntity register(@RequestBody AuthenticationRequest data) {
        String username = data.getUsername().trim();
        String password = data.getPassword().trim();
        userService.validate(username, password);
        userService.save(username, password);
        Map<Object, Object> model = new HashMap<>();
        model.put("username", username);
        return ok(model);
    }

    @SuppressWarnings("rawtypes")
    @PostMapping("/signin")
    public ResponseEntity signin(@RequestBody AuthenticationRequest data) {
        try {
            String username = data.getUsername().trim();
            var authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, data.getPassword().trim()));
            return getResponseEntity(
                    username,
                    jwtTokenProvider.createToken(authentication, false),
                    jwtTokenProvider.createToken(authentication, true)
            );
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid username/password supplied");
        }
    }

    @SuppressWarnings("rawtypes")
    @GetMapping("/refresh")
    public ResponseEntity refreshToken(@AuthenticationPrincipal UserDetails userDetails) {
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

    @SuppressWarnings("rawtypes")
    private ResponseEntity getResponseEntity(
            String username,
            String accessToken,
            String refreshToken
    ) {
        Map<Object, Object> model = new HashMap<>();
        model.put("username", username);
        model.put("accessToken", accessToken);
        model.put("refreshToken", refreshToken);
        return ok(model);
    }
}
