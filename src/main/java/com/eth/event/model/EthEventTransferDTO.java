package com.eth.event.model;

import lombok.Data;

import java.math.BigInteger;
@Data
public class EthEventTransferDTO {
    private String from;
    private String to;
    private Integer count;
    private BigInteger sum;
}
