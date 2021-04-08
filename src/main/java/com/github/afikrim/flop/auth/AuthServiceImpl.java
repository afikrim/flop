package com.github.afikrim.flop.auth;

import com.github.afikrim.flop.accounts.Account;
import com.github.afikrim.flop.accounts.AccountRepository;
import com.github.afikrim.flop.accounts.AccountRequest;
import com.github.afikrim.flop.roles.Role;
import com.github.afikrim.flop.roles.RoleRepository;
import com.github.afikrim.flop.users.User;
import com.github.afikrim.flop.users.UserRepository;
import com.github.afikrim.flop.users.UserRequest;
import com.github.afikrim.flop.userwallets.UserWallet;
import com.github.afikrim.flop.userwallets.UserWalletRepository;
import com.github.afikrim.flop.userwallets.UserWalletRequest;
import com.github.afikrim.flop.utils.exception.CustomException;
import com.github.afikrim.flop.utils.jwt.JwtUtil;
import com.github.afikrim.flop.wallets.Wallet;
import com.github.afikrim.flop.wallets.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.*;

@Service
public class AuthServiceImpl implements AuthService {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    JwtUtil jwtUtil;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private WalletRepository walletRepository;
    @Autowired
    private UserWalletRepository userWalletRepository;

    public AuthServiceImpl() {
        bCryptPasswordEncoder = new BCryptPasswordEncoder();
    }

