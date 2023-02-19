package com.eth.account.service.impl;

import com.eth.account.service.IAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.BatchRequest;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.response.EthGetCode;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountServiceImpl implements IAccountService {
    @Autowired
    private Web3j web3j;
    /**
     * 获取账户代码，如果是非合约账户返回0x
     * @param address
     * @return
     */
    @Override
    public String getAccountCode(String address) throws Exception{
        Request<?, EthGetCode> request = web3j.ethGetCode(address, DefaultBlockParameterName.LATEST);
        String code = request.send().getCode();
        return code;
    }

    /**
     * 批量获取账户代码，如果是非合约账户返回0x
     * @param address
     * @return
     */
    @Override
    public List<String> getAccountCode(Iterable<String> address) throws Exception {
        if(address == null || !address.iterator().hasNext()){
            return new ArrayList<>();
        }
        BatchRequest batchRequest = web3j.newBatch();
        for (String addr : address) {
            Request<?, EthGetCode> request = web3j.ethGetCode(addr, DefaultBlockParameterName.LATEST);
            batchRequest.add(request);
        }
        List<? extends EthGetCode> responses = (List<? extends EthGetCode>) batchRequest.sendAsync().get().getResponses();
        return responses.stream().map(EthGetCode::getCode).collect(Collectors.toList());
    }
}
