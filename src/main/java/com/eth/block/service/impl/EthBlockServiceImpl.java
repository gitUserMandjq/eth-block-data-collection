package com.eth.block.service.impl;

import com.eth.block.dao.EthBlockDao;
import com.eth.block.dao.EthBlockUncleDao;
import com.eth.block.model.EthBlockModel;
import com.eth.block.model.EthBlockUncleModel;
import com.eth.block.service.IEthBlockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.EthBlock;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigInteger;
@Service
public class EthBlockServiceImpl implements IEthBlockService {
    @Autowired
    private Web3j web3j;
    @Resource
    EthBlockDao ethBlockDao;
    @Resource
    EthBlockUncleDao ethBlockUncleDao;
    /**
     * 查询某个高度的区块
     * @param blockNumer
     * @return
     */
    @Override
    public EthBlock.Block getEthBlock(Long blockNumer) throws IOException {
        EthBlock.Block block = web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(
                BigInteger.valueOf(blockNumer)), true).send().getBlock();
        return block;
    }
    /**
     * 新增或者更新区块链
     * @param ethBlockModel
     * @return
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public EthBlockModel insertOrUpdateEthBlock(EthBlockModel ethBlockModel) throws Exception {
        ethBlockDao.save(ethBlockModel);
        return ethBlockModel;
    }
    /**
     * 新增或者更新区块链
     * @param uncleHash
     * @param blockNumber
     * @return
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public EthBlockUncleModel insertOrUpdateEthBlockUncle(String uncleHash, Long blockNumber) throws Exception {
        EthBlockUncleModel model = new EthBlockUncleModel(uncleHash, blockNumber);
        ethBlockUncleDao.save(model);
        return model;
    }

}
