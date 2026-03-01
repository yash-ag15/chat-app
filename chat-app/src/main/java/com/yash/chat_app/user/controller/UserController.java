package com.yash.chat_app.user.controller;

import com.yash.chat_app.user.User;
import com.yash.chat_app.user.dto.UserAuthResponse;
import com.yash.chat_app.user.dto.UserEditRequest;
import com.yash.chat_app.user.security.UserPrincipal;
import com.yash.chat_app.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5175")
@RestController
@RequestMapping("/api/users")
public class UserController {
    @GetMapping("me")

    public UserAuthResponse me(Authentication  authentication){
        UserPrincipal userPrincipal=(UserPrincipal)authentication.getPrincipal();
        User user=userPrincipal.getUser();
       UserAuthResponse userAuthResponse=new UserAuthResponse(user.getId(),
               user.getUsername(),
               user.getEmail(),user.getAbout());
       return userAuthResponse;


    }
    @Autowired
    UserService userService;
    @PutMapping("me")
    public UserAuthResponse me(Authentication authentication , @RequestBody UserEditRequest editRequest){
        UserPrincipal userPrincipal=(UserPrincipal)authentication.getPrincipal();
        User user=userPrincipal.getUser();
        UserAuthResponse authResponse= userService.editProfile(editRequest,user.getEmail());
        return authResponse;
    }
}
