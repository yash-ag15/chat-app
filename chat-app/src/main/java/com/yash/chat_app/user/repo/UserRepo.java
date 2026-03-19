package com.yash.chat_app.user.repo;

import com.yash.chat_app.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepo extends  JpaRepository<User,Long> {


    User findByEmail(String username);

    User findByUsername(String receiverName);

    List<User> findByUsernameIn(List<String> memberUserNames);
}
