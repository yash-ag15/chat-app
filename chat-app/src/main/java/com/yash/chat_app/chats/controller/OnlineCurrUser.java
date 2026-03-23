package com.yash.chat_app.chats.controller;


import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class OnlineCurrUser {
  private final Set<String> onlineUser= ConcurrentHashMap.newKeySet();
  public void add(String user){
      onlineUser.add(user);

  }
    public void remove(String user) {
        onlineUser.remove(user);
    }

    public Set<String> getAll() {
        return onlineUser;
    }
}
