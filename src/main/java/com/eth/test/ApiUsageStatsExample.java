package com.eth.test;

import com.eth.framework.base.common.utils.JsonUtil;
import com.eth.framework.base.common.utils.StringUtils;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * 查询炼金术api使用情况
 */
public class ApiUsageStatsExample {
    static SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyy-MM-dd");
    public static void main(String[] args) throws IOException, ParseException {
        // 导入OkHttp依赖，例如：
// implementation 'com.squareup.okhttp3:okhttp:4.9.2'

// 创建OkHttp客户端

// 指定Etherscan API URL和参数
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DATE, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date allStartTime = yyyyMMdd.parse("2018-1-1");
//        Date allStartTime = yyyyMMdd.parse("2023-1-1");
        while(calendar.getTimeInMillis() > allStartTime.getTime()){
            Date endTime = calendar.getTime();
            calendar.add(Calendar.MONTH, -1);
            Date startTime = calendar.getTime();
            String blockStart = getBlockNumberByDate(startTime, "before");
            String blockEnd = getBlockNumberByDate(endTime, "after");
            System.out.println(yyyyMMdd.format(startTime)+"\t"+yyyyMMdd.format(endTime)+"\t"+blockStart+"\t"+blockEnd);
        }
    }

    private static String getBlockNumberByDate(Date date, String closest) throws IOException {
        long timeInMillis = date.getTime()/1000L;
        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://api.etherscan.io/api").newBuilder();
        urlBuilder.addQueryParameter("module", "block");
        urlBuilder.addQueryParameter("action", "getblocknobytime");
        urlBuilder.addQueryParameter("timestamp", StringUtils.valueOf(timeInMillis));
        urlBuilder.addQueryParameter("closest", "before");//before,after
        urlBuilder.addQueryParameter("apikey", "1DJR9CXZM29FBPBCDTQJGGADRC3343WBF2");

// 构建HTTP请求
        String url = urlBuilder.build().toString();
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        SocketAddress sa = new InetSocketAddress("127.0.0.1", 7890);
        builder.proxy(new Proxy(Proxy.Type.HTTP, sa));
        OkHttpClient client = builder.build();
        Request request = new Request.Builder()
                .url(url)
                .build();
        String result;
// 发送HTTP请求并解析响应
        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();
            Map map = JsonUtil.string2Obj(responseBody);
            result = (String) map.get("result");
        }
        return result;
    }
}