    @Transactional
    @Override
    public AuthResponse register(UserRequest userRequest) {
        Optional<AccountRequest> optionalAccountRequest = userRequest.getAccount();

        if (optionalAccountRequest.isEmpty())
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing field account");

        AccountRequest accountRequest = optionalAccountRequest.get();
        if (accountRequest.getUsername() != null) {
            Optional<Account> existsAccount = accountRepository.findByUsername(accountRequest.getUsername());

            if (existsAccount.isPresent()) {
                throw new CustomException("Username already in used", HttpStatus.BAD_REQUEST);
            }
        }

        Optional<User> existsUser = userRepository.findByEmailOrPhone(userRequest.getEmail(), userRequest.getPhone());
        if (existsUser.isPresent()) {
            throw new CustomException("Email or Phone already in used", HttpStatus.BAD_REQUEST);
        }

        Optional<Role> optionalRole = roleRepository.findByCode("ROLE_USER");
        if (optionalRole.isEmpty()) {
            throw new CustomException("Something wrong with the server.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        Account account = new Account();
        if (accountRequest.getUsername() != null)
            account.setUsername(accountRequest.getUsername());
        account.setPassword(bCryptPasswordEncoder.encode(accountRequest.getPassword()));
        account.setCreatedAt(new Date());
        account.setUpdatedAt(new Date());

        User user = new User();
        user.setFullname(userRequest.getFullname());
        user.setEmail(userRequest.getEmail());
        user.setPhone(userRequest.getPhone());
        user.setRoles(new HashSet<>(Arrays.asList(optionalRole.get())));
        user.setAccount(account);
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());

        accountRepository.save(account);
        userRepository.save(user);

        Map<String, Object> claims = setClaims(user);
        String token = jwtUtil.generateToken(claims, user.getEmail());

        return new AuthResponse(token);
    }

    @Override
    public AuthResponse authenticate(String credential, String password) {
        Account account = null;
        try {
            Optional<Account> optionalAccount = accountRepository.getAccountWithCredential(credential);

            if (optionalAccount.isEmpty()) {
                throw new Exception("User not found!");
            }

            account = optionalAccount.get();
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(credential, password));
        } catch (Exception ex) {
            throw new CustomException(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }

        Map<String, Object> claims = setClaims(account.getUser());
        String token = jwtUtil.generateToken(claims, account.getUser().getEmail());

        return new AuthResponse(token);
    }

    @Override
    public List<UserWallet> getUserWallets(String credential) {
        Optional<Account> optionalAccount = accountRepository.getAccountWithCredential(credential);
        if (optionalAccount.isEmpty()) {
            throw new EntityNotFoundException("User not found!");
        }

        Account account = optionalAccount.get();
        User user = account.getUser();
        Set<UserWallet> setUserWallets = user.getUserWallets();

        return new ArrayList<>(setUserWallets);
    }

    @Override
    public UserWallet addNewUserWallet(String credential, UserWalletRequest userWalletRequest) {
        Optional<Account> optionalAccount = accountRepository.getAccountWithCredential(credential);
        if (optionalAccount.isEmpty()) {
            throw new EntityNotFoundException("User not found!");
        }

        Optional<Wallet> optionalWallet = walletRepository.findById(userWalletRequest.getWalletId());
        if (optionalWallet.isEmpty()) {
            throw new EntityNotFoundException("Wallet with id: " + userWalletRequest.getWalletId() + " not found.");
        }

        Account account = optionalAccount.get();
        Wallet wallet = optionalWallet.get();
        User user = account.getUser();

        UserWallet userWallet = new UserWallet();
        userWallet.setName(userWalletRequest.getName());
        userWallet.setPhone(userWalletRequest.getPhone());
        userWallet.setUser(user);
        userWallet.setWallet(wallet);
        userWallet.setCreatedAt(new Date());
        userWallet.setUpdatedAt(new Date());

        return userWalletRepository.save(userWallet);
    }

    @Override
    public UserWallet updateUserWallet(String credential, Long userWalletId, UserWalletRequest userWalletRequest) {
        Optional<Account> optionalAccount = accountRepository.getAccountWithCredential(credential);
        if (optionalAccount.isEmpty()) {
            throw new EntityNotFoundException("User not found!");
        }

        Optional<UserWallet> optionalUserWallet = userWalletRepository.findById(userWalletId);
        if (optionalUserWallet.isEmpty()) {
            throw new EntityNotFoundException(
                    "Wallet Relation with id: " + userWalletId + " not found.");
        }

        UserWallet userWallet = optionalUserWallet.get();

        if (userWalletRequest.getName() != null && !userWalletRequest.getName().equals(userWallet.getName())) {
            userWallet.setName(userWalletRequest.getName());
        }
        if (userWalletRequest.getPhone() != null && !userWalletRequest.getPhone().equals(userWallet.getPhone())) {
            userWallet.setPhone(userWalletRequest.getPhone());
        }
        if (userWalletRequest.getWalletId() != null
                && !userWalletRequest.getWalletId().equals(userWallet.getWallet().getId())) {
            Optional<Wallet> optionalWallet = walletRepository.findById(userWalletRequest.getWalletId());
            if (optionalWallet.isEmpty()) {
                throw new EntityNotFoundException("Wallet with id: " + userWalletRequest.getWalletId() + " not found.");
            }

            Wallet wallet = optionalWallet.get();
            userWallet.setWallet(wallet);
        }
        userWallet.setUpdatedAt(new Date());

        return userWalletRepository.save(userWallet);
    }

    @Override
    public void deleteUserWallet(String credential, Long userWalletId) {
        Optional<Account> optionalAccount = accountRepository.getAccountWithCredential(credential);
        if (optionalAccount.isEmpty()) {
            throw new EntityNotFoundException("User not found!");
        }

        Optional<UserWallet> optionalUserWallet = userWalletRepository.findById(userWalletId);
        if (optionalUserWallet.isEmpty()) {
            throw new EntityNotFoundException(
                    "Wallet Relation with id: " + userWalletId + " not found.");
        }

        userWalletRepository.deleteById(userWalletId);
    }

    private Map<String, Object> setClaims(User user) {
        Map<String, Object> payload = new HashMap<>();
        if (user.getAccount().getUsername() != null)
            payload.put("username", user.getAccount().getUsername());
        payload.put("name", user.getFullname());
        payload.put("email", user.getEmail());
        payload.put("phone", user.getPhone());

        HashMap<String, Object> claims = new HashMap<>();
        claims.put("id", UUID.randomUUID());
        claims.put("payload", payload);

        return claims;
    }
}
