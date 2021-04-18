package com.github.afikrim.flop.mutations;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MutationRepository extends JpaRepository<Mutation, Long> {

    @Query("SELECT m FROM Mutation m WHERE m.transaction.user.id = ?1")
    public List<Mutation> findAllByUserId(Long userId);

    @Query("SELECT m FROM Mutation m WHERE m.id = ?1 AND m.transaction.user.id = ?2")
    public Optional<Mutation> findByIdAndUserId(Long id, Long userId);

}
