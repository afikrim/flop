package com.github.afikrim.flop.users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.email = ?1 OR u.phone = ?2")
    Optional<User> findByEmailOrPhone(String email, String phone);

    @Query("SELECT u FROM User u WHERE u.account.isDeleted = false AND u.email = ?1")
    Optional<User> findByCredential(String credential);

    @Query("SELECT u FROM User u WHERE u.email = ?1")
    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.phone = ?1")
    Optional<User> findByPhone(String phone);

    @Query("SELECT u FROM User u WHERE u.email = ?1")
    User getUserByEmail(String email);

}
