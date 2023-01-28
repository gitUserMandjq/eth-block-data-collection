package com.eth.account.service.impl;

import com.eth.account.service.IAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.response.EthGetCode;

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
}
