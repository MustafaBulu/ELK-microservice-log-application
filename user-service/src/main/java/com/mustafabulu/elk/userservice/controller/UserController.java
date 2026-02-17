package com.mustafabulu.elk.userservice.controller;

import com.mustafabulu.elk.userservice.controller.docs.UserApiDoc;
import com.mustafabulu.elk.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController implements UserApiDoc {

    private final UserService userService;

    @Override
    @GetMapping("/get-user")
    public ResponseEntity<String> getUsers() {
        return ResponseEntity.ok(userService.getUsersMessage());
    }
}

