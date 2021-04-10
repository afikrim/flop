package com.github.afikrim.flop.systemwallets;

import java.util.List;

public interface SystemWalletService {

    public List<SystemWallet> getAll();

    public SystemWallet store(SystemWalletRequest systemWalletRequest);

    public SystemWallet getOne(Long id);

    public SystemWallet updateOne(Long id, SystemWalletRequest systemWalletRequest);

    public void deleteOne(Long id);

}
