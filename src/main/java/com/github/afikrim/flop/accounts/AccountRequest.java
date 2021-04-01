package com.github.afikrim.flop.accounts;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AccountRequest {

    @JsonProperty("username")
    private final String username;

    @JsonProperty("password")
    private final String password;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

}
