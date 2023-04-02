package com.eth.event.model;

import lombok.Data;

@Data
public class EthSmartAddressVO {
    public EthSmartAddressVO(String address) {
        this.address = address;
    }

    public EthSmartAddressVO() {
    }

    //地址
    private String address;
}
