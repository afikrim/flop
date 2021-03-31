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
        Link expectedStore = linkTo(methodOn(UserController.class).store(null)).withRel("store");

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
                .andExpect(jsonPath("_links.store").isNotEmpty())
                .andExpect(jsonPath("_links.store").isMap())
                .andExpect(jsonPath("_links.store.href").isNotEmpty())
                .andExpect(jsonPath("_links.store.href").isString())
                .andExpect(jsonPath("_links.store.href").value(expectedStore.getHref()));
    }

    @Test
    public void storeNew() throws Exception {
        String fullname = "Aziz Fikri";
        String email = "afikrim10@gmail.com";
        String phone = "0818349123";
        String username = "afikrim10";
        String password = "P@ssw0rd!";

        StringBuilder sb = new StringBuilder("{");
        sb.append("\"fullname\": \"" + fullname + "\",");
        sb.append("\"email\": \"" + email + "\",");
        sb.append("\"phone\": \"" + phone + "\",");
        sb.append("\"account\": {");
        sb.append("\"username\": \"" + username + "\",");
        sb.append("\"password\": \"" + password + "\"");
        sb.append("}");
        sb.append("}");

        Link expectedAll = linkTo(methodOn(UserController.class).index()).withRel("all");

        mockMvc.perform(
            post("/users")
                    .content(sb.toString())
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
        )
                .andDo(print()).andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("success").isNotEmpty())
                .andExpect(jsonPath("success").isBoolean())
                .andExpect(jsonPath("success").value(true))
                .andExpect(jsonPath("code").isNotEmpty())
                .andExpect(jsonPath("code").isString())
                .andExpect(jsonPath("code").value(ResponseCode.CREATED.toString()))
                .andExpect(jsonPath("message").isNotEmpty())
                .andExpect(jsonPath("message").isString())
                .andExpect(jsonPath("message").value("Successfully store new user"))
                .andExpect(jsonPath("data").isNotEmpty())
                .andExpect(jsonPath("data").isMap())
                .andExpect(jsonPath("data.id").isNotEmpty())
                .andExpect(jsonPath("data.id").isNumber())
                .andExpect(jsonPath("data.fullname").isNotEmpty())
                .andExpect(jsonPath("data.fullname").isString())
                .andExpect(jsonPath("data.fullname").value(fullname))
                .andExpect(jsonPath("data.email").isNotEmpty())
                .andExpect(jsonPath("data.email").isString())
                .andExpect(jsonPath("data.email").value(email))
                .andExpect(jsonPath("data.phone").isNotEmpty())
                .andExpect(jsonPath("data.phone").isString())
                .andExpect(jsonPath("data.phone").value(phone))
                .andExpect(jsonPath("data.account").isNotEmpty())
                .andExpect(jsonPath("data.account").isMap())
                .andExpect(jsonPath("data.account.id").isNotEmpty())
                .andExpect(jsonPath("data.account.id").isNumber())
                .andExpect(jsonPath("data.account.username").isNotEmpty())
                .andExpect(jsonPath("data.account.username").isString())
                .andExpect(jsonPath("data.account.username").value(username))
                .andExpect(jsonPath("data.created_at").isNotEmpty())
                .andExpect(jsonPath("data.created_at").isString())
                .andExpect(jsonPath("data.updated_at").isNotEmpty())
                .andExpect(jsonPath("data.updated_at").isString())
                .andExpect(jsonPath("data._links").isNotEmpty())
                .andExpect(jsonPath("data._links").isMap())
                .andExpect(jsonPath("data._links.self").isNotEmpty())
                .andExpect(jsonPath("data._links.self").isMap())
                .andExpect(jsonPath("data._links.self.href").isNotEmpty())
                .andExpect(jsonPath("data._links.self.href").isString())
                .andExpect(jsonPath("data._links.update").isNotEmpty())
                .andExpect(jsonPath("data._links.update").isMap())
                .andExpect(jsonPath("data._links.update.href").isNotEmpty())
                .andExpect(jsonPath("data._links.update.href").isString())
                .andExpect(jsonPath("data._links.delete").isNotEmpty())
                .andExpect(jsonPath("data._links.delete").isMap())
                .andExpect(jsonPath("data._links.delete.href").isNotEmpty())
                .andExpect(jsonPath("data._links.delete.href").isString())
                .andExpect(jsonPath("_links").isNotEmpty())
                .andExpect(jsonPath("_links").isMap())
                .andExpect(jsonPath("_links.all").isNotEmpty())
                .andExpect(jsonPath("_links.all").isMap())
                .andExpect(jsonPath("_links.all.href").isNotEmpty())
                .andExpect(jsonPath("_links.all.href").isString())
                .andExpect(jsonPath("_links.all.href").value(expectedAll.getHref()));
    }

    @Test
    public void storeNewAndGetUser() throws Exception {
        String fullname = "Aziz Fikri";
        String email = "afikrim10@gmail.com";
        String phone = "0818349123";
        String username = "afikrim10";
        String password = "P@ssw0rd!";

        StringBuilder sb = new StringBuilder("{");
        sb.append("\"fullname\": \"" + fullname + "\",");
        sb.append("\"email\": \"" + email + "\",");
        sb.append("\"phone\": \"" + phone + "\",");
        sb.append("\"account\": {");
        sb.append("\"username\": \"" + username + "\",");
        sb.append("\"password\": \"" + password + "\"");
        sb.append("}");
        sb.append("}");

        Link expectedAll = linkTo(methodOn(UserController.class).index()).withRel("all");
        Link expectedStore = linkTo(methodOn(UserController.class).store(null)).withRel("store");

        MvcResult storeResult = mockMvc.perform(
            post("/users")
                    .content(sb.toString())
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
        )
                .andDo(print()).andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("success").isNotEmpty())
                .andExpect(jsonPath("success").isBoolean())
                .andExpect(jsonPath("success").value(true))
                .andExpect(jsonPath("code").isNotEmpty())
                .andExpect(jsonPath("code").isString())
                .andExpect(jsonPath("code").value(ResponseCode.CREATED.toString()))
                .andExpect(jsonPath("message").isNotEmpty())
                .andExpect(jsonPath("message").isString())
                .andExpect(jsonPath("message").value("Successfully store new user"))
                .andExpect(jsonPath("data").isNotEmpty())
                .andExpect(jsonPath("data").isMap())
                .andExpect(jsonPath("data.id").isNotEmpty())
                .andExpect(jsonPath("data.id").isNumber())
                .andExpect(jsonPath("data.fullname").isNotEmpty())
                .andExpect(jsonPath("data.fullname").isString())
                .andExpect(jsonPath("data.fullname").value(fullname))
                .andExpect(jsonPath("data.email").isNotEmpty())
                .andExpect(jsonPath("data.email").isString())
                .andExpect(jsonPath("data.email").value(email))
                .andExpect(jsonPath("data.phone").isNotEmpty())
                .andExpect(jsonPath("data.phone").isString())
                .andExpect(jsonPath("data.phone").value(phone))
                .andExpect(jsonPath("data.account").isNotEmpty())
                .andExpect(jsonPath("data.account").isMap())
                .andExpect(jsonPath("data.account.id").isNotEmpty())
                .andExpect(jsonPath("data.account.id").isNumber())
                .andExpect(jsonPath("data.account.username").isNotEmpty())
                .andExpect(jsonPath("data.account.username").isString())
                .andExpect(jsonPath("data.account.username").value(username))
                .andExpect(jsonPath("data.created_at").isNotEmpty())
                .andExpect(jsonPath("data.created_at").isString())
                .andExpect(jsonPath("data.updated_at").isNotEmpty())
                .andExpect(jsonPath("data.updated_at").isString())
                .andExpect(jsonPath("data._links").isNotEmpty())
                .andExpect(jsonPath("data._links").isMap())
                .andExpect(jsonPath("data._links.self").isNotEmpty())
                .andExpect(jsonPath("data._links.self").isMap())
                .andExpect(jsonPath("data._links.self.href").isNotEmpty())
                .andExpect(jsonPath("data._links.self.href").isString())
                .andExpect(jsonPath("data._links.update").isNotEmpty())
                .andExpect(jsonPath("data._links.update").isMap())
                .andExpect(jsonPath("data._links.update.href").isNotEmpty())
                .andExpect(jsonPath("data._links.update.href").isString())
                .andExpect(jsonPath("data._links.delete").isNotEmpty())
                .andExpect(jsonPath("data._links.delete").isMap())
                .andExpect(jsonPath("data._links.delete.href").isNotEmpty())
                .andExpect(jsonPath("data._links.delete.href").isString())
                .andExpect(jsonPath("_links").isNotEmpty())
                .andExpect(jsonPath("_links").isMap())
                .andExpect(jsonPath("_links.all").isNotEmpty())
                .andExpect(jsonPath("_links.all").isMap())
                .andExpect(jsonPath("_links.all.href").isNotEmpty())
                .andExpect(jsonPath("_links.all.href").isString())
                .andExpect(jsonPath("_links.all.href").value(expectedAll.getHref()))
                .andReturn();

        String storeResultJsonString = storeResult.getResponse().getContentAsString();
        JSONObject storeResultJsonObject = new JSONObject(storeResultJsonString);

        Long userId = storeResultJsonObject.getJSONObject("data").getLong("id");
        String selfUrl = storeResultJsonObject.getJSONObject("data").getJSONObject("_links").getJSONObject("self").getString("href");
        mockMvc.perform(get(selfUrl)).andDo(print()).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("success").isNotEmpty())
                .andExpect(jsonPath("success").isBoolean())
                .andExpect(jsonPath("success").value(true))
                .andExpect(jsonPath("code").isNotEmpty())
                .andExpect(jsonPath("code").isString())
                .andExpect(jsonPath("code").value(ResponseCode.HTTP_OK.toString()))
                .andExpect(jsonPath("message").isNotEmpty())
                .andExpect(jsonPath("message").isString())
                .andExpect(jsonPath("message").value("Successfully retrieved user with id " + userId))
                .andExpect(jsonPath("data").isNotEmpty())
                .andExpect(jsonPath("data").isMap())
                .andExpect(jsonPath("data.id").isNotEmpty())
                .andExpect(jsonPath("data.id").isNumber())
                .andExpect(jsonPath("data.fullname").isNotEmpty())
                .andExpect(jsonPath("data.fullname").isString())
                .andExpect(jsonPath("data.fullname").value(fullname))
                .andExpect(jsonPath("data.email").isNotEmpty())
                .andExpect(jsonPath("data.email").isString())
                .andExpect(jsonPath("data.email").value(email))
                .andExpect(jsonPath("data.phone").isNotEmpty())
                .andExpect(jsonPath("data.phone").isString())
                .andExpect(jsonPath("data.phone").value(phone))
                .andExpect(jsonPath("data.account").isNotEmpty())
                .andExpect(jsonPath("data.account").isMap())
                .andExpect(jsonPath("data.account.id").isNotEmpty())
                .andExpect(jsonPath("data.account.id").isNumber())
                .andExpect(jsonPath("data.account.username").isNotEmpty())
                .andExpect(jsonPath("data.account.username").isString())
                .andExpect(jsonPath("data.account.username").value(username))
                .andExpect(jsonPath("data.created_at").isNotEmpty())
                .andExpect(jsonPath("data.created_at").isString())
                .andExpect(jsonPath("data.updated_at").isNotEmpty())
                .andExpect(jsonPath("data.updated_at").isString())
                .andExpect(jsonPath("data._links").isNotEmpty())
                .andExpect(jsonPath("data._links").isMap())
                .andExpect(jsonPath("data._links.update").isNotEmpty())
                .andExpect(jsonPath("data._links.update").isMap())
                .andExpect(jsonPath("data._links.update.href").isNotEmpty())
                .andExpect(jsonPath("data._links.update.href").isString())
                .andExpect(jsonPath("data._links.delete").isNotEmpty())
                .andExpect(jsonPath("data._links.delete").isMap())
                .andExpect(jsonPath("data._links.delete.href").isNotEmpty())
                .andExpect(jsonPath("data._links.delete.href").isString())
                .andExpect(jsonPath("_links").isNotEmpty())
                .andExpect(jsonPath("_links").isMap())
                .andExpect(jsonPath("_links.all").isNotEmpty())
                .andExpect(jsonPath("_links.all").isMap())
                .andExpect(jsonPath("_links.all.href").isNotEmpty())
                .andExpect(jsonPath("_links.all.href").isString())
                .andExpect(jsonPath("_links.all.href").value(expectedAll.getHref()))
                .andExpect(jsonPath("_links.store").isNotEmpty())
                .andExpect(jsonPath("_links.store").isMap())
                .andExpect(jsonPath("_links.store.href").isNotEmpty())
                .andExpect(jsonPath("_links.store.href").isString())
                .andExpect(jsonPath("_links.store.href").value(expectedStore.getHref()));
    }

    @Test
    public void storeAndUpdateUser() throws Exception {
        String fullname = "Aziz Fikri";
        String email = "afikrim10@gmail.com";
        String phone = "0818349123";
        String username = "afikrim10";
        String password = "P@ssw0rd!";

        StringBuilder sb = new StringBuilder("{");
        sb.append("\"fullname\": \"" + fullname + "\",");
        sb.append("\"email\": \"" + email + "\",");
        sb.append("\"phone\": \"" + phone + "\",");
        sb.append("\"account\": {");
        sb.append("\"username\": \"" + username + "\",");
        sb.append("\"password\": \"" + password + "\"");
        sb.append("}");
        sb.append("}");

        Link expectedAll = linkTo(methodOn(UserController.class).index()).withRel("all");
        Link expectedStore = linkTo(methodOn(UserController.class).store(null)).withRel("store");

        MvcResult storeResult = mockMvc.perform(
            post("/users")
                    .content(sb.toString())
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
        )
                .andDo(print()).andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("success").isNotEmpty())
                .andExpect(jsonPath("success").isBoolean())
                .andExpect(jsonPath("success").value(true))
                .andExpect(jsonPath("code").isNotEmpty())
                .andExpect(jsonPath("code").isString())
                .andExpect(jsonPath("code").value(ResponseCode.CREATED.toString()))
                .andExpect(jsonPath("message").isNotEmpty())
                .andExpect(jsonPath("message").isString())
                .andExpect(jsonPath("message").value("Successfully store new user"))
                .andExpect(jsonPath("data").isNotEmpty())
                .andExpect(jsonPath("data").isMap())
                .andExpect(jsonPath("data.id").isNotEmpty())
                .andExpect(jsonPath("data.id").isNumber())
                .andExpect(jsonPath("data.fullname").isNotEmpty())
                .andExpect(jsonPath("data.fullname").isString())
                .andExpect(jsonPath("data.fullname").value(fullname))
                .andExpect(jsonPath("data.email").isNotEmpty())
                .andExpect(jsonPath("data.email").isString())
                .andExpect(jsonPath("data.email").value(email))
                .andExpect(jsonPath("data.phone").isNotEmpty())
                .andExpect(jsonPath("data.phone").isString())
                .andExpect(jsonPath("data.phone").value(phone))
                .andExpect(jsonPath("data.account").isNotEmpty())
                .andExpect(jsonPath("data.account").isMap())
                .andExpect(jsonPath("data.account.id").isNotEmpty())
                .andExpect(jsonPath("data.account.id").isNumber())
                .andExpect(jsonPath("data.account.username").isNotEmpty())
                .andExpect(jsonPath("data.account.username").isString())
                .andExpect(jsonPath("data.account.username").value(username))
                .andExpect(jsonPath("data.created_at").isNotEmpty())
                .andExpect(jsonPath("data.created_at").isString())
                .andExpect(jsonPath("data.updated_at").isNotEmpty())
                .andExpect(jsonPath("data.updated_at").isString())
                .andExpect(jsonPath("data._links").isNotEmpty())
                .andExpect(jsonPath("data._links").isMap())
                .andExpect(jsonPath("data._links.self").isNotEmpty())
                .andExpect(jsonPath("data._links.self").isMap())
                .andExpect(jsonPath("data._links.self.href").isNotEmpty())
                .andExpect(jsonPath("data._links.self.href").isString())
                .andExpect(jsonPath("data._links.update").isNotEmpty())
                .andExpect(jsonPath("data._links.update").isMap())
                .andExpect(jsonPath("data._links.update.href").isNotEmpty())
                .andExpect(jsonPath("data._links.update.href").isString())
                .andExpect(jsonPath("data._links.delete").isNotEmpty())
                .andExpect(jsonPath("data._links.delete").isMap())
                .andExpect(jsonPath("data._links.delete.href").isNotEmpty())
                .andExpect(jsonPath("data._links.delete.href").isString())
                .andExpect(jsonPath("_links").isNotEmpty())
                .andExpect(jsonPath("_links").isMap())
                .andExpect(jsonPath("_links.all").isNotEmpty())
                .andExpect(jsonPath("_links.all").isMap())
                .andExpect(jsonPath("_links.all.href").isNotEmpty())
                .andExpect(jsonPath("_links.all.href").isString())
                .andExpect(jsonPath("_links.all.href").value(expectedAll.getHref()))
                .andReturn();

        fullname = "Fikri Mahmudi";
        email = "fikri@gmail.com";

        StringBuilder sb2 = new StringBuilder("{");
        sb2.append("\"fullname\": \"" + fullname + "\",");
        sb2.append("\"email\": \"" + email + "\",");
        sb2.append("\"phone\": \"" + phone + "\",");
        sb2.append("\"account\": {");
        sb2.append("\"username\": \"" + username + "\",");
        sb2.append("\"password\": \"" + password + "\"");
        sb2.append("}");
        sb2.append("}");

        String storeResultJsonString = storeResult.getResponse().getContentAsString();
        JSONObject storeResultJsonObject = new JSONObject(storeResultJsonString);

        Long userId = storeResultJsonObject.getJSONObject("data").getLong("id");
        String updateUrl = storeResultJsonObject.getJSONObject("data").getJSONObject("_links").getJSONObject("update").getString("href");

        mockMvc.perform(
            put(updateUrl)
                    .content(sb2.toString())
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
        )
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("success").isNotEmpty())
                .andExpect(jsonPath("success").isBoolean())
                .andExpect(jsonPath("success").value(true))
                .andExpect(jsonPath("code").isNotEmpty())
                .andExpect(jsonPath("code").isString())
                .andExpect(jsonPath("code").value(ResponseCode.HTTP_OK.toString()))
                .andExpect(jsonPath("message").isNotEmpty())
                .andExpect(jsonPath("message").isString())
                .andExpect(jsonPath("message").value("Successfully update user with id " + userId))
                .andExpect(jsonPath("data").isNotEmpty())
                .andExpect(jsonPath("data").isMap())
                .andExpect(jsonPath("data.id").isNotEmpty())
                .andExpect(jsonPath("data.id").isNumber())
                .andExpect(jsonPath("data.fullname").isNotEmpty())
                .andExpect(jsonPath("data.fullname").isString())
                .andExpect(jsonPath("data.fullname").value(fullname))
                .andExpect(jsonPath("data.email").isNotEmpty())
                .andExpect(jsonPath("data.email").isString())
                .andExpect(jsonPath("data.email").value(email))
                .andExpect(jsonPath("data.phone").isNotEmpty())
                .andExpect(jsonPath("data.phone").isString())
                .andExpect(jsonPath("data.phone").value(phone))
                .andExpect(jsonPath("data.account").isNotEmpty())
                .andExpect(jsonPath("data.account").isMap())
                .andExpect(jsonPath("data.account.id").isNotEmpty())
                .andExpect(jsonPath("data.account.id").isNumber())
                .andExpect(jsonPath("data.account.username").isNotEmpty())
                .andExpect(jsonPath("data.account.username").isString())
                .andExpect(jsonPath("data.account.username").value(username))
                .andExpect(jsonPath("data.created_at").isNotEmpty())
                .andExpect(jsonPath("data.created_at").isString())
                .andExpect(jsonPath("data.updated_at").isNotEmpty())
                .andExpect(jsonPath("data.updated_at").isString())
                .andExpect(jsonPath("data._links").isNotEmpty())
                .andExpect(jsonPath("data._links").isMap())
                .andExpect(jsonPath("data._links.self").isNotEmpty())
                .andExpect(jsonPath("data._links.self").isMap())
                .andExpect(jsonPath("data._links.self.href").isNotEmpty())
                .andExpect(jsonPath("data._links.self.href").isString())
                .andExpect(jsonPath("data._links.delete").isNotEmpty())
                .andExpect(jsonPath("data._links.delete").isMap())
                .andExpect(jsonPath("data._links.delete.href").isNotEmpty())
                .andExpect(jsonPath("data._links.delete.href").isString())
                .andExpect(jsonPath("_links").isNotEmpty())
                .andExpect(jsonPath("_links").isMap())
                .andExpect(jsonPath("_links.all").isNotEmpty())
                .andExpect(jsonPath("_links.all").isMap())
                .andExpect(jsonPath("_links.all.href").isNotEmpty())
                .andExpect(jsonPath("_links.all.href").isString())
                .andExpect(jsonPath("_links.all.href").value(expectedAll.getHref()))
                .andExpect(jsonPath("_links.store").isNotEmpty())
                .andExpect(jsonPath("_links.store").isMap())
                .andExpect(jsonPath("_links.store.href").isNotEmpty())
                .andExpect(jsonPath("_links.store.href").isString())
                .andExpect(jsonPath("_links.store.href").value(expectedStore.getHref()));
    }

    @Test
    public void storeAndDeleteUser() throws Exception {
        String fullname = "Aziz Fikri";
        String email = "afikrim10@gmail.com";
        String phone = "0818349123";
        String username = "afikrim10";
        String password = "P@ssw0rd!";

        StringBuilder sb = new StringBuilder("{");
        sb.append("\"fullname\": \"" + fullname + "\",");
        sb.append("\"email\": \"" + email + "\",");
        sb.append("\"phone\": \"" + phone + "\",");
        sb.append("\"account\": {");
        sb.append("\"username\": \"" + username + "\",");
        sb.append("\"password\": \"" + password + "\"");
        sb.append("}");
        sb.append("}");

        Link expectedAll = linkTo(methodOn(UserController.class).index()).withRel("all");
        Link expectedStore = linkTo(methodOn(UserController.class).store(null)).withRel("store");

        MvcResult storeResult = mockMvc.perform(
            post("/users")
                    .content(sb.toString())
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
        )
                .andDo(print()).andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("success").isNotEmpty())
                .andExpect(jsonPath("success").isBoolean())
                .andExpect(jsonPath("success").value(true))
                .andExpect(jsonPath("code").isNotEmpty())
                .andExpect(jsonPath("code").isString())
                .andExpect(jsonPath("code").value(ResponseCode.CREATED.toString()))
                .andExpect(jsonPath("message").isNotEmpty())
                .andExpect(jsonPath("message").isString())
                .andExpect(jsonPath("message").value("Successfully store new user"))
                .andExpect(jsonPath("data").isNotEmpty())
                .andExpect(jsonPath("data").isMap())
                .andExpect(jsonPath("data.id").isNotEmpty())
                .andExpect(jsonPath("data.id").isNumber())
                .andExpect(jsonPath("data.fullname").isNotEmpty())
                .andExpect(jsonPath("data.fullname").isString())
                .andExpect(jsonPath("data.fullname").value(fullname))
                .andExpect(jsonPath("data.email").isNotEmpty())
                .andExpect(jsonPath("data.email").isString())
                .andExpect(jsonPath("data.email").value(email))
                .andExpect(jsonPath("data.phone").isNotEmpty())
                .andExpect(jsonPath("data.phone").isString())
                .andExpect(jsonPath("data.phone").value(phone))
                .andExpect(jsonPath("data.account").isNotEmpty())
                .andExpect(jsonPath("data.account").isMap())
                .andExpect(jsonPath("data.account.id").isNotEmpty())
                .andExpect(jsonPath("data.account.id").isNumber())
                .andExpect(jsonPath("data.account.username").isNotEmpty())
                .andExpect(jsonPath("data.account.username").isString())
                .andExpect(jsonPath("data.account.username").value(username))
                .andExpect(jsonPath("data.created_at").isNotEmpty())
                .andExpect(jsonPath("data.created_at").isString())
                .andExpect(jsonPath("data.updated_at").isNotEmpty())
                .andExpect(jsonPath("data.updated_at").isString())
                .andExpect(jsonPath("data._links").isNotEmpty())
                .andExpect(jsonPath("data._links").isMap())
                .andExpect(jsonPath("data._links.self").isNotEmpty())
                .andExpect(jsonPath("data._links.self").isMap())
                .andExpect(jsonPath("data._links.self.href").isNotEmpty())
                .andExpect(jsonPath("data._links.self.href").isString())
                .andExpect(jsonPath("data._links.update").isNotEmpty())
                .andExpect(jsonPath("data._links.update").isMap())
                .andExpect(jsonPath("data._links.update.href").isNotEmpty())
                .andExpect(jsonPath("data._links.update.href").isString())
                .andExpect(jsonPath("data._links.delete").isNotEmpty())
                .andExpect(jsonPath("data._links.delete").isMap())
                .andExpect(jsonPath("data._links.delete.href").isNotEmpty())
                .andExpect(jsonPath("data._links.delete.href").isString())
                .andExpect(jsonPath("_links").isNotEmpty())
                .andExpect(jsonPath("_links").isMap())
                .andExpect(jsonPath("_links.all").isNotEmpty())
                .andExpect(jsonPath("_links.all").isMap())
                .andExpect(jsonPath("_links.all.href").isNotEmpty())
                .andExpect(jsonPath("_links.all.href").isString())
                .andExpect(jsonPath("_links.all.href").value(expectedAll.getHref()))
                .andReturn();

        String storeResultJsonString = storeResult.getResponse().getContentAsString();
        JSONObject storeResultJsonObject = new JSONObject(storeResultJsonString);

        String deleteUrl = storeResultJsonObject.getJSONObject("data").getJSONObject("_links").getJSONObject("delete").getString("href");
        mockMvc.perform(delete(deleteUrl)).andDo(print()).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("success").isNotEmpty())
                .andExpect(jsonPath("success").isBoolean())
                .andExpect(jsonPath("success").value(true))
                .andExpect(jsonPath("code").isNotEmpty())
                .andExpect(jsonPath("code").isString())
                .andExpect(jsonPath("code").value(ResponseCode.HTTP_OK.toString()))
                .andExpect(jsonPath("message").isNotEmpty())
                .andExpect(jsonPath("message").isString())
                .andExpect(jsonPath("message").value("Successfully destroy user"))
                .andExpect(jsonPath("data").isEmpty())
                .andExpect(jsonPath("_links").isNotEmpty())
                .andExpect(jsonPath("_links").isMap())
                .andExpect(jsonPath("_links.all").isNotEmpty())
                .andExpect(jsonPath("_links.all").isMap())
                .andExpect(jsonPath("_links.all.href").isNotEmpty())
                .andExpect(jsonPath("_links.all.href").isString())
                .andExpect(jsonPath("_links.all.href").value(expectedAll.getHref()))
                .andExpect(jsonPath("_links.store").isNotEmpty())
                .andExpect(jsonPath("_links.store").isMap())
                .andExpect(jsonPath("_links.store.href").isNotEmpty())
                .andExpect(jsonPath("_links.store.href").isString())
                .andExpect(jsonPath("_links.store.href").value(expectedStore.getHref()));
    }

}
