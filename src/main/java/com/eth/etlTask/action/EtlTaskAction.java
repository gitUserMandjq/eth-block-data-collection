package com.eth.etlTask.action;

import com.eth.etlTask.model.EtlTaskProcessModel;
import com.eth.etlTask.model.TaskProcessDashDTO;
import com.eth.etlTask.service.IEtlTaskProcessService;
import com.eth.etlTask.service.IEtlTaskService;
import com.eth.framework.base.common.model.WebApiBaseResult;
import com.eth.framework.base.common.utils.JsonUtil;
import com.eth.framework.base.sysMessage.model.MessageConst;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/etl/task")
public class EtlTaskAction {
    @Resource
    IEtlTaskService etlTaskService;
    @Resource
    IEtlTaskProcessService etlTaskProcessService;
    @RequestMapping(value = "/etlCommonBlock")
    public WebApiBaseResult etlCommonBlock(HttpSession httpSession, HttpServletRequest request
            , @RequestParam("start") Long start
            , @RequestParam("end") Long end
            , HttpServletResponse response) throws Exception {
        etlTaskService.etlCommonBlockAsync(start, end, 20, false);
        return WebApiBaseResult.success();
    }
    @RequestMapping(value = "/dealErrorComtask")
    public WebApiBaseResult dealErrorComtask(HttpSession httpSession, HttpServletRequest request
            , @RequestParam("errNum") Integer errNum
            , HttpServletResponse response) throws Exception {
        etlTaskService.dealErrorComtask(errNum);
        return WebApiBaseResult.success();
    }

    /**
     * 生成数据
     * @param httpSession
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/addComTaskProcess")
    public WebApiBaseResult addComTaskProcess(HttpSession httpSession, HttpServletRequest request
            , @RequestParam("searchContent") String searchContent
//            ,@RequestParam(value = "startTime", required = false)@DateTimeFormat(pattern="yyyy-MM-dd") Date startTime
//            ,@RequestParam(value = "endTime", required = false)@DateTimeFormat(pattern="yyyy-MM-dd") Date endTime
            , HttpServletResponse response) throws Exception {

        List<Map> listMap = JsonUtil.string2ListMap(searchContent);
        SimpleDateFormat yyyyMMddHHmmss = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<EtlTaskProcessModel> taskList = new ArrayList<>();
        for(Map m:listMap){
            String transType = (String) m.get("transType");
            String startTimeStr = (String) m.get("startTime");
            String endTimeStr = (String) m.get("endTime");
            Date startTime = yyyyMMddHHmmss.parse(startTimeStr);
            Date endTime = yyyyMMddHHmmss.parse(endTimeStr);
            List<EtlTaskProcessModel> list = etlTaskProcessService.addEtlTaskProcess(MessageConst.TYPE_COMTASK, startTime, endTime);
            taskList.addAll(list);
        }
        if(taskList.isEmpty()){
            throw new Exception("数据已经生成");
        }
        TaskProcessDashDTO processDash = new TaskProcessDashDTO();
        for(EtlTaskProcessModel process:taskList){
            etlTaskProcessService.processEtlTaskProcessService(process);
            processDash.setDealNum(processDash.getDealNum() + process.getCurrentBlockNumber() - process.getStartBlockNumber());
            processDash.setTotalNum(processDash.getTotalNum() + process.getEndBlockNumber() - process.getStartBlockNumber());
        }
        return WebApiBaseResult.success(processDash);
    }


}
