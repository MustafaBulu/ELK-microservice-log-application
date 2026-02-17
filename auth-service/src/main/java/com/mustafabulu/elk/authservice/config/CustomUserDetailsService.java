package com.mustafabulu.elk.authservice.config;

import com.mustafabulu.elk.authservice.entity.UserCredential;
import com.mustafabulu.elk.authservice.repository.UserCredentialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserCredentialRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserCredential credential = repository.findByName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with name: " + username));
        return new CustomUserDetails(credential);
    }
}

