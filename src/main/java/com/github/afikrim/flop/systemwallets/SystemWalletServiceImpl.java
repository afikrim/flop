package com.github.afikrim.flop.systemwallets;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;

import com.github.afikrim.flop.wallets.Wallet;
import com.github.afikrim.flop.wallets.WalletRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SystemWalletServiceImpl implements SystemWalletService {

    @Autowired
    SystemWalletRepository systemWalletRepository;

    @Autowired
    WalletRepository walletRepository;

    @Override
    public List<SystemWallet> getAll() {
        List<SystemWallet> systemWallets = systemWalletRepository.findAll();

        return systemWallets;
    }

    @Override
    public SystemWallet store(SystemWalletRequest systemWalletRequest) {
        Optional<Wallet> optionalWallet = walletRepository.findById(systemWalletRequest.getWalletId());
        if (optionalWallet.isEmpty()) {
            throw new EntityNotFoundException("Wallet with id: " + systemWalletRequest.getWalletId() + " not found.");
        }

        Wallet wallet = optionalWallet.get();
        Optional<SystemWallet> optionalSystemWallet = systemWalletRepository.findByPhoneAndWalletId(systemWalletRequest.getPhone(), systemWalletRequest.getWalletId());
        if (optionalSystemWallet.isPresent()) {
            throw new EntityExistsException("Wallet with type " + wallet.getName() + " and phone number " + systemWalletRequest.getPhone() + " already exists");
        }

        SystemWallet systemWallet = new SystemWallet();
        systemWallet.setWallet(wallet);
        systemWallet.setPhone(systemWalletRequest.getPhone());
        systemWallet.setName(systemWalletRequest.getName());
        systemWallet.setBalance(0L);
        systemWallet.setCreatedAt(new Date());
        systemWallet.setUpdatedAt(new Date());

        return systemWalletRepository.save(systemWallet);
    }

    @Override
    public SystemWallet getOne(Long id) {
        Optional<SystemWallet> optionalSystemWallet = systemWalletRepository.findById(id);
        if (optionalSystemWallet.isEmpty()) {
            throw new EntityNotFoundException("Wallet with id " + id + " not found.");
        }

        SystemWallet systemWallet = optionalSystemWallet.get();

        return systemWallet;
    }

    @Override
    public SystemWallet updateOne(Long id, SystemWalletRequest systemWalletRequest) {
        Optional<SystemWallet> optionalSystemWallet = systemWalletRepository.findById(id);
        if (optionalSystemWallet.isEmpty()) {
            throw new EntityNotFoundException("Wallet with id " + id + " not found.");
        }

        SystemWallet systemWallet = optionalSystemWallet.get();
        if (systemWalletRequest.getWalletId() != null && !systemWalletRequest.getWalletId().equals(systemWallet.getWallet().getId())) {
            Optional<Wallet> optionalWallet = walletRepository.findById(systemWalletRequest.getWalletId());
            if (optionalWallet.isEmpty()) {
                throw new EntityNotFoundException("Wallet with id: " + systemWalletRequest.getWalletId() + " not found.");
            }

            Wallet wallet = optionalWallet.get();
            systemWallet.setWallet(wallet);
        }

        if (systemWalletRequest.getPhone() != null && !systemWalletRequest.getPhone().equals(systemWallet.getPhone())) {
            systemWallet.setPhone(systemWalletRequest.getPhone());
        }

        if (systemWalletRequest.getName() != null && !systemWalletRequest.getName().equals(systemWallet.getName())) {
            systemWallet.setName(systemWalletRequest.getName());
        }

        systemWallet.setUpdatedAt(new Date());

        return systemWalletRepository.save(systemWallet);
    }

    @Override
    public void deleteOne(Long id) {
        Optional<SystemWallet> optionalSystemWallet = systemWalletRepository.findById(id);
        if (optionalSystemWallet.isEmpty()) {
            throw new EntityNotFoundException("Wallet with id " + id + " not found.");
        }

        systemWalletRepository.deleteById(id);
    }

}
