package com.eth.etlTask.service;

import com.eth.etlTask.model.EtlTaskProcessModel;

import java.util.Date;
import java.util.List;

public interface IEtlTaskProcessService {
    /**
     * 新增etl任务（如果任务已经完成则不返回）
     * @param type
     * @param starttime
     * @param endtime
     * @return
     */
    List<EtlTaskProcessModel> addEtlTaskProcess(String type, Date starttime, Date endtime) throws Exception;
    /**
     * 执行任务
     * @param process
     * @return
     */
    void processEtlTaskProcessService(EtlTaskProcessModel process) throws Exception;
}
