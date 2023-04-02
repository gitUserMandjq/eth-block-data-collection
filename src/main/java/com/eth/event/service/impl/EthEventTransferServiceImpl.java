package com.eth.event.service.impl;

import com.eth.account.service.IAccountService;
import com.eth.event.dao.EthEventTransferDao;
import com.eth.event.dao.EthEventTransferSmartDao;
import com.eth.event.model.*;
import com.eth.event.service.IEthEventTransferService;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.web3j.utils.Numeric;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.*;

@Service
public class EthEventTransferServiceImpl implements IEthEventTransferService {
    @Resource
    EthEventTransferDao ethEventTransferDao;
    @Resource
    IAccountService accountService;
    @Resource
    EthEventTransferSmartDao ethEventTransferSmartDao;
    /**
     * 批量新增交易事件
     * @param transferList
     * @return
     */
    @Override
    public void addBatchEventTransfer(List<EthEventTransferModel> transferList) throws Exception {
        ethEventTransferDao.batchIgnoreSave(transferList, 500);
    }

    @Override
    public void addBatchEventTransferSmart(List<EthEventTransferSmartModel> transferList) throws Exception {
        ethEventTransferSmartDao.batchIgnoreSave(transferList, 500);
    }
    @Override
    public void addEventTransferSmart(EthEventTransferSmartModel transfer) throws Exception {
        ethEventTransferSmartDao.save(transfer);
    }

    @Override
    public Set<String> getSmartAddress(String address, List<EthEventTransferQO> transferQOList) throws Exception {
        if(transferQOList.isEmpty()){
            throw new Exception("筛选条件不能为空");
        }
        //卖出查询
        List<EthEventTransferQO> fromQOList = new ArrayList<>();
        //买入查询
        List<EthEventTransferQO> toQOList = new ArrayList<>();
        for(EthEventTransferQO qo:transferQOList){
            if("0".equals(qo.getTransType())){
                toQOList.add(qo);
            }else{
                fromQOList.add(qo);
            }
        }
        //卖出交易列表
        List<EthEventTransferDTO> fromTransferList = ethEventTransferDao.listFromGroup(address, fromQOList);
        //买入交易列表
        List<EthEventTransferDTO> toTransferList = ethEventTransferDao.listToGroup(address, toQOList);
        //获得卖出账号
        Set<String> fromTransferSet = new HashSet<>();
        for(EthEventTransferDTO fromTransfer:fromTransferList){
            if(fromTransfer.getCount() >= 3){//只留下交易3次以上的账号
                fromTransferSet.add(fromTransfer.getFrom());
            }
        }
        //获得买入账号
        Set<String> toTransferSet = new HashSet<>();
        for(EthEventTransferDTO toTransfer:toTransferList){
            if(toTransfer.getCount() >= 3){//只留下交易3次以上的账号
                toTransferSet.add(toTransfer.getFrom());
            }
        }
        {
            //只留下同时买入和卖出的账号
            filterSameAddress(fromTransferSet, toTransferSet);
        }
        //判断账号是否是合约账号
        List<String> accountCode = accountService.getAccountCode(fromTransferSet);
        Iterator<String> iterator = fromTransferSet.iterator();
        int i=0;
        while(iterator.hasNext()){
            iterator.next();
            String code = accountCode.get(i);
            if(!"0x".equals(code)){//删除合约账号
                iterator.remove();
            }
            i++;
        }
        return fromTransferSet;
    }

