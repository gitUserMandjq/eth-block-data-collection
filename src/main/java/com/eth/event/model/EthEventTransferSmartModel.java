package com.eth.event.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigInteger;
import java.util.Date;

@Entity
@Table(name = "eth_event_transfer_smart")
@Data
public class EthEventTransferSmartModel {
    @Id
    @Column(name="id")
    private String id;//主键
    @Column(name="block_number")
    private Long blockNumber;//区块高度
    @Column(name="log_index")
    private Integer logIndex;//事件编号
    @Column(name="txn_hash")
    private String txnHash;//交易hash
    @Column(name="removed")
    private Integer removed;
    @Column(name="type")
    private String type;
    @Column(name="timestamp")
    private Date timestamp;//交易时间
    @Column(name="token_address")
    private String tokenAddress;//代币地址
    @Column(name="operator")
    private String operator = "";//操作人？
    @Column(name="from_address")
    private String fromAddress;//转出地址
    @Column(name="to_address")
    private String toAddress;//转入地址
    @Column(name="token_id")
    private String tokenId;//NFT编号
    @Column(name="token_value")
    private BigInteger tokenValue = BigInteger.valueOf(0);//交易数量
    @Column(name="created_at")
    private Date createdAt;
    @Column(name="updated_at")
    private Date updatedAt;
    @Column(name="method_type")
    private String methodType;//交易函数类型

    public EthEventTransferSmartModel(EthEventTransferModel model) {
        this.id = model.getId();
        this.blockNumber = model.getBlockNumber();
        this.logIndex = model.getLogIndex();
        this.txnHash = model.getTxnHash();
        this.removed = model.getRemoved();
        this.type = model.getType();
        this.timestamp = model.getTimestamp();
        this.tokenAddress = model.getTokenAddress();
        this.operator = model.getOperator();
        this.fromAddress = model.getFromAddress();
        this.toAddress = model.getToAddress();
        this.tokenId = model.getTokenId();
        this.tokenValue = model.getTokenValue();
        this.createdAt = model.getCreatedAt();
        this.updatedAt = model.getUpdatedAt();
        this.methodType = model.getMethodType();
    }

    public EthEventTransferSmartModel() {

    }

}
