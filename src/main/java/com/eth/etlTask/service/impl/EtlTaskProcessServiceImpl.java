package com.eth.etlTask.service.impl;

import com.eth.etlTask.dao.EtlTaskProcessDao;
import com.eth.etlTask.model.EtlTaskProcessModel;
import com.eth.etlTask.service.IEtlTaskProcessService;
import com.eth.etlTask.service.IEtlTaskService;
import com.eth.framework.base.common.constant.KeyConst;
import com.eth.framework.base.common.utils.ObjectManageUtils;
import com.eth.framework.base.common.utils.Web3jUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

@Service
@Slf4j
public class EtlTaskProcessServiceImpl implements IEtlTaskProcessService {
    @Resource
    EtlTaskProcessDao etlTaskProcessDao;
    @Resource
    IEtlTaskService etlTaskService;
//    static Map<Long, EtlTaskProcessModel> processTaskMap = new HashMap<>();//保存正在执行的任务
    /**
     * 新增etl任务（如果任务已经完成则不返回）
     * @param type
     * @param starttime
     * @param endtime
     * @return
     */
    @Override
    public List<EtlTaskProcessModel> addEtlTaskProcess(String type, Date starttime, Date endtime) throws Exception {
        //先根据开始时间和结束时间直接查询
        List<EtlTaskProcessModel> etlTaskList = etlTaskProcessDao.listEtlTaskProcessEquals(type, starttime, endtime);
        List<EtlTaskProcessModel> notStartOrProcessList = new ArrayList<>();
        if(!etlTaskList.isEmpty()){
            EtlTaskProcessModel process = etlTaskList.get(0);
            //如果任务未完成则返回
            if(!EtlTaskProcessModel.STATUS_FINISH.equals(process.getStatus())){
                notStartOrProcessList.add(process);
            }
            return notStartOrProcessList;
        }
        List<EtlTaskProcessModel> passTaskList = etlTaskProcessDao.listEtlTaskProcessPass(type, starttime, endtime);
        if(passTaskList.isEmpty()){//如果不存在任何任务，新增etl任务
            EtlTaskProcessModel etlTaskProcessModel = addTaskProcess(type, starttime, endtime);
            notStartOrProcessList.add(etlTaskProcessModel);
        }else{
            EtlTaskProcessModel temp = null;
            for(EtlTaskProcessModel process:passTaskList){
                if(temp == null){
                    if(process.getStarttime().getTime() > starttime.getTime()){
                        //如果通过的第一个任务的开始时间大于开始时间，则新增一个任务
                        EtlTaskProcessModel etlTaskProcessModel = addTaskProcess(type, starttime, process.getStarttime());
                        notStartOrProcessList.add(etlTaskProcessModel);
                    }
                }else{
                    if(temp.getEndtime().getTime() < process.getStarttime().getTime()){//如果通过的任务不连续，则新增任务
                        EtlTaskProcessModel etlTaskProcessModel = addTaskProcess(type, temp.getEndtime(), process.getStarttime());
                        notStartOrProcessList.add(etlTaskProcessModel);
                    }
                }
                temp = process;
            }
            if(temp.getEndtime().getTime() < endtime.getTime()){
                //如果通过的最后一个任务的结束时间小于结束时间，则新增一个任务
                EtlTaskProcessModel etlTaskProcessModel = addTaskProcess(type, temp.getEndtime(), endtime);
                notStartOrProcessList.add(etlTaskProcessModel);
            }
        }
        return notStartOrProcessList;
    }
    /**
     * 执行任务
     * @param process
     * @return
     */
    @Async
    @Override
    public void processEtlTaskProcessService(EtlTaskProcessModel process) throws Exception {
        String key = KeyConst.PROCESSTASKMAP + process.getId();
        if(ObjectManageUtils.containsValue(key)){//任务正在执行
            return;
        }
        ObjectManageUtils.setValue(key, process);
        //开始任务
        processedTask(process);
        Long startBlockNumber = process.getCurrentBlockNumber();
        Long endBlockNumber = process.getEndBlockNumber();
        Integer batchNum = 20;
        log.info("startNumber:{},endNumer:{}", startBlockNumber, endBlockNumber);
        CountDownLatch latch = new CountDownLatch((int)(endBlockNumber - startBlockNumber + 1));
        Semaphore lock = new Semaphore(20);
        for(long i = startBlockNumber;i<=endBlockNumber;i+=batchNum){
            long end = i + batchNum - 1;
            if(end > endBlockNumber){
                end = endBlockNumber;
            }
            List<Long> blockNumberList = new ArrayList<>();
            for(long j=i;j<=end;j++){
                blockNumberList.add(j);
            }
            //改变当前任务
            changeCurrent(process, end);
            etlTaskService.etlCommonBlock(blockNumberList, 0, true, latch, lock);
        }
        //阻塞直到任务完成
        latch.await();
        //结束任务
        finishProcessTask(process);
        ObjectManageUtils.remove(key);
    }
    /**
     * 开始执行未完成的任务
     * @throws Exception
     */
    @Override
    public void startEtlTaskProcessService() {
        List<EtlTaskProcessModel> list = etlTaskProcessDao.listEtlTaskProcessProcess();
        for(EtlTaskProcessModel task:list){
            try {
                processEtlTaskProcessService(task);
            }catch (Exception e){
                log.error(e.getMessage(), e);
            }
        }
    }

    private void changeCurrent(EtlTaskProcessModel process, long end) {
        process.setCurrentBlockNumber(end);
        etlTaskProcessDao.save(process);
    }

    private void finishProcessTask(EtlTaskProcessModel process) {
        process.setStatus(EtlTaskProcessModel.STATUS_FINISH);
        etlTaskProcessDao.save(process);
    }

    private void processedTask(EtlTaskProcessModel process) {
        process.setStatus(EtlTaskProcessModel.STATUS_PROCESSED);
        etlTaskProcessDao.save(process);
    }

    private EtlTaskProcessModel addTaskProcess(String type, Date starttime, Date endtime) throws Exception {
        EtlTaskProcessModel model = new EtlTaskProcessModel();
        model.setStarttime(starttime);
        model.setEndtime(endtime);
        //获取开始区块
        Long startBlockNumber = Web3jUtil.getBlockNumberByDate(starttime, Web3jUtil.Closest.BEFORE);
        model.setStartBlockNumber(startBlockNumber);
        model.setCurrentBlockNumber(startBlockNumber);
        Long endBlockNumber = Web3jUtil.getBlockNumberByDate(starttime, Web3jUtil.Closest.AFTER);
        model.setEndBlockNumber(endBlockNumber);
        model.setCreatedAt(new Date());
        model.setUpdatedAt(new Date());
        model.setStatus(EtlTaskProcessModel.STATUS_NEW);
        model.setType(type);
        model = etlTaskProcessDao.save(model);
        return model;
    }
}
