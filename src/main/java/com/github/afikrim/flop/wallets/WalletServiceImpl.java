package com.github.afikrim.flop.wallets;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

import com.github.afikrim.flop.systemwallets.SystemWallet;
import com.github.afikrim.flop.systemwallets.SystemWalletRequest;
import com.github.afikrim.flop.transactions.Transaction;
import com.github.afikrim.flop.transactions.TransactionRepository;
import com.github.afikrim.flop.transactions.TransactionStatus;
import com.github.afikrim.flop.transactions.TransactionType;
import com.github.afikrim.flop.users.User;
import com.github.afikrim.flop.users.UserRepository;
import com.github.afikrim.flop.utils.exception.CustomException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class WalletServiceImpl implements WalletService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Override
    public List<Wallet> getAll() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        List<Wallet> wallets = walletRepository.findAll();

        for (Wallet wallet : wallets) {
            if (userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
                Link update = linkTo(methodOn(WalletController.class).update(wallet.getCode(), null)).withRel("update");
                Link delete = linkTo(methodOn(WalletController.class).destroy(wallet.getCode())).withRel("delete");
                Link enable = linkTo(methodOn(WalletController.class).destroy(wallet.getCode())).withRel("enable");
                Link disable = linkTo(methodOn(WalletController.class).destroy(wallet.getCode())).withRel("disable");

                wallet.add(update);
                wallet.add(delete);
                wallet.add(enable);
                wallet.add(disable);
            } else {
                wallet.setSystemWallet(null);
            }
        }

        return wallets;
    }

    @Override
    public Wallet getOne(String code) {
        Optional<Wallet> optionalWallet = walletRepository.findByCode(code);
        if (optionalWallet.isEmpty()) {
            throw new EntityNotFoundException("Wallet with this code not found");
        }

        Wallet wallet = optionalWallet.get();

        Link update = linkTo(methodOn(WalletController.class).update(wallet.getCode(), null)).withRel("update");
        Link enable = linkTo(methodOn(WalletController.class).enable(wallet.getCode())).withRel("enable");
        Link disable = linkTo(methodOn(WalletController.class).disable(wallet.getCode())).withRel("disable");
        Link delete = linkTo(methodOn(WalletController.class).destroy(wallet.getCode())).withRel("delete");

        wallet.add(update);
        wallet.add(delete);
        wallet.add(enable);
        wallet.add(disable);

        return wallet;
    }

    @Transactional
    @Override
    public Wallet store(WalletRequest walletRequest) {
        Optional<SystemWalletRequest> optionalSystemWalletRequest = walletRequest.getSystemWallet();
        if (optionalSystemWalletRequest.isEmpty()) {
            throw new CustomException("Field system_wallet is required.", HttpStatus.BAD_REQUEST);
        }

        SystemWalletRequest systemWalletRequest = optionalSystemWalletRequest.get();
        Optional<Wallet> optionalWallet = walletRepository.findByCode(walletRequest.getCode());
        if (optionalWallet.isPresent()) {
            throw new EntityExistsException("Wallet with this code already exists");
        }

        SystemWallet systemWallet = new SystemWallet();
        systemWallet.setPhone(systemWalletRequest.getPhone());
        systemWallet.setCreatedAt(new Date());
        systemWallet.setUpdatedAt(new Date());

        Wallet wallet = new Wallet();
        wallet.setCode(walletRequest.getCode());
        wallet.setName(walletRequest.getName());
        wallet.setEnabled(false);
        wallet.setSystemWallet(systemWallet);
        systemWallet.setWallet(wallet);

        Link update = linkTo(methodOn(WalletController.class).update(wallet.getCode(), null)).withRel("update");
        Link enable = linkTo(methodOn(WalletController.class).enable(wallet.getCode())).withRel("enable");
        Link disable = linkTo(methodOn(WalletController.class).disable(wallet.getCode())).withRel("disable");
        Link delete = linkTo(methodOn(WalletController.class).destroy(wallet.getCode())).withRel("delete");

        wallet.add(update);
        wallet.add(delete);
        wallet.add(enable);
        wallet.add(disable);

        return walletRepository.save(wallet);
    }

    @Transactional
    @Override
    public Wallet updateOne(String code, WalletRequest walletRequest) {
        Optional<Wallet> optionalWallet = walletRepository.findByCode(code);
        if (optionalWallet.isEmpty()) {
            throw new EntityNotFoundException("Wallet with this code not found");
        }

        Wallet wallet = optionalWallet.get();
        Optional<SystemWalletRequest> optionalSystemWalletRequest = walletRequest.getSystemWallet();
        if (walletRequest.getCode() != null && !walletRequest.getCode().equals(wallet.getCode())) {
            Optional<Wallet> walletCodeExists = walletRepository.findByCode(walletRequest.getCode());
            if (walletCodeExists.isPresent()) {
                throw new EntityExistsException("Wallet with this code already exists");
            }

            wallet.setCode(walletRequest.getCode());
        }
        if (walletRequest.getName() != null && !walletRequest.getName().equals(wallet.getName())) {
            wallet.setName(walletRequest.getName());
        }
        if (walletRequest.getEnabled() != null && !walletRequest.getEnabled().equals(wallet.getEnabled())) {
            wallet.setEnabled(walletRequest.getEnabled());
        }

        if (optionalSystemWalletRequest.isPresent()) {
            SystemWalletRequest systemWalletRequest = optionalSystemWalletRequest.get();
            SystemWallet systemWallet = wallet.getSystemWallet();

            if (systemWalletRequest.getPhone() != null && !systemWalletRequest.getPhone().equals(systemWallet.getPhone())) {
                systemWallet.setPhone(systemWalletRequest.getPhone());
            }

            systemWallet.setUpdatedAt(new Date());
            wallet.setSystemWallet(systemWallet);
        }

        Link delete = linkTo(methodOn(WalletController.class).destroy(wallet.getCode())).withRel("delete");
        Link enable = linkTo(methodOn(WalletController.class).enable(wallet.getCode())).withRel("enable");
        Link disable = linkTo(methodOn(WalletController.class).disable(wallet.getCode())).withRel("disable");

        wallet.add(delete);
        wallet.add(enable);
        wallet.add(disable);

        return walletRepository.save(wallet);
    }

    @Override
    public void deleteOne(String code) {
        Optional<Wallet> optionalWallet = walletRepository.findByCode(code);
        if (optionalWallet.isEmpty()) {
            throw new EntityNotFoundException("Wallet with this code not found");
        }

        walletRepository.delete(optionalWallet.get());
    }

    @Override
    public Wallet updateStatus(String code, Boolean status) {
        Optional<Wallet> optionalWallet = walletRepository.findByCode(code);
        if (optionalWallet.isEmpty()) {
            throw new EntityNotFoundException("Wallet with this code not found");
        }

        Wallet wallet = optionalWallet.get();
        wallet.setEnabled(status);

        Link update = linkTo(methodOn(WalletController.class).update(wallet.getCode(), null)).withRel("update");
        Link delete = linkTo(methodOn(WalletController.class).destroy(wallet.getCode())).withRel("delete");

        wallet.add(update);
        wallet.add(delete);

        if (!status.booleanValue()) {
            Link enable = linkTo(methodOn(WalletController.class).enable(wallet.getCode())).withRel("enable");
            wallet.add(enable);
        } else {
            Link disable = linkTo(methodOn(WalletController.class).disable(wallet.getCode())).withRel("disable");
            wallet.add(disable);
        }

        return walletRepository.save(wallet);
    }

    @Transactional
    @Override
    public Wallet deposit(String code, Long amount) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetail = (UserDetails) authentication.getPrincipal();
        User user = userRepository.getUserByEmail(userDetail.getUsername());

        Optional<Wallet> optionalWallet = walletRepository.findByCode(code);
        if (optionalWallet.isEmpty()) {
            throw new EntityNotFoundException("Wallet with this code not found");
        }

        Wallet wallet = optionalWallet.get();
        SystemWallet systemWallet = wallet.getSystemWallet();

        systemWallet.increaseBalance(amount);
        if (systemWallet.getBalance() > 50000) {
            systemWallet.setIsAvailable(true);
        }

        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setDestination(wallet);
        transaction.setType(TransactionType.TOPUP);
        transaction.setAmount(amount);
        transaction.setStatus(TransactionStatus.COMPLETED);
        transaction.setCreatedAt(new Date());
        transaction.setUpdatedAt(new Date());

        transactionRepository.save(transaction);

        return walletRepository.save(wallet);
    }

}
