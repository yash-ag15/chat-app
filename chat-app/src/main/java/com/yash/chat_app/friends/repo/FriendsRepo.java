package com.yash.chat_app.friends.repo;

import com.yash.chat_app.friends.entity.FriendRequest;
import com.yash.chat_app.friends.entity.FriendRequestStatus;
import com.yash.chat_app.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendsRepo  extends JpaRepository<FriendRequest,Long> {






    List<FriendRequest> findByReceiverAndStatus(User currentUser, FriendRequestStatus friendRequestStatus);

    boolean existsBySenderAndReceiverAndStatus(User sender, User receiver, FriendRequestStatus friendRequestStatus);


}
