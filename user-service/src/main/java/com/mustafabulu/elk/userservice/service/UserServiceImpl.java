package com.mustafabulu.elk.userservice.service;

import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Override
    public String getUsersMessage() {
        return "These are all the users";
    }
}

