package com.github.afikrim.flop.users;

import java.util.List;

public interface UserService {

    List<User> getAll();

    User getOne(Long id);

    User destroyOne(Long id);

    User getOneByCredential(String username);

}
