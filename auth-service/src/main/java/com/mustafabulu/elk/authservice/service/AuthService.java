package com.mustafabulu.elk.authservice.service;

import com.mustafabulu.elk.authservice.entity.UserCredential;

public interface AuthService {

    String saveUser(UserCredential credential);

    String generateToken(String username);

    void validateToken(String token);
}

