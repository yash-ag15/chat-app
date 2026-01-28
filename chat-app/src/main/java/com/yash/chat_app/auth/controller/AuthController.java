package com.yash.chat_app.auth.controller;

import com.yash.chat_app.auth.service.AuthService;
import com.yash.chat_app.auth.dto.LoggingRequest;
import com.yash.chat_app.auth.dto.RegistrationRequest;
import com.yash.chat_app.auth.jwt.JWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;


@PostMapping("/register")

    public String register(@RequestBody RegistrationRequest request){
 authService.savedUser(request);

return "User Successfully registered";

}

    @Autowired
    AuthenticationManager authenticationManager;
@Autowired
JWTService jwtService;
@PostMapping("/login")
    public String login(@RequestBody LoggingRequest loggingRequest){


    Authentication authentication= authenticationManager
            .authenticate(new UsernamePasswordAuthenticationToken(loggingRequest.email(),loggingRequest.password()));

    if(authentication.isAuthenticated())return jwtService.generateToken(loggingRequest.email()) ;
    else
        return "Failed";
}
}
