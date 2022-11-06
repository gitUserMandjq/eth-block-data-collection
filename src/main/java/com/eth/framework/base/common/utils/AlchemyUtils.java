package com.eth.framework.base.common.utils;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

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
    static {
        ETH_HOST = ETH_HOST_STR.split(",");
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

    /**
     * 获得合约信息
     * nft合约，{"address":"0x67b36ba196804db198ac4c0a8359a1fafa5e5cff","contractMetadata":{"name":"Meta RPG","symbol":"META","totalSupply":"2500","tokenType":"ERC721"}}
     * 非nft合约，{"address":"0xb8c77482e45f1f44de1745f52c74426c631bdd52","contractMetadata":{"name":"","symbol":"","totalSupply":"","tokenType":"UNKNOWN"}}
     * @param contractAddress
     * @return
     * @throws IOException
     */
    public static String getContractMetadata(String contractAddress) throws IOException {
        OkHttpClient client = okHttpBuilder.build();
        {
            String body = null;
            Date beginTime = new Date();
            String url = getAlchemyPath() + "/getContractMetadata?" +
                    "contractAddress=" + contractAddress;
            log.info("url:" + url);
//            MediaType mediaType = MediaType.parse("application/json");
            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .addHeader("Accept", "application/json")
                    .build();
            try {
                int count = 0;
                int max = 3;
                while(count < max){
                    Response response = null;
                    try {
                        response = client.newCall(request).execute();
                        body = response.body().string();
                        log.info("body:" + body);
                        //如果成功了跳出循环
                        break;
                    } catch (Exception e) {
                        count++;
                    }
                }
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
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(2, TimeUnit.MINUTES)
                .writeTimeout(2, TimeUnit.MINUTES)
                .readTimeout(2, TimeUnit.MINUTES);
        // 设置代理地址
//        SocketAddress sa = new InetSocketAddress("127.0.0.1", 60959);
//        builder.proxy(new Proxy(Proxy.Type.HTTP, sa));
        OkHttpClient client = builder.build();
        {
            String body = null;
            Date beginTime = new Date();
            String url = getAlchemyPath() + "/getNFTMetadata?" +
                    "contractAddress=" + contractAddress + "&tokenId=" + tokenId + "&refreshCache=false";
            log.info("url:" + url);
            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .addHeader("Accept", "application/json")
                    //                    .addHeader("X-API-KEY", "null")
                    .build();
            try {
                body = callResponse(client, request);
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
    private static String callResponse(OkHttpClient client, Request request) throws Exception{

        int count = 0;
        int max = 3;
        String body = null;
        while(true){
            try {
                Response response = client.newCall(request).execute();
                body = response.body().string();
                //如果成功了跳出循环
                break;
            } catch (Exception e) {
                count++;
                log.info("retry:"+count);
                if(count >= max){
                    throw e;
                }
            }
        }
        return body;
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
    public static String alchemygetTransactionReceipts(Long blockNumber) throws IOException {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        //内容较长
        builder.connectTimeout(5, TimeUnit.MINUTES)
                .writeTimeout(5, TimeUnit.MINUTES)
                .readTimeout(5, TimeUnit.MINUTES);
        // 设置代理地址
//        SocketAddress sa = new InetSocketAddress("127.0.0.1", 60959);
//        builder.proxy(new Proxy(Proxy.Type.HTTP, sa));
        OkHttpClient client = builder.build();
        {
            String body = null;
            Date beginTime = new Date();
            String data = "{" +
                    "\"id\": "+blockNumber+"," +
                    "\"jsonrpc\": \"2.0\"," +
                    "\"method\": \"alchemy_getTransactionReceipts\"," +
                    "\"params\": [" +
                    "{" +
                    "\"blockNumber\": \"0x"+Long.toHexString(blockNumber)+"\"" +
                    "}" +
                    "]" +
                    "}";
            String url = getAlchemyPath();
            log.info("url:" + url);
            log.info("data:" + data);
            MediaType mediaType= MediaType.parse("application/json");
            RequestBody requestBody= RequestBody.create(mediaType, data);
            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .addHeader("Accept", "application/json")
                    //                    .addHeader("X-API-KEY", "null")
                    .build();
            try {
                body = callResponse(client, request);
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
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        // 设置代理地址
//        SocketAddress sa = new InetSocketAddress("127.0.0.1", 60959);
//        builder.proxy(new Proxy(Proxy.Type.HTTP, sa));
        OkHttpClient client = builder.build();
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
            MediaType mediaType= MediaType.parse("application/json");
            RequestBody requestBody= RequestBody.create(mediaType, data);
            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .addHeader("Accept", "application/json")
                    //                    .addHeader("X-API-KEY", "null")
                    .build();
            try {
                body = callResponse(client, request);
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
    public static void main(String[] args) throws IOException {
//            String contractAddress = "0x67b36ba196804db198ac4c0a8359a1fafa5e5cff";//nft合约
////        String contractAddress = "0xb8c77482e45f1f44de1745f52c74426c631bdd52";//非nft合约
//        try {
//            getContractMetadata(contractAddress);
//        } catch (IOException e) {
//            log.info(e.getMessage(), e);
//        }
//        //一个ens交易
//        //https://cn.etherscan.com/tx/0x10dd605a4a917eff0e60492dfbeed7ba47320007c77a8b1a90f899f603a1ed89
        String tokenId = "55410952201049487871791681327342603684221801302599016058612157535488542032089";
        try {
            getNFTMetadata(ENSCONSTRACTADDRESS, tokenId);
        } catch (IOException e) {
            log.info(e.getMessage(), e);
        }
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
