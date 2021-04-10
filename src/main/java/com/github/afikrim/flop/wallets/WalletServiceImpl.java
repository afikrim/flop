package com.github.afikrim.flop.wallets;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Service;

@Service
public class WalletServiceImpl implements WalletService {

    @Autowired
    private WalletRepository walletRepository;

    @Override
    public List<Wallet> getAll() {
        List<Wallet> wallets = walletRepository.findAll();

        for (Wallet wallet: wallets) {
            Link update = linkTo(methodOn(WalletController.class).update(wallet.getCode(), null)).withRel("update");
            Link delete = linkTo(methodOn(WalletController.class).destroy(wallet.getCode())).withRel("delete");

            wallet.add(update);
            wallet.add(delete);
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

        wallet.add(update);
        wallet.add(delete);

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

        wallet.add(delete);

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

}
