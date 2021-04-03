package com.github.afikrim.flop;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.github.afikrim.flop.accounts.AccountRepository;
import com.github.afikrim.flop.auth.AuthController;
import com.github.afikrim.flop.users.UserController;
import com.github.afikrim.flop.users.UserRepository;
import com.github.afikrim.flop.utils.response.ResponseCode;

import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.Link;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
public class UserHttpTests {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    @AfterEach
    public void emptyDatabase() throws Exception {
        accountRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void getAllUsers() throws Exception {
        Link expectedRegister = linkTo(methodOn(AuthController.class).register(null)).withRel("register");

        mockMvc.perform(get("/users")).andDo(print()).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("success").isNotEmpty())
                .andExpect(jsonPath("success").isBoolean())
                .andExpect(jsonPath("success").value(true))
                .andExpect(jsonPath("code").isNotEmpty())
                .andExpect(jsonPath("code").isString())
                .andExpect(jsonPath("code").value(ResponseCode.HTTP_OK.toString()))
                .andExpect(jsonPath("message").isNotEmpty())
                .andExpect(jsonPath("message").isString())
                .andExpect(jsonPath("message").value("Successfully retrieved all users"))
                .andExpect(jsonPath("data").isEmpty())
                .andExpect(jsonPath("data").isArray())
                .andExpect(jsonPath("_links").isNotEmpty())
                .andExpect(jsonPath("_links").isMap())
                .andExpect(jsonPath("_links.register").isNotEmpty())
                .andExpect(jsonPath("_links.register").isMap())
                .andExpect(jsonPath("_links.register.href").isNotEmpty())
                .andExpect(jsonPath("_links.register.href").isString())
                .andExpect(jsonPath("_links.register.href").value(expectedRegister.getHref()));
    }

}
