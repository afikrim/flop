package com.github.afikrim.flop.accounts;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AccountRequest {

    @JsonProperty("username")
    private String username;

    @JsonProperty("password")
    private String password;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

}
