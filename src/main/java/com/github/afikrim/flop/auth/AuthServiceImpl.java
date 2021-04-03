package com.github.afikrim.flop.auth;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

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
import org.springframework.hateoas.Link;
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

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public AuthServiceImpl() {
        bCryptPasswordEncoder = new BCryptPasswordEncoder();
    }

    @Transactional
    @Override
    public AuthResponse register(UserRequest userRequest) {
        Optional<AccountRequest> optionalAccountRequest = userRequest.getAccount();

        if (!optionalAccountRequest.isPresent())
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
        user.setRoles(new HashSet<>(Arrays.asList(optionalRole.get())));
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
        Account account = null;
        try {
            Optional<Account> optionalAccount = accountRepository.getAccountWithCredential(credential);

            if (optionalAccount.isEmpty()) {
                throw new Exception("User not found!");
            }

            account = optionalAccount.get();
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(credential, password));
        } catch (Exception ex) {
            throw new CustomException(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }

        Map<String, Object> claims = setClaims(account.getUser());
        String token = jwtUtil.generateToken(claims, account.getUser().getEmail());

        return new AuthResponse(token);
    }

    @Override
    public User profile(String credential) {
        Optional<Account> optionalAccount = accountRepository.getAccountWithCredential(credential);
        if (optionalAccount.isEmpty()) {
            throw new EntityNotFoundException("User not found!");
        }

        Account account = optionalAccount.get();

        Link self = linkTo(methodOn(AuthController.class).updateProfile(null)).withRel("update");
        account.getUser().add(self);

        return account.getUser();
    }

    @Transactional
    @Override
    public User updateProfile(String credential, UserRequest userRequest) {
        Optional<Account> optionalAccount = accountRepository.getAccountWithCredential(credential);
        if (optionalAccount.isEmpty()) {
            throw new EntityNotFoundException("User not found!");
        }

        Account tempAccount = optionalAccount.get();
        User tempUser = tempAccount.getUser();

        Optional<AccountRequest> optionalAccountRequest = userRequest.getAccount();

        if (userRequest.getFullname() != null && !userRequest.getFullname().equals(tempUser.getFullname()))
            tempUser.setFullname(userRequest.getFullname());

        if (userRequest.getEmail() != null && !userRequest.getEmail().equals(tempUser.getEmail())) {
            Optional<Account> optionalAccountWithNewEmail = accountRepository
                    .getAccountWithCredential(userRequest.getEmail());
            if (optionalAccountWithNewEmail.isPresent())
                throw new CustomException("Email already in used.", HttpStatus.BAD_REQUEST);

            tempUser.setEmail(userRequest.getEmail());
        }

        if (userRequest.getPhone() != null && !userRequest.getPhone().equals(tempUser.getPhone())) {
            Optional<Account> optionalAccountWithNewPhone = accountRepository
                    .getAccountWithCredential(userRequest.getPhone());
            if (optionalAccountWithNewPhone.isPresent())
                throw new CustomException("Phone already in used.", HttpStatus.BAD_REQUEST);

            tempUser.setPhone(userRequest.getPhone());
        }

        if (optionalAccountRequest.isPresent()) {
            AccountRequest tempAccountRequest = optionalAccountRequest.get();

            if (tempAccountRequest.getUsername() != null
                    && !tempAccountRequest.getUsername().equals(tempAccount.getUsername())) {
                Optional<Account> optionalAccountWithNewUsername = accountRepository
                        .getAccountWithCredential(tempAccountRequest.getUsername());
                if (optionalAccountWithNewUsername.isPresent())
                    throw new CustomException("Username already in used.", HttpStatus.BAD_REQUEST);

                tempAccount.setUsername(tempAccountRequest.getUsername());
            }

            if (tempAccountRequest.getPassword() != null)
                tempAccount.setPassword(//
                        bCryptPasswordEncoder.encode(tempAccountRequest.getPassword())//
                );

            tempAccount.setUpdatedAt(new Date());
            tempUser.setAccount(tempAccount);
        }

        tempUser.setUpdatedAt(new Date());

        Link self = linkTo(methodOn(AuthController.class).profile()).withRel("self");
        tempUser.add(self);

        return userRepository.save(tempUser);
    }

    @Override
    public void deleteProfile(String credential) {
        Optional<Account> optionalAccount = accountRepository.getAccountWithCredential(credential);
        if (optionalAccount.isEmpty()) {
            throw new EntityNotFoundException("User not found!");
        }

        Account tempAccount = optionalAccount.get();
        User tempUser = tempAccount.getUser();

        tempAccount.setIsDeleted(true);
        tempUser.setUpdatedAt(new Date());

        accountRepository.save(tempAccount);
        userRepository.save(tempUser);
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
