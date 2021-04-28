package com.github.afikrim.flop.transactions;

import java.util.List;

import com.github.afikrim.flop.users.User;

public interface TransactionService {

    public List<Transaction> getAllByUser(User user);

    public Transaction storeTransfer(User user, TransactionRequest transactionRequest);

    public Transaction storeTopup(TransactionRequest transactionRequest);

    public Transaction getOneByUser(User user, Long id);

    public Transaction updateStatusByUser(User user, Long id, TransactionStatus transactionStatus);

    public List<Transaction> getAll();

    public Transaction getOne(Long id);

    public Transaction updateStatus(Long id, TransactionStatus transactionStatus);

}
