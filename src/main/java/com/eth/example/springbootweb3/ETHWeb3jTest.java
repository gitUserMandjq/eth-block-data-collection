package com.eth.example.springbootweb3;


import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class ETHWeb3jTest {

    /**
     * 完成web3的初始化   下面的地址引入区块链节点地址
     */
//    public static Web3j web3j = Web3j.build(new HttpService("https://org:8545/"));
    public static String ETH_HOST = "J4ipt__b_exS1cez4CO9KhRkXEYWxUcJ,UdxThvKeKc9FbOwZZPtiWdjWs-_ex_pN,ml7esjvnZ2twDU8WenOz0U2c1iRXOyS5,K_oaFqRcqOsrpEgvdnm8RZN5-pqq04zQ,1dnGPiAwFzE35GHLaqXT1cLJtQAGjjGG,80tBHWkwsdPpnNVeDzFaIsf385mCDZuS,LRvE9ITS5Eg1w6Cngm3K8GpMAf8Tun1_";
    public static String HOST = "https://eth-mainnet.alchemyapi.io";
    public static String NULL_ADDRESS = "0x0000000000000000000000000000000000000000";
    public static String TRANSFER_EVENT_TOPIC = "0xddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef";
    public static String TRANSFER_EVENT_SGINGLE_TOPIC = "0xc3d58168c5ae7397731d063d5bbf3d657854427343f4c083240f7aacaa2d0f62";
    public static String TRANSFER_EVENT_BATCH_TOPIC = "0x4a39dc06d4c0dbc64b70af90fd698a233a518aa5d07e595d983b8c0526c8f7fb";
    public static String EVENT_NAME_REGISTERED = "0xb3d987963d01b2f68493b4bdb130988f157ea43070d4ad840fee0466ed9370d9";
    public static String ENS_ADDRESS = "0x57f1887a8bf19b14fc0df6fd9b2acc9af147ea85";
    public static Web3j web3j = Web3j.build(new HttpService(HOST+"/v2/J4ipt__b_exS1cez4CO9KhRkXEYWxUcJ"));
//    public static Web3j web3j = Web3j.build(new HttpService("https://mainnet.infura.io/v3/"));
//    static {
//        OkHttpClient.Builder builder = new OkHttpClient.Builder();
//        // 设置代理地址
//        SocketAddress sa = new InetSocketAddress("127.0.0.1", 7890);
//        builder.proxy(new Proxy(Proxy.Type.HTTP, sa));
//        web3j = Web3j.build(new HttpService("https://mainnet.infura.io/v3/8D57dAc649760e11660B38f201292095f0000eA8", builder.build()));
//    }
    public static void main(String[] args) {
        BigInteger latestBlock;
        try {
//            //获取ETH的最新区块号
//            latestBlock = web3j.ethBlockNumber().send().getBlockNumber();
//            log.info("最新区块数："+latestBlock);
//            //通过区块号获取交易
////            EthBlock.Block block = web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(
////                    latestBlock.subtract(new BigInteger("3"))), true).send().getBlock();
//            EthBlock.Block block = web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(
//                    BigInteger.valueOf(14924138L)), true).send().getBlock();
//            log.info("最新区块-3："+ JSONUtil.toJsonStr(block));
//            List<EthBlock.TransactionResult> ethGetBlance = block.getTransactions();
//            log.info("交易："+JSONUtil.toJsonStr(ethGetBlance.get(0)));
//            //通过hash获取交易
//            Optional<Transaction> transactions = web3j.ethGetTransactionByHash("0x32f0830fe141ffbf6642301aabc07ec1465772c1cbc1ab0a0c2713bec7438dfa").send().getTransaction();
//            log.info("交易："+JSONUtil.toJsonStr(transactions.get()));
//            EthBlock.TransactionObject transactionObject = (EthBlock.TransactionObject) ethGetBlance.get(0);
            Optional<TransactionReceipt> transactionReceipt = web3j.ethGetTransactionReceipt("0x50f87895cf8a385a492545b17e23a21046df2525159942b2fdfdfbea49d2a981").send().getTransactionReceipt();
            log.info("交易回执："+JSONUtil.toJsonStr(transactionReceipt.get()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<EthBlock.TransactionResult> txs = null;
        try {
            //也可以直接获取最新交易
            txs = web3j.ethGetBlockByNumber(DefaultBlockParameterName.LATEST, true).send().getBlock().getTransactions();
        } catch (IOException e) {
            e.printStackTrace();
        }
        txs.forEach(tx -> {
            EthBlock.TransactionObject transaction = (EthBlock.TransactionObject) tx.get();
//            System.out.println(transaction.getFrom());
        });
    }

}
