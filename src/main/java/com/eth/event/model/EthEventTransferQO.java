package com.eth.event.model;

import lombok.Data;

import java.util.Date;
@Data
public class EthEventTransferQO {
    //查询买入或卖出 买入0 卖出1
    private String transType;
    //开始时间
    private Date startTime;
    //结束时间
    private Date endTime;
}
