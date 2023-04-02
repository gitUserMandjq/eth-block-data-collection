package com.eth.account.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "eth_account_smart_contract")
@Data
public class EthAccountSmartContractModel {
    @Id
    @Column(name="id")
    private String id;//主键
    @Column(name="token_address")
    private String tokenAddress;//代币地址
    @Column(name="address")
    private String address;//钱包地址
    @Column(name="created_at")
    private Date createdAt;
    @Column(name="updated_at")
    private Date updatedAt;
}
