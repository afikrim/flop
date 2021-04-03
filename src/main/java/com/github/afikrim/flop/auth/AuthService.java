package com.github.afikrim.flop.auth;

import com.github.afikrim.flop.users.User;
import com.github.afikrim.flop.users.UserRequest;

public interface AuthService {

    AuthResponse register(UserRequest userRequest);

    AuthResponse authenticate(String credential, String password);

    User profile(String credential);

    User updateProfile(String credential, UserRequest userRequest);

    void deleteProfile(String credential);

}
