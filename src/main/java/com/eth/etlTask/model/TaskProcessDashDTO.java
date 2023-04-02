package com.eth.etlTask.model;

import lombok.Data;

@Data
public class TaskProcessDashDTO {
    //已处理区块数量
    private Long dealNum = 0L;
    //总区块数量
    private Long totalNum = 0L;
}
