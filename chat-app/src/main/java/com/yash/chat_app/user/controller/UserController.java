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
import org.springframework.web.multipart.MultipartFile;

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
               user.getEmail(),user.getAbout(),
               user.getProfilePhotoUrl());
       return userAuthResponse;


    }
    @Autowired
    UserService userService;
    @PutMapping(value = "/me", consumes = "multipart/form-data")
    public UserAuthResponse me(Authentication authentication ,  @RequestPart("user") UserEditRequest editRequest,
                               @RequestPart(value = "file", required = false) MultipartFile file){
        UserPrincipal userPrincipal=(UserPrincipal)authentication.getPrincipal();
        User user=userPrincipal.getUser();
        UserAuthResponse authResponse= userService.editProfile(editRequest,user.getEmail()
        ,file);
        return authResponse;
    }
}
