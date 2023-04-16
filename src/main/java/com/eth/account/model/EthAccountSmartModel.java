package com.eth.account.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "eth_account_smart")
@Data
public class EthAccountSmartModel {
    @Id
    @Column(name="address")
    private String address;//钱包地址
    @Column(name="created_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdAt;
    @Column(name="updated_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updatedAt;
    //合约地址
    @Column(name="contract_address")
    private String contractAddress;
    //合约名称
    @Column(name="contract_name")
    private String contractName;
    //合约logo
    @Column(name="contract_logo")
    private String contractLogo;
    //交易类型
    @Column(name="trans_type")
    private String transType;
    //交易时间
    @Column(name="timestamp")
    private Date timestamp;
}
