package com.github.afikrim.flop.transactions;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

import com.github.afikrim.flop.systemwallets.SystemWallet;
import com.github.afikrim.flop.systemwallets.SystemWalletRepository;
import com.github.afikrim.flop.users.User;
import com.github.afikrim.flop.userwallets.UserWallet;
import com.github.afikrim.flop.userwallets.UserWalletRepository;
import com.github.afikrim.flop.utils.exception.CustomException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private SystemWalletRepository systemWalletRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserWalletRepository userWalletRepository;

    @Override
    public List<Transaction> getAllByUser(User user) {
        return user.getTransactions();
    }

    @Transactional
    @Override
    public Transaction storeByUser(User user, TransactionRequest transactionRequest) {
        if (transactionRequest.getAmount() < 50000) {
            throw new CustomException("Transfer must be more than IDR 50,000", HttpStatus.BAD_REQUEST);
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails currentUser = (UserDetails) authentication.getPrincipal();
        if (!currentUser.getUsername().equals(user.getEmail())) {
            throw new CustomException("You are not authorized to access this resources", HttpStatus.UNAUTHORIZED);
        }

        Optional<UserWallet> optionalDestination = userWalletRepository.findByIdAndUserId(transactionRequest.getSource(), user.getId());
        if (optionalDestination.isEmpty()) {
            throw new EntityNotFoundException("Wallet with id " + transactionRequest.getDestination() + " not found.");
        }

        UserWallet destination = optionalDestination.get();
        Optional<SystemWallet> optionalSource = systemWalletRepository.findByWalletId(transactionRequest.getDestination());
        if (optionalSource.isEmpty()) {
            throw new EntityNotFoundException("System does not provide wallet with id " + transactionRequest.getDestination() + " or this wallet currently not ready.");
        }

        SystemWallet source = optionalSource.get();
        if (source.getBalance() < transactionRequest.getAmount()) {
            if (source.getBalance() < 50000) {
                source.setIsAvailable(false);

                systemWalletRepository.save(source);
            }

            throw new CustomException("This wallet currently unavailable.", HttpStatus.BAD_REQUEST);
        }

        source.decreaseBalance(transactionRequest.getAmount());
        if (source.getBalance() < 50000) {
            source.setIsAvailable(false);
        }

        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setSource(source);
        transaction.setDestination(destination);
        transaction.setAmount(transactionRequest.getAmount());
        transaction.setStatus(TransactionStatus.PENDING);
        transaction.setCreatedAt(new Date());
        transaction.setUpdatedAt(new Date());

        return transactionRepository.save(transaction);
    }

    @Override
    public Transaction getOneByUser(User user, Long id) {
        Optional<Transaction> optionalTransaction = transactionRepository.findByIdAndUserId(id, user.getId());
        if (optionalTransaction.isEmpty()) {
            throw new EntityNotFoundException("Transaction with id: " + id + " not found.");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails currentUser = (UserDetails) authentication.getPrincipal();
        if (!currentUser.getUsername().equals(user.getEmail())) {
            throw new CustomException("You are not authorized to access this resources", HttpStatus.UNAUTHORIZED);
        }

        Transaction transaction = optionalTransaction.get();

        return transaction;
    }

    @Transactional
    @Override
    public Transaction updateStatusByUser(User user, Long id, TransactionStatus transactionStatus) {
        Optional<Transaction> optionalTransaction = transactionRepository.findByIdAndUserId(id, user.getId());
        if (optionalTransaction.isEmpty()) {
            throw new EntityNotFoundException("Transaction with id: " + id + " not found.");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails currentUser = (UserDetails) authentication.getPrincipal();
        if (!currentUser.getUsername().equals(user.getEmail())) {
            throw new CustomException("You are not authorized to access this resources", HttpStatus.UNAUTHORIZED);
        }

        Transaction transaction = optionalTransaction.get();
        if (transaction.getStatus().equals(TransactionStatus.COMPLETED)//
                || transaction.getStatus().equals(TransactionStatus.FAILED)//
                || transaction.getStatus().equals(TransactionStatus.CANCEL)) {
            throw new CustomException("Cannot modify transaction, because transaction already at completed state",
                    HttpStatus.BAD_REQUEST);
        }

        int currentStatus = transaction.getStatus().ordinal();
        int nextStatus = transactionStatus.ordinal();
        if (nextStatus < currentStatus) {
            throw new CustomException("Cannot modify status to previous status.", HttpStatus.BAD_REQUEST);
        }

        transaction.setStatus(transactionStatus);

        return transactionRepository.save(transaction);
    }

    @Override
    public List<Transaction> getAll() {
        return transactionRepository.findAll();
    }

    @Override
    public Transaction getOne(Long id) {
        Optional<Transaction> optionalTransaction = transactionRepository.findById(id);
        if (optionalTransaction.isEmpty()) {
            throw new EntityNotFoundException("Transaction with id: " + id + " not found.");
        }

        return optionalTransaction.get();
    }

    @Override
    public Transaction updateStatus(Long id, TransactionStatus transactionStatus) {
        Optional<Transaction> optionalTransaction = transactionRepository.findById(id);
        if (optionalTransaction.isEmpty()) {
            throw new EntityNotFoundException("Transaction with id: " + id + " not found.");
        }

        Transaction transaction = optionalTransaction.get();
        if (transaction.getStatus().equals(TransactionStatus.COMPLETED)//
                || transaction.getStatus().equals(TransactionStatus.FAILED)//
                || transaction.getStatus().equals(TransactionStatus.CANCEL)) {
            throw new CustomException("Cannot modify transaction, because transaction already at completed state",
                    HttpStatus.BAD_REQUEST);
        }

        int currentStatus = transaction.getStatus().ordinal();
        int nextStatus = transactionStatus.ordinal();
        if (nextStatus < currentStatus) {
            throw new CustomException("Cannot modify status to previous status.", HttpStatus.BAD_REQUEST);
        }

        transaction.setStatus(transactionStatus);

        return transactionRepository.save(transaction);
    }

}
