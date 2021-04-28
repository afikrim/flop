package com.github.afikrim.flop.systemwallets;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SystemWalletRepository extends JpaRepository<SystemWallet, Integer> {

    @Query("SELECT sw FROM SystemWallet sw WHERE sw.phone = ?1 AND sw.wallet.id = ?2")
    public Optional<SystemWallet> findByPhoneAndWalletId(String phone, Integer walletId);

    @Query("SELECT sw FROM SystemWallet sw WHERE sw.wallet.id = ?1 AND sw.isAvailable = true")
    public Optional<SystemWallet> findByWalletId(Integer walletId);

}
