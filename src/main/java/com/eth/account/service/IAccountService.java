package com.eth.account.service;

public interface IAccountService {
    /**
     * 获取账户代码，如果是非合约账户返回0x
     * @param address
     * @return
     */
    String getAccountCode(String address) throws Exception;
}
