package com.eth.framework.base.sysUser.service.impl;

import com.eth.framework.base.sysUser.dao.SysUserDao;
import com.eth.framework.base.sysUser.model.SysUserModel;
import com.eth.framework.base.sysUser.service.ISysUserService;
import com.eth.framework.base.common.utils.SnowflakeIdUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Optional;

@Service
public class SysUserServiceImpl implements ISysUserService {
    @Resource
    SysUserDao sysUserDao;

    SnowflakeIdUtils idWorker = new SnowflakeIdUtils(0, 0);
    /**
     * 登录
     * @param address
     * @return
     * @throws Exception
     */
    @Override
    public SysUserModel logIn(String address) throws Exception {
        SysUserModel sysUser = sysUserDao.findByUserAddress(address);
        if(sysUser == null){
            sysUser = new SysUserModel();
            sysUser.setUser_id(idWorker.nextId());
            sysUser.setUser_address(address);
            sysUser.setCreate_time(new Date());
            sysUser.setEnabled(1);
            sysUser.setUser_name(address.substring(0,6));
            sysUser.setUser_nick_name(address.substring(0,6));
        }
        sysUser.setLast_login_time(new Date());
        sysUserDao.save(sysUser);
        return sysUser;
    }
    /**
     * 修改用户名称
     * @param userId
     * @param userName
     * @return
     * @throws Exception
     */
    @Override
    public SysUserModel modifyUserName(Long userId, String userName) throws Exception {
        Optional<SysUserModel> optional = sysUserDao.findById(userId);
        if(!optional.isPresent()){
            throw new Exception("User is not login");
        }
        SysUserModel sysUser = optional.get();
        sysUser.setUser_name(userName);
        sysUser.setUser_nick_name(userName);
        sysUserDao.save(sysUser);
        return sysUser;
    }
}
