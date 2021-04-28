package com.github.afikrim.flop.auth;

import com.github.afikrim.flop.accounts.Account;
import com.github.afikrim.flop.accounts.AccountRepository;
import com.github.afikrim.flop.accounts.AccountRequest;
import com.github.afikrim.flop.roles.Role;
import com.github.afikrim.flop.roles.RoleRepository;
import com.github.afikrim.flop.users.User;
import com.github.afikrim.flop.users.UserRepository;
import com.github.afikrim.flop.users.UserRequest;
import com.github.afikrim.flop.utils.exception.CustomException;
import com.github.afikrim.flop.utils.jwt.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.*;

@Service
public class AuthServiceImpl implements AuthService {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private RoleRepository roleRepository;

    public AuthServiceImpl() {
        bCryptPasswordEncoder = new BCryptPasswordEncoder();
    }

    @Transactional
    @Override
    public AuthResponse register(UserRequest userRequest) {
        Optional<AccountRequest> optionalAccountRequest = userRequest.getAccount();

        if (optionalAccountRequest.isEmpty())
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing field account");

        AccountRequest accountRequest = optionalAccountRequest.get();
        if (accountRequest.getUsername() != null) {
            Optional<Account> existsAccount = accountRepository.findByUsername(accountRequest.getUsername());

            if (existsAccount.isPresent()) {
                throw new CustomException("Username already in used", HttpStatus.BAD_REQUEST);
            }
        }

        Optional<User> existsUser = userRepository.findByEmailOrPhone(userRequest.getEmail(), userRequest.getPhone());
        if (existsUser.isPresent()) {
            throw new CustomException("Email or Phone already in used", HttpStatus.BAD_REQUEST);
        }

        Optional<Role> optionalRole = roleRepository.findByCode("ROLE_USER");
        if (optionalRole.isEmpty()) {
            throw new CustomException("Something wrong with the server.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        Account account = new Account();
        if (accountRequest.getUsername() != null)
            account.setUsername(accountRequest.getUsername());
        account.setPassword(bCryptPasswordEncoder.encode(accountRequest.getPassword()));
        account.setCreatedAt(new Date());
        account.setUpdatedAt(new Date());

        User user = new User();
        user.setFullname(userRequest.getFullname());
        user.setEmail(userRequest.getEmail());
        user.setPhone(userRequest.getPhone());
        user.setRoles(new ArrayList<>(Arrays.asList(optionalRole.get())));
        user.setAccount(account);
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());

        accountRepository.save(account);
        userRepository.save(user);

        Map<String, Object> claims = setClaims(user);
        String token = jwtUtil.generateToken(claims, user.getEmail());

        return new AuthResponse(token);
    }

    @Override
    public AuthResponse authenticate(String credential, String password) {
        User user = null;
        Optional<User> optionalUser = userRepository.findByCredential(credential);

        if (optionalUser.isEmpty()) {
            throw new EntityNotFoundException("User not found!");
        }

        user = optionalUser.get();
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(credential, password));

        Map<String, Object> claims = setClaims(user);
        String token = jwtUtil.generateToken(claims, user.getEmail());

        return new AuthResponse(token);
    }

    private Map<String, Object> setClaims(User user) {
        Map<String, Object> payload = new HashMap<>();
        if (user.getAccount().getUsername() != null)
            payload.put("username", user.getAccount().getUsername());
        payload.put("name", user.getFullname());
        payload.put("email", user.getEmail());
        payload.put("phone", user.getPhone());

        HashMap<String, Object> claims = new HashMap<>();
        claims.put("id", UUID.randomUUID());
        claims.put("payload", payload);

        return claims;
    }
}
