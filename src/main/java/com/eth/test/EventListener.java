package com.eth.test;

import com.eth.framework.base.common.utils.Web3jUtil;
import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.EthBlockNumber;
import org.web3j.protocol.core.methods.response.Log;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

public class EventListener {

    public static void main(String[] args) throws Exception {
        // 创建一个web3j对象，连接到以太坊节点
        Web3j web3j = Web3jUtil.getInstance().getWeb3j();
        Request<?, EthBlockNumber> request = Web3jUtil.getInstance().getWeb3j().ethBlockNumber();
        BigInteger blockNumber = request.send().getBlockNumber();
        System.out.println();
        Long start = 16895591L;
        System.out.println(blockNumber.longValue() - start);
        // 创建一个EthFilter对象，指定要监听的区块范围和合约地址
//        EthFilter ethFilter = new EthFilter(DefaultBlockParameterName.LATEST,
//                DefaultBlockParameterName.LATEST,
//                "0x7fc66500c84a76ad7e9c93437bfc5ac33e2ddae9");
        EthFilter ethFilter = new EthFilter(DefaultBlockParameter.valueOf(BigInteger.valueOf(start)),
                DefaultBlockParameterName.LATEST,
                "0x7fc66500c84a76ad7e9c93437bfc5ac33e2ddae9");
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
            // 在这里处理每个事件日志
            System.out.println("Event log received: " + log);
            // 你可以根据事件日志中的主题和数据来解析具体的事件类型和参数
            String from = log.getTopics().get(1);
            String to = log.getTopics().get(2);
            String value = log.getData();
            System.out.println("Transfer event: from " + from + " to " + to + " value " + value);
            // 其他类型的事件类似处理
        });
//        Thread.sleep(5000L);
        System.out.println(subscribe.isDisposed());
        subscribe.dispose();
        System.out.println(subscribe.isDisposed());
    }

}