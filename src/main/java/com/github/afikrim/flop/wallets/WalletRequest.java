package com.github.afikrim.flop.wallets;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class WalletRequest {

    private String code;
    private String name;
    private Boolean enabled;

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public Boolean getEnabled() {
        return enabled;
    }

}
