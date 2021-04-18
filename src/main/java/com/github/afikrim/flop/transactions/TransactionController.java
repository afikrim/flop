package com.github.afikrim.flop.transactions;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(produces = { MediaType.APPLICATION_JSON_VALUE })
public class TransactionController {

    @Autowired
    TransactionService transactionService;

    @Autowired
    UserService userService;

    @GetMapping(value = "/v1/users/{userId}/transactions")
    public ResponseEntity<Response<List<Transaction>>> allByUser(@PathVariable Long userId) {
        User user = userService.getOne(userId);

        List<Transaction> transactions = transactionService.getAllByUser(user);
        Response<List<Transaction>> response = new Response<>(true, ResponseCode.HTTP_OK, "Successfully get all transactions", transactions);

        Link store = linkTo(methodOn(this.getClass()).storeByUser(userId, null)).withRel("store");

        response.add(store);

        return ResponseEntity.ok().body(response);
    }

    @PostMapping(value = "/v1/users/{userId}/transactions")
    public ResponseEntity<Response<Transaction>> storeByUser(@PathVariable Long userId, @RequestBody TransactionRequest transactionRequest) {
        User user = userService.getOne(userId);

        Transaction transaction = transactionService.storeTransfer(user, transactionRequest);
        Response<Transaction> response = new Response<>(true, ResponseCode.HTTP_OK, "Successfully store new transaction", transaction);

        Link all = linkTo(methodOn(this.getClass()).allByUser(userId)).withRel("all");

        response.add(all);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping(value = "/v1/users/{userId}/transactions/{id}")
    public ResponseEntity<Response<Transaction>> getOneByUser(@PathVariable Long userId, @PathVariable Long id) {
        User user = userService.getOne(userId);

        Transaction transaction = transactionService.getOneByUser(user, id);
        Response<Transaction> response = new Response<>(true, ResponseCode.HTTP_OK, "Successfully get a transaction", transaction);

        Link all = linkTo(methodOn(this.getClass()).allByUser(userId)).withRel("all");
        Link store = linkTo(methodOn(this.getClass()).storeByUser(userId, null)).withRel("store");

        response.add(all);
        response.add(store);

        return ResponseEntity.ok().body(response);
    }

    @GetMapping(value = "/v1/users/{userId}/transactions/{id}/cancel")
    public ResponseEntity<Response<Transaction>> cancel(@PathVariable Long userId, @PathVariable Long id, TransactionRequest transactionRequest) {
        User user = userService.getOne(userId);

        Transaction transaction = transactionService.updateStatusByUser(user, id, TransactionStatus.CANCEL);
        Response<Transaction> response = new Response<>(true, ResponseCode.HTTP_OK, "Successfully cancel transaction", transaction);

        Link all = linkTo(methodOn(this.getClass()).allByUser(userId)).withRel("all");
        Link store = linkTo(methodOn(this.getClass()).storeByUser(userId, null)).withRel("store");

        response.add(all);
        response.add(store);

        return ResponseEntity.ok().body(response);
    }

    @GetMapping(value = "/v1/transactions")
    public ResponseEntity<Response<List<Transaction>>> getAll() {
        List<Transaction> transactions = transactionService.getAll();
        Response<List<Transaction>> response = new Response<>(true, ResponseCode.HTTP_OK, "Successfully get all transactions", transactions);

        return ResponseEntity.ok().body(response);
    }

    @GetMapping(value = "/v1/transactions/{id}")
    public ResponseEntity<Response<Transaction>> getOne(@PathVariable Long id) {
        Transaction transaction = transactionService.getOne(id);
        Response<Transaction> response = new Response<>(true, ResponseCode.HTTP_OK, "Successfully get transaction with id: " + id, transaction);

        Link all = linkTo(methodOn(this.getClass()).getAll()).withRel("all");

        response.add(all);

        return ResponseEntity.ok().body(response);
    }

    @GetMapping(value = "/v1/transactions/{id}/on-process")
    public ResponseEntity<Response<Transaction>> process(@PathVariable Long id, TransactionRequest transactionRequest) {
        Transaction transaction = transactionService.updateStatus(id, TransactionStatus.ON_PROCESS);
        Response<Transaction> response = new Response<>(true, ResponseCode.HTTP_OK, "Successfully update transaction status", transaction);

        Link all = linkTo(methodOn(this.getClass()).getAll()).withRel("all");

        response.add(all);

        return ResponseEntity.ok().body(response);
    }

    @GetMapping(value = "/v1/transactions/{id}/failed")
    public ResponseEntity<Response<Transaction>> failed(@PathVariable Long id, TransactionRequest transactionRequest) {
        Transaction transaction = transactionService.updateStatus(id, TransactionStatus.FAILED);
        Response<Transaction> response = new Response<>(true, ResponseCode.HTTP_OK, "Successfully update transaction status", transaction);

        Link all = linkTo(methodOn(this.getClass()).getAll()).withRel("all");

        response.add(all);

        return ResponseEntity.ok().body(response);
    }

    @GetMapping(value = "/v1/transactions/{id}/completed")
    public ResponseEntity<Response<Transaction>> completed(@PathVariable Long id, TransactionRequest transactionRequest) {
        Transaction transaction = transactionService.updateStatus(id, TransactionStatus.COMPLETED);
        Response<Transaction> response = new Response<>(true, ResponseCode.HTTP_OK, "Successfully update transaction status", transaction);

        Link all = linkTo(methodOn(this.getClass()).getAll()).withRel("all");

        response.add(all);

        return ResponseEntity.ok().body(response);
    }

}
