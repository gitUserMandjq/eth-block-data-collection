package com.eth.etlTask.service.impl;

import com.eth.account.model.EthContractsModel;
import com.eth.account.service.IAccountService;
import com.eth.block.model.EthBlockModel;
import com.eth.block.model.EthBlockUncleModel;
import com.eth.block.service.IEthBlockService;
import com.eth.ens.model.EthNftDTO;
import com.eth.ens.model.EthEnsInfoModel;
import com.eth.ens.service.IEthEnsInfoService;
import com.eth.etlTask.service.IEtlTaskService;
import com.eth.event.model.EthEventTransferModel;
import com.eth.event.service.IEthEventTransferService;
import com.eth.framework.base.common.utils.AlchemyUtils;
import com.eth.framework.base.common.utils.JsonUtil;
import com.eth.framework.base.sysMessage.model.MessageConst;
import com.eth.framework.base.sysMessage.model.SysErrorMessageModel;
import com.eth.framework.base.sysMessage.model.SysMessageModel;
import com.eth.framework.base.sysMessage.service.ISysErrorMessageService;
import com.eth.framework.base.sysMessage.service.ISysMessageService;
import com.eth.nft.service.IEthNftInfoService;
import com.eth.transaction.consts.EthEventTopicConst;
import com.eth.transaction.model.EthTxnModel;
import com.eth.transaction.model.EthTxnReceiptDTO;
import com.eth.transaction.service.IEthTxnService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.utils.Numeric;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EtlTaskServiceImpl implements IEtlTaskService {
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
    @Resource
    IEthEventTransferService ethEventTransferService;
    @Resource
    IEthNftInfoService ethNftInfoService;
    @Resource
    IAccountService accountService;

    ExecutorService threadPool= Executors.newFixedThreadPool(30);



    /**
     * 解析某一高度的区块链数据
     * @param blockNumberList
     * @throws Exception
     */
    @Override
    public void etlCommonBlock(List<Long> blockNumberList, Integer retry) {
        etlCommonBlock(blockNumberList, retry, true);
    }
    /**
     * 解析某一高度的区块链数据
     * @param blockNumberList
     * @throws Exception
     */
    @Override
    public void etlCommonBlock(List<Long> blockNumberList, Integer retry, boolean filterNumber) {
        Date beginTime = new Date();
        String taskType = MessageConst.TYPE_COMTASK;
        try {
            log.info("ethCommonBlock:retry:{}, filterNumber:{}, blockNumberList:{}", retry, filterNumber,blockNumberList.toString());
            if(filterNumber){
                //筛选区块，如果已经处理过了，那么就不再处理
                filterBlockNumer(blockNumberList, taskType);
            }
//            //通过web3.eth方法获取区块链和交易数据
            List<EthBlock.Block> blockList = ethBlockService.getEthBlock(blockNumberList);
            List<EthBlockModel> blockModelList = new ArrayList<>();
            List<EthBlockUncleModel> blockUncleModelList = new ArrayList<>();
            for(EthBlock.Block block:blockList){
                EthBlockModel blockModel = new EthBlockModel(block);
                blockModelList.add(blockModel);
//                ethBlockService.insertOrUpdateEthBlock(blockModel);
                {//新增或者编辑叔块表
                    List<String> uncles = block.getUncles();
                    for(String uncleHash:uncles){
                        EthBlockUncleModel model = new EthBlockUncleModel(uncleHash, block.getNumber().longValue());
                        blockUncleModelList.add(model);
//                        ethBlockService.insertOrUpdateEthBlockUncle(uncleHash, block.getNumber().longValue());
                    }
                }
            }
            ethBlockService.insertBatchEthBlock(blockModelList);
            ethBlockService.insertBatchEthBlockUncle(blockUncleModelList);
            {
                //初步处理交易信息
                HashMap<String, EthTxnModel> transactionMap = dealTransactionMap(blockList);
                //获取交易回执，交易回执返回的顺序其实和交易的顺序一致
                String body = AlchemyUtils.alchemygetTransactionReceipts(blockNumberList);
                Map<String, Object> resultMap = JsonUtil.string2Obj(body);
                Map result = (Map) resultMap.get("result");
                //初步处理交易信息
                HashMap<String, EthNftDTO> nftMap = new HashMap<>();
                Set<String> tokenIds = new HashSet<>();
                List<EthEventTransferModel> transferList = new ArrayList<>();
                if(result.containsKey("receipts")){
                    List<Map> receipts = (List<Map>) result.get("receipts");
    //                JSON.parseArray(JSON.toJSONString(receipts), EthTxnReceiptDTO.class);
                    for(Map m:receipts){
                        EthTxnReceiptDTO receipt = JsonUtil.mapToBean(m, new EthTxnReceiptDTO());
                        String blockNumberStr = receipt.getBlockNumber();
                        BigInteger blockNumber = Numeric.decodeQuantity(blockNumberStr);
                        String transactionHash = receipt.getTransactionHash();
                        String type = receipt.getType();
                        //通过交易回执设置交易的回执信息
                        EthTxnModel txn = transactionMap.get(transactionHash);
                        txn.setGasUsed(Numeric.decodeQuantity(receipt.getGasUsed()));
                        txn.setGasFee(Numeric.decodeQuantity(receipt.getGasUsed()).add(txn.getGasPrice()).toString());
                        txn.setContractAddress(receipt.getContractAddress());
                        txn.setCumulativeGasUsed(Numeric.decodeQuantity(receipt.getCumulativeGasUsed()));
                        txn.setEffectiveGasPrice(Numeric.decodeQuantity(receipt.getEffectiveGasPrice()));
                        txn.setStatus(Numeric.decodeQuantity(receipt.getStatus()).intValue());
                        List<Map> logs = (List<Map>) m.get("logs");
                        txn.setLogsNum(logs.size());
                        String contractAddress = receipt.getContractAddress();
                        //获取事件日志，通过事件日志获得transfer交易信息
                        for(Map l:logs){
//                            log.info(JsonUtil.object2String(l));
                            String address = (String) l.get("address");//有关ENS的合约要靠事件调用来找到
                            List<String> topics = (List<String>) l.get("topics");
                            String topic0 = topics.get(0);//topic0就是函数名
                            if(EthEventTopicConst.TRANSFER_EVENT_TOPIC.equals(topic0)){//tokenId在topic[3]
                                //处理交易函数
                                EthEventTransferModel transfer = dealTransferEvent(blockNumber, transactionHash, type, txn, l, address, topics, topic0);
                                transferList.add(transfer);
                                //解析nft数据
                                EthContractsModel contract = accountService.getContractByAddress(contractAddress);
                                if(contract != null && (EthContractsModel.TYPE_ERC721.equals(contract.getType()) 
                                        || EthContractsModel.TYPE_ERC1155.equals(contract.getType()))){//如果是erc721或者erc1155合约，代表是nft
                                    asseNftInfo(transfer, tokenIds, nftMap);
                                }
                            }else if(EthEventTopicConst.TRANSFER_EVENT_SGINGLE_TOPIC.equals(topic0)){
                                //处理单个交易（erc1155）
                                EthEventTransferModel transfer = dealTransferSingleEvent(blockNumber, transactionHash, type, txn, l, address, topics);
                                transferList.add(transfer);
                                //解析nft数据
                                EthContractsModel contract = accountService.getContractByAddress(contractAddress);
                                if(contract != null && (EthContractsModel.TYPE_ERC721.equals(contract.getType())
                                        || EthContractsModel.TYPE_ERC1155.equals(contract.getType()))){//如果是erc721或者erc1155合约，代表是nft
                                    asseNftInfo(transfer, tokenIds, nftMap);
                                }
                            }else if(EthEventTopicConst.TRANSFER_EVENT_BATCH_TOPIC.equals(topic0)){
                                //处理批量交易（erc1155）
                                List<EthEventTransferModel> transferTemplateList = dealTransferBatchEvent(blockNumber, transactionHash, type, txn, l, address, topics);
                                transferList.addAll(transferTemplateList);
                                //解析nft数据
                                EthContractsModel contract = accountService.getContractByAddress(contractAddress);
                                if(contract != null && (EthContractsModel.TYPE_ERC721.equals(contract.getType())
                                        || EthContractsModel.TYPE_ERC1155.equals(contract.getType()))){//如果是erc721或者erc1155合约，代表是nft
                                    for(EthEventTransferModel transfer:transferTemplateList){
                                        asseNftInfo(transfer, tokenIds, nftMap);
                                    }
                                }
                            }
                        }
                    }
                }
                Date beginTime1 = new Date();
                //批量新增交易信息,交易信息太多，暂时不处理
//                ethTxnService.batchInsertTransaction(transactionMap);
                ethEventTransferService.addBatchEventTransfer(transferList);
                Map<String, Map> metaMap = getMetaMap(tokenIds);
                Iterator<Map.Entry<String, EthNftDTO>> iterator = nftMap.entrySet().iterator();
                List<EthNftDTO> valueList = new ArrayList<>();
                while(iterator.hasNext()){
                    Map.Entry<String, EthNftDTO> next = iterator.next();
                    EthNftDTO value = next.getValue();
                    Map map = metaMap.get(value.getTokenId());
                    value.setMeta(map);
                    valueList.add(value);
                }
                ethNftInfoService.batchInsertOrUpdateEns(valueList);
                log.info("batchInsertTransaction-costTime:{}ms",new Date().getTime() - beginTime1.getTime());
            }
            Long costTime = new Date().getTime() - beginTime.getTime();
            String message = "etlEthBlock消耗时间:"+(costTime+"ms");
            log.info(message);
            sysMessageService.addSysMessage(taskType, message, blockNumberList, costTime);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            if(++retry < 3){//重试三次
                etlCommonBlock(blockNumberList, retry);
                return;
            }else{
                String message = e.getMessage();
                if(e.getMessage().length() > 300){
                    message = message.substring(0, 300);
                }
                sysErrorMessageService.addSysErrorMessage(taskType, message, blockNumberList);
            }
        } finally {
        }
    }
    private void asseNftInfo(EthEventTransferModel transfer, Set<String> tokenIds, HashMap<String, EthNftDTO> nftMap){
        String tokenId = transfer.getTokenId();
        String from = transfer.getFromAddress();
        String to = transfer.getToAddress();
        tokenIds.add(tokenId);
        EthNftDTO ethEnsDTO;
        if(nftMap.containsKey(tokenId)){
            ethEnsDTO = nftMap.get(tokenId);
        }else{
            ethEnsDTO = new EthNftDTO();
            ethEnsDTO.setTokenId(tokenId);
            ethEnsDTO.setAddress(transfer.getTokenAddress());
            nftMap.put(tokenId, ethEnsDTO);
        }
        ethEnsDTO.setFrom(from);
        ethEnsDTO.setTo(to);
    }
    /**
     * 筛选区块，如果已经处理过了则去除
     * @param blockNumberList
     * @param taskType
     */
    private void filterBlockNumer(List<Long> blockNumberList, String taskType) {
        List<SysMessageModel> sysMessageModels = sysMessageService.listSysMessageByBlockNumber(taskType, blockNumberList);
        for(SysMessageModel message:sysMessageModels){
            blockNumberList.remove(message.getBlockNumber());
        }
    }

    /**
     * 处理批量交易事件（erc1155）
     * @param blockNumber
     * @param transactionHash
     * @param type
     * @param txn
     * @param l
     * @param address
     * @param topics
     */
    private List<EthEventTransferModel> dealTransferBatchEvent(BigInteger blockNumber, String transactionHash, String type, EthTxnModel txn, Map l, String address, List<String> topics) {
        List<EthEventTransferModel> templateList = ethEventTransferService.getBatchEthEventTransferModels(blockNumber, transactionHash, type, txn.getTimestamp(), l, address, topics);
        return templateList;
    }



    /**
     * 处理单个交易事件（erc1155）
     * @param blockNumber
     * @param transactionHash
     * @param type
     * @param txn
     * @param l
     * @param address
     * @param topics
     */
    private EthEventTransferModel dealTransferSingleEvent(BigInteger blockNumber, String transactionHash, String type, EthTxnModel txn, Map l, String address, List<String> topics) {
        EthEventTransferModel transfer = ethEventTransferService.getSingleEthEventTransferModel(blockNumber, transactionHash, type, txn.getTimestamp(), l, address, topics);
        return transfer;
    }


    /**
     * 处理一般交易事件
     * @param blockNumber
     * @param transactionHash
     * @param type
     * @param txn
     * @param l
     * @param address
     * @param topics
     * @param topic0
     */
    private EthEventTransferModel dealTransferEvent(BigInteger blockNumber, String transactionHash, String type, EthTxnModel txn, Map l, String address, List<String> topics, String topic0) {
        String data = (String) l.get("data");
        String logIndexStr = (String) l.get("logIndex");
        Boolean removed = (Boolean) l.get("removed");
        BigInteger logIndex = Numeric.decodeQuantity(logIndexStr);
        EthEventTransferModel transfer = ethEventTransferService.getEthEventTransferModel(blockNumber, transactionHash, type, txn.getTimestamp(), data, logIndex, removed, address, topics);
        return transfer;
    }

    /**
     * 解析某一高度的区块链数据
     * @param blockNumber
     * @throws Exception
     */
    @Override
    public void etlCommonBlock(List<Long> blockNumber, Integer retry, boolean filterNumber, CountDownLatch latch){
        threadPool.submit(()->{
            etlCommonBlock(blockNumber, retry, filterNumber);
            for(int i=0;i< blockNumber.size();i++){
                latch.countDown();
            }
        });
    }

    /**
     * 解析某一高度的区块链数据
     * @param blockNumber
     * @throws Exception
     */
    @Override
    public void etlCommonBlock(List<Long> blockNumber, Integer retry, boolean filterNumber, CountDownLatch latch, Semaphore lock){
        try {
            lock.acquire();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        threadPool.submit(()->{
            etlCommonBlock(blockNumber, retry, filterNumber);
            for(int i=0;i< blockNumber.size();i++){
                latch.countDown();
            }
            lock.release();
        });
    }
    /**
     * 解析某一高度的区块链数据
     * @throws Exception
     */
    @Override
    public void etlCommonBlock(Integer batchNum, Long maxBlock, boolean filterNumber) throws Exception {
        Long startBlockNumber = sysMessageService.getMaxBlockNumber(MessageConst.TYPE_COMTASK);
        Long endBlockNumber = ethBlockService.getCurrentBlockNumber().longValue();
        if(endBlockNumber - startBlockNumber > maxBlock){
            endBlockNumber = startBlockNumber + maxBlock;
        }
        etlCommonBlock(startBlockNumber, endBlockNumber, batchNum, filterNumber);
    }
    /**
     * 解析某一高度的区块链数据
     * @param startBlockNumber
     * @param endBlockNumber
     * @throws Exception
     */
    @Override
    @Async
    public void etlCommonBlockAsync(Long startBlockNumber, Long endBlockNumber, Integer batchNum, boolean filterNumber) throws Exception {
        etlCommonBlock(startBlockNumber, endBlockNumber, batchNum, filterNumber);
    }
    /**
     * 解析某一高度的区块链数据
     * @param startBlockNumber
     * @param endBlockNumber
     * @throws Exception
     */
    @Override
    public void etlCommonBlock(Long startBlockNumber, Long endBlockNumber, Integer batchNum, boolean filterNumber) throws Exception {
        log.info("startNumber:{},endNumer:{}", startBlockNumber, endBlockNumber);
        Date beginTime = new Date();
        CountDownLatch latch = new CountDownLatch((int)(endBlockNumber - startBlockNumber + 1));
        Semaphore lock = new Semaphore(20);
        for(long i = startBlockNumber;i<=endBlockNumber;i+=batchNum){
            long end = i + batchNum - 1;
            if(end > endBlockNumber){
                end = endBlockNumber;
            }
            List<Long> blockNumberList = new ArrayList<>();
            for(long j=i;j<=end;j++){
                blockNumberList.add(j);
            }
            etlCommonBlock(blockNumberList, 0, filterNumber, latch, lock);
        }
        //阻塞直到任务完成
        latch.await();
        log.info("etlCommonBlock-allCostTime:{}ms", new Date().getTime() - beginTime.getTime());
    }
    /**
     * 解析某一高度的区块链数据
     * @param blockNumber
     * @throws Exception
     */
    @Override
    public void etlEns(Long blockNumber, Integer retry) {
        etlEns(Arrays.asList(blockNumber), retry);
    }
    /**
     * 解析某一高度的区块链数据
     * @param blockNumber
     * @throws Exception
     */
    @Override
    public void etlEns(List<Long> blockNumber, Integer retry) {
        Date beginTime = new Date();
        String taskType = MessageConst.TYPE_ETHTASK;
        try {
            //筛选区块，如果已经处理过了，那么就不再处理
            filterBlockNumer(blockNumber, taskType);
            //初步处理交易信息
            HashMap<String, EthNftDTO> ensMap = new HashMap<>();
            //获取交易回执，交易回执返回的顺序其实和交易的顺序一致
            String body = AlchemyUtils.alchemygetTransactionReceipts(blockNumber);
            Map<String, Object> resultMap = JsonUtil.string2Obj(body);
            Map result = (Map) resultMap.get("result");
            Set<String> tokenIds = new HashSet<>();
            if(result.containsKey("receipts")){
                List<Map> receipts = (List<Map>) result.get("receipts");
                //                JSON.parseArray(JSON.toJSONString(receipts), EthTxnReceiptDTO.class);
                for(Map m:receipts){
                    EthTxnReceiptDTO receipt = JsonUtil.mapToBean(m, new EthTxnReceiptDTO());
                    List<Map> logs = (List<Map>) m.get("logs");
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
                                log.info("TransactionHash:"+receipt.getTransactionHash());
//                                    String nftMetadata = AlchemyUtils.getNFTMetadata(address, tokenId);
                                tokenIds.add(tokenId);
                                EthNftDTO ethEnsDTO;
                                if(ensMap.containsKey(tokenId)){
                                    ethEnsDTO = ensMap.get(tokenId);
                                }else{
                                    ethEnsDTO = new EthNftDTO();
//                                        ethEnsDTO.setMeta(nftMetadata);
                                    ethEnsDTO.setTokenId(tokenId);
                                    ethEnsDTO.setAddress(address);
                                    ensMap.put(tokenId, ethEnsDTO);
                                }
                                ethEnsDTO.setFrom(from);
                                ethEnsDTO.setTo(to);
                            }
                        }
                    }
                }
            }
            Map<String, Map> metaMap = getMetaMap(tokenIds);
            Iterator<Map.Entry<String, EthNftDTO>> iterator = ensMap.entrySet().iterator();
            List<EthNftDTO> valueList = new ArrayList<>();
            while(iterator.hasNext()){
                Map.Entry<String, EthNftDTO> next = iterator.next();
                EthNftDTO value = next.getValue();
                Map map = metaMap.get(value.getTokenId());
                value.setMeta(map);
                valueList.add(value);
            }
            ethEnsInfoService.batchInsertOrUpdateEns(valueList);
            Long costTime = new Date().getTime() - beginTime.getTime();
            String message = "etlEthBlock"+blockNumber.toString()+"消耗时间:"+(costTime+"ms");
            log.info(message);
            sysMessageService.addSysMessage(taskType, message, blockNumber, costTime);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            if(++retry < 3){//重试三次
                etlEns(blockNumber, retry);
                return;
            }else{
                String message = e.getMessage();
                if(e.getMessage().length() > 300){
                    message = message.substring(0, 300);
                }
                sysErrorMessageService.addSysErrorMessage(taskType, message, blockNumber);
            }
        } finally {
        }
    }

    private static Map<String, Map> getMetaMap(Set<String> tokenIds) throws IOException {
        if(tokenIds.isEmpty()){
            return new HashMap<>();
        }
        String metaListStr = AlchemyUtils.getNFTMetadataBatch(AlchemyUtils.ENSCONSTRACTADDRESS, tokenIds, EthEnsInfoModel.tokenType);
        List<Map> metaList = JsonUtil.string2Obj(metaListStr);
        Map<String, Map> metaMap = metaList.stream().collect(Collectors.toMap(map -> {
            Map idMap = (Map) map.get("id");
            String tokenId = (String) idMap.get("tokenId");
            return tokenId;
        }, v -> v));
        return metaMap;
    }

    /**
     * 解析某一高度的区块链数据
     * @param blockNumber
     * @throws Exception
     */
    @Override
    public void etlEns(List<Long> blockNumber, Integer retry, CountDownLatch latch){
        threadPool.submit(()->{
            etlEns(blockNumber, retry);
            for(int i=0;i< blockNumber.size();i++){
                latch.countDown();
            }
        });
    }
    /**
     * 解析某一高度的区块链数据
     * @param blockNumber
     * @throws Exception
     */
    @Override
    public void etlEns(List<Long> blockNumber, Integer retry, CountDownLatch latch, Semaphore lock){
        try {
            lock.acquire();//这个锁是为了保证线程池的等待队列限制在一定规模，避免无限扩大
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        threadPool.submit(()->{
            etlEns(blockNumber, retry);
            for(int i=0;i< blockNumber.size();i++){
                latch.countDown();
            }
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
    public void etlEns(Long startBlockNumber, Long endBlockNumber) throws Exception {
        for(long i = startBlockNumber;i<endBlockNumber;i++){
            etlEns(i, 0);
        }
    }
    /**
     * 处理异常区块链数据
     * @throws Exception
     */
    @Override
    public void dealErrorEth(Integer errorNum) throws Exception {
        List<SysErrorMessageModel> errorList = sysErrorMessageService.listNotDealSysErrorMessage(MessageConst.TYPE_ETHTASK, errorNum);
        CountDownLatch latch = new CountDownLatch((int)(errorList.size()));
        List<Long> blockIds = new ArrayList<>();
        List<Long> logIds = new ArrayList<>();
        for(SysErrorMessageModel error:errorList){
            blockIds.add(error.getBlockNumber());
            logIds.add(error.getId());
            if(blockIds.size() >= 10){
                etlEns(blockIds, 0, latch);
                blockIds = new ArrayList<>();
            }
        }
        if(!blockIds.isEmpty()){
            etlEns(blockIds, 0, latch);
        }
        latch.await();
        sysErrorMessageService.dealSysErrorMessage(logIds);
    }

    @Override
    public void dealErrorComtask(Integer errorNum) throws Exception {
        List<SysErrorMessageModel> errorList = sysErrorMessageService.listNotDealSysErrorMessage(MessageConst.TYPE_COMTASK, errorNum);
        CountDownLatch latch = new CountDownLatch((int)(errorList.size()));
        List<Long> blockIds = new ArrayList<>();
        List<Long> logIds = new ArrayList<>();
        for(SysErrorMessageModel error:errorList){
            blockIds.add(error.getBlockNumber());
            logIds.add(error.getId());
            if(blockIds.size() >= 30){
                etlCommonBlock(blockIds, 0, false);
                sysErrorMessageService.dealSysErrorMessage(logIds);
                logIds = new ArrayList<>();
                blockIds = new ArrayList<>();
            }
        }
        if(!blockIds.isEmpty()){
            etlCommonBlock(blockIds, 0, false);
            sysErrorMessageService.dealSysErrorMessage(logIds);
        }
        latch.await();
    }

    private static HashMap<String, EthTxnModel> dealTransactionMap(EthBlock.Block block) {
        HashMap<String, EthTxnModel> transactionMap = new HashMap<>();
        List<EthBlock.TransactionResult> txs = block.getTransactions();
        txs.forEach(tx -> {//遍历交易列表
            EthBlock.TransactionObject transaction = (EthBlock.TransactionObject) tx.get();
            EthTxnModel txn = new EthTxnModel(transaction);
            txn.setTimestamp(new Date(block.getTimestamp().longValue() * 1000L));
            transactionMap.put(txn.getTxnHash(), txn);
//            System.out.println(transaction.getFrom());
        });
        return transactionMap;
    }
    private static HashMap<String, EthTxnModel> dealTransactionMap(List<EthBlock.Block> blockList) {
        HashMap<String, EthTxnModel> transactionMap = new HashMap<>();
        for(int i=0;i<blockList.size();i++){
            EthBlock.Block block = blockList.get(i);
            List<EthBlock.TransactionResult> txs = block.getTransactions();
            txs.forEach(tx -> {//遍历交易列表
                EthBlock.TransactionObject transaction = (EthBlock.TransactionObject) tx.get();
                EthTxnModel txn = new EthTxnModel(transaction);
                txn.setTimestamp(new Date(block.getTimestamp().longValue() * 1000L));
                transactionMap.put(txn.getTxnHash(), txn);
//            System.out.println(transaction.getFrom());
            });
        }
        return transactionMap;
    }

    public static void main(String[] args) {
//        List<Long> ids = new ArrayList<>();
//        ids.add(1L);
//        System.out.println(ids.toString());
        String data = "0x000000000000000000000000000000000000000000000020861f2a1fc4cac000";
//        String data = "0x00000425b4462e19460bedb4bccfcf16d270975ef882f03831bf3d40f7342355";
//        String data = "0x52769477a7d940000";
//        String data = "0x52769477a7d940000";
        data = data.replaceAll("0x[0]*", "0x");
        System.out.println(data);
        BigInteger value = Numeric.decodeQuantity(data);
        System.out.println(value);
        System.out.println(value.longValue());
    }
}
