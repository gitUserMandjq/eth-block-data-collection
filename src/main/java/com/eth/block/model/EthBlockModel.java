package com.eth.block.model;

import com.eth.framework.base.common.utils.StringUtils;
import lombok.Data;
import org.web3j.protocol.core.methods.response.EthBlock;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "eth_blocks")
@Data
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
    @Column(updatable = false)
    private Date createdAt;//系统时间
    private Date updatedAt;//系统时间
    public EthBlockModel(EthBlock.Block block) {
        this.id = block.getNumber().longValue();
        this.blockNumber = block.getNumber().longValue();
        this.blockHash = block.getHash();
        this.parentHash = block.getParentHash();
        this.miner = block.getMiner();
        this.difficulty = block.getDifficulty().toString();
        this.totalDifficulty = block.getTotalDifficulty().toString();
        this.blockSize = block.getSize().toString();
        this.nonce = StringUtils.valueOf(block.getNonceRaw());
        this.extraData = block.getExtraData();
        this.gasLimit = block.getGasLimit().toString();
        this.gasUsed = block.getGasUsed().toString();
        this.timestamp = block.getTimestamp().longValue();
        this.txnCount = block.getTransactions().size();
        if(block.getBaseFeePerGasRaw() != null){
            this.baseFeePerGas = block.getBaseFeePerGas().toString();
            this.burntFee = block.getBaseFeePerGas().multiply(block.getGasUsed()).toString();
        }else{
            this.baseFeePerGas = "0";
            this.burntFee = "0";
        }
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    public EthBlockModel() {

    }

    @Override
    public String toString() {
        return "EthBlockModel{" +
                "id=" + id +
                ", blockNumber=" + blockNumber +
                ", blockHash='" + blockHash + '\'' +
                ", parentHash='" + parentHash + '\'' +
                ", miner='" + miner + '\'' +
                ", difficulty='" + difficulty + '\'' +
                ", totalDifficulty='" + totalDifficulty + '\'' +
                ", blockSize='" + blockSize + '\'' +
                ", nonce='" + nonce + '\'' +
                ", extraData='" + extraData + '\'' +
                ", gasLimit='" + gasLimit + '\'' +
                ", gasUsed='" + gasUsed + '\'' +
                ", timestamp=" + timestamp +
                ", txnCount=" + txnCount +
                ", baseFeePerGas='" + baseFeePerGas + '\'' +
                ", burntFee='" + burntFee + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
