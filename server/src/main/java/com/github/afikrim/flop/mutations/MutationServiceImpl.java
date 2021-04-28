package com.github.afikrim.flop.mutations;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import com.github.afikrim.flop.users.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MutationServiceImpl implements MutationService {

    @Autowired
    private MutationRepository mutationRepository;

    @Override
    public List<Mutation> getAllByUser(User user) {
        List<Mutation> mutations = mutationRepository.findAllByUserId(user.getId());

        return mutations;
    }

    @Override
    public Mutation getOneByUser(User user, Long id) {
        Optional<Mutation> optionalMutation = mutationRepository.findByIdAndUserId(id, user.getId());
        if (optionalMutation.isEmpty()) {
            throw new EntityNotFoundException("Mutation with id: " + id + " not found.");
        }

        Mutation mutation = optionalMutation.get();

        return mutation;
    }

    @Override
    public List<Mutation> getAll() {
        List<Mutation> mutations = mutationRepository.findAll();

        return mutations;
    }

    @Override
    public Mutation getOne(Long id) {
        Optional<Mutation> optionalMutation = mutationRepository.findById(id);
        if (optionalMutation.isEmpty()) {
            throw new EntityNotFoundException("Mutation with id: " + id + " not found.");
        }

        Mutation mutation = optionalMutation.get();

        return mutation;
    }

}
