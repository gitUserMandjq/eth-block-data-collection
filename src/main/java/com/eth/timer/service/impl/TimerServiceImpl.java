package com.eth.timer.service.impl;

import com.eth.block.service.IEthBlockService;
import com.eth.ens.model.EthEnsInfoModel;
import com.eth.etlTask.service.IEtlTaskService;
import com.eth.framework.base.sysMessage.model.SysErrorMessageModel;
import com.eth.framework.base.sysMessage.service.ISysMessageService;
import com.eth.timer.service.ITimerService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

@Service
public class TimerServiceImpl implements ITimerService {
    @Resource
    IEtlTaskService etlTaskService;
    @Resource
    ISysMessageService sysMessageService;
    @Resource
    IEthBlockService ethBlockService;

    @Override
    public void dealEtlTask(Long high, Integer batchNumber) throws Exception {
        Long start = getStartBlockNumber();
        dealEtlTask(start, high, batchNumber);
    }

    private Long getStartBlockNumber() {
        Long start = sysMessageService.getMaxBlockNumber(SysErrorMessageModel.TYPE_ETHTASK);
        if(start == null){
            start = EthEnsInfoModel.createBlockHeight;
        }else{
            start ++;
        }
        return start;
    }

    @Override
    public void dealEtlTask(Integer batchNumber) throws Exception {
        Long start = getStartBlockNumber();
        Long end = ethBlockService.getCurrentBlockNumber().longValue();
        if(end - start > 10000){//一次最多处理1W条，大约5分钟
            end = start + 10000;
        }
        dealEtlTask(start, end, batchNumber);
    }

    /**
     * 处理某个范围的区块链任务
     * @param start
     * @param high
     * @param batchNumber
     * @throws Exception
     */
    @Override
    public void dealEtlTask(Long start, Long high, Integer batchNumber) throws Exception {
        if(start > high){
            return;
        }
        CountDownLatch latch = new CountDownLatch((int)(high - start + 1));
        Semaphore lock = new Semaphore(20);
        Date startTime = new Date();
        for(long i = start; i<= high; i = i+batchNumber){
            long begin = i;
            long end = i+batchNumber - 1;
            if(end > high){
                end = high;
            }
            List<Long> blockNumber = new ArrayList<>();
            for(long j=begin;j<=end;j++){
                blockNumber.add(j);
            }
            etlTaskService.etlEns(blockNumber, 0, latch, lock);
//      etlTaskService.etlEthBlock(i, 0, latch, lock);
            System.out.println("dealEtlTask：allTime"+i+":"+(new Date().getTime() - startTime.getTime()));
        }
        latch.await();
        System.out.println("dealEtlTask：totalAllTime:"+(new Date().getTime() - startTime.getTime()));
    }
    /**
     * 处理错误的任务
     * @throws Exception
     */
    @Override
    public void dealErrorEthTask() throws Exception {
        etlTaskService.dealErrorEth(5000);
    }
}
