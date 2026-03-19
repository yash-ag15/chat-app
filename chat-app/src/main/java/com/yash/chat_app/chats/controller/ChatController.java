package com.yash.chat_app.chats.controller;

import com.yash.chat_app.chats.dto.AddMemberRequest;
import com.yash.chat_app.chats.dto.ChatResponse;
import com.yash.chat_app.chats.dto.CreateGroupRequest;
import com.yash.chat_app.chats.dto.MessageResponse;
import com.yash.chat_app.chats.entity.Chat;
import com.yash.chat_app.chats.service.ChatService;
import com.yash.chat_app.user.User;
import com.yash.chat_app.user.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin(origins = "http://localhost:5175")
@RestController
public class ChatController {
    @Autowired
    ChatService chatService;
//@GetMapping("/chat/private/{username}")
//    public Chat getPrivateChat(Authentication authentication, @PathVariable String username){
//    UserPrincipal userPrincipal=(UserPrincipal)authentication.getPrincipal();
//    User currUser=userPrincipal.getUser();
//    return chatService.makePrivateChat(currUser,username);
//}
@GetMapping("/chats")
public List<ChatResponse> getAllChat(Authentication authentication ){
    UserPrincipal userPrincipal=(UserPrincipal) authentication.getPrincipal();
    User user= userPrincipal.getUser();
    return chatService.getAllChat(user);
}

@GetMapping("/messages/{chatId}")
    public Page<MessageResponse> getChatMessages(Authentication authentication, @PathVariable Long chatId, @RequestParam int page,
                                                 @RequestParam int size){
    UserPrincipal userPrincipal=(UserPrincipal) authentication.getPrincipal();
    User user= userPrincipal.getUser();
   return chatService.getChatMessages(user,chatId,page,size);

}
@PostMapping("/chats/group")
    public ResponseEntity<String> createGroup(Authentication authentication, @RequestBody CreateGroupRequest createGroupRequest){
    UserPrincipal userPrincipal=(UserPrincipal) authentication.getPrincipal();
    User user= userPrincipal.getUser();

    String chatName=createGroupRequest.chatName();
    List<String > memberUserNames =createGroupRequest.memberUserNames();
     chatService.createGroup(user,chatName,memberUserNames);
return ResponseEntity.ok().body("Group created");

}
@PostMapping("chats/{chatId}/members")
public ResponseEntity<String>addMember(Authentication authentication, @RequestBody AddMemberRequest addMemberRequest, @PathVariable Long chatId){
    UserPrincipal userPrincipal=(UserPrincipal) authentication.getPrincipal();
    User user= userPrincipal.getUser();

    chatService.addMember(user,chatId,addMemberRequest);
    return ResponseEntity.ok().body("Members added");

}

}
