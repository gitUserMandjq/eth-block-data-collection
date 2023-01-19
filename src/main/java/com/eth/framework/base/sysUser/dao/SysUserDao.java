package com.eth.framework.base.sysUser.dao;

import com.eth.framework.base.sysUser.model.SysUserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface SysUserDao extends JpaRepository<SysUserModel,Long>  {
    /**
     * 根据地址查询用户
     * @param address
     * @return
     */
    @Query(value="select u from SysUserModel u where u.user_address = ?1")
    SysUserModel findByUserAddress(String address);
}
