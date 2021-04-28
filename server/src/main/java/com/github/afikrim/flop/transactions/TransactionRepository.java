package com.github.afikrim.flop.transactions;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("SELECT t FROM Transaction t WHERE t.id = ?1 AND t.user.id = ?2")
    public Optional<Transaction> findByIdAndUserId(Long id, Long userId);

}
