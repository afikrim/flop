package com.github.afikrim.flop.users;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.afikrim.flop.accounts.AccountRequest;
import lombok.AllArgsConstructor;

import java.util.Optional;

@AllArgsConstructor
public class UserRequest {

    @JsonProperty("fullname")
    private final String fullname;

    @JsonProperty("email")
    private final String email;

    @JsonProperty("phone")
    private final String phone;

    @JsonProperty("account")
    private final Optional<AccountRequest> account;

    public String getFullname() {
        return fullname;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public Optional<AccountRequest> getAccount() {
        return account;
    }

}
