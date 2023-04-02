package com.eth.listener.service.impl;

import com.eth.account.service.IAccountService;
import com.eth.block.service.IEthBlockService;
import com.eth.event.model.EthEventTransferModel;
import com.eth.event.model.EthEventTransferSmartModel;
import com.eth.event.service.IEthEventTransferService;
import com.eth.framework.base.common.utils.Web3jUtil;
import com.eth.listener.dao.EthBlockListenerDao;
import com.eth.listener.model.EthBlockListenerEventConst;
import com.eth.listener.model.EthBlockListenerModel;
import com.eth.listener.service.IEthBlockListenerService;
import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.Log;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigInteger;
import java.util.*;

@Service
public class EthBlockListenerServiceImpl implements IEthBlockListenerService {
    @Resource
    EthBlockListenerDao ethBlockListenerDao;
    @Resource
    IEthBlockService ethBlockService;
    @Resource
    IEthEventTransferService ethEventTransferService;
    @Resource
    IAccountService accountService;
    private static final Map<String, Disposable> listenerMap = new HashMap<>();
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addBatchEthListener(Iterable<EthBlockListenerModel> list) throws Exception {
        Iterator<EthBlockListenerModel> iterator = list.iterator();
        while(iterator.hasNext()){
            EthBlockListenerModel next = iterator.next();
            next.setId(next.getType()+"-"+next.getContractAddress()+"-"+next.getEvent());
            next.setCreatedAt(new Date());
            next.setUpdatedAt(new Date());
        }
        ethBlockListenerDao.batchIgnoreSave(list, 500);
    }
    @Override
    public void startEthListener(EthBlockListenerModel listener) throws IOException {
        BigInteger currentBlockNumber = ethBlockService.getCurrentBlockNumber();
        startEthListener(listener, currentBlockNumber);
    }
    public void startEthListener(EthBlockListenerModel listener, BigInteger currentBlockNumber) throws IOException {
        if(!listenerMap.containsKey(listener.getId())){//监听器未启动
            // 创建一个web3j对象，连接到以太坊节点
            Web3j web3j = Web3jUtil.getInstance().getWeb3j();
            DefaultBlockParameter start;
            if(listener.getBlockNumber() == null || currentBlockNumber.longValue() - listener.getBlockNumber() > 10000){
                //经过初步测试可以监听的范围是66000多区块
                start = DefaultBlockParameterName.LATEST;
            }else{
                start = DefaultBlockParameter.valueOf(BigInteger.valueOf(listener.getBlockNumber()));
            }
            EthFilter ethFilter = new EthFilter(start,
                    DefaultBlockParameterName.LATEST,
                    listener.getContractAddress());
            if(EthBlockListenerEventConst.TRANSFER.equals(listener.getEvent())){
                Event event = new Event("Transfer",
                        Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {
                                                        }, new TypeReference<Address>() {
                                                        },
                                new TypeReference<Uint256>() {
                                }));
                ethFilter.addSingleTopic(EventEncoder.encode(event));
                // 使用web3j.ethLogFlowable(ethFilter)方法来获取一个可观察的流，它会发出匹配的事件日志
                Flowable<Log> logFlowable = web3j.ethLogFlowable(ethFilter);
                Disposable subscribe = web3j.ethLogFlowable(ethFilter).takeWhile(log -> true).subscribe(log -> {
                    // Process the event
                    List<String> topics = log.getTopics();
                    String from;
                    String to;
                    String data = log.getData();
                    String address = log.getAddress();
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
                    //查询监听的聪明钱包
                    Set<String> smartContractByTokenAddress = accountService.getSmartContractByTokenAddress(address);
                    if(smartContractByTokenAddress.contains(from) || smartContractByTokenAddress.contains(to)){
                        BigInteger blockNumber = log.getBlockNumber();
                        String transactionHash = log.getTransactionHash();
                        String type = log.getType();
                        Date timestamp = new Date();//没有时间，得从区块信息里取,暂时使用当前时间
                        BigInteger logIndex = log.getLogIndex();
                        Boolean removed = false;
                        // 在这里处理每个事件日志
                        EthEventTransferModel transfer = ethEventTransferService.getEthEventTransferModel(from, to, blockNumber, transactionHash, type, timestamp, data, logIndex, removed, address, topics);
                        EthEventTransferSmartModel smart = new EthEventTransferSmartModel(transfer);
                        //新增聪明钱包交易记录
                        ethEventTransferService.addEventTransferSmart(smart);
                    }
                });
                listenerMap.put(listener.getId(), subscribe);
            }
        }
    }

    @Override
    public void stopEthListener(EthBlockListenerModel listener) {
        if(listenerMap.containsKey(listener.getId())){
            Disposable disposable = listenerMap.get(listener);
            if(!disposable.isDisposed()){
                disposable.dispose();
            }
            listenerMap.remove(listener.getId());
        }
    }

    @Override
    public void startInitEthListenerAll() throws IOException {
        BigInteger currentBlockNumber = ethBlockService.getCurrentBlockNumber();
        List<EthBlockListenerModel> all = ethBlockListenerDao.findAll();
        for(EthBlockListenerModel listener:all){
            if(listener.isEnable()){
                startEthListener(listener, currentBlockNumber);
            }
        }
    }
}
