package com.github.afikrim.flop.userwallets;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface UserWalletRepository extends JpaRepository<UserWallet, Long> {

    @Query("SELECT uw FROM UserWallet uw WHERE uw.phone = ?1 AND uw.user.id = ?2 AND uw.wallet.id = ?3")
    public Optional<UserWallet> findByPhoneAndUserIdAndWalletId(String phone, Long userId, Integer walletId);

    @Modifying
    @Query("DELETE FROM UserWallet uw WHERE uw.id = ?1 AND uw.user.id = ?2")
    public void deleteByIdAndUserId(Long id, Long userId);

}
