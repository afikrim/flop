package com.github.afikrim.flop.auth;

import com.github.afikrim.flop.users.UserRequest;

public interface AuthService {

    public AuthResponse register(UserRequest userRequest);
    public AuthResponse authenticate(String credential, String password);

}
