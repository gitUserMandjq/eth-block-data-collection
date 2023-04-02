package com.eth.framework.base.common.utils;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
public class Web3jUtil {
    private static final byte[] LOCKER = new byte[0];
    private static Web3jUtil mInstance;
    private Web3j[] web3jList;
    private static final ConnectionPool CONNECTION_POOL = new ConnectionPool(256, 5L, TimeUnit.MINUTES);
    private int count;
    //炼金术的秘钥
    public static String ETH_HOST_STR = "J4ipt__b_exS1cez4CO9KhRkXEYWxUcJ,UdxThvKeKc9FbOwZZPtiWdjWs-_ex_pN,ml7esjvnZ2twDU8WenOz0U2c1iRXOyS5,K_oaFqRcqOsrpEgvdnm8RZN5-pqq04zQ,1dnGPiAwFzE35GHLaqXT1cLJtQAGjjGG,80tBHWkwsdPpnNVeDzFaIsf385mCDZuS,LRvE9ITS5Eg1w6Cngm3K8GpMAf8Tun1_";
    public static String isProxy = "0";
    static {
        try {
            InputStreamReader is = new InputStreamReader(
                    AlchemyUtils.class.getClassLoader().getResourceAsStream("application.properties"),
                    "utf-8");
            Properties property = new Properties();
            property.load(is);
            ETH_HOST_STR = PropertiesUtils.readValue(property, "alchemy.server.apptokens1")
                + "," + PropertiesUtils.readValue(property, "alchemy.server.apptokens2");
            isProxy = PropertiesUtils.readValue(property, "alchemy.server.isProxy");
        } catch (IOException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }
    private Web3jUtil() {
        String[] split = ETH_HOST_STR.split(",");
        web3jList = new Web3j[split.length];
        count = split.length;
        for(int i=0;i<split.length;i++){
            String key = split[i];
            String ip  = "https://eth-mainnet.alchemyapi.io/v2/"+key;
            OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
            builder.connectTimeout(30*1000, TimeUnit.MILLISECONDS);
            builder.writeTimeout(30*1000, TimeUnit.MILLISECONDS);
            builder.readTimeout(30*1000, TimeUnit.MILLISECONDS);
            OkHttpClient httpClient = builder.build();
            web3jList[i] = Web3j.build(new HttpService(ip,httpClient,false));
        }
    }
    public Web3jUtil(String tokens) {
        String[] split = tokens.split(",");
        web3jList = new Web3j[split.length];
        count = split.length;
        for(int i=0;i<split.length;i++){
            String key = split[i];
            String ip  = "https://eth-mainnet.alchemyapi.io/v2/"+key;
            OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
            builder.connectTimeout(30*1000, TimeUnit.MILLISECONDS);
            builder.writeTimeout(30*1000, TimeUnit.MILLISECONDS);
            builder.readTimeout(30*1000, TimeUnit.MILLISECONDS);
            OkHttpClient httpClient = builder.build();
            web3jList[i] = Web3j.build(new HttpService(ip,httpClient,false));
        }
    }

    /**
     * 单例模式获取 NetUtils
     *
     * @return {@link Web3jUtil}
     */
    public static Web3jUtil getInstance() {
        if (mInstance == null) {
            synchronized (LOCKER) {
                if (mInstance == null) {
                    mInstance = new Web3jUtil();
                }
            }
        }
        return mInstance;
    }
    public Web3j getWeb3j(){
        int index = new Random().nextInt(count);
        return web3jList[index];
    }
    public enum Closest{
        BEFORE("before"),AFTER("after");
        private String code;

        Closest(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }
    }
    public static Long getBlockNumberByDate(Date date, Closest closest) throws Exception {
        long timeInMillis = date.getTime()/1000L;
        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://api.etherscan.io/api").newBuilder();
        urlBuilder.addQueryParameter("module", "block");
        urlBuilder.addQueryParameter("action", "getblocknobytime");
        urlBuilder.addQueryParameter("timestamp", StringUtils.valueOf(timeInMillis));
        urlBuilder.addQueryParameter("closest", closest.getCode());//before,after
        urlBuilder.addQueryParameter("apikey", "1DJR9CXZM29FBPBCDTQJGGADRC3343WBF2");
// 构建HTTP请求
        String url = urlBuilder.build().toString();
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        // 设置代理地址
        if("1".equals(isProxy)){
            SocketAddress sa = new InetSocketAddress("127.0.0.1", 7890);
            builder.proxy(new Proxy(Proxy.Type.HTTP, sa));
        }
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
        return NumberUtils.longValueOf(result);
    }
}
