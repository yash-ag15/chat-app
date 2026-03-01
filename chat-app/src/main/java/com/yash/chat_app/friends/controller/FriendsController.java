package com.yash.chat_app.friends.controller;

import com.yash.chat_app.friends.dto.FriendRequestResponse;
import com.yash.chat_app.friends.entity.FriendRequest;
import com.yash.chat_app.friends.service.FriendService;
import com.yash.chat_app.user.User;
import com.yash.chat_app.user.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin(origins = "http://localhost:5175")
@RestController
@RequestMapping("friends")
public class FriendsController {

    @Autowired
    FriendService friendService;


    @PostMapping("/requests/{receiverName}")
    public ResponseEntity<String> sendRequest(@PathVariable String receiverName, Authentication authentication){
        UserPrincipal userPrincipal=(UserPrincipal)authentication.getPrincipal();
        User sender =userPrincipal.getUser();
        friendService.sendRequest(sender,receiverName);
return ResponseEntity.ok("Request Send");

    }
    @GetMapping("/requests")
    public List<FriendRequestResponse> getAllIncomingRequest(Authentication authentication){
        UserPrincipal userPrincipal=(UserPrincipal)authentication.getPrincipal();
        User currentUser =userPrincipal.getUser();
        return friendService.getAllIncomingRequest(currentUser);


    }

    @PutMapping("/request/accept/{requestId}")
    public ResponseEntity<String> acceptRequest(@PathVariable Long requestId,Authentication authentication){
        UserPrincipal userPrincipal=(UserPrincipal)authentication.getPrincipal();
        User currentUser =userPrincipal.getUser();
        friendService.acceptRequest(currentUser,requestId);
        return ResponseEntity.ok("Request Accepted");
    }

    @PutMapping("/request/reject/{requestId}")
    public ResponseEntity<String> rejectRequest(@PathVariable Long requestId, Authentication authentication){
        UserPrincipal userPrincipal=(UserPrincipal)authentication.getPrincipal();
        User currentUser =userPrincipal.getUser();
        friendService.rejectRequest(currentUser,requestId);
        return ResponseEntity.ok("Request Rejected");
    }


}
