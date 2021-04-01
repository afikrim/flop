package com.github.afikrim.flop.users;

import com.github.afikrim.flop.utils.response.Response;
import com.github.afikrim.flop.utils.response.ResponseCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping(value = "/users", produces = {MediaType.APPLICATION_JSON_VALUE})
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<?> index() {
        List<User> users = userService.getAll();
        Response<?> response = new Response<>(true, ResponseCode.HTTP_OK, "Successfully retrieved all users", users);

        Link store = linkTo(methodOn(this.getClass()).store(null)).withRel("store");

        response.add(store);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping
    public ResponseEntity<?> store(@RequestBody UserRequest userRequest) {
        User user = userService.store(userRequest);
        Response<?> response = new Response<>(true, ResponseCode.CREATED, "Successfully store new user", user);

        Link all = linkTo(methodOn(this.getClass()).index()).withRel("all");

        response.add(all);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        User user = userService.getOne(id);
        Response<?> response = new Response<>(true, ResponseCode.HTTP_OK, "Successfully retrieved user with id " + id,
                user);

        Link all = linkTo(methodOn(this.getClass()).index()).withRel("all");
        Link store = linkTo(methodOn(this.getClass()).store(null)).withRel("store");

        response.add(all);
        response.add(store);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody UserRequest userRequest) {
        User user = userService.updateOne(id, userRequest);
        Response<?> response = new Response<>(true, ResponseCode.HTTP_OK, "Successfully update user with id " + id, user);

        Link all = linkTo(methodOn(this.getClass()).index()).withRel("all");
        Link store = linkTo(methodOn(this.getClass()).store(null)).withRel("store");

        response.add(all);
        response.add(store);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> destroy(@PathVariable Long id) {
        User user = userService.destroyOne(id);
        Response<?> response = new Response<>(true, ResponseCode.HTTP_OK, "Successfully destroy user", user);

        Link all = linkTo(methodOn(this.getClass()).index()).withRel("all");
        Link store = linkTo(methodOn(this.getClass()).store(null)).withRel("store");

        response.add(all);
        response.add(store);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
