package com.yash.chat_app.user.service;

import com.yash.chat_app.user.User;
import com.yash.chat_app.user.dto.UserAuthResponse;
import com.yash.chat_app.user.dto.UserEditRequest;
import com.yash.chat_app.user.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service

public class UserService {
    @Autowired
    UserRepo userRepo;
    public UserAuthResponse editProfile(UserEditRequest editRequest, String email) {

        User user=userRepo.findByEmail(email);
        user.setUsername(editRequest.userName());
        user.setAbout(editRequest.about());
        userRepo.save(user);
         return new UserAuthResponse(user.getId(), user.getUsername(),
                 user.getEmail(),
                 user.getAbout());
    }
}
