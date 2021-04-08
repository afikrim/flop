package com.github.afikrim.flop.auth;

import java.util.List;

import com.github.afikrim.flop.users.User;
import com.github.afikrim.flop.users.UserRequest;
import com.github.afikrim.flop.userwallets.UserWallet;
import com.github.afikrim.flop.userwallets.UserWalletRequest;

public interface AuthService {

    AuthResponse register(UserRequest userRequest);

    AuthResponse authenticate(String credential, String password);

    List<UserWallet> getUserWallets(String credential);

    UserWallet addNewUserWallet(String credential, UserWalletRequest userWalletRequest);

    UserWallet updateUserWallet(String credential, Long userWalletId, UserWalletRequest userWalletRequest);

    void deleteUserWallet(String credential, Long userWalletId);

}
