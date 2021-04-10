package com.github.afikrim.flop.userwallets;

import java.util.List;

import com.github.afikrim.flop.users.User;

public interface UserWalletService {

    public List<UserWallet> getAll(User user);

    public UserWallet store(User user, UserWalletRequest userWalletRequest);

    public UserWallet getOne(User user, Long id);

    public UserWallet updateOne(User user, Long id, UserWalletRequest userWalletRequest);

    public void deleteOne(User user, Long id);

}
