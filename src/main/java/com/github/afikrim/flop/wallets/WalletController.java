package com.github.afikrim.flop.wallets;

import java.util.List;

import com.github.afikrim.flop.utils.response.Response;
import com.github.afikrim.flop.utils.response.ResponseCode;

import org.springframework.beans.factory.annotation.Autowired;
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

        return ResponseEntity.ok().body(response);
    }

    @PostMapping
    public ResponseEntity<Response<Wallet>> store(@RequestBody WalletRequest walletRequest) {
        Wallet wallet = walletService.store(walletRequest);
        Response<Wallet> response = new Response<>(true, ResponseCode.CREATED, "Successfully created a new wallet",
                wallet);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping(value = "/{code}")
    public ResponseEntity<Response<Wallet>> update(@PathVariable String code, @RequestBody WalletRequest walletRequest) {
        Wallet wallet = walletService.updateOne(code, walletRequest);
        Response<Wallet> response = new Response<>(true, ResponseCode.HTTP_OK,
                "Successfully update wallet with code: " + code, wallet);

        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping(value = "/{code}")
    public ResponseEntity<Response<Wallet>> destroy(@PathVariable String code) {
        walletService.deleteOne(code);
        ;
        Response<Wallet> response = new Response<>(true, ResponseCode.HTTP_OK,
                "Successfully delete wallet with code: " + code, null);

        return ResponseEntity.ok().body(response);
    }

}
