package com.yash.chat_app.user.service;

import com.cloudinary.Cloudinary;
import com.yash.chat_app.chats.dto.ChatResponse;
import com.yash.chat_app.chats.entity.Chat;
import com.yash.chat_app.chats.entity.ChatMember;
import com.yash.chat_app.chats.repo.ChatMemberRepo;
import com.yash.chat_app.chats.repo.ChatRepo;
import com.yash.chat_app.user.User;
import com.yash.chat_app.user.dto.GroupMemberDto;
import com.yash.chat_app.user.dto.GroupProfileResponse;
import com.yash.chat_app.user.dto.UserAuthResponse;
import com.yash.chat_app.user.dto.UserEditRequest;
import com.yash.chat_app.user.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service

public class UserService {
    @Autowired
    UserRepo userRepo;
    @Autowired
    ChatRepo chatRepo;

    @Autowired
    ChatMemberRepo memberRepo;

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

    public GroupProfileResponse updateGroupPhoto(Long chatId, MultipartFile file, User user) {


        Chat chat = chatRepo.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat not found"));

        if(!chat.isGroup()){
            throw new RuntimeException("Not a Group Chat");
        }
   try{
    if(file!=null&&!file.isEmpty()){

        Map uploadResult=cloudinary.uploader().upload(
                file.getBytes(),
                Map.of("folder","chat-app/profile")
        );
        String imageUrl=uploadResult.get("secure_url").toString();
        chat.setGroupPhotoUrl(imageUrl);
    }
    }
 catch (Exception e){
     throw new RuntimeException("Image upload failed");
 }
   chatRepo.save(chat);

        List<ChatMember>chatMembers=memberRepo.findByChat(chat);
        List<GroupMemberDto>members=chatMembers.stream().map(
                cm->new GroupMemberDto(
                        cm.getUser().getId(),
                        cm.getUser().getUsername(),
                        cm.getUser().getProfilePhotoUrl(),
                        cm.getUser().getAbout()
                )
        ).toList();

    return new GroupProfileResponse(
            chat.getId(),
            chat.getName(),
            members.size(),
            chat.getGroupPhotoUrl(),
            members
    );
    }
}
