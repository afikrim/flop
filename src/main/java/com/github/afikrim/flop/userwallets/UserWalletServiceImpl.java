package com.github.afikrim.flop.userwallets;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.github.afikrim.flop.users.User;
import com.github.afikrim.flop.utils.exception.CustomException;
import com.github.afikrim.flop.wallets.Wallet;
import com.github.afikrim.flop.wallets.WalletRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserWalletServiceImpl implements UserWalletService {

    @Autowired
    private UserWalletRepository userWalletRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Override
    public List<UserWallet> getAll(User user) {
        Set<UserWallet> userWallets = user.getWallets();

        for (UserWallet userWallet: userWallets) {
            Link self = linkTo(methodOn(UserWalletController.class).get(user.getId(), userWallet.getId())).withRel("self");
            Link update = linkTo(methodOn(UserWalletController.class).update(user.getId(), userWallet.getId(), null)).withRel("update");
            Link delete = linkTo(methodOn(UserWalletController.class).destroy(user.getId(), userWallet.getId())).withRel("delete");

            userWallet.add(self);
            userWallet.add(update);
            userWallet.add(delete);
        }

        return new ArrayList<>(userWallets);
    }

    @Override
    public UserWallet store(User user, UserWalletRequest userWalletRequest) {
        Optional<Wallet> optionalWallet = walletRepository.findById(userWalletRequest.getWalletId());
        if (optionalWallet.isEmpty()) {
            throw new EntityNotFoundException("Wallet with id " + userWalletRequest.getWalletId() + " not found.");
        }

        Wallet wallet = optionalWallet.get();
        Optional<UserWallet> optionalUserWallet = userWalletRepository.findByPhoneAndUserIdAndWalletId(
                userWalletRequest.getPhone(), //
                user.getId(), //
                userWalletRequest.getWalletId()//
        );
        if (optionalUserWallet.isPresent()) {
            throw new EntityExistsException("Wallet type " + wallet.getName() + " with phone number "
                    + userWalletRequest.getPhone() + " already exists.");
        }

        UserWallet userWallet = new UserWallet();
        userWallet.setName(userWalletRequest.getName());
        userWallet.setPhone(userWalletRequest.getPhone());
        userWallet.setUser(user);
        userWallet.setWallet(wallet);
        userWallet.setCreatedAt(new Date());
        userWallet.setUpdatedAt(new Date());

        Link self = linkTo(methodOn(UserWalletController.class).get(user.getId(), userWallet.getId())).withRel("self");
        Link update = linkTo(methodOn(UserWalletController.class).update(user.getId(), userWallet.getId(), null)).withRel("update");
        Link delete = linkTo(methodOn(UserWalletController.class).destroy(user.getId(), userWallet.getId())).withRel("delete");

        userWallet.add(self);
        userWallet.add(update);
        userWallet.add(delete);

        return userWalletRepository.save(userWallet);
    }

    @Override
    public UserWallet getOne(User user, Long id) {
        Set<UserWallet> userWallets = user.getWallets();

        Optional<UserWallet> optionalUserWallet = userWalletRepository.findById(id);
        if (optionalUserWallet.isEmpty()) {
            throw new EntityNotFoundException("Wallet with id: " + id + " not found.");
        }

        UserWallet userWallet = optionalUserWallet.get();
        if (!userWallets.contains(userWallet)) {
            throw new CustomException("You are not authorize to access this resource", HttpStatus.UNAUTHORIZED);
        }

        Link update = linkTo(methodOn(UserWalletController.class).update(user.getId(), userWallet.getId(), null)).withRel("update");
        Link delete = linkTo(methodOn(UserWalletController.class).destroy(user.getId(), userWallet.getId())).withRel("delete");

        userWallet.add(update);
        userWallet.add(delete);

        return userWallet;
    }

    @Override
    public UserWallet updateOne(User user, Long id, UserWalletRequest userWalletRequest) {
        Set<UserWallet> userWallets = user.getWallets();

        Optional<UserWallet> optionalUserWallet = userWalletRepository.findById(id);
        if (optionalUserWallet.isEmpty()) {
            throw new EntityNotFoundException("Wallet with id: " + id + " not found.");
        }

        UserWallet userWallet = optionalUserWallet.get();
        if (!userWallets.contains(userWallet)) {
            throw new CustomException("You are not authorize to access this resource", HttpStatus.UNAUTHORIZED);
        }

        if (userWalletRequest.getWalletId() != null
                && !userWalletRequest.getWalletId().equals(userWallet.getWallet().getId())) {
            Optional<Wallet> optionalWallet = walletRepository.findById(userWalletRequest.getWalletId());

            if (optionalWallet.isEmpty()) {
                throw new EntityNotFoundException("Wallet with id " + userWalletRequest.getWalletId() + " not found.");
            }

            userWallet.setWallet(optionalWallet.get());
        }

        if (userWalletRequest.getName() != null && !userWalletRequest.getName().equals(userWallet.getName())) {
            userWallet.setName(userWalletRequest.getName());
        }

        if (userWalletRequest.getPhone() != null && !userWalletRequest.getPhone().equals(userWallet.getPhone())) {
            userWallet.setPhone(userWalletRequest.getPhone());
        }

        userWallet.setUpdatedAt(new Date());

        Link self = linkTo(methodOn(UserWalletController.class).get(user.getId(), userWallet.getId())).withRel("self");
        Link delete = linkTo(methodOn(UserWalletController.class).destroy(user.getId(), userWallet.getId())).withRel("delete");

        userWallet.add(self);
        userWallet.add(delete);

        return userWalletRepository.save(userWallet);
    }

    @Transactional
    @Override
    public void deleteOne(User user, Long id) {
        Set<UserWallet> userWallets = user.getWallets();

        Optional<UserWallet> optionalUserWallet = userWalletRepository.findById(id);
        if (optionalUserWallet.isEmpty()) {
            throw new EntityNotFoundException("Wallet with id: " + id + " not found.");
        }

        UserWallet userWallet = optionalUserWallet.get();
        if (!userWallets.contains(userWallet)) {
            throw new CustomException("You are not authorize to access this resource", HttpStatus.UNAUTHORIZED);
        }

        userWalletRepository.deleteByIdAndUserId(id, user.getId());
    }

}
