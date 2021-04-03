package com.github.afikrim.flop.auth;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
public class AuthRequest implements Serializable {

    private static final long serialVersionUID = -4917677990502824858L;

    private String credential;
    private String password;

    public String getCredential() {
        return credential;
    }

    public String getPassword() {
        return password;
    }

}
