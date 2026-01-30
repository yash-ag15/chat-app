package com.yash.chat_app.friends.service;

import com.yash.chat_app.friends.entity.FriendConnected;
import com.yash.chat_app.friends.repo.FriendsConnectedRepo;
import com.yash.chat_app.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FriendsConnectedService {
    @Autowired
    FriendsConnectedRepo friendsConnectedRepo;

    public void makeFriends(User sender, User receiver) {

        FriendConnected friendConnected=new FriendConnected();
        friendConnected.setUser1(sender);
        friendConnected.setUser2(receiver);

        friendsConnectedRepo.save(friendConnected);

    }
}
