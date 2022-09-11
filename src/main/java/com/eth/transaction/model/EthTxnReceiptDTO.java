package com.eth.transaction.model;

import lombok.Data;

@Data
public class EthTxnReceiptDTO {
    //交易hash值
    private String transactionHash;
    //区块hash值
    private String blockHash;
    //区块高度
    private String blockNumber;
    //合约地址
    private String contractAddress;
    //累计消耗gas
    private String cumulativeGasUsed;
    //实际gas价格
    private String effectiveGasPrice;
    //发起地址
    private String from;
    //目标地址
    private String to;
    //消耗gas
    private String gasUsed;
    //日志bloom
    private String logsBloom;
    //状态
    private String status;
    //交易位置
    private String transactionIndex;
    //类型
    private String type;
    //logs事件日志列表

    @Override
    public String toString() {
        return "EthTxnReceiptDTO{" +
                "transactionHash='" + transactionHash + '\'' +
                ", blockHash='" + blockHash + '\'' +
                ", blockNumber='" + blockNumber + '\'' +
                ", contractAddress='" + contractAddress + '\'' +
                ", cumulativeGasUsed='" + cumulativeGasUsed + '\'' +
                ", effectiveGasPrice='" + effectiveGasPrice + '\'' +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", gasUsed='" + gasUsed + '\'' +
                ", logsBloom='" + logsBloom + '\'' +
                ", status='" + status + '\'' +
                ", transactionIndex='" + transactionIndex + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
