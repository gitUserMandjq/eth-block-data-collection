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
    @Column(name="id")
    private Long id;//主键id
    @Column(name="block_number")
    private Long blockNumber;//区块高度
    @Column(name="block_hash")
    private String blockHash;//区块hash
    @Column(name="parent_hash")
    private String parentHash;//父块hash
    @Column(name="miner")
    private String miner;//挖矿账号
    @Column(name="difficulty")
    private String difficulty;//区块难度
    @Column(name="total_difficulty")
    private String totalDifficulty;//总体挖矿难度
    @Column(name="block_size")
    private String blockSize;//交易容量
    @Column(name="nonce")
    private String nonce;//区块随机数
    @Column(name="extra_data")
    private String extraData;//挖矿额外信息
    @Column(name="gas_limit")
    private String gasLimit;//区块GAS限制
    @Column(name="gas_used")
    private String gasUsed;//区块已消耗GAS
    @Column(name="timestamp")
    private Date timestamp;//区块产出时间
    @Column(name="txn_count")
    private Integer txnCount;//交易容量
    @Column(name="base_fee_per_gas")
    private String baseFeePerGas;//基础GAS费（销毁ETH）
    @Column(name="burnt_fee")
    private String burntFee;//销毁ETH
    @Column(name="created_at")
    private Date createdAt;//系统时间
    @Column(name="updated_at")
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
        this.timestamp = new Date(block.getTimestamp().longValue() * 1000L);
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
