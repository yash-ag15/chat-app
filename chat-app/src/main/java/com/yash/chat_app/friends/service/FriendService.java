package com.yash.chat_app.friends.service;

import com.yash.chat_app.friends.dto.FriendRequestResponse;
import com.yash.chat_app.friends.entity.FriendRequest;
import com.yash.chat_app.friends.entity.FriendRequestStatus;
import com.yash.chat_app.friends.repo.FriendsConnectedRepo;
import com.yash.chat_app.friends.repo.FriendsRepo;
import com.yash.chat_app.user.User;
import com.yash.chat_app.user.repo.UserRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class FriendService {
    @Autowired
    UserRepo userRepo;

    @Autowired
    FriendsRepo friendsRepo;

    @Autowired
    FriendsConnectedRepo friendsConnectedRepo;

    @Autowired
    FriendsConnectedService friendsConnectedService;

    public void sendRequest(User sender, String receiverName) {

        User receiver = userRepo.findByUsername(receiverName);

        if(receiver==null)throw new RuntimeException("User not Found");



if(sender.getId().equals(receiver.getId())){
    throw new RuntimeException("Cannot Send Request to yourself");
}
        boolean pendingExists =
                friendsRepo.existsBySenderAndReceiverAndStatus(
                        sender, receiver, FriendRequestStatus.PENDING
                ) ||
                        friendsRepo.existsBySenderAndReceiverAndStatus(
                                receiver, sender, FriendRequestStatus.PENDING
                        );

        if (pendingExists) {
            throw new RuntimeException("Friend request already pending");
        }


        if (friendsConnectedRepo.existsByUser1AndUser2OrUser1AndUser2(sender, receiver,receiver,sender)) {
            throw new RuntimeException("Users are already friends");
        }

FriendRequest request=new FriendRequest();
request.setSender(sender);
request.setReceiver(receiver);
request.setStatus(FriendRequestStatus.PENDING);
friendsRepo.save(request);

    }

    public List<FriendRequestResponse> getAllIncomingRequest(User currentUser) {

     List<FriendRequest> request=   friendsRepo.findByReceiverAndStatus(currentUser,FriendRequestStatus.PENDING);

     List<FriendRequestResponse> friendRequestResponses=new ArrayList<>();


     for(FriendRequest request1:request){

         FriendRequestResponse requestResponse=new FriendRequestResponse(request1.getId(),
                 request1.getSender().getId(),
                 request1.getSender().getUsername(),
                 request1.getReceiver().getId(),
                 request1.getStatus(),
                 request1.getCreatedAt());

         friendRequestResponses.add(requestResponse);
     }



        return friendRequestResponses;

    }

    public void acceptRequest(User currentUser, Long requestId) {
        FriendRequest request = friendsRepo.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Friend request not found"));

        request.setStatus(FriendRequestStatus.ACCEPTED);

        friendsConnectedService.makeFriends(request.getSender(),request.getReceiver());

        friendsRepo.delete(request);





    }

    public void rejectRequest(User currentUser, Long requestId) {

        FriendRequest request = friendsRepo.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Friend request not found"));

        request.setStatus(FriendRequestStatus.REJECTED);

        friendsRepo.save(request);
    }
}
