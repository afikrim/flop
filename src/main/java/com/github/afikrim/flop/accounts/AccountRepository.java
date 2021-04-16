package com.github.afikrim.flop.accounts;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    @Query("SELECT a from Account a WHERE a.username = ?1 AND a.isDeleted = false")
    Optional<Account> findByUsername(String username);

}
