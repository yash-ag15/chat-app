package com.yash.chat_app.user.security;

import com.yash.chat_app.user.User;
import com.yash.chat_app.user.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailService implements UserDetailsService {
    @Autowired
    private UserRepo userRepo;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

    User user =userRepo.findByEmail(username);

    if(user==null)throw new UsernameNotFoundException("User 404");
    else
        return new UserPrincipal(user);
    }
}
