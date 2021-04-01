package com.github.afikrim.flop.accounts;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    @Query("SELECT a FROM Account a JOIN a.user u WHERE a.username = ?1 OR u.email = ?1 OR u.phone = ?1")
    Optional<Account> getAccountWithCredential(String credential);

    @Query("SELECT a from Account a WHERE a.username = ?1")
    Optional<Account> findByUsername(String username);

}
