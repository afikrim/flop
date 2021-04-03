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
@RequestMapping(value = "/v1/users", produces = {MediaType.APPLICATION_JSON_VALUE})
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<Response<List<User>>> index() {
        List<User> users = userService.getAll();
        Response<List<User>> response = new Response<>(true, ResponseCode.HTTP_OK, "Successfully retrieved all users", users);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Response<User>> get(@PathVariable Long id) {
        User user = userService.getOne(id);
        Response<User> response = new Response<>(true, ResponseCode.HTTP_OK, "Successfully retrieved user with id " + id,
                user);

        Link all = linkTo(methodOn(this.getClass()).index()).withRel("all");
        response.add(all);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Response<User>> destroy(@PathVariable Long id) {
        User user = userService.destroyOne(id);
        Response<User> response = new Response<>(true, ResponseCode.HTTP_OK, "Successfully destroy user", user);

        Link all = linkTo(methodOn(this.getClass()).index()).withRel("all");
        response.add(all);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
