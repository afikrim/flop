package com.github.afikrim.flop.auth;

import com.github.afikrim.flop.users.UserRequest;
import com.github.afikrim.flop.utils.response.Response;
import com.github.afikrim.flop.utils.response.ResponseCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/auth", produces = {MediaType.APPLICATION_JSON_VALUE})
public class AuthController {

    @Autowired
    AuthService authService;

    @PostMapping(value = "/register")
    public ResponseEntity<Response<AuthResponse>> register(@RequestBody UserRequest userRequest) {
        AuthResponse authResponse = authService.register(userRequest);
        Response<AuthResponse> response = new Response<>(true, ResponseCode.CREATED, "Successfully register", authResponse);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping(value = "/login")
    public ResponseEntity<Response<AuthResponse>> login(@RequestBody AuthRequest authRequest) {
        AuthResponse authResponse = authService.authenticate(authRequest.getCredential(), authRequest.getPassword());
        Response<AuthResponse> response = new Response<>(true, ResponseCode.HTTP_OK, "Successfully logged in", authResponse);

        return ResponseEntity.ok().body(response);
    }

}
