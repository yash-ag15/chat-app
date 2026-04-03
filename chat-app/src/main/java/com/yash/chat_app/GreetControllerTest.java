package com.yash.chat_app;

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;



@RestController
public class GreetControllerTest {
    @GetMapping("/hello")
    public String hello() {
        return "Hello";
    }

    @Autowired
    private Cloudinary cloudinary;

    @PostMapping("/test-upload")
    public String testUpload(@RequestParam("file") MultipartFile file) {
        try {
            Map uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    Map.of("folder", "chat-app/test")
            );

            return uploadResult.get("secure_url").toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "Upload failed";
        }

    }
}