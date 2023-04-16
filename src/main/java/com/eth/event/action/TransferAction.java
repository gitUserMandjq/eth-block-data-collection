package com.eth.event.action;

import com.eth.account.model.EthContractsModel;
import com.eth.account.service.IAccountService;
import com.eth.event.model.EthEventTransferQO;
import com.eth.event.model.EthSmartAddressVO;
import com.eth.event.service.IEthEventTransferService;
import com.eth.framework.base.common.model.WebApiBaseResult;
import com.eth.framework.base.common.utils.ExcelUtils;
import com.eth.framework.base.common.utils.JsonUtil;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/eth/transfer")
public class TransferAction {
    @Resource
    IAccountService accountService;
    @Resource
    IEthEventTransferService ethEventTransferService;
    @RequestMapping(value = "/getSmartAddress")
    public WebApiBaseResult getSmartAddress(HttpSession httpSession, HttpServletRequest request
            , @RequestParam("contractAddress") String contractAddress
            , @RequestParam("searchContent") String searchContent
            , HttpServletResponse response) throws Exception {
        List<Map> listMap = JsonUtil.string2ListMap(searchContent);
        SimpleDateFormat yyyyMMddHHmmss = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<EthEventTransferQO> ethEventTransferQO = new ArrayList<>();
        for(Map m:listMap){
            String transType = (String) m.get("transType");
            String startTime = (String) m.get("startTime");
            String endTime = (String) m.get("endTime");
            EthEventTransferQO qo = new EthEventTransferQO();
            qo.setTransType(transType);
            qo.setStartTime(yyyyMMddHHmmss.parse(startTime));
            qo.setEndTime(yyyyMMddHHmmss.parse(endTime));
            ethEventTransferQO.add(qo);
        }
        Set<String> smartAddress = ethEventTransferService.getSmartAddress(contractAddress, ethEventTransferQO);
        List<EthSmartAddressVO> smartList = new ArrayList<>();
        EthContractsModel contract = accountService.getContractByAddress(contractAddress);
        if(contract != null){
            for(String address:smartAddress){
                smartList.add(new EthSmartAddressVO(address, contractAddress, contract.getName(), contract.getLogo()));
            }
        }else{
            for(String address:smartAddress){
                smartList.add(new EthSmartAddressVO(address, contractAddress));
            }
        }
        return WebApiBaseResult.success(smartList);
    }
    @RequestMapping(value = "/getSmartAddressExcel")
    public WebApiBaseResult getSmartAddressExcel(HttpSession httpSession, HttpServletRequest request
            , @RequestParam("contractAddress") String contractAddress
            , @RequestParam("searchContent") String searchContent
            , HttpServletResponse response) throws Exception {
        List<Map> listMap = JsonUtil.string2ListMap(searchContent);
        SimpleDateFormat yyyyMMddHHmmss = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<EthEventTransferQO> ethEventTransferQO = new ArrayList<>();
        for(Map m:listMap){
            String transType = (String) m.get("transType");
            String startTime = (String) m.get("startTime");
            String endTime = (String) m.get("endTime");
            EthEventTransferQO qo = new EthEventTransferQO();
            qo.setTransType(transType);
            qo.setStartTime(yyyyMMddHHmmss.parse(startTime));
            qo.setEndTime(yyyyMMddHHmmss.parse(endTime));
            ethEventTransferQO.add(qo);
        }
        Set<String> smartAddress = ethEventTransferService.getSmartAddress(contractAddress, ethEventTransferQO);
        List<EthSmartAddressVO> smartList = new ArrayList<>();
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet();
        int i = 0;
        for(String address:smartAddress){
            HSSFRow row = sheet.createRow(i);
            HSSFCell cell0 = row.createCell(0);//聪明钱包
            cell0.setCellValue(address);
            HSSFCell cell1 = row.createCell(1);//合约地址
            cell1.setCellValue(contractAddress);
            i++;
        }
        //导出excel文件
        ExcelUtils.exportExcel(request, response, wb, "聪明钱包");
        return WebApiBaseResult.success(smartList);
    }

    public static void main(String[] args) throws IOException, ParseException {
        String searchContent = "[{\"transType\":\"0\",\"startTime\":\"2023-1-1 00:00:00\",\"endTime\":\"2023-2-1 00:00:00\"}]";
        List<Map> listMap = JsonUtil.string2ListMap(searchContent);
        SimpleDateFormat yyyyMMddHHmmss = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<EthEventTransferQO> ethEventTransferQO = new ArrayList<>();
        for(Map m:listMap){
            String transType = (String) m.get("transType");
            String startTime = (String) m.get("startTime");
            String endTime = (String) m.get("endTime");
            EthEventTransferQO qo = new EthEventTransferQO();
            qo.setTransType(transType);
            qo.setStartTime(yyyyMMddHHmmss.parse(startTime));
            qo.setEndTime(yyyyMMddHHmmss.parse(endTime));
            ethEventTransferQO.add(qo);
        }
        System.out.println(ethEventTransferQO.get(0).getStartTime().getTime());
        System.out.println(yyyyMMddHHmmss.parse("2023-1-1 00:00:00").getTime());
        System.out.println(new Date(1679840393000L));
    }


}
