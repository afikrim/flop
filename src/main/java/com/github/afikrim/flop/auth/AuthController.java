package com.github.afikrim.flop.auth;

import com.github.afikrim.flop.users.User;
import com.github.afikrim.flop.users.UserRequest;
import com.github.afikrim.flop.utils.response.Response;
import com.github.afikrim.flop.utils.response.ResponseCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/v1/auth", produces = { MediaType.APPLICATION_JSON_VALUE })
public class AuthController {

    @Autowired
    AuthService authService;

    @PostMapping(value = "/register")
    public ResponseEntity<Response<AuthResponse>> register(@RequestBody UserRequest userRequest) {
        AuthResponse authResponse = authService.register(userRequest);
        Response<AuthResponse> response = new Response<>(true, ResponseCode.CREATED, "Successfully register",
                authResponse);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping(value = "/login")
    public ResponseEntity<Response<AuthResponse>> login(@RequestBody AuthRequest authRequest) {
        AuthResponse authResponse = authService.authenticate(authRequest.getCredential(), authRequest.getPassword());
        Response<AuthResponse> response = new Response<>(true, ResponseCode.HTTP_OK, "Successfully logged in",
                authResponse);

        return ResponseEntity.ok().body(response);
    }

    @GetMapping(value = "/profile")
    public ResponseEntity<Response<User>> profile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = authService.profile(userDetails.getUsername());

        Response<User> response = new Response<>(true, ResponseCode.HTTP_OK, "Successfully get user profile", user);

        return ResponseEntity.ok().body(response);
    }

    @PutMapping(value = "/profile")
    public ResponseEntity<Response<User>> updateProfile(@RequestBody UserRequest userRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = authService.updateProfile(userDetails.getUsername(), userRequest);

        Response<User> response = new Response<>(true, ResponseCode.HTTP_OK, "Successfully get user profile", user);

        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping(value = "/profile")
    public ResponseEntity<Response<User>> deleteProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        authService.deleteProfile(userDetails.getUsername());

        Response<User> response = new Response<>(true, ResponseCode.HTTP_OK, "Successfully delete user profile", null);

        return ResponseEntity.ok().body(response);
    }

}
