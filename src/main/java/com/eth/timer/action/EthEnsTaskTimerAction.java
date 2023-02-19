package com.eth.timer.action;

import com.eth.framework.base.common.model.WebApiBaseResult;
import com.eth.timer.service.ITimerService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/eth/ens/timer")
public class EthEnsTaskTimerAction {
    @Resource
    ITimerService timerService;

    /**
     * 生成ens数据
     * @param httpSession
     * @param request
     * @param start
     * @param high
     * @param batchNumer
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/dealEtlTask", method = RequestMethod.GET)
    public WebApiBaseResult dealEtlTask(HttpSession httpSession, HttpServletRequest request
            , @RequestParam(value = "start", required = false) Long start
            , @RequestParam(value = "high", required = false) Long high
            , @RequestParam(value = "batchNumer", required = false) Integer batchNumer) throws Exception {
        timerService.dealEtlEnsTask(start, high, batchNumer);
        return WebApiBaseResult.success();
    }
}
