package com.yash.chat_app.user.controller;

import com.yash.chat_app.user.User;
import com.yash.chat_app.user.dto.UserAuthResponse;
import com.yash.chat_app.user.security.UserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @GetMapping("me")

    public UserAuthResponse me(Authentication  authentication){
        UserPrincipal userPrincipal=(UserPrincipal)authentication.getPrincipal();
        User user=userPrincipal.getUser();
       UserAuthResponse userAuthResponse=new UserAuthResponse(user.getId(),
               user.getUsername(),
               user.getEmail());
       return userAuthResponse;


    }
}
