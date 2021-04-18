package com.github.afikrim.flop.wallets;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.afikrim.flop.systemwallets.SystemWalletRequest;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class WalletRequest {

    private String code;
    private String name;
    private Boolean enabled;

    @JsonProperty("system_wallet")
    private Optional<SystemWalletRequest> systemWallet;

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public Optional<SystemWalletRequest> getSystemWallet() {
        return systemWallet;
    }

}
