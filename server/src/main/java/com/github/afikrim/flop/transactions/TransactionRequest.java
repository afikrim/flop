package com.github.afikrim.flop.transactions;

public class TransactionRequest {

    private Long destination;

    private Integer source;

    private Long amount;

    public TransactionRequest() {
    }

    public TransactionRequest(Long destination, Integer source, Long amount) {
        this.destination = destination;
        this.source = source;
        this.amount = amount;
    }

    public Long getDestination() {
        return destination;
    }

    public Integer getSource() {
        return source;
    }

    public Long getAmount() {
        return amount;
    }

}