    private static void filterSameAddress(Set<String> fromTransferSet, Set<String> toTransferSet) {
        Iterator<String> iterator = fromTransferSet.iterator();
        //只留下同时买入和卖出的账号
        while(iterator.hasNext()){
            String next = iterator.next();
            if(!toTransferSet.contains(next)){
                iterator.remove();
            }
        }
    }
    @Override
    public EthEventTransferModel getEthEventTransferModel(BigInteger blockNumber, String transactionHash, String type, Date timestamp, String data, BigInteger logIndex, Boolean removed, String address, List<String> topics) {
        String from;
        String to;
        if(topics.size() == 1){//有的消息的from和to数据都在data里
            //0x0000000000000000000000009c6e6e963460cc027a33744248a177727900a4b8000000000000000000000000b1690c08e213a35ed9bab7b318de14420fb57d8c00000000000000000000000000000000000000000000000000000000000f41fb
            from = "0x"+data.substring(26, 66);
            to = "0x"+data.substring(90, 130);
            data = "0x"+data.substring(130);
        }else if(topics.size() == 3){//from和to在topic里
            from = topics.get(1).replace("0x000000000000000000000000","0x");
            to =  topics.get(2).replace("0x000000000000000000000000","0x");
        }else{//from和to和data都在topic里，ens就是这样
            from = topics.get(1).replace("0x000000000000000000000000","0x");
            to =  topics.get(2).replace("0x000000000000000000000000","0x");
            data = topics.get(3);
        }
        String valueStr;
        valueStr = dealHex(data);
        if("0x".equals(valueStr)){
            valueStr = "0x0";
        }
        BigInteger value = Numeric.decodeQuantity(valueStr);
//                                String tokenId = topics.get(3);
//                                log.info("from:"+from);
//                                log.info("to:"+to);
//                                log.info("tokenId:"+tokenId);
//                                log.info("TransactionHash:"+txn.getTxnHash());
//        String methed_id = topic0.substring(0, 10);
        EthEventTransferModel transfer = new EthEventTransferModel();
        transfer.setId(blockNumber +"-"+logIndex);
        setTransferInfo(blockNumber, transactionHash, type, timestamp, address, from, to, logIndex, removed, valueStr, value, transfer);
        transfer.setMethodType(TransferConst.TRANSFER);
        return transfer;
    }
    @Override
    public EthEventTransferModel getSingleEthEventTransferModel(BigInteger blockNumber, String transactionHash, String type, Date timestamp, Map l, String address, List<String> topics) {
        String operator= topics.get(1).replace("0x000000000000000000000000","0x");
        String from = topics.get(2).replace("0x000000000000000000000000","0x");
        String to =  topics.get(3).replace("0x000000000000000000000000","0x");
        String data = (String) l.get("data");
        String tokenId = data.substring(0, 66);
        String tokenNumHex = dealHex("0x" + data.substring(66, 130));
        BigInteger tokenNum = Numeric.decodeQuantity(tokenNumHex);
        String logIndexStr = (String) l.get("logIndex");
        BigInteger logIndex = Numeric.decodeQuantity(logIndexStr);
        Boolean removed = (Boolean) l.get("removed");
//        String methed_id = topic0.substring(0, 10);
        EthEventTransferModel transfer = new EthEventTransferModel();
        transfer.setId(blockNumber +"-"+logIndex);
        setTransferInfo(blockNumber, transactionHash, type, timestamp, address, from, to, logIndex, removed, tokenId, tokenNum, transfer);
        transfer.setOperator(operator);
        transfer.setMethodType(TransferConst.TRANSFER_SINGLE);
        return transfer;
    }
    @Override
    public List<EthEventTransferModel> getBatchEthEventTransferModels(BigInteger blockNumber, String transactionHash, String type, Date timestamp, Map l, String address, List<String> topics) {
        String operator= topics.get(1).replace("0x000000000000000000000000","0x");
        String from = topics.get(2).replace("0x000000000000000000000000","0x");
        String to =  topics.get(3).replace("0x000000000000000000000000","0x");
        String data = (String) l.get("data");
        //$tokens = [];
        //        $str = substr($str,194,strlen($str));
        //        $count = (strlen($str) / 64 - 1) / 2;
        //        for($i=0;$i<$count;$i++){
        //            $tokens[$i] = ["tokenId"=>self::toInt("0x".substr($str,$i*64,64))];
        //        }
        //        $str = substr($str,64*$count+64);
        //        for($i=0;$i<$count;$i++){
        //            $tokens[$i]["tokenNum"] = self::toInt("0x".substr($str,$i*64,64));
        //        }
        data = data.substring(194);
        int count = (data.length() / 64 - 1) / 2;
        String[] tokenIdArr = new String[count];
        BigInteger[] tokenNumArr = new BigInteger[count];
        for(int i = 0;i<count;i++){
            tokenIdArr[i] = "0x" + data.substring(i*64,i*64+64);
        }
        data = data.substring(64*count+64);
        for(int i = 0;i<count;i++){
            String hex = "0x" + data.substring(i * 64, i * 64 + 64);
            String tokenNumHex = dealHex(hex);
            tokenNumArr[i] = Numeric.decodeQuantity(tokenNumHex);
        }
        String logIndexStr = (String) l.get("logIndex");
        BigInteger logIndex = Numeric.decodeQuantity(logIndexStr);
        Boolean removed = (Boolean) l.get("removed");
//        String methed_id = topic0.substring(0, 10);
        List<EthEventTransferModel> templateList = new ArrayList<>();
        for(int i = 0;i<count;i++){
            String tokenId = tokenIdArr[i];
            BigInteger tokenNum = tokenNumArr[i];
            EthEventTransferModel transfer = new EthEventTransferModel();
            transfer.setId(blockNumber +"-"+logIndex + "-"+i);
            setTransferInfo(blockNumber, transactionHash, type, timestamp, address, from, to, logIndex, removed, tokenId, tokenNum, transfer);
            transfer.setOperator(operator);
            transfer.setMethodType(TransferConst.TRANSFER_BATCH);
            templateList.add(transfer);
        }
        return templateList;
    }
    private static void setTransferInfo(BigInteger blockNumber, String transactionHash, String type, Date timestamp, String address, String from, String to, BigInteger logIndex, Boolean removed, String tokenId, BigInteger tokenNum, EthEventTransferModel transfer) {
        transfer.setBlockNumber(blockNumber.longValue());
        transfer.setLogIndex(logIndex.intValue());
        transfer.setTxnHash(transactionHash);
        transfer.setRemoved(removed?1:0);
        transfer.setType(type);
        transfer.setTimestamp(timestamp);
        transfer.setTokenAddress(address);
        transfer.setFromAddress(from);
        transfer.setToAddress(to);
        transfer.setTokenId(tokenId);
        transfer.setTokenValue(tokenNum);
        transfer.setCreatedAt(new Date());
        transfer.setUpdatedAt(new Date());
    }
    @NotNull
    private static String dealHex(String hex) {
        String tokenNumHex = hex.replaceAll("0x[0]*", "0x");
        if("0x".equals(tokenNumHex)){
            tokenNumHex = "0x0";
        }
        return tokenNumHex;
    }
}
