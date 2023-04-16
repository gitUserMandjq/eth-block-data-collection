package com.eth.account.action;

import com.eth.account.model.EthAccountSmartContractModel;
import com.eth.account.model.EthAccountSmartModel;
import com.eth.account.service.IAccountService;
import com.eth.event.model.EthEventTransferSmartModel;
import com.eth.event.service.IEthEventTransferService;
import com.eth.framework.base.common.model.PageData;
import com.eth.framework.base.common.model.PageParam;
import com.eth.framework.base.common.model.WebApiBaseResult;
import com.eth.framework.base.common.utils.ExcelUtils;
import com.eth.framework.base.common.utils.PageUtils;
import com.eth.framework.base.common.utils.StringUtils;
import com.eth.listener.model.EthBlockListenerModel;
import com.eth.listener.service.IEthBlockListenerService;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.jetbrains.annotations.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.ByteArrayOutputStream;
import java.util.*;

@RestController
@RequestMapping("/eth/account")
public class AccountAction {
    @Resource
    IAccountService accountService;
    @Resource
    IEthBlockListenerService ethBlockListenerService;
    @Resource
    IEthEventTransferService ethEventTransferService;

    @RequestMapping(value = "/analyzeAccountType")
    public WebApiBaseResult analyzeAccountType(HttpSession httpSession, HttpServletRequest request
            , @RequestParam("file") MultipartFile file
            , HttpServletResponse response) throws Exception {
        WebApiBaseResult result = new WebApiBaseResult();
        if(file == null) {
            throw new Exception("文件不能为空");
        }
        Workbook wb = ExcelUtils.readExcel(file);
        Sheet sheet = wb.getSheetAt(0);
        //获取最大行数
        int rownum = sheet.getPhysicalNumberOfRows();
        List<String> addresses = new ArrayList<>();
        for (int i = 0; i < rownum; i++) {
            Row row = sheet.getRow(i);
            Cell adressCell = row.getCell(0);
            String address = ExcelUtils.getCellFormatValue(adressCell);
            //地址\x替换成0x
            address = address.replace("\\x","0x");
            addresses.add(address);
        }
        List<String> accountCodeList = accountService.getAccountCode(addresses);
        List<AccountType> accountTypeList = new ArrayList<>();
        for (int i = 0; i < rownum; i++) {
            Row row = sheet.getRow(i);
            Cell adressCell = row.getCell(0);
            String address = addresses.get(i);
//            String address = ExcelUtils.getCellFormatValue(adressCell);
//            //地址\x替换成0x
//            address = address.replace("\\x","0x");
            adressCell.setCellValue(address);
            AccountType accountType = new AccountType();
            accountType.setAddress(address);
            String accountCode = accountCodeList.get(i);
//            String accountCode = accountService.getAccountCode(address);
            accountTypeList.add(accountType);
            if("0x".equals(accountCode)){
                accountType.setType("外部账户");
            }else{
                accountType.setType("合约账户");
            }
            //在第二列插入地址类型
            ExcelUtils.addColumn(row, 1, accountType.getType());
        }
        String fileName = "账户类型解析";
        //导出excel文件
//        ExcelUtils.exportExcel(request, response, getAccountTypeExcel(accountTypeList), fileName);
//        wb.write();
        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        wb.write(byteArray);
        String base64 = Base64.getEncoder().encodeToString(byteArray.toByteArray());
        Map<String, Object> map = new HashMap<>();
        map.put("base64", base64);
        map.put("fileName", fileName);
        map.put("accountTypeList",accountTypeList);
        result.setData(map);
        return result;
    }
    @RequestMapping(value = "/importSmartAddress")
    public WebApiBaseResult importSmartAddress(HttpSession httpSession, HttpServletRequest request
            , @RequestParam("file") MultipartFile file
            , HttpServletResponse response) throws Exception {
        WebApiBaseResult result = new WebApiBaseResult();
        if(file == null) {
            throw new Exception("文件不能为空");
        }
        Workbook wb = ExcelUtils.readExcel(file);
        Sheet sheet = wb.getSheetAt(0);
        //获取最大行数
        int rownum = sheet.getPhysicalNumberOfRows();
        Map<String, EthAccountSmartModel> accountSmartMap = new HashMap<>();
        Map<String, EthBlockListenerModel> listenerMap = new HashMap<>();
        List<EthAccountSmartContractModel> accountSmartContractList = new ArrayList<>();
        for (int i = 0; i < rownum; i++) {
            Row row = sheet.getRow(i);
            Cell adressCell = row.getCell(0);
            //聪明钱包地址
            String address = ExcelUtils.getCellFormatValue(adressCell);
            Cell adressCell2 = row.getCell(1);
            //聪明钱包地址
            String contractAddress = ExcelUtils.getCellFormatValue(adressCell2);
            //地址\x替换成0x
            address = address.replace("\\x","0x");
            if(!accountSmartMap.containsKey(address)){
                EthAccountSmartModel smart = new EthAccountSmartModel();
                smart.setAddress(address);
                accountSmartMap.put(address, smart);
            }
            if(!listenerMap.containsKey(contractAddress)){
                EthBlockListenerModel listenerModel = new EthBlockListenerModel();
                listenerModel.setContractAddress(contractAddress);
                listenerModel.setType("event");
                listenerModel.setEvent("Transfer");
                listenerMap.put(contractAddress, listenerModel);
            }
            EthAccountSmartContractModel contractModel = new EthAccountSmartContractModel();
            contractModel.setAddress(address);
            contractModel.setTokenAddress(contractAddress);
            accountSmartContractList.add(contractModel);
        }
        accountService.addBatchAccountSmart(accountSmartMap.values());
        accountService.addBatchAccountSmartContract(accountSmartContractList);
        ethBlockListenerService.addBatchEthListener(listenerMap.values());
        return result;
    }

