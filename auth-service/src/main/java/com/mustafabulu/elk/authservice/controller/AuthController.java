package com.mustafabulu.elk.authservice.controller;

import com.mustafabulu.elk.authservice.controller.docs.AuthApiDoc;
import com.mustafabulu.elk.authservice.dto.AuthRequest;
import com.mustafabulu.elk.authservice.entity.UserCredential;
import com.mustafabulu.elk.authservice.service.AuthService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController implements AuthApiDoc {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;

    @Override
    @PostMapping("/register")
    public ResponseEntity<String> addNewUser(@Valid @RequestBody UserCredential user) {
        return ResponseEntity.ok(authService.saveUser(user));
    }

    @Override
    @PostMapping("/token")
    public ResponseEntity<String> getToken(@Valid @RequestBody AuthRequest authRequest) {
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        if (authenticate.isAuthenticated()) {
            String token = authService.generateToken(authRequest.getUsername());
            return ResponseEntity.ok(token);
        }
        throw new IllegalArgumentException("Invalid access");
    }

    @Override
    @GetMapping("/validate")
    public ResponseEntity<String> validateToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid authorization header format");
        }
        String token = authorizationHeader.substring(7);
        authService.validateToken(token);
        return ResponseEntity.ok("Token is valid");
    }
}

