package com.eth.etlTask.service.impl;

import com.eth.block.model.EthBlockModel;
import com.eth.block.service.IEthBlockService;
import com.eth.ens.model.EthEnsDTO;
import com.eth.ens.service.IEthEnsInfoService;
import com.eth.etlTask.service.IEtlTaskService;
import com.eth.framework.base.common.utils.AlchemyUtils;
import com.eth.framework.base.common.utils.JsonUtil;
import com.eth.framework.base.sysMessage.model.SysErrorMessageModel;
import com.eth.framework.base.sysMessage.model.SysMessageModel;
import com.eth.framework.base.sysMessage.service.ISysErrorMessageService;
import com.eth.framework.base.sysMessage.service.ISysMessageService;
import com.eth.transaction.consts.EthEventTopicConst;
import com.eth.transaction.model.EthTxnModel;
import com.eth.transaction.model.EthTxnReceiptDTO;
import com.eth.transaction.service.IEthTxnService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.utils.Numeric;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EthTaskServiceImpl implements IEtlTaskService {
    @Resource
    IEthBlockService ethBlockService;
    @Resource
    IEthTxnService ethTxnService;
    @Resource
    IEthEnsInfoService ethEnsInfoService;
    @Resource
    ISysErrorMessageService sysErrorMessageService;
    @Resource
    ISysMessageService sysMessageService;

    ExecutorService threadPool= Executors.newFixedThreadPool(20);



    /**
     * 解析某一高度的区块链数据
     * @param blockNumber
     * @throws Exception
     */
    @Override
    public void etlEthBlock(Long blockNumber, Integer retry) {
        Date beginTime = new Date();
        try {
            //通过web3.eth方法获取区块链和交易数据
            EthBlock.Block block = ethBlockService.getEthBlock(blockNumber);
            EthBlockModel blockModel = new EthBlockModel(block);
            ethBlockService.insertOrUpdateEthBlock(blockModel);
            {//新增或者编辑叔块表
                List<String> uncles = block.getUncles();
                for(String uncleHash:uncles){
                    ethBlockService.insertOrUpdateEthBlockUncle(uncleHash, blockNumber);
                }
            }
            {
                //初步处理交易信息
                HashMap<String, EthTxnModel> transactionMap = dealTransactionMap(block, blockModel);
                HashMap<String, EthTxnModel> transactionEnsMap = new HashMap<>();
                HashMap<String, EthEnsDTO> ensMap = new HashMap<>();
                //获取交易回执，交易回执返回的顺序其实和交易的顺序一致
                String body = AlchemyUtils.alchemygetTransactionReceipts(blockNumber);
                Map<String, Object> resultMap = JsonUtil.string2Obj(body);
                Map result = (Map) resultMap.get("result");
                if(result.containsKey("receipts")){
                    List<Map> receipts = (List<Map>) result.get("receipts");
    //                JSON.parseArray(JSON.toJSONString(receipts), EthTxnReceiptDTO.class);
                    for(Map m:receipts){
                        EthTxnReceiptDTO receipt = JsonUtil.mapToBean(m, new EthTxnReceiptDTO());
                        EthTxnModel txn = transactionMap.get(receipt.getTransactionHash());
                        txn.setGasUsed(Numeric.decodeQuantity(receipt.getGasUsed()));
                        txn.setGasFee(Numeric.decodeQuantity(receipt.getGasUsed()).add(txn.getGasPrice()).toString());
                        txn.setContractAddress(receipt.getContractAddress());
                        txn.setCumulativeGasUsed(Numeric.decodeQuantity(receipt.getCumulativeGasUsed()));
                        txn.setEffectiveGasPrice(Numeric.decodeQuantity(receipt.getEffectiveGasPrice()));
                        txn.setStatus(Numeric.decodeQuantity(receipt.getStatus()).intValue());
                        List<Map> logs = (List<Map>) m.get("logs");
                        txn.setLogsNum(logs.size());
                        String contractAddress = receipt.getContractAddress();
                        for(Map l:logs){
                            String address = (String) l.get("address");//有关ENS的合约要靠事件调用来找到
                            if(AlchemyUtils.ENSCONSTRACTADDRESS.equalsIgnoreCase(address)){//如果是ENS合约
                                List<String> topics = (List<String>) l.get("topics");
                                String topic0 = topics.get(0);//topic0就是函数名
                                if(EthEventTopicConst.TRANSFER_EVENT_TOPIC.equals(topic0)){//tokenId在topic[3]
                                    String from = topics.get(1).replace("0x000000000000000000000000","0x");
                                    String to =  topics.get(2).replace("0x000000000000000000000000","0x");
                                    String tokenId = topics.get(3);
                                    log.info("from:"+from);
                                    log.info("to:"+to);
                                    log.info("tokenId:"+tokenId);
                                    log.info("TransactionHash:"+txn.getTxnHash());
                                    String nftMetadata = AlchemyUtils.getNFTMetadata(address, tokenId);
                                    transactionEnsMap.put(txn.getTxnHash(), txn);
                                    EthEnsDTO ethEnsDTO;
                                    if(ensMap.containsKey(tokenId)){
                                        ethEnsDTO = ensMap.get(tokenId);
                                    }else{
                                        ethEnsDTO = new EthEnsDTO();
                                        ethEnsDTO.setMeta(nftMetadata);
                                        ethEnsDTO.setTokenId(tokenId);
                                        ensMap.put(tokenId, ethEnsDTO);
                                    }
                                    ethEnsDTO.setFrom(from);
                                    ethEnsDTO.setTo(to);
                                    ethEnsDTO.setTxn(txn);
                                }
                            }
                        }
                    }
                }
                //批量新增交易信息
    //            ethTxnService.batchInsertTransaction(transactionMap);
                ethTxnService.batchInsertTransactionEns(transactionEnsMap);//交易数量有点多，先只保存ENS的数据
                Iterator<Map.Entry<String, EthEnsDTO>> iterator = ensMap.entrySet().iterator();
                while(iterator.hasNext()){
                    Map.Entry<String, EthEnsDTO> next = iterator.next();
                    EthEnsDTO value = next.getValue();
                    ethEnsInfoService.insertOrUpdateEns(value);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            if(retry < 3){//重试三次
                retry ++;
                etlEthBlock(blockNumber, retry);
                return;
            }else{
                sysErrorMessageService.addSysErrorMessage(SysErrorMessageModel.TYPE_ETHTASK, e.getMessage(), blockNumber);
            }
        } finally {
            String message = "etlEthBlock消耗时间:"+(new Date().getTime() - beginTime.getTime()+"ms");
            log.info(message);
            sysMessageService.addSysMessage(SysMessageModel.TYPE_ETHTASK, message, blockNumber);
        }
    }

    /**
     * 解析某一高度的区块链数据
     * @param blockNumber
     * @throws Exception
     */
    @Override
    public void etlEthBlock(Long blockNumber, Integer retry, CountDownLatch latch){
        threadPool.submit(()->{
            etlEthBlock(blockNumber, retry);
            latch.countDown();
        });
    }
    /**
     * 解析某一高度的区块链数据
     * @param blockNumber
     * @throws Exception
     */
    @Override
    public void etlEthBlock(Long blockNumber, Integer retry, CountDownLatch latch, Semaphore lock){
        try {
            lock.acquire();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        threadPool.submit(()->{
            etlEthBlock(blockNumber, retry);
            latch.countDown();
            lock.release();
        });
    }
    /**
     * 解析某一高度的区块链数据
     * @param startBlockNumber
     * @param endBlockNumber
     * @throws Exception
     */
    @Override
    public void etlEthBlock(Long startBlockNumber, Long endBlockNumber) throws Exception {
        for(long i = startBlockNumber;i<endBlockNumber;i++){
            etlEthBlock(i, 0);
        }
    }
    /**
     * 处理异常区块链数据
     * @throws Exception
     */
    @Override
    public void dealErrorEth() throws Exception {
        List<SysErrorMessageModel> errorList = sysErrorMessageService.listNotDealSysErrorMessage(SysErrorMessageModel.TYPE_ETHTASK);
        CountDownLatch latch = new CountDownLatch((int)(errorList.size()));
        for(SysErrorMessageModel error:errorList){
            etlEthBlock(error.getBlockNumber(), 0, latch);
        }
        latch.await();
        List<Long> ids = errorList.stream().map(SysErrorMessageModel::getId).collect(Collectors.toList());
        sysErrorMessageService.dealSysErrorMessage(ids);
    }

    private static HashMap<String, EthTxnModel> dealTransactionMap(EthBlock.Block block, EthBlockModel blockModel) {
        HashMap<String, EthTxnModel> transactionMap = new HashMap<>();
        List<EthBlock.TransactionResult> txs = block.getTransactions();
        txs.forEach(tx -> {//遍历交易列表
            EthBlock.TransactionObject transaction = (EthBlock.TransactionObject) tx.get();
            EthTxnModel txn = new EthTxnModel(transaction);
            txn.setTimestamp(blockModel.getTimestamp());
            transactionMap.put(txn.getTxnHash(), txn);
//            System.out.println(transaction.getFrom());
        });
        return transactionMap;
    }

    public static void main(String[] args) {
        long l = 9999978301729250L;
    }
}
