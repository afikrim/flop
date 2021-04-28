package com.github.afikrim.flop.wallets;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface WalletRepository extends JpaRepository<Wallet, Integer> {

    @Query("SELECT w FROM Wallet w WHERE w.code = ?1")
    public Optional<Wallet> findByCode(String code);

}
