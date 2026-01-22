package com.library.common.security;


import com.library.common.exception.UserNotFoundException;
import com.library.user.entity.Users;
import com.library.user.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class CustomUserDetailService implements UserDetailsService {

    @Autowired
    private UsersRepository usersRepository;

    @Override
    public UserDetails loadUserByUsername(String input) throws UsernameNotFoundException {
        Users email = usersRepository.findByEmailOrUsername(input, input)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found with email or username:- " + input)
                );
        return new CustomUserDetails(email);
    }
}
