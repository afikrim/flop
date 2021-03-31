package com.github.afikrim.flop.auth;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
public class AuthRequest implements Serializable {

    private String credential;
    private String password;

    public String getCredential() {
        return credential;
    }

    public String getPassword() {
        return password;
    }

}
