package com.github.afikrim.flop.userwallets;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;

import com.github.afikrim.flop.users.User;
import com.github.afikrim.flop.users.UserService;
import com.github.afikrim.flop.utils.response.Response;
import com.github.afikrim.flop.utils.response.ResponseCode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/v1/users/{userId}/wallets", produces = { MediaType.APPLICATION_JSON_VALUE })
public class UserWalletController {

    @Autowired
    UserService userService;

    @Autowired
    UserWalletService userWalletService;

    @GetMapping
    public ResponseEntity<Response<List<UserWallet>>> index(@PathVariable Long userId) {
        User user = userService.getOne(userId);

        List<UserWallet> userWallets = userWalletService.getAll(user);
        Response<List<UserWallet>> response = new Response<>(true, ResponseCode.HTTP_OK, "Successfully get all wallets", userWallets);

        Link store = linkTo(methodOn(this.getClass()).store(userId, null)).withRel("store");

        response.add(store);

        return ResponseEntity.ok().body(response);
    }

    @PostMapping
    public ResponseEntity<Response<UserWallet>> store(@PathVariable Long userId, @RequestBody UserWalletRequest userWalletRequest) {
        User user = userService.getOne(userId);

        UserWallet userWallet = userWalletService.store(user, userWalletRequest);
        Response<UserWallet> response = new Response<>(true, ResponseCode.CREATED, "Successfully insert new wallet to your account", userWallet);

        Link index = linkTo(methodOn(this.getClass()).index(userId)).withRel("all");

        response.add(index);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Response<UserWallet>> get(@PathVariable Long userId, @PathVariable Long id) {
        User user = userService.getOne(userId);

        UserWallet userWallet = userWalletService.getOne(user, id);
        Response<UserWallet> response = new Response<>(true, ResponseCode.HTTP_OK, "Successfully get wallet", userWallet);

        Link index = linkTo(methodOn(this.getClass()).index(userId)).withRel("all");
        Link store = linkTo(methodOn(this.getClass()).store(userId, null)).withRel("store");

        response.add(index);
        response.add(store);

        return ResponseEntity.ok().body(response);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Response<UserWallet>> update(@PathVariable Long userId, @PathVariable Long id, @RequestBody UserWalletRequest userWalletRequest) {
        User user = userService.getOne(userId);

        UserWallet userWallet = userWalletService.updateOne(user, id, userWalletRequest);
        Response<UserWallet> response = new Response<>(true, ResponseCode.HTTP_OK, "Successfully update wallet data", userWallet);

        Link index = linkTo(methodOn(this.getClass()).index(userId)).withRel("all");
        Link store = linkTo(methodOn(this.getClass()).store(userId, null)).withRel("store");

        response.add(index);
        response.add(store);

        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Response<UserWallet>> destroy(@PathVariable Long userId, @PathVariable Long id) {
        User user = userService.getOne(userId);

        userWalletService.deleteOne(user, id);
        Response<UserWallet> response = new Response<>(true, ResponseCode.HTTP_OK, "Successfully delete wallet data", null);

        Link index = linkTo(methodOn(this.getClass()).index(userId)).withRel("all");
        Link store = linkTo(methodOn(this.getClass()).store(userId, null)).withRel("store");

        response.add(index);
        response.add(store);

        return ResponseEntity.ok().body(response);
    }

}