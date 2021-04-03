package com.github.afikrim.flop.users;

import java.util.List;

public interface UserService {

    List<User> getAll();

    User getOne(Long id);

    User updateOne(Long id, UserRequest userRequest);

    User destroyOne(Long id);

    User getOneByCredential(String username);

}
