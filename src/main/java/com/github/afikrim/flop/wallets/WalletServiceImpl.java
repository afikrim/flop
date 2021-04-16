package com.github.afikrim.flop.wallets;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class WalletServiceImpl implements WalletService {

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
            }
        }

        return wallets;
    }

    @Override
    public Wallet store(WalletRequest walletRequest) {
        Optional<Wallet> optionalWallet = walletRepository.findByCode(walletRequest.getCode());
        if (optionalWallet.isPresent()) {
            throw new EntityExistsException("Wallet with this code already exists");
        }

        Wallet wallet = new Wallet();

        wallet.setCode(walletRequest.getCode());
        wallet.setName(walletRequest.getName());
        wallet.setEnabled(false);

        Link update = linkTo(methodOn(WalletController.class).update(wallet.getCode(), null)).withRel("update");
        Link delete = linkTo(methodOn(WalletController.class).destroy(wallet.getCode())).withRel("delete");
        Link enable = linkTo(methodOn(WalletController.class).enable(wallet.getCode())).withRel("enable");
        Link disable = linkTo(methodOn(WalletController.class).disable(wallet.getCode())).withRel("disable");

        wallet.add(update);
        wallet.add(delete);
        wallet.add(enable);
        wallet.add(disable);

        return walletRepository.save(wallet);
    }

    @Override
    public Wallet updateOne(String code, WalletRequest walletRequest) {
        Optional<Wallet> optionalWallet = walletRepository.findByCode(code);
        if (optionalWallet.isEmpty()) {
            throw new EntityNotFoundException("Wallet with this code not found");
        }

        Wallet wallet = optionalWallet.get();
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

}
