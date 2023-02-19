package com.eth.framework.base.common.utils;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Response;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.util.*;

@Slf4j
public class AlchemyUtils {
    //复用builder，创建的client共享连接池、线程池和其他配置
    static OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder();
    //炼金术的域名
    public static String HOST = "https://eth-mainnet.alchemyapi.io";
    //炼金术的秘钥
    public static String ETH_HOST_STR = "J4ipt__b_exS1cez4CO9KhRkXEYWxUcJ,UdxThvKeKc9FbOwZZPtiWdjWs-_ex_pN,ml7esjvnZ2twDU8WenOz0U2c1iRXOyS5,K_oaFqRcqOsrpEgvdnm8RZN5-pqq04zQ,1dnGPiAwFzE35GHLaqXT1cLJtQAGjjGG,80tBHWkwsdPpnNVeDzFaIsf385mCDZuS,LRvE9ITS5Eg1w6Cngm3K8GpMAf8Tun1_";
    public static String[] ETH_HOST;
    public static final String ENSCONSTRACTADDRESS = "0x57f1887a8BF19b14fC0dF6Fd9B2acc9Af147eA85";//ENS的交换合约
    public static String isProxy = "0";
    static {
        try {
            InputStreamReader is = new InputStreamReader(
                    AlchemyUtils.class.getClassLoader().getResourceAsStream("application.properties"),
                    "utf-8");
            Properties property = new Properties();
            property.load(is);
            HOST = PropertiesUtils.readValue(property, "alchemy.server.host");
            ETH_HOST_STR = PropertiesUtils.readValue(property, "alchemy.server.apptokens");
            isProxy = PropertiesUtils.readValue(property, "alchemy.server.isProxy");
            ETH_HOST = ETH_HOST_STR.split(",");
        } catch (IOException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 获取随机秘钥
     * @return
     */
    public static String getRandomEthHost(){
        int index = new Random().nextInt(ETH_HOST.length);
        return ETH_HOST[index];
    }
    public static String getAlchemyPath(){
        return HOST + "/v2/" + getRandomEthHost();
    }
    public static String getAlchemyNftPath(){
        return HOST + "/nft"+ "/v2/" + getRandomEthHost();
    }

    /**
     * 获得合约信息
     * nft合约，{"address":"0x67b36ba196804db198ac4c0a8359a1fafa5e5cff","contractMetadata":{"name":"Meta RPG","symbol":"META","totalSupply":"2500","tokenType":"ERC721"}}
     * 非nft合约，{"address":"0xb8c77482e45f1f44de1745f52c74426c631bdd52","contractMetadata":{"name":"","symbol":"","totalSupply":"","tokenType":"UNKNOWN"}}
     * @param contractAddress
     * @return
     * @throws IOException
     */
    public String getContractMetadata(String contractAddress) throws IOException {
//        OkHttpClient client = okHttpBuilder.build();
        {
            String body = null;
            Date beginTime = new Date();
            String url = getAlchemyPath() + "/getContractMetadata?" +
                    "contractAddress=" + contractAddress;
            log.info("url:" + url);
//            MediaType mediaType = MediaType.parse("application/json");
//            Request request = new Request.Builder()
//                    .url(url)
//                    .get()
//                    .addHeader("Accept", "application/json")
//                    .build();
            try {
                body = new CallResponseHandle(new CallResponse() {
                    @Override
                    public Response newCall() {
                        return OkHttpClientUtil.getInstance().getData(url);
                    }
                }).callResponse();
//                body = callResponse(client, request);
                judgeResult(body);
            } catch (Exception e) {
                log.info("error:"+url);
                log.error(e.getMessage(), e);
                throw new RuntimeException(e);
            } finally {
                log.info("costTime:"+(new Date().getTime() - beginTime.getTime())+"ms");
            }
            return body;
        }
    }
    public static String getTokenMetadata(Iterable<String> contractAddress) throws IOException {
//        OkHttpClient client = okHttpBuilder.build();
        {
            String body = null;
            Date beginTime = new Date();
            String url = getAlchemyPath();
            log.info("url:" + url);
            Map<String, Object> paraData = new HashMap<>();
            paraData.put("id", 0L);
            paraData.put("jsonrpc","2.0");
            paraData.put("method","alchemy_getTokenMetadata");
            paraData.put("params", contractAddress);
//            MediaType mediaType = MediaType.parse("application/json");
            String requestParamStr = JsonUtil.object2String(paraData);
            log.info("requestParam:"+requestParamStr);
//            RequestBody requestBody = RequestBody.create(mediaType, requestParamStr);
//            Request request = new Request.Builder()
//                    .url(url)
//                    .post(requestBody)
//                    .addHeader("Accept", "application/json")
//                    .addHeader("content-type", "application/json")
//                    .build();
            try {
                body = new CallResponseHandle(new CallResponse() {
                    @Override
                    public Response newCall() {
                        return OkHttpClientUtil.getInstance().postData(url, paraData);
                    }
                }).callResponse();
//                body = callResponse(client, request);
                log.info("body:" + body);
                judgeResult(body);
            } catch (Exception e) {
                log.info("error:"+url);
                log.error(e.getMessage(), e);
                throw new RuntimeException(e);
            } finally {
                log.info("costTime:"+(new Date().getTime() - beginTime.getTime())+"ms");
            }
            return body;
        }
    }
    public static class ContractMetadataBatchParam{
        private Iterable<String> contractAddresses;

        public ContractMetadataBatchParam(Iterable<String> contractAddresses) {
            this.contractAddresses = contractAddresses;
        }

        public Iterable<String> getContractAddresses() {
            return contractAddresses;
        }

        public void setContractAddresses(Iterable<String> contractAddresses) {
            this.contractAddresses = contractAddresses;
        }
    }
    /**
     * 获得nft的数据
     *
     * @param contractAddresss
     * {"contractAddresses":["0xe785E82358879F061BC3dcAC6f0444462D4b5330","0xbc4ca0eda7647a8ab7c2061c2e118a18a936f13d"]}
     * @return
     * @throws IOException
     */
    public static String getContractMetadataBatch(Iterable<String> contractAddresss) throws IOException{
//        OkHttpClient.Builder builder = new OkHttpClient.Builder();
//        builder.connectTimeout(2, TimeUnit.MINUTES)
//                .writeTimeout(2, TimeUnit.MINUTES)
//                .readTimeout(2, TimeUnit.MINUTES);
//        // 设置代理地址
////        SocketAddress sa = new InetSocketAddress("127.0.0.1", 60959);
////        builder.proxy(new Proxy(Proxy.Type.HTTP, sa));
//        OkHttpClient client = builder.build();
        {
            String body = null;
            Date beginTime = new Date();
            String url = getAlchemyNftPath() + "/getContractMetadataBatch";
            log.info("url:" + url);
            //组装请求参数
            //{"contractAddresses":["0xe785E82358879F061BC3dcAC6f0444462D4b5330","0xbc4ca0eda7647a8ab7c2061c2e118a18a936f13d"]}
//            MediaType mediaType = MediaType.parse("application/json");
            ContractMetadataBatchParam paraMap = new ContractMetadataBatchParam(contractAddresss);
            String requestParamStr = JsonUtil.object2String(paraMap);
            log.info("requestParam:"+requestParamStr);
//            RequestBody requestBody = RequestBody.create(mediaType, requestParamStr);
//            Request request = new Request.Builder()
//                    .url(url)
//                    .post(requestBody)
//                    .addHeader("accept", "application/json")
//                    .addHeader("content-type", "application/json")
//                    .build();
            try {
                body = new CallResponseHandle(new CallResponse() {
                    @Override
                    public Response newCall() {
                        return OkHttpClientUtil.getInstance().postData(url, requestParamStr);
                    }
                }).callResponse();
//                body = callResponse(client, request);
                log.info("body:" + body);
                judgeResult(body);
            } catch (Exception e) {
                log.info("error:"+url);
                log.error(e.getMessage(), e);
                throw new RuntimeException(e);
            } finally {
                log.info("costTime:"+(new Date().getTime() - beginTime.getTime())+"ms");
            }
            return body;

        }
    }
    /**
     * 获得nft的数据
     * {"contract":{"address":"0x57f1887a8BF19b14fC0dF6Fd9B2acc9Af147eA85"},"id":{"tokenId":"102858502995565732640093888466685851349467133070378883292988898339044383603257","tokenMetadata":{"tokenType":"ERC721"}},"title":"manatees.eth","description":"manatees.eth, an ENS name.","tokenUri":{"raw":"https://metadata.ens.domains/mainnet/0x57f1887a8bf19b14fc0df6fd9b2acc9af147ea85/102858502995565732640093888466685851349467133070378883292988898339044383603257","gateway":"https://metadata.ens.domains/mainnet/0x57f1887a8bf19b14fc0df6fd9b2acc9af147ea85/102858502995565732640093888466685851349467133070378883292988898339044383603257"},"media":[{"raw":"https://metadata.ens.domains/mainnet/0x57f1887a8bf19b14fc0df6fd9b2acc9af147ea85/0xe367d97f9dd4e46f206e64aa04ec4cb0da200513b00eed0235bd8c2643c93a39/image","gateway":"https://metadata.ens.domains/mainnet/0x57f1887a8bf19b14fc0df6fd9b2acc9af147ea85/0xe367d97f9dd4e46f206e64aa04ec4cb0da200513b00eed0235bd8c2643c93a39/image"}],"metadata":{"is_normalized":true,"name":"manatees.eth","description":"manatees.eth, an ENS name.","attributes":[{"trait_type":"Created Date","display_type":"date","value":1651107892000},{"trait_type":"Length","display_type":"number","value":8},{"trait_type":"Segment Length","display_type":"number","value":8},{"trait_type":"Character Set","display_type":"string","value":"letter"},{"trait_type":"Registration Date","display_type":"date","value":1651107892000},{"trait_type":"Expiration Date","display_type":"date","value":1808892652000}],"name_length":8,"segment_length":8,"url":"https://app.ens.domains/name/manatees.eth","version":0,"background_image":"https://metadata.ens.domains/mainnet/avatar/manatees.eth","image":"https://metadata.ens.domains/mainnet/0x57f1887a8bf19b14fc0df6fd9b2acc9af147ea85/0xe367d97f9dd4e46f206e64aa04ec4cb0da200513b00eed0235bd8c2643c93a39/image","image_url":"https://metadata.ens.domains/mainnet/0x57f1887a8bf19b14fc0df6fd9b2acc9af147ea85/0xe367d97f9dd4e46f206e64aa04ec4cb0da200513b00eed0235bd8c2643c93a39/image"},"timeLastUpdated":"2022-09-11T10:23:10.626Z"}
     * @param contractAddress
     * @param tokenId
     * @return
     * @throws IOException
     */
    public static String getNFTMetadata(String contractAddress, String tokenId) throws IOException{
//        OkHttpClient.Builder builder = new OkHttpClient.Builder();
//        builder.connectTimeout(2, TimeUnit.MINUTES)
//                .writeTimeout(2, TimeUnit.MINUTES)
//                .readTimeout(2, TimeUnit.MINUTES);
//        // 设置代理地址
////        SocketAddress sa = new InetSocketAddress("127.0.0.1", 60959);
////        builder.proxy(new Proxy(Proxy.Type.HTTP, sa));
//        OkHttpClient client = builder.build();
        {
            String body = null;
            Date beginTime = new Date();
            String url = getAlchemyPath() + "/getNFTMetadata?" +
                    "contractAddress=" + contractAddress + "&tokenId=" + tokenId + "&refreshCache=false";
            log.info("url:" + url);
//            Request request = new Request.Builder()
//                    .url(url)
//                    .get()
//                    .addHeader("Accept", "application/json")
//                    //                    .addHeader("X-API-KEY", "null")
//                    .build();
            try {
                body = new CallResponseHandle(new CallResponse() {
                    @Override
                    public Response newCall() {
                        return OkHttpClientUtil.getInstance().getData(url);
                    }
                }).callResponse();
//                body = callResponse(client, request);
                log.info("body:" + body);
                judgeResult(body);
            } catch (Exception e) {
                log.info("error:"+url);
                log.error(e.getMessage(), e);
                throw new RuntimeException(e);
            } finally {
                log.info("costTime:"+(new Date().getTime() - beginTime.getTime())+"ms");
            }
            return body;

        }
    }
    public static class NFTMetadataBatchParam{
        private boolean refreshCache = false;
        private List<Tokens> tokens = new ArrayList<>();
        public Tokens createToken(String contractAddress, String tokenId, String tokenType){
            return new Tokens(contractAddress, tokenId, tokenType);
        }
        public class Tokens{
            private String tokenId;
            private String tokenType = "ERC721";
            private String contractAddress;

            public Tokens(String contractAddress, String tokenId, String tokenType) {
                this.tokenId = tokenId;
                this.tokenType = tokenType;
                this.contractAddress = contractAddress;
            }

            public String getTokenId() {
                return tokenId;
            }

            public void setTokenId(String tokenId) {
                this.tokenId = tokenId;
            }

            public String getTokenType() {
                return tokenType;
            }

            public void setTokenType(String tokenType) {
                this.tokenType = tokenType;
            }

            public String getContractAddress() {
                return contractAddress;
            }

            public void setContractAddress(String contractAddress) {
                this.contractAddress = contractAddress;
            }
        }

        public List<Tokens> getTokens() {
            return tokens;
        }

        public void setTokens(List<Tokens> tokens) {
            this.tokens = tokens;
        }

        public boolean isRefreshCache() {
            return refreshCache;
        }

        public void setRefreshCache(boolean refreshCache) {
            this.refreshCache = refreshCache;
        }

    }
    /**
     * 获得nft的数据
     * {"contract":{"address":"0x57f1887a8BF19b14fC0dF6Fd9B2acc9Af147eA85"},"id":{"tokenId":"102858502995565732640093888466685851349467133070378883292988898339044383603257","tokenMetadata":{"tokenType":"ERC721"}},"title":"manatees.eth","description":"manatees.eth, an ENS name.","tokenUri":{"raw":"https://metadata.ens.domains/mainnet/0x57f1887a8bf19b14fc0df6fd9b2acc9af147ea85/102858502995565732640093888466685851349467133070378883292988898339044383603257","gateway":"https://metadata.ens.domains/mainnet/0x57f1887a8bf19b14fc0df6fd9b2acc9af147ea85/102858502995565732640093888466685851349467133070378883292988898339044383603257"},"media":[{"raw":"https://metadata.ens.domains/mainnet/0x57f1887a8bf19b14fc0df6fd9b2acc9af147ea85/0xe367d97f9dd4e46f206e64aa04ec4cb0da200513b00eed0235bd8c2643c93a39/image","gateway":"https://metadata.ens.domains/mainnet/0x57f1887a8bf19b14fc0df6fd9b2acc9af147ea85/0xe367d97f9dd4e46f206e64aa04ec4cb0da200513b00eed0235bd8c2643c93a39/image"}],"metadata":{"is_normalized":true,"name":"manatees.eth","description":"manatees.eth, an ENS name.","attributes":[{"trait_type":"Created Date","display_type":"date","value":1651107892000},{"trait_type":"Length","display_type":"number","value":8},{"trait_type":"Segment Length","display_type":"number","value":8},{"trait_type":"Character Set","display_type":"string","value":"letter"},{"trait_type":"Registration Date","display_type":"date","value":1651107892000},{"trait_type":"Expiration Date","display_type":"date","value":1808892652000}],"name_length":8,"segment_length":8,"url":"https://app.ens.domains/name/manatees.eth","version":0,"background_image":"https://metadata.ens.domains/mainnet/avatar/manatees.eth","image":"https://metadata.ens.domains/mainnet/0x57f1887a8bf19b14fc0df6fd9b2acc9af147ea85/0xe367d97f9dd4e46f206e64aa04ec4cb0da200513b00eed0235bd8c2643c93a39/image","image_url":"https://metadata.ens.domains/mainnet/0x57f1887a8bf19b14fc0df6fd9b2acc9af147ea85/0xe367d97f9dd4e46f206e64aa04ec4cb0da200513b00eed0235bd8c2643c93a39/image"},"timeLastUpdated":"2022-09-11T10:23:10.626Z"}
     * @param contractAddress
     * @param tokenIds
     * @param tokenType ERC721
     * @return
     * @throws IOException
     */
    public static String getNFTMetadataBatch(String contractAddress, Set<String> tokenIds, String tokenType) throws IOException{
//        OkHttpClient.Builder builder = new OkHttpClient.Builder();
//        builder.connectTimeout(2, TimeUnit.MINUTES)
//                .writeTimeout(2, TimeUnit.MINUTES)
//                .readTimeout(2, TimeUnit.MINUTES);
//        // 设置代理地址
////        SocketAddress sa = new InetSocketAddress("127.0.0.1", 60959);
////        builder.proxy(new Proxy(Proxy.Type.HTTP, sa));
//        OkHttpClient client = builder.build();
        {
            String body = null;
            Date beginTime = new Date();
            String url = getAlchemyNftPath() + "/getNFTMetadataBatch";
            log.info("url:" + url);
            //组装请求参数
            //{
            //	"tokens": [{
            //		"tokenId": "55410952201049487871791681327342603684221801302599016058612157535488542032089",
            //		"tokenType": "ERC721",
            //		"contractAddress": "0x57f1887a8BF19b14fC0dF6Fd9B2acc9Af147eA85"
            //	}],
            //	"refreshCache": false
            //}
//            MediaType mediaType = MediaType.parse("application/json");
            NFTMetadataBatchParam paraMap = new NFTMetadataBatchParam();
            for(String tokenId:tokenIds){
                paraMap.getTokens().add(paraMap.createToken(contractAddress, tokenId, tokenType));
            }
            String requestParamStr = JsonUtil.object2String(paraMap);
            log.info("requestParam:"+requestParamStr);
//            RequestBody requestBody = RequestBody.create(mediaType, requestParamStr);
//            Request request = new Request.Builder()
//                    .url(url)
//                    .post(requestBody)
//                    .addHeader("accept", "application/json")
//                    .addHeader("content-type", "application/json")
//                    .build();
            try {
                body = new CallResponseHandle(new CallResponse() {
                    @Override
                    public Response newCall() {
                        return OkHttpClientUtil.getInstance().postData(url, requestParamStr);
                    }
                }).callResponse();
//                body = callResponse(client, request);
//                log.info("body:" + body);
                judgeResult(body);
            } catch (Exception e) {
                log.info("error:"+url);
                log.error(e.getMessage(), e);
                throw new RuntimeException(e);
            } finally {
                log.info("costTime:"+(new Date().getTime() - beginTime.getTime())+"ms");
            }
            return body;

        }
    }
    /**
     * 获得nft的数据
     * {"contract":{"address":"0x57f1887a8BF19b14fC0dF6Fd9B2acc9Af147eA85"},"id":{"tokenId":"102858502995565732640093888466685851349467133070378883292988898339044383603257","tokenMetadata":{"tokenType":"ERC721"}},"title":"manatees.eth","description":"manatees.eth, an ENS name.","tokenUri":{"raw":"https://metadata.ens.domains/mainnet/0x57f1887a8bf19b14fc0df6fd9b2acc9af147ea85/102858502995565732640093888466685851349467133070378883292988898339044383603257","gateway":"https://metadata.ens.domains/mainnet/0x57f1887a8bf19b14fc0df6fd9b2acc9af147ea85/102858502995565732640093888466685851349467133070378883292988898339044383603257"},"media":[{"raw":"https://metadata.ens.domains/mainnet/0x57f1887a8bf19b14fc0df6fd9b2acc9af147ea85/0xe367d97f9dd4e46f206e64aa04ec4cb0da200513b00eed0235bd8c2643c93a39/image","gateway":"https://metadata.ens.domains/mainnet/0x57f1887a8bf19b14fc0df6fd9b2acc9af147ea85/0xe367d97f9dd4e46f206e64aa04ec4cb0da200513b00eed0235bd8c2643c93a39/image"}],"metadata":{"is_normalized":true,"name":"manatees.eth","description":"manatees.eth, an ENS name.","attributes":[{"trait_type":"Created Date","display_type":"date","value":1651107892000},{"trait_type":"Length","display_type":"number","value":8},{"trait_type":"Segment Length","display_type":"number","value":8},{"trait_type":"Character Set","display_type":"string","value":"letter"},{"trait_type":"Registration Date","display_type":"date","value":1651107892000},{"trait_type":"Expiration Date","display_type":"date","value":1808892652000}],"name_length":8,"segment_length":8,"url":"https://app.ens.domains/name/manatees.eth","version":0,"background_image":"https://metadata.ens.domains/mainnet/avatar/manatees.eth","image":"https://metadata.ens.domains/mainnet/0x57f1887a8bf19b14fc0df6fd9b2acc9af147ea85/0xe367d97f9dd4e46f206e64aa04ec4cb0da200513b00eed0235bd8c2643c93a39/image","image_url":"https://metadata.ens.domains/mainnet/0x57f1887a8bf19b14fc0df6fd9b2acc9af147ea85/0xe367d97f9dd4e46f206e64aa04ec4cb0da200513b00eed0235bd8c2643c93a39/image"},"timeLastUpdated":"2022-09-11T10:23:10.626Z"}
     * @param contractAddress
     * @return
     * @throws IOException
     */
    public static String getNftsForContract(String contractAddress) throws IOException{
//        OkHttpClient.Builder builder = new OkHttpClient.Builder();
//        builder.connectTimeout(5, TimeUnit.MINUTES)
//                .writeTimeout(5, TimeUnit.MINUTES)
//                .readTimeout(5, TimeUnit.MINUTES);
//        // 设置代理地址
////        SocketAddress sa = new InetSocketAddress("127.0.0.1", 60959);
////        builder.proxy(new Proxy(Proxy.Type.HTTP, sa));
//        OkHttpClient client = builder.build();
        {
            String body = null;
            Date beginTime = new Date();
            String url = getAlchemyPath() + "/getNftsForContract?" +
                    "contractAddress=" + contractAddress + "&startToken=" + 0 + "&omitMetadata=false";
            log.info("url:" + url);
//            Request request = new Request.Builder()
//                    .url(url)
//                    .get()
//                    .addHeader("Accept", "application/json")
//                    //                    .addHeader("X-API-KEY", "null")
//                    .build();
            try {
                body = new CallResponseHandle(new CallResponse() {
                    @Override
                    public Response newCall() {
                        return OkHttpClientUtil.getInstance().getData(url);
                    }
                }).callResponse();
//                body = callResponse(client, request);
                log.info("body:" + body);
                judgeResult(body);
            } catch (Exception e) {
                log.info("error:"+url);
                log.error(e.getMessage(), e);
                throw new RuntimeException(e);
            } finally {
                log.info("costTime:"+(new Date().getTime() - beginTime.getTime())+"ms");
            }
            return body;

        }
    }

    /**
     * 判断返回值是否报错
     * @param body
     * @throws Exception
     */
    private static void judgeResult(String body) throws Exception {
        if(body.startsWith("{") && body.indexOf("\"error\":") > -1){
            log.info("返回错误信息 {}",body);
            Map<String, Object> resultMap = JsonUtil.string2Obj(body);
            Map<String, Object> error = (Map<String, Object>) resultMap.get("error");
            throw new Exception("炼金术接口报错："+ StringUtils.valueOf(error.get("code")) +StringUtils.valueOf(error.get("message")));
        }
    }
    interface CallResponse{
        public Response newCall();
    }
    static class CallResponseHandle{
        private CallResponse callResponse;

        public CallResponseHandle(CallResponse callResponse) {
            this.callResponse = callResponse;
        }

        public String callResponse(
//                OkHttpClient client, Request request
            ) throws Exception{

            int count = 0;
            int max = 3;
            String body = null;
            while(true){
                try {
//                    Response response = client.newCall(request).execute();
                    Response response = callResponse.newCall();
                    body = response.body().string();
                    //如果成功了跳出循环
                    break;
                } catch (Exception e) {
                    count++;
                    Thread.sleep(1000L);
                    log.info("retry:"+count);
                    if(count >= max){
                        throw e;
                    }
                }
            }
            return body;
        }
    }
    /**
     * 通过https://metadata.ens.domains/mainnet获取已过期得ens信息,得翻墙走代理
     * 已过期的meta
     * {"message":"'caizhuoyan.eth' is already been expired at Mon, 04 May 2020 00:00:00 GMT."}
     * 未过期的meta
     * {"is_normalized":true,"name":"rehbein.eth","description":"rehbein.eth, an ENS name.","attributes":[{"trait_type":"Created Date","display_type":"date","value":1500099683000},{"trait_type":"Length","display_type":"number","value":7},{"trait_type":"Segment Length","display_type":"number","value":7},{"trait_type":"Character Set","display_type":"string","value":"letter"},{"trait_type":"Registration Date","display_type":"date","value":1661223139000},{"trait_type":"Expiration Date","display_type":"date","value":1692780091000}],"name_length":7,"segment_length":7,"url":"https://app.ens.domains/name/rehbein.eth","version":0,"background_image":"https://metadata.ens.domains/mainnet/avatar/rehbein.eth","image":"https://metadata.ens.domains/mainnet/0x57f1887a8bf19b14fc0df6fd9b2acc9af147ea85/0xe88035fbf85c5a3294f84d9cfbe92cdcd2ade4db4d18a1980236733458dffd9c/image","image_url":"https://metadata.ens.domains/mainnet/0x57f1887a8bf19b14fc0df6fd9b2acc9af147ea85/0xe88035fbf85c5a3294f84d9cfbe92cdcd2ade4db4d18a1980236733458dffd9c/image"}
     * @param raw
     * @return
     * @throws IOException
     */
    public static String getNFTMetadataByRaw(String raw) throws IOException{
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        // 设置代理地址
        if("1".equals(isProxy)){
            SocketAddress sa = new InetSocketAddress("127.0.0.1", 53998);
            builder.proxy(new Proxy(Proxy.Type.HTTP, sa));
        }
        OkHttpClient client = builder.build();
        {
            String body = null;
            Date beginTime = new Date();
            String url = raw;
            log.info("url:" + url);
//            Request request = new Request.Builder()
//                    .url(url)
//                    .get()
//                    .addHeader("Accept", "application/json")
//                    //                    .addHeader("X-API-KEY", "null")
//                    .build();
            try {
                body = new CallResponseHandle(new CallResponse() {
                    @Override
                    public Response newCall() {
                        return OkHttpClientUtil.getInstance().getData(client, url);
                    }
                }).callResponse();
//                body = callResponse(client, request);
                log.info("body:" + body);
            } catch (Exception e) {
                log.info("error:"+url);
                log.error(e.getMessage(), e);
                throw new RuntimeException(e);
            } finally {
                log.info("costTime:"+(new Date().getTime() - beginTime.getTime())+"ms");
            }
            return body;

        }
    }
    /**
     * 通过区块高度查询交易回执列表
     * {"jsonrpc":"2.0","id":1,"result":{"receipts":[
     * {"transactionHash":"0x97cfeb8349767558ed08f76ab460eda7d6b7b1e68b26fddb03e7ab81de3c0de2",
     * "blockHash":"0xcc82c663101a539328caa9aedbaa5d19b882993a78e5303c58dc1b3478381b3e",
     * "blockNumber":"0xe3a679",
     * "contractAddress":null,
     * "cumulativeGasUsed":"0x5208",
     * "effectiveGasPrice":"0xc9205d089",
     * "from":"0x0392b64b8bfda184f0a72ce37d73dc7df978c4f7",
     * "gasUsed":"0x5208",
     * "logs":[],
     * "logsBloom":"0x00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
     * "status":"0x1",
     * "to":"0xb4dcb6d1645603674c56e80fe20d121ce74b5006",
     * "transactionIndex":"0x0",
     * "type":"0x0"}]}}
     * @throws IOException
     */
    public static String alchemygetTransactionReceipts(List<Long> blockNumber) throws Exception {
//        OkHttpClient.Builder builder = new OkHttpClient.Builder();
//        //内容较长
//        builder.connectTimeout(5, TimeUnit.MINUTES)
//                .writeTimeout(5, TimeUnit.MINUTES)
//                .readTimeout(5, TimeUnit.MINUTES);
//        // 设置代理地址
////        SocketAddress sa = new InetSocketAddress("127.0.0.1", 60959);
////        builder.proxy(new Proxy(Proxy.Type.HTTP, sa));
//        OkHttpClient client = builder.build();
        {
            String body = null;
            Date beginTime = new Date();
            Map<String, Object> paraData = new HashMap<>();
            paraData.put("id", blockNumber.get(0));
            paraData.put("jsonrpc","2.0");
            paraData.put("method","alchemy_getTransactionReceipts");
            List<Map<String, String>> blockNumberList = new ArrayList<>();
            paraData.put("params", blockNumberList);
            for(Long n:blockNumber){
                Map<String, String> m = new HashMap<>();
                m.put("blockNumber","0x"+Long.toHexString(n));
                blockNumberList.add(m);
            }
            String data = JsonUtil.object2String(paraData);
            String url = getAlchemyPath();
            log.info("url:" + url);
            log.info("data:" + data);
//            MediaType mediaType= MediaType.parse("application/json");
//            RequestBody requestBody= RequestBody.create(mediaType, data);
//            Request request = new Request.Builder()
//                    .url(url)
//                    .post(requestBody)
//                    .addHeader("Accept", "application/json")
//                    //                    .addHeader("X-API-KEY", "null")
//                    .build();
            try {
                body = new CallResponseHandle(new CallResponse() {
                    @Override
                    public Response newCall() {
                        return OkHttpClientUtil.getInstance().postData(url, data);
                    }
                }).callResponse();
//                body = callResponse(client, request);
                judgeResult(body);
            } catch (Exception e) {
                log.info("error:"+url);
                log.error(e.getMessage(), e);
                throw new RuntimeException(e);
            } finally {
                log.info("costTime:"+(new Date().getTime() - beginTime.getTime())+"ms");
            }
//            log.info("body:" + body);
            return body;
        }
    }

    /**
     * 根据区块高度获得区块链数据
     * @param blockNumber 区块高度
     * @param includeTransaction 是否包含交易信息
     * @return
     * @throws IOException
     */
    public static String getBlockByNumber(Long blockNumber, boolean includeTransaction) throws IOException {
//        OkHttpClient.Builder builder = new OkHttpClient.Builder();
//        // 设置代理地址
////        SocketAddress sa = new InetSocketAddress("127.0.0.1", 60959);
////        builder.proxy(new Proxy(Proxy.Type.HTTP, sa));
//        OkHttpClient client = builder.build();
        {
            String body = null;
            Date beginTime = new Date();
            String data = "{" +
                    "\"id\": "+blockNumber+"," +
                    "\"jsonrpc\": \"2.0\"," +
                    "\"method\": \"eth_getBlockByNumber\"," +
                    "\"params\": [" +
                    "\"0x"+Long.toHexString(blockNumber)+"\"," +//区块高度
                    includeTransaction + //是否包含交易信息
                    "]" +
                    "}";
            String url = getAlchemyPath();
            log.info("url:" + url);
            log.info("data:" + data);
//            MediaType mediaType= MediaType.parse("application/json");
//            RequestBody requestBody= RequestBody.create(mediaType, data);
//            Request request = new Request.Builder()
//                    .url(url)
//                    .post(requestBody)
//                    .addHeader("Accept", "application/json")
//                    //                    .addHeader("X-API-KEY", "null")
//                    .build();
            try {
                body = new CallResponseHandle(new CallResponse() {
                    @Override
                    public Response newCall() {
                        return OkHttpClientUtil.getInstance().postData(url, data);
                    }
                }).callResponse();
//                body = callResponse(client, request);
                log.info("body:" + body);
                judgeResult(body);
            } catch (Exception e) {
                log.info("error:"+url);
                log.error(e.getMessage(), e);
                throw new RuntimeException(e);
            } finally {
                log.info("costTime:"+(new Date().getTime() - beginTime.getTime())+"ms");
            }
            return body;
        }
    }
    public static void main(String[] args) throws IOException {
//            String contractAddress = "0x67b36ba196804db198ac4c0a8359a1fafa5e5cff";//nft合约
////        String contractAddress = "0xb8c77482e45f1f44de1745f52c74426c631bdd52";//非nft合约
//        try {
//            getContractMetadata(contractAddress);
//        } catch (IOException e) {
//            log.info(e.getMessage(), e);
//        }
        //一个ens交易
        //https://cn.etherscan.com/tx/0x10dd605a4a917eff0e60492dfbeed7ba47320007c77a8b1a90f899f603a1ed89
        String tokenId = "55410952201049487871791681327342603684221801302599016058612157535488542032089";
        //未过期token
        String tokenIdunexpired = "0xe88035fbf85c5a3294f84d9cfbe92cdcd2ade4db4d18a1980236733458dffd9c";//
        //已过期token
        String tokenIdexpired = "0x4cbedf505977ad2333f03571681875b18ea2e1837c0791da20ee246e3ea7f34c";//
//        try {
//            getNFTMetadata(ENSCONSTRACTADDRESS, tokenIdexpired);
//            Set<String> tokenIds = new HashSet<>();
//            tokenIds.add(tokenId);
//            tokenIds.add(tokenIdunexpired);
//            tokenIds.add(tokenIdexpired);
//            getNFTMetadataBatch(ENSCONSTRACTADDRESS, tokenIds, "ERC721");
//        } catch (IOException e) {
//            log.info(e.getMessage(), e);
//        }
        {
            //{"contractAddresses":["0xe785E82358879F061BC3dcAC6f0444462D4b5330","0xbc4ca0eda7647a8ab7c2061c2e118a18a936f13d"]}
            List<String> contractAddresses = new ArrayList<>();
            contractAddresses.add("0xe785E82358879F061BC3dcAC6f0444462D4b5330");
            contractAddresses.add("0xbc4ca0eda7647a8ab7c2061c2e118a18a936f13d");
            contractAddresses.add("0x8D57dAc649760e11660B38f201292095f0000eA8");
            contractAddresses.add(ENSCONSTRACTADDRESS);
            contractAddresses.add("0xB8c77482e45F1F44dE1745F52C74426C631bDD52");
            getContractMetadataBatch(contractAddresses);
        }
        {
            //{"contractAddresses":["0xe785E82358879F061BC3dcAC6f0444462D4b5330","0xbc4ca0eda7647a8ab7c2061c2e118a18a936f13d"]}
            List<String> contractAddresses = new ArrayList<>();
//            contractAddresses.add("0xe785E82358879F061BC3dcAC6f0444462D4b5330");
//            contractAddresses.add("0xbc4ca0eda7647a8ab7c2061c2e118a18a936f13d");
//            contractAddresses.add("0x8D57dAc649760e11660B38f201292095f0000eA8");
            contractAddresses.add(ENSCONSTRACTADDRESS);
//            contractAddresses.add("0xB8c77482e45F1F44dE1745F52C74426C631bDD52");
            getTokenMetadata(contractAddresses);
        }
//        String raw = "https://metadata.ens.domains/mainnet/0x57f1887a8bf19b14fc0df6fd9b2acc9af147ea85/0x4cbedf505977ad2333f03571681875b18ea2e1837c0791da20ee246e3ea7f34c";
//        try {
//            String body = getNFTMetadataByRaw(raw);
//            Map map = JsonUtil.string2Obj(body);
//            String message = (String) map.get("message");
//            String[] split = message.split(" is already been expired at ");
//            String domain = split[0];
//            domain = domain.substring(1, domain.length() - 1);
//            String date = split[1];
//            SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz.", Locale.US);
//        } catch (IOException e) {
//            log.info(e.getMessage(), e);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//        {
//            String body = alchemygetTransactionReceipts(14919291L);
//            Map result = JsonUtil.string2Obj(body);
//            System.out.println(result.get("jsonrpc"));
//            System.out.println(result.get("id"));
//            Map result1 = (Map) result.get("result");
//            List<Map> receipts = (List<Map>) result1.get("receipts");
//            for(Map m:receipts){
//                EthTxnReceiptDTO receipt = JsonUtil.mapToBean(m, new EthTxnReceiptDTO());
//                if("0x10dd605a4a917eff0e60492dfbeed7ba47320007c77a8b1a90f899f603a1ed89".equals(receipt.getTransactionHash())){
//                    System.out.println(JsonUtil.object2String(m));
//                }
//            }
//        }
        {
//            String body = getBlockByNumber(14919289L, true);
        }
    }
}
