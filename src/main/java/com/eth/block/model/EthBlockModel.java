package com.eth.block.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "base_user", catalog = "highnetcloud")
@Getter
@Setter
public class EthBlockModel {
    @Id
    private Long id;//主键id
    private Long blockNumber;//区块高度
    private String blockHash;//区块hash
    private String parentHash;//父块hash
    private String miner;//挖矿账号
    private String difficulty;//区块难度
    private String totalDifficulty;//总体挖矿难度
    private String blockSize;//交易容量
    private String nonce;//区块随机数
    private String extraData;//挖矿额外信息
    private String gasLimit;//区块GAS限制
    private String gasUsed;//区块已消耗GAS
    private Long timestamp;//区块产出时间
    private Integer txnCount;//交易容量
    private String baseFeePerGas;//基础GAS费（销毁ETH）
    private String burntFee;//销毁ETH
    private Date createdAt;//系统时间
    private Date updatedAt;//系统时间

}
