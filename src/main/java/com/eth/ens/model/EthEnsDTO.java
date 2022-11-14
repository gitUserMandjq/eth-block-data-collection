package com.eth.ens.model;

import com.eth.transaction.model.EthTxnModel;
import lombok.Data;

@Data
public class EthEnsDTO {
    private String tokenId;//nft编号
    private String meta;//nft原始信息
    private String from;//卖方
    private String to;//买方
    private String address;//合约地址
    private EthTxnModel txn;//最后一次交易
}
