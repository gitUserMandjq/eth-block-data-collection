package com.eth.event.service;

import com.eth.event.model.EthEventTransferModel;
import com.eth.event.model.EthEventTransferQO;
import com.eth.event.model.EthEventTransferSmartModel;
import com.eth.framework.base.common.model.PageData;
import com.eth.framework.base.common.model.PageParam;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IEthEventTransferService {
    /**
     * 批量新增交易事件
     * @param transferList
     * @return
     */
    void addBatchEventTransfer(List<EthEventTransferModel> transferList) throws Exception;
    /**
     * 批量新增交易事件
     * @param transferList
     * @return
     */
    void addBatchEventTransferSmart(List<EthEventTransferSmartModel> transferList) throws Exception;

    void addEventTransferSmart(EthEventTransferSmartModel transfer) throws Exception;

    /**
     *
     * @param address
     * @param transferQOList
     * @return
     * @throws Exception
     */
    Set<String> getSmartAddress(String address, List<EthEventTransferQO> transferQOList)throws Exception;

    EthEventTransferModel getEthEventTransferModel(BigInteger blockNumber, String transactionHash, String type, Date timestamp, String data, BigInteger logIndex, Boolean removed, String address, List<String> topics);

    EthEventTransferModel getEthEventTransferModel(String from, String to, BigInteger blockNumber, String transactionHash, String type, Date timestamp, String data, BigInteger logIndex, Boolean removed, String address, List<String> topics);

    EthEventTransferModel getSingleEthEventTransferModel(BigInteger blockNumber, String transactionHash, String type, Date timestamp, Map l, String address, List<String> topics);

    List<EthEventTransferModel> getBatchEthEventTransferModels(BigInteger blockNumber, String transactionHash, String type, Date timestamp, Map l, String address, List<String> topics);
    /**
     * 查询监听的聪明钱包交易
     * @param smartAddress
     * @param startTime
     * @param endTime
     * @param pageInfo
     * @return
     */
    PageData<EthEventTransferSmartModel> listAccountSmartTransfer(String smartAddress, Date startTime, Date endTime, PageParam pageInfo) throws Exception;
}
