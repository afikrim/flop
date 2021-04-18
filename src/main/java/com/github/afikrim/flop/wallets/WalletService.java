package com.github.afikrim.flop.wallets;

import java.util.List;

public interface WalletService {

    public List<Wallet> getAll();

    public Wallet getOne(String code);

    public Wallet store(WalletRequest walletRequest);

    public Wallet updateOne(String code, WalletRequest walletRequest);

    public void deleteOne(String code);

    public Wallet updateStatus(String code, Boolean status);

    public Wallet deposit(String code, Long amount);

}
