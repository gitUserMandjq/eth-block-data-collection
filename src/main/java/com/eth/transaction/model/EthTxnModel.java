package com.eth.transaction.model;

import com.eth.framework.base.common.utils.StringUtils;
import lombok.Data;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.utils.Numeric;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigInteger;
import java.util.Date;

@Entity
@Table(name = "eth_txns")
@Data
public class EthTxnModel {

    @Id
    private String txnHash;
    private Long blockNumber;
    private Integer txnIndex;
    private String fromAddress;
    private String toAddress;
    private BigInteger ethValue;
    private BigInteger gasUsed;
    private BigInteger gasPrice;
    private String gasFee;
    private Integer status;
    private BigInteger nonce;
    private Long timestamp;
    private String contractAddress;
    private BigInteger cumulativeGasUsed;
    private BigInteger effectiveGasPrice;
    private String input;
    private BigInteger maxFeePerGas;
    private BigInteger maxPriorityFeePerGas;
    private Integer isError;
    private String errMsg;
    private String methodId;
    private Integer type;
    private Integer logsNum;
    private Date createdAt;
    private Date updatedAt;

    public EthTxnModel(EthBlock.TransactionObject ta) {
        this.blockNumber = ta.getBlockNumber().longValue();
        this.txnHash = ta.getHash();
        this.txnIndex = ta.getTransactionIndex().intValue();
        this.fromAddress = ta.getFrom();
        this.toAddress = ta.getTo();
        this.ethValue = ta.getValue();
        this.gasUsed = BigInteger.valueOf(0);//从交易回执中获取
        this.gasPrice = ta.getGasPrice();
        this.gasFee = "0";//从交易回执中获取
        this.status = -1;//从交易回执中获取
        this.nonce = ta.getNonce();
        this.timestamp = timestamp;//从区块中获取
        this.contractAddress = "";//从交易回执中获取
        this.cumulativeGasUsed = BigInteger.valueOf(0);//从交易回执中获取
        this.effectiveGasPrice = BigInteger.valueOf(0);//从交易回执中获取
        this.input = ta.getInput();
        if(ta.getMaxFeePerGasRaw() != null){
            this.maxFeePerGas = ta.getMaxFeePerGas();
        }else{
            this.maxFeePerGas = BigInteger.valueOf(0);
        }
        if(ta.getMaxPriorityFeePerGasRaw() != null){
            this.maxPriorityFeePerGas = ta.getMaxPriorityFeePerGas();
        }else{
            this.maxPriorityFeePerGas = BigInteger.valueOf(0);
        }
        this.isError = 2;
        this.errMsg = "";
        if(!StringUtils.isEmpty(ta.getInput()) && !ta.getInput().equals("0x")){
            if(ta.getInput().length() > 10){
                this.methodId = ta.getInput().substring(0, 10);//数据前十位是方法名
            }else{
                this.methodId = ta.getInput();
            }
        }
        if(ta.getType() != null){
            this.type = Numeric.decodeQuantity(ta.getType()).intValue();
        }
        this.logsNum = logsNum;//从交易回执中获取
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    public EthTxnModel() {

    }

    @Override
    public String toString() {
        return "EthTxnModel{" +
                " blockNumber=" + blockNumber +
                ", txnHash='" + txnHash + '\'' +
                ", txnIndex=" + txnIndex +
                ", fromAddress='" + fromAddress + '\'' +
                ", toAddress='" + toAddress + '\'' +
                ", ethValue='" + ethValue + '\'' +
                ", gasUsed='" + gasUsed + '\'' +
                ", gasPrice='" + gasPrice + '\'' +
                ", gasFee='" + gasFee + '\'' +
                ", status=" + status +
                ", nonce='" + nonce + '\'' +
                ", timestamp=" + timestamp +
                ", contractAddress='" + contractAddress + '\'' +
                ", cumulativeGasUsed='" + cumulativeGasUsed + '\'' +
                ", effectiveGasPrice='" + effectiveGasPrice + '\'' +
                ", input='" + input + '\'' +
                ", maxFeePerGas='" + maxFeePerGas + '\'' +
                ", maxPriorityFeePerGas='" + maxPriorityFeePerGas + '\'' +
                ", isError=" + isError +
                ", errMsg='" + errMsg + '\'' +
                ", methodId='" + methodId + '\'' +
                ", type=" + type +
                ", logsNum=" + logsNum +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
