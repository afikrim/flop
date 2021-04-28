package com.github.afikrim.flop.mutations;

import java.util.List;

import com.github.afikrim.flop.users.User;

public interface MutationService {

    public List<Mutation> getAllByUser(User user);

    public Mutation getOneByUser(User user, Long id);

    public List<Mutation> getAll();

    public Mutation getOne(Long id);

}
