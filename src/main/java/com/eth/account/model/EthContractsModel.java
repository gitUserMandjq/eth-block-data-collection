package com.eth.account.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "eth_contracts")
@Data
public class EthContractsModel {
    @Id
    @Column(name="address")
    private String address;//合约地址
    @Column(name="block_number")
    private Long blockNumber;//创建区块高度
    @Column(name="timestamp")
    private Long timestamp;//合约创建时间
    @Column(name="txn_hash")
    private String txnHash;//创建交易
    @Column(name="creater")
    private String creater;//合约所有者
    @Column(name="name")
    private String name;//合约名称
    @Column(name="symbol")
    private String symbol;//合约符号
    @Column(name="decimals")
    private String decimals = "0";//合约精度
    @Column(name="logo")
    private String logo;//合约LOGO
    @Column(name="code")
    private String code;//合约code
    @Column(name="abi")
    private String abi;//合约abi
    @Column(name="type")
    private String type;//合约标准
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name="created_at")
    private Date createdAt = new Date();
    @Column(name="updated_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updatedAt = new Date();
    public static final String TYPE_ERC20 = "erc20";
    public static final String TYPE_ERC721 = "erc721";
    public static final String TYPE_ERC1155 = "erc1155";
    public static final String TYPE_ANY = "any";
}
