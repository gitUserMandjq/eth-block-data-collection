package com.eth.account.service;

import java.util.List;

public interface IAccountService {
    /**
     * 获取账户代码，如果是非合约账户返回0x
     * @param address
     * @return
     */
    String getAccountCode(String address) throws Exception;
    /**
     * 批量获取账户代码，如果是非合约账户返回0x
     * @param address
     * @return
     */
    List<String> getAccountCode(Iterable<String> address) throws Exception;
}
