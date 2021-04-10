package com.github.afikrim.flop.systemwallets;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SystemWalletRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("wallet_id")
    private Integer walletId;

    private String phone;

    private String name;

    public Integer getWalletId() {
        return walletId;
    }

    public String getPhone() {
        return phone;
    }

    public String getName() {
        return name;
    }

}
