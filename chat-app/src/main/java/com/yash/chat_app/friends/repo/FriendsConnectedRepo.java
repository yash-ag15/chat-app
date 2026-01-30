package com.yash.chat_app.friends.repo;

import com.yash.chat_app.friends.entity.FriendConnected;
import com.yash.chat_app.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendsConnectedRepo extends JpaRepository<FriendConnected,Long> {




    boolean existsByUser1AndUser2OrUser1AndUser2(User sender, User receiver, User receiver1, User sender1);
}
