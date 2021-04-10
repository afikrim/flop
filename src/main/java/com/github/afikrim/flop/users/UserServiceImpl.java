package com.github.afikrim.flop.users;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.github.afikrim.flop.accounts.Account;
import com.github.afikrim.flop.accounts.AccountRepository;
import com.github.afikrim.flop.accounts.AccountRequest;
import com.github.afikrim.flop.userwallets.UserWalletController;
import com.github.afikrim.flop.utils.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    @Override
    public List<User> getAll() {
        List<User> users = userRepository.findAll();

        for (User user : users) {
            Link self = linkTo(methodOn(UserController.class).get(user.getId())).withRel("self");
            Link delete = linkTo(methodOn(UserController.class).destroy(user.getId())).withRel("delete");
            Link wallets = linkTo(methodOn(UserWalletController.class).index(user.getId())).withRel("wallets");

            user.add(self);
            user.add(delete);
            user.add(wallets);
        }

        return users;
    }

    @Override
    public User getOne(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            throw new EntityNotFoundException("User with id " + id + " not found");
        }

        User tempUser = optionalUser.get();
        if (!userDetails.getUsername().equals(tempUser.getEmail())) {
            throw new CustomException("You are not authorized to access this resource", HttpStatus.UNAUTHORIZED);
        }

        Link update = linkTo(methodOn(UserController.class).update(id, null)).withRel("update");
        Link delete = linkTo(methodOn(UserController.class).destroy(id)).withRel("delete");
        Link wallets = linkTo(methodOn(UserWalletController.class).index(id)).withRel("wallets");

        tempUser.add(update);
        tempUser.add(delete);
        tempUser.add(wallets);

        return tempUser;
    }

    @Transactional
    @Override
    public User updateOne(Long id, UserRequest userRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            throw new EntityNotFoundException("User not found!");
        }

        User tempUser = optionalUser.get();
        Account tempAccount = tempUser.getAccount();
        if (!userDetails.getUsername().equals(tempUser.getEmail())) {
            throw new CustomException("You are not authorized to access this resource", HttpStatus.UNAUTHORIZED);
        }

        Optional<AccountRequest> optionalAccountRequest = userRequest.getAccount();

        if (userRequest.getFullname() != null && !userRequest.getFullname().equals(tempUser.getFullname())) {
            tempUser.setFullname(userRequest.getFullname());
        }

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

            if (tempAccountRequest.getPassword() != null) {
                tempAccount.setPassword(//
                        bCryptPasswordEncoder.encode(tempAccountRequest.getPassword())//
                );
            }

            tempAccount.setUpdatedAt(new Date());
            tempUser.setAccount(tempAccount);
        }

        Link update = linkTo(methodOn(UserController.class).update(id, null)).withRel("update");
        Link delete = linkTo(methodOn(UserController.class).destroy(id)).withRel("delete");
        Link wallets = linkTo(methodOn(UserWalletController.class).index(id)).withRel("wallets");

        tempUser.add(update);
        tempUser.add(delete);
        tempUser.add(wallets);

        return userRepository.save(tempUser);
    }

    @Transactional
    @Override
    public User destroyOne(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            throw new EntityNotFoundException("User with id " + id + " not found");
        }

        User tempUser = optionalUser.get();
        if (!userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))
                && !userDetails.getUsername().equals(tempUser.getEmail())) {
            throw new CustomException("You are not authorized to access this resource", HttpStatus.UNAUTHORIZED);
        }

        tempUser.getAccount().setIsDeleted(true);
        userRepository.save(tempUser);

        return null;
    }

}
