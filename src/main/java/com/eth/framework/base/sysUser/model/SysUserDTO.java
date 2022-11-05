package com.eth.framework.base.sysUser.model;

import lombok.Data;

@Data
public class SysUserDTO {
    private Long user_id;
    private String user_name;

    public SysUserDTO(Long user_id, String user_name) {
        this.user_id = user_id;
        this.user_name = user_name;
    }
}
