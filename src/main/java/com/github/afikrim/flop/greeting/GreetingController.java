package com.github.afikrim.flop.greeting;

import com.github.afikrim.flop.users.UserController;
import com.github.afikrim.flop.utils.response.Response;
import com.github.afikrim.flop.utils.response.ResponseCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping(value = "/", produces = {MediaType.APPLICATION_JSON_VALUE})
public class GreetingController {

    @Value("${spring.application.name}")
    private String appName;

    @Value("${spring.application.version}")
    private String appVersion;

    @GetMapping
    public ResponseEntity<Response<Greeting>> index() {
        Greeting greeting = new Greeting(appName, appVersion);

        Link users = linkTo(methodOn(UserController.class).index()).withRel("users");

        greeting.add(users);
        Response<Greeting> response = new Response<>(true, ResponseCode.HTTP_OK, "Hi There!", greeting);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
