package com.yash.chat_app.friends.controller;

import com.yash.chat_app.friends.dto.FriendRequestResponse;
import com.yash.chat_app.friends.entity.FriendRequest;
import com.yash.chat_app.friends.service.FriendService;
import com.yash.chat_app.user.User;
import com.yash.chat_app.user.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("friends")
public class FriendsController {

    @Autowired
    FriendService friendService;


    @PostMapping("/requests/{receiverName}")
    public String sendRequest(@PathVariable String receiverName, Authentication authentication){
        UserPrincipal userPrincipal=(UserPrincipal)authentication.getPrincipal();
        User sender =userPrincipal.getUser();
        friendService.sendRequest(sender,receiverName);
return "Request Send";

    }
    @GetMapping("/requests")
    public List<FriendRequestResponse> getAllIncomingRequest(Authentication authentication){
        UserPrincipal userPrincipal=(UserPrincipal)authentication.getPrincipal();
        User currentUser =userPrincipal.getUser();
        return friendService.getAllIncomingRequest(currentUser);


    }

    @PutMapping("/request/accept/{requestId}")
    public String acceptRequest(@PathVariable Long requestId,Authentication authentication){
        UserPrincipal userPrincipal=(UserPrincipal)authentication.getPrincipal();
        User currentUser =userPrincipal.getUser();
        friendService.acceptRequest(currentUser,requestId);
        return "Request Accepted";
    }

    @PutMapping("/request/reject/{requestId}")
    public String rejectRequest(@PathVariable Long requestId,Authentication authentication){
        UserPrincipal userPrincipal=(UserPrincipal)authentication.getPrincipal();
        User currentUser =userPrincipal.getUser();
        friendService.rejectRequest(currentUser,requestId);
        return "Request Rejected";
    }


}
