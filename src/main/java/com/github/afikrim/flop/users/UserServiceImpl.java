package com.github.afikrim.flop.users;

import com.github.afikrim.flop.accounts.Account;
import com.github.afikrim.flop.accounts.AccountRepository;
import com.github.afikrim.flop.accounts.AccountRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import javax.persistence.EntityNotFoundException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public List<User> getAll() {
        return userRepository.findAll().stream().map(user -> {
            Link self = linkTo(methodOn(UserController.class).get(user.getId())).withRel("self");
            Link update = linkTo(methodOn(UserController.class).update(user.getId(), null)).withRel("update");
            Link delete = linkTo(methodOn(UserController.class).destroy(user.getId())).withRel("delete");

            user.add(self);
            user.add(update);
            user.add(delete);

            return user;
        }).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public User store(UserRequest userRequest) {
        Optional<AccountRequest> optionalAccountRequest = userRequest.getAccount();

        if (!optionalAccountRequest.isPresent())
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing field account");

        AccountRequest accountRequest = optionalAccountRequest.get();
        Account account = new Account();
        account.setUsername(accountRequest.getUsername());
        account.setPassword(accountRequest.getPassword());
        account.setCreatedAt(new Date());
        account.setUpdatedAt(new Date());

        User user = new User();
        user.setFullname(userRequest.getFullname());
        user.setEmail(userRequest.getEmail());
        user.setPhone(userRequest.getPhone());
        user.setAccount(account);
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());

        accountRepository.save(account);
        userRepository.save(user);

        Link self = linkTo(methodOn(UserController.class).get(user.getId())).withRel("self");
        Link update = linkTo(methodOn(UserController.class).update(user.getId(), null)).withRel("update");
        Link delete = linkTo(methodOn(UserController.class).destroy(user.getId())).withRel("delete");

        user.add(self);
        user.add(update);
        user.add(delete);

        return user;
    }

    @Override
    public User getOne(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);

        if (!optionalUser.isPresent()) {
            throw new EntityNotFoundException("User with id " + id + " not found");
        }

        User tempUser = optionalUser.get();

        Link update = linkTo(methodOn(UserController.class).update(id, null)).withRel("update");
        Link delete = linkTo(methodOn(UserController.class).destroy(id)).withRel("delete");

        tempUser.add(update);
        tempUser.add(delete);

        return tempUser;
    }

    @Transactional
    @Override
    public User updateOne(Long id, UserRequest userRequest) {
        Optional<User> optionalUser = userRepository.findById(id);

        if (!optionalUser.isPresent()) {
            throw new EntityNotFoundException("User with id " + id + " not found");
        }

        User tempUser = optionalUser.get();
        Account tempAccount = tempUser.getAccount();

        Optional<AccountRequest> optionalAccount = userRequest.getAccount();

        if (userRequest.getFullname() != null)
            tempUser.setFullname(userRequest.getFullname());
        if (userRequest.getEmail() != null)
            tempUser.setEmail(userRequest.getEmail());
        if (userRequest.getPhone() != null)
            tempUser.setPhone(userRequest.getPhone());
        if (optionalAccount.isPresent()) {
            AccountRequest tempAccountRequest = optionalAccount.get();

            if (tempAccountRequest.getUsername() != null)
                tempAccount.setUsername(tempAccountRequest.getUsername());
            if (tempAccountRequest.getPassword() != null)
                tempAccount.setPassword(tempAccountRequest.getPassword());

            tempAccount.setUpdatedAt(new Date());
            tempUser.setAccount(tempAccount);
        }

        tempUser.setUpdatedAt(new Date());

        Link self = linkTo(methodOn(UserController.class).get(id)).withRel("self");
        Link delete = linkTo(methodOn(UserController.class).destroy(id)).withRel("delete");

        tempUser.add(self);
        tempUser.add(delete);

        return userRepository.save(tempUser);
    }

    @Transactional
    @Override
    public User destroyOne(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);

        if (!optionalUser.isPresent()) {
            throw new EntityNotFoundException("User with id " + id + " not found");
        }

        User tempUser = optionalUser.get();
        accountRepository.deleteById(tempUser.getAccount().getId());

        return null;
    }

}
