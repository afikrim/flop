package com.github.afikrim.flop.wallets;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WalletServiceImpl implements WalletService {

    @Autowired
    private WalletRepository walletRepository;

    @Override
    public List<Wallet> getAll() {
        return walletRepository.findAll();
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

        return walletRepository.save(wallet);
    }

    @Override
    public Wallet getOne(String code) {
        Optional<Wallet> optionalWallet = walletRepository.findByCode(code);
        if (optionalWallet.isEmpty()) {
            throw new EntityNotFoundException("Wallet with this code not found");
        }

        return optionalWallet.get();
    }

    @Override
    public Wallet updateOne(String code, WalletRequest walletRequest) {
        Optional<Wallet> optionalWallet = walletRepository.findByCode(code);
        if (optionalWallet.isEmpty()) {
            throw new EntityNotFoundException("Wallet with this code not found");
        }

        Wallet tempWallet = optionalWallet.get();
        if (walletRequest.getCode() != null && !walletRequest.getCode().equals(tempWallet.getCode())) {
            Optional<Wallet> walletCodeExists = walletRepository.findByCode(walletRequest.getCode());
            if (walletCodeExists.isPresent()) {
                throw new EntityExistsException("Wallet with this code already exists");
            }

            tempWallet.setCode(walletRequest.getCode());
        }
        if (walletRequest.getName() != null && !walletRequest.getName().equals(tempWallet.getName())) {
            tempWallet.setName(walletRequest.getName());
        }
        if (walletRequest.getEnabled() != null && walletRequest.getEnabled() != tempWallet.getEnabled()) {
            tempWallet.setEnabled(walletRequest.getEnabled());
        }

        return walletRepository.save(tempWallet);
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
