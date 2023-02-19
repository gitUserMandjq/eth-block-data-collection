package com.eth.timer.schedule;

import com.eth.timer.service.ITimerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@ConditionalOnExpression(value="${scheduled.enabled:false}")
@Slf4j
public class EnsSchedule {
    @Resource
    ITimerService timerService;

    /**
     * 分析ens
     */
    @Scheduled(cron = "0 0/5 * * * ? ")   //每十分钟执行一次
    private void dealEtlTask() {
        log.info("开始执行定时器dealEtlTask");
        try {
            timerService.dealEtlEnsTask(20);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        log.info("结束执行定时器dealEtlTask");
    }

    /**
     * 处理报错的任务
     */
    @Scheduled(cron = "0 0/5 * * * ? ")   //每十分钟执行一次
    private void dealErrorEthTask() {
        log.info("开始执行定时器dealErrorEthTask");
        try {
            for(int i=0;i<5;i++){
                timerService.dealErrorEthTask(100);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        log.info("结束执行定时器dealErrorEthTask");
    }
}
