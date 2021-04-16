package com.github.afikrim.flop.users.details;

import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import com.github.afikrim.flop.users.User;
import com.github.afikrim.flop.users.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailServiceImpl implements UserDetailService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> optionalUser = userRepository.findByCredential(username);

        if (optionalUser.isEmpty()) {
            throw new EntityNotFoundException("User not found.");
        }

        User user = optionalUser.get();
        return new UserDetail(user);
    }

}
