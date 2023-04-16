package com.eth;

import com.eth.account.service.IAccountService;
import com.eth.etlTask.service.IEtlTaskProcessService;
import com.eth.listener.service.IEthBlockListenerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class StartInit implements ApplicationListener<ApplicationStartedEvent> {
    @Value("${startInit.enabled:1}")
    private String enabled;

    public
    @Resource
    IEtlTaskProcessService etlTaskProcessService;
    @Resource
    IEthBlockListenerService ethBlockListenerService;
    @Resource
    IAccountService accountService;
    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        log.info("startInit.enabled：{}", enabled);
        if("1".equals(enabled)){
            //开始交易计算任务
            etlTaskProcessService.startEtlTaskProcessService();
            //启动聪明钱包监听器
            ethBlockListenerService.startInitEthListenerAll();
            //加载合约地址
            accountService.initContractMap();
        }
    }
}
