package com.eth.block.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "eth_blocks_uncles")
@Data
public class EthBlockUncleModel {
    @Id
    @Column(name="uncle_hash")
    private String uncleHash;//叔块hash
    @Column(name="block_number")
    private Long blockNumber;//区块高度
    @Column(name="created_at")
    private Date createdAt;//系统时间
    @Column(name="updated_at")
    private Date updatedAt;//系统时间

    public EthBlockUncleModel(String uncleHash, Long blockNumber) {
        this.uncleHash = uncleHash;
        this.blockNumber = blockNumber;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    public EthBlockUncleModel() {

    }

}
