package com.yash.chat_app.chats.dto;

public record MessageRequest(
   String receiverName,
   Long chatId,
   String content

) {

}
