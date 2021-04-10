package com.github.afikrim.flop.systemwallets;

import java.util.List;

public interface SystemWalletService {

    public List<SystemWallet> getAll();

    public SystemWallet store(SystemWalletRequest systemWalletRequest);

    public SystemWallet getOne(Integer id);

    public SystemWallet updateOne(Integer id, SystemWalletRequest systemWalletRequest);

    public void deleteOne(Integer id);

}
