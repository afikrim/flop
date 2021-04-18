package com.github.afikrim.flop.mutations;

import java.util.List;

import com.github.afikrim.flop.users.User;
import com.github.afikrim.flop.users.UserService;
import com.github.afikrim.flop.utils.response.Response;
import com.github.afikrim.flop.utils.response.ResponseCode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(produces = { MediaType.APPLICATION_JSON_VALUE })
public class MutationController {

    @Autowired
    MutationService mutationService;

    @Autowired
    UserService userService;

    @GetMapping(value = "/v1/{userId}/mutations")
    public ResponseEntity<Response<List<Mutation>>> allByUser(@PathVariable Long userId) {
        User user = userService.getOne(userId);

        List<Mutation> mutations = mutationService.getAllByUser(user);
        Response<List<Mutation>> response = new Response<>(true, ResponseCode.HTTP_OK, "Successfully get all your mutations", mutations);

        return ResponseEntity.ok().body(response);
    }

    @GetMapping(value = "/v1/{userId}/mutations/{id}")
    public ResponseEntity<Response<Mutation>> oneByUser(@PathVariable Long userId, @PathVariable Long id) {
        User user = userService.getOne(userId);

        Mutation mutation = mutationService.getOneByUser(user, id);
        Response<Mutation> response = new Response<>(true, ResponseCode.HTTP_OK, "Successfully get all your mutations", mutation);

        return ResponseEntity.ok().body(response);
    }

    @GetMapping(value = "/v1/mutations")
    public ResponseEntity<Response<List<Mutation>>> all() {
        List<Mutation> mutations = mutationService.getAll();
        Response<List<Mutation>> response = new Response<>(true, ResponseCode.HTTP_OK, "Successfully get all your mutations", mutations);

        return ResponseEntity.ok().body(response);
    }

    @GetMapping(value = "/v1/mutations/{id}")
    public ResponseEntity<Response<Mutation>> one(@PathVariable Long id) {
        Mutation mutation = mutationService.getOne(id);
        Response<Mutation> response = new Response<>(true, ResponseCode.HTTP_OK, "Successfully get all your mutations", mutation);

        return ResponseEntity.ok().body(response);
    }

}
