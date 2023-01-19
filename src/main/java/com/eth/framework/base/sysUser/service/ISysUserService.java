package com.eth.framework.base.sysUser.service;

import com.eth.framework.base.sysUser.model.SysUserModel;

public interface ISysUserService {
    /**
     * 登录
     * @param address
     * @return
     * @throws Exception
     */
    SysUserModel logIn(String address)throws Exception;
    /**
     * 修改用户名称
     * @param userId
     * @param userName
     * @return
     * @throws Exception
     */
    SysUserModel modifyUserName(Long userId, String userName)throws Exception;
}
