package com.github.afikrim.flop.users.details;

import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import com.github.afikrim.flop.accounts.Account;
import com.github.afikrim.flop.accounts.AccountRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailServiceImpl implements UserDetailService {

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Account> optionalAccount = accountRepository.getAccountWithCredential(username);

        if (optionalAccount.isEmpty()) {
            throw new EntityNotFoundException("User not found.");
        }

        Account account = optionalAccount.get();
        return new UserDetail(account.getUser());
    }

}