    /**
     * 删除聪明钱包地址
     * @param httpSession
     * @param request
     * @param address
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/deleteSmartAddress")
    public WebApiBaseResult deleteSmartAddress(HttpSession httpSession, HttpServletRequest request
            , @RequestParam("address") String address
            , HttpServletResponse response) throws Exception {
        accountService.deleteAccountSmart(address);
        return WebApiBaseResult.success();
    }


    @NotNull
    private static HSSFWorkbook getAccountTypeExcel(List<AccountType> accountTypeList) {
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet();
        for(int i = 0; i< accountTypeList.size(); i++) {
            HSSFRow row = sheet.createRow(i);
            AccountType type = accountTypeList.get(i);
            HSSFCell cell0 = row.createCell(0);//序号
            cell0.setCellValue(type.getAddress());
            HSSFCell cell1 = row.createCell(1);//公司名称
            cell1.setCellValue(type.getType());
        }
        return wb;
    }

    public static class AccountType{
        private String address;
        private String type;

        public AccountType() {
            super();
        }

        public AccountType(String address, String type) {
            this.address = address;
            this.type = type;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    /**
     * 查询导入钱包
     * @param httpSession
     * @param request
     * @param page
     * @param size
     * @param sidx
     * @param sord
     * @param startTime
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/listSmartAddress", method = RequestMethod.GET)
    public WebApiBaseResult listSmartAddress(HttpSession httpSession, HttpServletRequest request
            ,@RequestParam(value = "page", required = false) Integer page
            ,@RequestParam(value = "rows", required = false) Integer size
            ,@RequestParam(value = "sidx", required = false) String sidx
            ,@RequestParam(value = "sord", required = false) String sord
            ,@RequestParam(value = "tokenName", required = false) String tokenName
            ,@RequestParam(value = "startTime", required = false)@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") Date startTime
            ,@RequestParam(value = "endTime", required = false)@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") Date endTime) throws Exception {
        PageParam pageParam;
        if(StringUtils.isEmpty(sidx)){
            pageParam = PageUtils.constructPageParam(page, size, 0, "createdAt", "desc");
        }else{
            pageParam = PageUtils.constructPageParam(page, size, 0, sidx, sord);
        }
        PageData<EthAccountSmartModel> pageData = accountService.listAccountSmart(tokenName, startTime, pageParam);
        return WebApiBaseResult.success(pageData);
    }
    /**
     * 查询监听聪明钱包的交易
     * @param httpSession
     * @param request
     * @param page
     * @param size
     * @param sidx
     * @param sord
     * @param smartAddress
     * @param startTime
     * @param endTime
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/listSmartAddressTransfer", method = RequestMethod.GET)
    public WebApiBaseResult listSmartAddressTransfer(HttpSession httpSession, HttpServletRequest request
            ,@RequestParam(value = "page", required = false) Integer page
            ,@RequestParam(value = "rows", required = false) Integer size
            ,@RequestParam(value = "sidx", required = false) String sidx
            ,@RequestParam(value = "sord", required = false) String sord
            ,@RequestParam(value = "smartAddress", required = false) String smartAddress
            ,@RequestParam(value = "startTime", required = false)@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") Date startTime
            ,@RequestParam(value = "endTime", required = false)@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") Date endTime) throws Exception {
        PageParam pageParam;
        if(StringUtils.isEmpty(sidx)){
            pageParam = PageUtils.constructPageParam(page, size, 0, "timestamp", "desc");
        }else{
            pageParam = PageUtils.constructPageParam(page, size, 0, sidx, sord);
        }
        PageData<EthEventTransferSmartModel> pageData = ethEventTransferService.listAccountSmartTransfer(smartAddress, startTime, endTime, pageParam);
        return WebApiBaseResult.success(pageData);
    }
}
