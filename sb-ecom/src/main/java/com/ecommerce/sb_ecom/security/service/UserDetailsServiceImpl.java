package com.ecommerce.sb_ecom.security.service;

import com.ecommerce.sb_ecom.Model.User;
import com.ecommerce.sb_ecom.Repositry.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UserRepository userRepository;
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user= userRepository.findByUserName(username)
                .orElseThrow(()-> new UsernameNotFoundException("user Name not found"));


        return UserDetailsImpl.build(user);
    }
}
