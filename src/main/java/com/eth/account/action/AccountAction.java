package com.eth.account.action;

import com.eth.account.service.IAccountService;
import com.eth.framework.base.common.model.WebApiBaseResult;
import com.eth.framework.base.common.utils.ExcelUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.RequestMapping;
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

    @RequestMapping(value = "/analyzeAccountType")
    public WebApiBaseResult listEnsDomain(HttpSession httpSession, HttpServletRequest request
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
}
