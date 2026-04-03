package com.yash.chat_app.user.service;

import com.cloudinary.Cloudinary;
import com.yash.chat_app.user.User;
import com.yash.chat_app.user.dto.UserAuthResponse;
import com.yash.chat_app.user.dto.UserEditRequest;
import com.yash.chat_app.user.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service

public class UserService {
    @Autowired
    UserRepo userRepo;

    @Autowired
    private Cloudinary cloudinary;
    public UserAuthResponse editProfile(UserEditRequest editRequest, String email, MultipartFile file) {

        User user=userRepo.findByEmail(email);
        user.setUsername(editRequest.userName());
        user.setAbout(editRequest.about());

        if(file != null && !file.isEmpty()){

            try{

                Map uploadResult=cloudinary.uploader().upload(
                        file.getBytes(),
                        Map.of(
                                "folder","chat-app/profile"
                        )
                );
                String imageUrl = uploadResult.get("secure_url").toString();
                String publicId = uploadResult.get("public_id").toString();


                user.setProfilePhotoUrl(imageUrl);
                user.setCloudinaryPublicId(publicId);

            }
            catch(Exception e){
               throw new RuntimeException("Image upload failed");
            }

        }
        userRepo.save(user);
         return new UserAuthResponse(user.getId(), user.getUsername(),
                 user.getEmail(),
                 user.getAbout(),
                 user.getProfilePhotoUrl());
    }
}
