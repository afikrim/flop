package com.github.afikrim.flop.wallets;

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
@RequestMapping(value = "/v1/wallets", produces = { MediaType.APPLICATION_JSON_VALUE })
public class WalletController {

    @Autowired
    private WalletService walletService;

    @GetMapping
    public ResponseEntity<Response<List<Wallet>>> index() {
        List<Wallet> wallets = walletService.getAll();
        Response<List<Wallet>> response = new Response<>(true, ResponseCode.HTTP_OK, "Successfully get all wallets",
                wallets);

        Link store = linkTo(methodOn(this.getClass()).store(null)).withRel("store");

        response.add(store);

        return ResponseEntity.ok().body(response);
    }

    @PostMapping
    public ResponseEntity<Response<Wallet>> store(@RequestBody WalletRequest walletRequest) {
        Wallet wallet = walletService.store(walletRequest);
        Response<Wallet> response = new Response<>(true, ResponseCode.CREATED, "Successfully created a new wallet",
                wallet);

        Link index = linkTo(methodOn(this.getClass()).index()).withRel("all");

        response.add(index);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping(value = "/{code}")
    public ResponseEntity<Response<Wallet>> update(@PathVariable String code,
            @RequestBody WalletRequest walletRequest) {
        Wallet wallet = walletService.updateOne(code, walletRequest);
        Response<Wallet> response = new Response<>(true, ResponseCode.HTTP_OK,
                "Successfully update wallet with code: " + code, wallet);

        Link index = linkTo(methodOn(this.getClass()).index()).withRel("all");
        Link store = linkTo(methodOn(this.getClass()).store(null)).withRel("store");

        response.add(index);
        response.add(store);

        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping(value = "/{code}")
    public ResponseEntity<Response<Wallet>> destroy(@PathVariable String code) {
        walletService.deleteOne(code);
        Response<Wallet> response = new Response<>(true, ResponseCode.HTTP_OK,
                "Successfully delete wallet with code: " + code, null);

        Link index = linkTo(methodOn(this.getClass()).index()).withRel("all");
        Link store = linkTo(methodOn(this.getClass()).store(null)).withRel("store");

        response.add(index);
        response.add(store);

        return ResponseEntity.ok().body(response);
    }

    @GetMapping(value = "/{code}/enable")
    public ResponseEntity<Response<Wallet>> enable(@PathVariable String code) {
        Wallet wallet = walletService.updateStatus(code, true);
        Response<Wallet> response = new Response<>(true, ResponseCode.HTTP_OK,
                "Successfully enable wallet with code: " + code, wallet);

        Link index = linkTo(methodOn(this.getClass()).index()).withRel("all");
        Link store = linkTo(methodOn(this.getClass()).store(null)).withRel("store");

        response.add(index);
        response.add(store);

        return ResponseEntity.ok().body(response);
    }

    @GetMapping(value = "/{code}/disable")
    public ResponseEntity<Response<Wallet>> disable(@PathVariable String code) {
        Wallet wallet = walletService.updateStatus(code, false);
        Response<Wallet> response = new Response<>(true, ResponseCode.HTTP_OK,
                "Successfully disable wallet with code: " + code, wallet);

        Link index = linkTo(methodOn(this.getClass()).index()).withRel("all");
        Link store = linkTo(methodOn(this.getClass()).store(null)).withRel("store");

        response.add(index);
        response.add(store);

        return ResponseEntity.ok().body(response);
    }

    @PostMapping(value = "/{code}/deposit")
    public ResponseEntity<Response<Wallet>> deposit(@PathVariable String code, @RequestBody WalletTopupRequest walletTopupRequest) {
        Wallet wallet = walletService.deposit(code, walletTopupRequest.getAmount());
        Response<Wallet> response = new Response<>(true, ResponseCode.HTTP_OK,
                "Successfully topup wallet with code: " + code, wallet);

        Link index = linkTo(methodOn(this.getClass()).index()).withRel("all");
        Link store = linkTo(methodOn(this.getClass()).store(null)).withRel("store");

        response.add(index);
        response.add(store);

        return ResponseEntity.ok().body(response);
    }

}
