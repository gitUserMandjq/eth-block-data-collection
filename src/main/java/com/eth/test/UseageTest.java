package com.eth.test;

import com.eth.framework.base.common.utils.AlchemyUtils;
import com.eth.framework.base.common.utils.Web3jUtil;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.BatchRequest;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.response.EthBlock;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UseageTest {
    public static void main(String[] args) throws Exception {
        Long begin = 15527360L;
        Long end = 16819591L;
        Long diff = end - begin;
        Long testNum = 100L;
        Long span = diff/testNum;
        //2232610308@qq.com
        String token1 = "79knpzBnvGYKLiqI5R3680v0OI08NFsy";
        //flagdai@gmail.com
        String token2 = "vcFE11IHVQGktQhZynaGo3d5BS9i_dFm";
        Web3j web3j = new Web3jUtil(token1).getWeb3j();
        Web3j web3j2 = new Web3jUtil(token2).getWeb3j();
        {
            List<Long> numberList1 = getNumberList(begin + span * 51, 30);
//        List<Long> numberList2 = getNumberList(begin, 10);
//            getBlock(web3j, numberList1);
            AlchemyUtils.alchemygetTransactionReceipts(numberList1, token1);
        }
//        {
//            List<Long> numberList1 = getNumberList(begin + span * 52, 100);
////        List<Long> numberList2 = getNumberList(begin, 10);
////            getBlock(web3j2, numberList1);
//            AlchemyUtils.alchemygetTransactionReceipts(numberList1, token2);
//        }
    }
    private static List<Long> getNumberList(Long start, Integer count){
        List<Long> numberList = new ArrayList<>();
        for(int i=0;i<count;i++){
            numberList.add(start + i);
        }
        System.out.println("number:"+start+"\t"+(start + count));
        return numberList;
    }
    private static List<EthBlock.Block> getBlock(Web3j web3j, Iterable<Long> blockNumerList) throws IOException {
        BatchRequest batchRequest = web3j.newBatch();
        for (Long blockNumer : blockNumerList) {
            Request<?, EthBlock> request = web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(
                    BigInteger.valueOf(blockNumer)), true);
            batchRequest.add(request);
        }
//        List<? extends EthBlock> responses = (List<? extends EthBlock>) batchRequest.sendAsync().get().getResponses();
        List<? extends EthBlock> responses = (List<? extends EthBlock>) batchRequest.send().getResponses();
        List<EthBlock.Block> list = responses.stream().map(EthBlock::getBlock).collect(Collectors.toList());
        System.out.println("getBlockSize:"+list.size());
        return list;
    }
}
