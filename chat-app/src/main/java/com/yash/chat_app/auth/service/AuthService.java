package com.yash.chat_app.auth.service;

import com.yash.chat_app.auth.dto.RegistrationRequest;
import com.yash.chat_app.user.User;
import com.yash.chat_app.user.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Autowired
    private UserRepo repo;
    @Autowired
    private BCryptPasswordEncoder encoder;


    public void savedUser(RegistrationRequest request) {
User user=new User();

user.setEmail(request.email());
user.setUsername(request.username());
user.setPassword(encoder.encode(request.password()));
user.setRole("ROLE_USER");
repo.save(user);



    }

}
