package com.github.afikrim.flop.users;

import com.github.afikrim.flop.accounts.Account;
import com.github.afikrim.flop.accounts.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Optional;

@Service
public class UserDetailServiceImpl implements UserDetailsService {

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Account> optionalAccount = accountRepository.getAccountWithCredential(username);

        if (!optionalAccount.isPresent()) {
            throw new EntityNotFoundException("Account not found!");
        }

        Account account = optionalAccount.get();

        return new org.springframework.security.core.userdetails.User(username, account.getPassword(), new ArrayList<>());
    }
}
