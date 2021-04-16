package com.github.afikrim.flop.systemwallets;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class SystemWalletTopupRequest {

    private Long amount;

    public Long getAmount() {
        return amount;
    }

}
