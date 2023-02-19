package com.eth.block.service;

import com.eth.block.model.EthBlockModel;
import com.eth.block.model.EthBlockUncleModel;
import org.web3j.protocol.core.methods.response.EthBlock;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

public interface IEthBlockService {
    /**
     * 查询某个高度的区块
     * @param blockNumer
     * @return
     */
    EthBlock.Block getEthBlock(Long blockNumer) throws IOException;
    /**
     * 查询某个高度的区块
     * @param blockNumerList
     * @return
     */
    List<EthBlock.Block> getEthBlock(Iterable<Long> blockNumerList) throws Exception;

    /**
     * 新增或者更新区块链
     * @param ethBlockModel
     * @return
     * @throws Exception
     */
    EthBlockModel insertOrUpdateEthBlock(EthBlockModel ethBlockModel)throws Exception;
    /**
     * 新增或者更新区块链
     * @param uncleHash
     * @param blockNumber
     * @return
     * @throws Exception
     */
    EthBlockUncleModel insertOrUpdateEthBlockUncle(String uncleHash, Long blockNumber)throws Exception;

    /**
     * 获取当前区块高度
     * @return
     */
    BigInteger getCurrentBlockNumber() throws IOException;
}
