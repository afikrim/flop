package com.github.afikrim.flop.systemwallets;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;

import com.github.afikrim.flop.wallets.Wallet;
import com.github.afikrim.flop.wallets.WalletRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
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

        for (SystemWallet systemWallet: systemWallets) {
            Link self = linkTo(methodOn(SystemWalletController.class).get(systemWallet.getId())).withRel("self");
            Link update = linkTo(methodOn(SystemWalletController.class).update(systemWallet.getId(), null)).withRel("update");
            Link delete = linkTo(methodOn(SystemWalletController.class).destroy(systemWallet.getId())).withRel("delete");

            systemWallet.add(self);
            systemWallet.add(update);
            systemWallet.add(delete);
        }

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

        Link self = linkTo(methodOn(SystemWalletController.class).get(systemWallet.getId())).withRel("self");
        Link update = linkTo(methodOn(SystemWalletController.class).update(systemWallet.getId(), null)).withRel("update");
        Link delete = linkTo(methodOn(SystemWalletController.class).destroy(systemWallet.getId())).withRel("delete");

        systemWallet.add(self);
        systemWallet.add(update);
        systemWallet.add(delete);

        return systemWalletRepository.save(systemWallet);
    }

    @Override
    public SystemWallet getOne(Integer id) {
        Optional<SystemWallet> optionalSystemWallet = systemWalletRepository.findById(id);
        if (optionalSystemWallet.isEmpty()) {
            throw new EntityNotFoundException("Wallet with id " + id + " not found.");
        }

        SystemWallet systemWallet = optionalSystemWallet.get();

        Link update = linkTo(methodOn(SystemWalletController.class).update(systemWallet.getId(), null)).withRel("update");
        Link delete = linkTo(methodOn(SystemWalletController.class).destroy(systemWallet.getId())).withRel("delete");

        systemWallet.add(update);
        systemWallet.add(delete);

        return systemWallet;
    }

    @Override
    public SystemWallet updateOne(Integer id, SystemWalletRequest systemWalletRequest) {
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

        Link self = linkTo(methodOn(SystemWalletController.class).get(systemWallet.getId())).withRel("self");
        Link delete = linkTo(methodOn(SystemWalletController.class).destroy(systemWallet.getId())).withRel("delete");

        systemWallet.add(self);
        systemWallet.add(delete);

        return systemWalletRepository.save(systemWallet);
    }

    @Override
    public void deleteOne(Integer id) {
        Optional<SystemWallet> optionalSystemWallet = systemWalletRepository.findById(id);
        if (optionalSystemWallet.isEmpty()) {
            throw new EntityNotFoundException("Wallet with id " + id + " not found.");
        }

        systemWalletRepository.deleteById(id);
    }

    @Override
    public SystemWallet deposit(Integer id, SystemWalletTopupRequest systemWalletTopupRequest) {
        Optional<SystemWallet> optionalSystemWallet = systemWalletRepository.findById(id);
        if (optionalSystemWallet.isEmpty()) {
            throw new EntityNotFoundException("Wallet with id " + id + " not found.");
        }

        SystemWallet systemWallet = optionalSystemWallet.get();
        systemWallet.increaseBalance(systemWalletTopupRequest.getAmount());

        if (systemWallet.getBalance() > 50000) {
            systemWallet.setIsAvailable(true);
        }

        Link self = linkTo(methodOn(SystemWalletController.class).get(systemWallet.getId())).withRel("self");
        Link update = linkTo(methodOn(SystemWalletController.class).update(systemWallet.getId(), null)).withRel("update");
        Link delete = linkTo(methodOn(SystemWalletController.class).destroy(systemWallet.getId())).withRel("delete");

        systemWallet.add(self);
        systemWallet.add(update);
        systemWallet.add(delete);

        return systemWalletRepository.save(systemWallet);
    }

}
