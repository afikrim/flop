package com.github.afikrim.flop.transactions;

public class TransactionRequest {

    private Integer destination;
    
    private Long source;

    private Long amount;

    public Integer getDestination() {
        return destination;
    }

    public Long getSource() {
        return source;
    }

    public Long getAmount() {
        return amount;
    }

}
