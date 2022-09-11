package com.eth.block.model;

import com.eth.framework.base.utils.StringUtils;
import lombok.Data;
import org.web3j.protocol.core.methods.response.EthBlock;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "eth_blocks_uncles")
@Data
public class EthBlockUncleModel {
    @Id
    private String uncleHash;//叔块hash
    private Long blockNumber;//区块高度
    private Date createdAt;//系统时间
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
