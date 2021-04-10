package com.github.afikrim.flop.systemwallets;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;

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
@RequestMapping(value = "/v1/system-wallets", produces = { MediaType.APPLICATION_JSON_VALUE })
public class SystemWalletController {

    @Autowired
    SystemWalletService systemWalletService;

    @GetMapping
    public ResponseEntity<Response<List<SystemWallet>>> index() {
        List<SystemWallet> systemWallets = systemWalletService.getAll();
        Response<List<SystemWallet>> response = new Response<>(true, ResponseCode.HTTP_OK, "Successfully get all wallets", systemWallets);

        Link store = linkTo(methodOn(this.getClass()).store(null)).withRel("store");

        response.add(store);

        return ResponseEntity.ok().body(response);
    }

    @PostMapping
    public ResponseEntity<Response<SystemWallet>> store(@RequestBody SystemWalletRequest systemWalletRequest) {
        SystemWallet systemWallet = systemWalletService.store(systemWalletRequest);
        Response<SystemWallet> response = new Response<>(true, ResponseCode.HTTP_OK, "Successfully store new wallet", systemWallet);

        Link index = linkTo(methodOn(this.getClass()).index()).withRel("all");

        response.add(index);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Response<SystemWallet>> get(@PathVariable Long id) {
        SystemWallet systemWallet = systemWalletService.getOne(id);
        Response<SystemWallet> response = new Response<>(true, ResponseCode.HTTP_OK, "Successfully get a wallet", systemWallet);

        Link store = linkTo(methodOn(this.getClass()).store(null)).withRel("store");
        Link index = linkTo(methodOn(this.getClass()).index()).withRel("all");

        response.add(store);
        response.add(index);

        return ResponseEntity.ok().body(response);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Response<SystemWallet>> update(@PathVariable Long id, @RequestBody SystemWalletRequest systemWalletRequest) {
        SystemWallet systemWallet = systemWalletService.updateOne(id, systemWalletRequest);
        Response<SystemWallet> response = new Response<>(true, ResponseCode.HTTP_OK, "Successfully update a wallet", systemWallet);

        Link store = linkTo(methodOn(this.getClass()).store(null)).withRel("store");
        Link index = linkTo(methodOn(this.getClass()).index()).withRel("all");

        response.add(store);
        response.add(index);

        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Response<SystemWallet>> destroy(@PathVariable Long id) {
        systemWalletService.deleteOne(id);
        Response<SystemWallet> response = new Response<>(true, ResponseCode.HTTP_OK, "Successfully delete a wallet", null);

        Link store = linkTo(methodOn(this.getClass()).store(null)).withRel("store");
        Link index = linkTo(methodOn(this.getClass()).index()).withRel("all");

        response.add(store);
        response.add(index);

        return ResponseEntity.ok().body(response);
    }

}
