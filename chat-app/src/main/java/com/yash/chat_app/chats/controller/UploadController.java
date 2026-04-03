package com.yash.chat_app.chats.controller;

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
@CrossOrigin(origins = "http://localhost:5175")
@RestController
@RequestMapping("/upload")
public class UploadController {
  @Autowired
    Cloudinary cloudinary;
@PostMapping("/message-image")
  public String uploadImage(@RequestParam("file")MultipartFile file){

    try{

        Map uploadedResult= cloudinary.uploader().upload(
                file.getBytes(),
                Map.of("folder", "chat-app/messages")

        );
        return uploadedResult.get("secure_url").toString();
    }

    catch (Exception e) {
        throw new RuntimeException(e);
    }

  }

}
