package com.eth.event.model;

import lombok.Data;

@Data
public class EthSmartAddressVO {
    public EthSmartAddressVO(String address, String contractAddress) {
        this.address = address;
        this.contractAddress = contractAddress;
    }
    public EthSmartAddressVO(String address, String contractAddress, String contractName, String contractLogo) {
        this.address = address;
        this.contractAddress = contractAddress;
        this.contractName = contractName;
        this.contractLogo = contractLogo;
    }

    public EthSmartAddressVO() {
    }

    //地址
    private String address;
    //合约地址
    private String contractAddress;
    //合约名称
    private String contractName;
    //合约Logo
    private String contractLogo;
}
