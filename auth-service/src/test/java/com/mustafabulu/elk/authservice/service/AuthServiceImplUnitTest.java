package com.mustafabulu.elk.authservice.service;

import com.mustafabulu.elk.authservice.entity.UserCredential;
import com.mustafabulu.elk.authservice.outbox.OutboxService;
import com.mustafabulu.elk.authservice.repository.UserCredentialRepository;
import com.mustafabulu.elk.eventcontract.UserRegisteredEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplUnitTest {

    @Mock
    private UserCredentialRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private OutboxService outboxService;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void saveUser_shouldThrowWhenUserAlreadyExists() {
        UserCredential credential = new UserCredential(0, "demo", "demo@test.com", "raw-pass-123");
        when(repository.existsByNameOrEmail("demo", "demo@test.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.saveUser(credential))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Username or email already exists");

        verify(repository, never()).save(any(UserCredential.class));
        verify(outboxService, never()).enqueueUserRegisteredEvent(any(UserRegisteredEvent.class));
    }

    @Test
    void saveUser_shouldEncodePersistAndEnqueueEvent() {
        UserCredential credential = new UserCredential(0, "demo", "demo@test.com", "raw-pass-123");
        when(repository.existsByNameOrEmail("demo", "demo@test.com")).thenReturn(false);
        when(passwordEncoder.encode("raw-pass-123")).thenReturn("encoded-pass");
        when(repository.save(any(UserCredential.class))).thenAnswer(invocation -> {
            UserCredential saved = invocation.getArgument(0);
            saved.setId(42);
            return saved;
        });

        String result = authService.saveUser(credential);

        assertThat(result).isEqualTo("user added to the system");

        ArgumentCaptor<UserCredential> credentialCaptor = ArgumentCaptor.forClass(UserCredential.class);
        verify(repository).save(credentialCaptor.capture());
        assertThat(credentialCaptor.getValue().getPassword()).isEqualTo("encoded-pass");

        ArgumentCaptor<UserRegisteredEvent> eventCaptor = ArgumentCaptor.forClass(UserRegisteredEvent.class);
        verify(outboxService).enqueueUserRegisteredEvent(eventCaptor.capture());
        UserRegisteredEvent event = eventCaptor.getValue();
        assertThat(event.getUserId()).isEqualTo(42);
        assertThat(event.getUsername()).isEqualTo("demo");
        assertThat(event.getEmail()).isEqualTo("demo@test.com");
        assertThat(event.getEventType()).isEqualTo("USER_REGISTERED");
    }

    @Test
    void generateToken_shouldDelegateToJwtService() {
        when(jwtService.generateToken("demo")).thenReturn("jwt-token");

        String token = authService.generateToken("demo");

        assertThat(token).isEqualTo("jwt-token");
        verify(jwtService).generateToken("demo");
    }

    @Test
    void validateToken_shouldDelegateToJwtService() {
        authService.validateToken("token-value");

        verify(jwtService).validateToken(eq("token-value"));
    }
}

