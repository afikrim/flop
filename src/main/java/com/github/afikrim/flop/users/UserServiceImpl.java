package com.github.afikrim.flop.users;

import com.github.afikrim.flop.accounts.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
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
            Link delete = linkTo(methodOn(UserController.class).destroy(user.getId())).withRel("delete");

            user.add(self);
            user.add(delete);

            return user;
        }).collect(Collectors.toList());
    }

    @Override
    public User getOne(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);

        if (!optionalUser.isPresent()) {
            throw new EntityNotFoundException("User with id " + id + " not found");
        }

        User tempUser = optionalUser.get();

        Link delete = linkTo(methodOn(UserController.class).destroy(id)).withRel("delete");

        tempUser.add(delete);

        return tempUser;
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

    public User getOneByCredential(String credential) {
        Optional<User> optionalUser = userRepository.findByEmailOrPhone(credential, credential);

        if (!optionalUser.isPresent()) {
            throw new EntityNotFoundException("User not found.");
        }

        User user = optionalUser.get();

        return user;
    }

}
