package com.yash.chat_app.chats.service;

import com.yash.chat_app.chats.dto.AddMemberRequest;
import com.yash.chat_app.chats.dto.ChatResponse;
import com.yash.chat_app.chats.dto.MessageResponse;
import com.yash.chat_app.chats.entity.Chat;
import com.yash.chat_app.chats.entity.ChatMember;
import com.yash.chat_app.chats.entity.Message;
import com.yash.chat_app.chats.entity.Roles;
import com.yash.chat_app.chats.repo.ChatMemberRepo;
import com.yash.chat_app.chats.repo.ChatRepo;
import com.yash.chat_app.chats.repo.MessageRepo;
import com.yash.chat_app.exception.BadRequestException;
import com.yash.chat_app.exception.UserNotFoundException;
import com.yash.chat_app.friends.entity.FriendConnected;
import com.yash.chat_app.friends.repo.FriendsConnectedRepo;
import com.yash.chat_app.user.User;
import com.yash.chat_app.user.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ChatService {

    @Autowired
    UserRepo userRepo;

    @Autowired
    ChatRepo chatRepo;

    @Autowired
    ChatMemberRepo memberRepo;

    @Autowired
    MessageRepo messageRepo;

    @Autowired
    FriendsConnectedRepo friendsConnectedRepo;
    public void makePrivateChat(User currUser, String username) {

        User receiver = userRepo.findByUsername(username);

        if (receiver == null) {
            throw new RuntimeException("User not found");
        }

        Optional<Chat> containChat = chatRepo.findPrivateChat(currUser, receiver);

        if (containChat.isPresent()) {
          throw new RuntimeException("Chat Already exist");
        }

        Chat chat = new Chat();
        chat.setGroup(false);

        chat = chatRepo.save(chat);

        ChatMember chatMember1 = new ChatMember();
        chatMember1.setChat(chat);
        chatMember1.setUser(currUser);
        chatMember1.setRoles(Roles.MEMBER);

        ChatMember chatMember2 = new ChatMember();
        chatMember2.setChat(chat);
        chatMember2.setUser(receiver);
        chatMember2.setRoles(Roles.MEMBER);

        memberRepo.save(chatMember1);
        memberRepo.save(chatMember2);


    }

    public List<ChatResponse> getAllChat(User user) {

        List<ChatMember> members = memberRepo.findByUser(user);

        List<ChatResponse> chats = new ArrayList<>();

        for (ChatMember m : members) {

            Chat chat = m.getChat();

            String chatName;
String profilePhotoUrl=null;

            if (chat.isGroup()) {
                chatName = chat.getName();
            } else {

                List<ChatMember> chatMembers = memberRepo.findByChat(chat);

                chatName = null;

                for (ChatMember cm : chatMembers) {

                    if (!cm.getUser().getId().equals(user.getId())) {
                        chatName = cm.getUser().getUsername();
                        profilePhotoUrl=cm.getUser().getProfilePhotoUrl();
                        break;
                    }
                }
            }


            Optional<Message> lastMsg =
                    messageRepo.findTopByChatIdOrderBySentAtDesc(chat.getId());
if(lastMsg.isEmpty()&&!chat.isGroup()){
    continue;
}

            String lastMessage = null;
            Instant lastMessageTime = null;

            if (lastMsg.isPresent()) {
                lastMessage = lastMsg.get().getContent();
                lastMessageTime = lastMsg.get().getSentAt();
            }

            ChatResponse response = new ChatResponse(
                    chat.getId(),
                    chat.isGroup(),
                    chatName,
                    chat.getCreatedAt(),
                    lastMessage,
                    lastMessageTime,
                    profilePhotoUrl
            );

            chats.add(response);
        }

        return chats;
    }

    public Page<MessageResponse> getChatMessages(User user, Long chatId,int page,int size) {

        Chat chat = chatRepo.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat not found"));

        boolean isMember = memberRepo.existsByChatAndUser(chat, user);

        if(!isMember){
            throw new RuntimeException("User not part of chat");
        }

        Pageable pageable = PageRequest.of(page, size);

        Page<Message> messages =
                messageRepo.findByChatIdOrderBySentAtDesc(chatId, pageable);


        return messages.map(m ->
                new MessageResponse(
                        m.getId(),
                        m.getSender().getUsername(),
                        m.getContent(),
                        m.getSentAt(),
                        chatId,
                        m.getStatus().name(),
                        m.getImageUrl()

                )
        );
    }

    public List<ChatResponse> getAllChatStartingWith(User currentUser, String prefix) {

        List<FriendConnected>list1=friendsConnectedRepo.findByUser1AndUser2_UsernameStartingWithIgnoreCase(currentUser,prefix);
        List<FriendConnected>list2=friendsConnectedRepo.findByUser2AndUser1_UsernameStartingWithIgnoreCase(currentUser,prefix);
        List<FriendConnected>friendConnected= new ArrayList<>();
        friendConnected.addAll(list1);
        friendConnected.addAll(list2);

        List<ChatResponse> chatResponses = new ArrayList<>();

        for (FriendConnected f : friendConnected) {

            User friend;
            String profilePhotoUrl=null;
            if (f.getUser1().getId().equals(currentUser.getId())) {
                friend = f.getUser2();
                profilePhotoUrl=f.getUser2().getProfilePhotoUrl();
            } else {
                friend = f.getUser1();
                profilePhotoUrl=f.getUser1().getProfilePhotoUrl();
            }

            Optional<Chat> chat = chatRepo.findPrivateChat(currentUser, friend);

            if(chat.isEmpty()){
                continue;
            }
            Chat chatEntity = chat.get();

            Optional<Message> lastMsg =
                    messageRepo.findTopByChatIdOrderBySentAtDesc(chatEntity.getId());

            String lastMessage = null;
            Instant lastMessageTime = null;

            if (lastMsg.isPresent()) {
                lastMessage = lastMsg.get().getContent();
                lastMessageTime = lastMsg.get().getSentAt();
            }

            chatResponses.add(
                    new ChatResponse(
                            chatEntity.getId(),
                            chatEntity.isGroup(),
                            friend.getUsername(),
                            chatEntity.getCreatedAt(),
                            lastMessage,
                            lastMessageTime,
                            profilePhotoUrl
                    )
            );
        }
        return chatResponses;
    }


    public void createGroup(User user, String chatName, List<String> memberUserNames) {

        Chat chat = new Chat();
        chat.setGroup(true);
        chat.setName(chatName);

        chat = chatRepo.save(chat);


        List<User> users = userRepo.findByUsernameIn(memberUserNames);

        if (users.size() != memberUserNames.size()) {
            throw new UserNotFoundException("One or more users do not exist");
        }

        List<ChatMember> members = new ArrayList<>();


        ChatMember admin = new ChatMember();
        admin.setRoles(Roles.ADMIN);
        admin.setChat(chat);
        admin.setUser(user);
        members.add(admin);


        for (User u : users) {
            ChatMember chatMember = new ChatMember();
            chatMember.setUser(u);
            chatMember.setChat(chat);
            chatMember.setRoles(Roles.MEMBER);
            members.add(chatMember);
        }


        memberRepo.saveAll(members);
    }


   public void addMember(User user, Long chatId, AddMemberRequest addMemberRequest) {
       Optional<Chat> chat =chatRepo.findById(chatId);
       if(chat.isEmpty())
           throw new RuntimeException("Chat Not found");

        if(!memberRepo.existsByChatAndUserAndRoles(chat.get(),user,Roles.ADMIN)){
            throw new BadRequestException("Only Admin can add the member");

        }
        List<User> users = userRepo.findByUsernameIn(addMemberRequest.memberUserNames());

        if (users.size() != addMemberRequest.memberUserNames().size()) {
            throw new UserNotFoundException("One or more users do not exist");
        }

        List<ChatMember> members = new ArrayList<>();


        for (User u : users) {
            if(memberRepo.existsByChatAndUser(chat.get(), u)){
                throw new RuntimeException(u.getUsername()+" is already in the group");

            }
            ChatMember chatMember = new ChatMember();
            chatMember.setUser(u);
            chatMember.setChat(chat.get());
            chatMember.setRoles(Roles.MEMBER);
            members.add(chatMember);
        }


        memberRepo.saveAll(members);


    }
}