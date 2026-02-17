package com.mustafabulu.elk.authservice.service;

import com.mustafabulu.elk.authservice.entity.UserCredential;
import com.mustafabulu.elk.authservice.outbox.OutboxService;
import com.mustafabulu.elk.authservice.repository.UserCredentialRepository;
import com.mustafabulu.elk.eventcontract.UserRegisteredEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserCredentialRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final OutboxService outboxService;

    @Override
    @Transactional
    public String saveUser(UserCredential credential) {
        if (Boolean.TRUE.equals(repository.existsByNameOrEmail(credential.getName(), credential.getEmail()))) {
            throw new IllegalArgumentException("Username or email already exists");
        }
        credential.setPassword(passwordEncoder.encode(credential.getPassword()));
        UserCredential savedCredential = repository.save(credential);

        outboxService.enqueueUserRegisteredEvent(UserRegisteredEvent.create(
                savedCredential.getId(),
                savedCredential.getName(),
                savedCredential.getEmail()
        ));

        return "user added to the system";
    }

    @Override
    public String generateToken(String username) {
        return jwtService.generateToken(username);
    }

    @Override
    public void validateToken(String token) {
        jwtService.validateToken(token);
    }
}

