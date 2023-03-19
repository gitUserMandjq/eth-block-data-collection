package com.eth.block.service.impl;

import com.eth.block.dao.EthBlockDao;
import com.eth.block.dao.EthBlockUncleDao;
import com.eth.block.model.EthBlockModel;
import com.eth.block.model.EthBlockUncleModel;
import com.eth.block.service.IEthBlockService;
import com.eth.framework.base.common.utils.Web3jUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.web3j.protocol.core.BatchRequest;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthBlockNumber;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EthBlockServiceImpl implements IEthBlockService {
//    @Autowired
//    private Web3j web3j;
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
        EthBlock.Block block = Web3jUtil.getInstance().getWeb3j().ethGetBlockByNumber(DefaultBlockParameter.valueOf(
                BigInteger.valueOf(blockNumer)), true).send().getBlock();
        return block;
    }

    /**
     * 查询某个高度的区块
     * @param blockNumerList
     * @return
     */
    @Override
    public List<EthBlock.Block> getEthBlock(Iterable<Long> blockNumerList) throws Exception{
        if(blockNumerList == null || !blockNumerList.iterator().hasNext()){
            return new ArrayList<>();
        }
//        List<EthBlock.Block> list = new ArrayList<>();
//        for (Long blockNumer : blockNumerList) {
//            list.add(getEthBlock(blockNumer));
//        }
//        return list;
        BatchRequest batchRequest = Web3jUtil.getInstance().getWeb3j().newBatch();
        for (Long blockNumer : blockNumerList) {
            Request<?, EthBlock> request = Web3jUtil.getInstance().getWeb3j().ethGetBlockByNumber(DefaultBlockParameter.valueOf(
                    BigInteger.valueOf(blockNumer)), true);
            batchRequest.add(request);
        }
//        List<? extends EthBlock> responses = (List<? extends EthBlock>) batchRequest.sendAsync().get().getResponses();
        List<? extends EthBlock> responses = (List<? extends EthBlock>) batchRequest.send().getResponses();
        return responses.stream().map(EthBlock::getBlock).collect(Collectors.toList());
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
     * @param ethBlockModel
     * @return
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertBatchEthBlock(Iterable<EthBlockModel> ethBlockModel)throws Exception{
        ethBlockDao.batchIgnoreSave(ethBlockModel, 500);
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
    /**
     * 新增或者更新区块链
     * @param list
     * @return
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertBatchEthBlockUncle(Iterable<EthBlockUncleModel> list)throws Exception{
        ethBlockUncleDao.batchIgnoreSave(list, 500);
    }
    /**
     * 获取当前区块高度
     * @return
     */
    @Override
    public BigInteger getCurrentBlockNumber() throws IOException {
        Request<?, EthBlockNumber> request = Web3jUtil.getInstance().getWeb3j().ethBlockNumber();
        return request.send().getBlockNumber();
    }

}
