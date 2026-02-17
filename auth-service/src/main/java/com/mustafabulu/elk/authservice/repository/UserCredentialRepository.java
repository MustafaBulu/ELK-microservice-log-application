package com.mustafabulu.elk.authservice.repository;

import com.mustafabulu.elk.authservice.entity.UserCredential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserCredentialRepository extends JpaRepository<UserCredential, Integer> {
    Optional<UserCredential> findByName(String username);

    Boolean existsByNameOrEmail(String name, String email);
}

