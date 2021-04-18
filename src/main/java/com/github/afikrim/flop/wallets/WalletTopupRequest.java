package com.github.afikrim.flop.wallets;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class WalletTopupRequest {

    private Long amount;

    public Long getAmount() {
        return amount;
    }

}
