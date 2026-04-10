package com.yash.chat_app.user.controller;

import com.yash.chat_app.chats.entity.Chat;
import com.yash.chat_app.chats.entity.ChatMember;
import com.yash.chat_app.chats.repo.ChatMemberRepo;
import com.yash.chat_app.chats.repo.ChatRepo;
import com.yash.chat_app.user.User;
import com.yash.chat_app.user.dto.*;
import com.yash.chat_app.user.repo.UserRepo;
import com.yash.chat_app.user.security.UserPrincipal;
import com.yash.chat_app.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    UserRepo userRepo;

    @Autowired
    ChatRepo chatRepo;
    @Autowired
    ChatMemberRepo memberRepo;

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

    @GetMapping("/profile/{userId}")
    public UserProfileResponse getUserProfile(@PathVariable Long userId) {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return new UserProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getAbout(),
                user.getProfilePhotoUrl()
        );
}

@GetMapping("/chat/{chatId}")
public GroupProfileResponse getGroupProfile(@PathVariable Long chatId) {

    Chat chat = chatRepo.findById(chatId)
            .orElseThrow(() -> new RuntimeException("Chat not found"));

    if (!chat.isGroup()) {
        throw new RuntimeException("Not a group chat");
    }

    List<ChatMember> chatMembers = memberRepo.findByChat(chat);

    List<GroupMemberDto> members = chatMembers.stream()
            .map(cm -> new GroupMemberDto(
                    cm.getUser().getId(),
                    cm.getUser().getUsername(),
                    cm.getUser().getProfilePhotoUrl(),
                    cm.getUser().getAbout()
            ))
            .toList();

    return new GroupProfileResponse(
            chat.getId(),
            chat.getName(),
            members.size(),
            chat.getGroupPhotoUrl(),
            members
    );
}
@PutMapping("/chat/{chatId}")
    public GroupProfileResponse updateGroupPhoto(@PathVariable Long chatId,@RequestPart("file")MultipartFile file, Authentication authentication){
    UserPrincipal userPrincipal=(UserPrincipal)authentication.getPrincipal();
    User user=userPrincipal.getUser();
    GroupProfileResponse profileResponse=userService.updateGroupPhoto(chatId,file,user);

return profileResponse;
}

}
