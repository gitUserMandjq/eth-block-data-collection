package com.example.springbootweb3;

import okhttp3.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;

public class OkHttpClientTest {
    public static String HOST = "https://eth-mainnet.alchemyapi.io";
    public static void main(String[] args) throws IOException {
        getContractMetadata();
//        alchemygetTransactionReceipts();
    }

    /**
     * 获取合约的meta信息
     * @throws IOException
     */
    private static void getContractMetadata() throws IOException{
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        // 设置代理地址
//        SocketAddress sa = new InetSocketAddress("127.0.0.1", 60959);
//        builder.proxy(new Proxy(Proxy.Type.HTTP, sa));
        OkHttpClient client = builder.build();
        {
//            String contractAddress = "0x67b36ba196804db198ac4c0a8359a1fafa5e5cff";//nft合约
            String contractAddress = "0xb8c77482e45f1f44de1745f52c74426c631bdd52";//非nft合约
            MediaType mediaType= MediaType.parse("application/json");
            Request request = new Request.Builder()
                    .url(HOST+"/v2/J4ipt__b_exS1cez4CO9KhRkXEYWxUcJ/getContractMetadata?" +
                            "contractAddress="+contractAddress+"")
                    .get()
                    .addHeader("Accept", "application/json")
//                    .addHeader("X-API-KEY", "null")
                    .build();
            Response response = client.newCall(request).execute();
            System.out.println(response.body().string());
        }
    }
    /**
     * 获取nft的meta信息
     * @throws IOException
     */
    private static void getNFTMetadata() throws IOException{
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        // 设置代理地址
//        SocketAddress sa = new InetSocketAddress("127.0.0.1", 60959);
//        builder.proxy(new Proxy(Proxy.Type.HTTP, sa));
        OkHttpClient client = builder.build();
        {
//            String contractAddress = "0x67b36ba196804db198ac4c0a8359a1fafa5e5cff";//nft合约
            String contractAddress = "0xb8c77482e45f1f44de1745f52c74426c631bdd52";//非nft合约
            MediaType mediaType= MediaType.parse("application/json");
            Request request = new Request.Builder()
                    .url(HOST+"/v2/J4ipt__b_exS1cez4CO9KhRkXEYWxUcJ/getNFTMetadata?" +
                            "contractAddress="+contractAddress+"&tokenId=0&refreshCache=false")
                    .get()
                    .addHeader("Accept", "application/json")
//                    .addHeader("X-API-KEY", "null")
                    .build();
            Response response = client.newCall(request).execute();
            System.out.println(response.body().string());
        }
    }
    /**
     * 通过区块高度查询交易回执列表
     * @throws IOException
     */
    private static void alchemygetTransactionReceipts() throws IOException {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        // 设置代理地址
//        SocketAddress sa = new InetSocketAddress("127.0.0.1", 60959);
//        builder.proxy(new Proxy(Proxy.Type.HTTP, sa));
        OkHttpClient client = builder.build();
        {
            String data = "{\n" +
                    "     \"id\": 1,\n" +
                    "     \"jsonrpc\": \"2.0\",\n" +
                    "     \"method\": \"alchemy_getTransactionReceipts\",\n" +
                    "     \"params\": [\n" +
                    "          {\n" +
                    "               \"blockNumber\": \"0xE3B96A\"\n" +
                    "          }\n" +
                    "     ]\n" +
                    "}";
            MediaType mediaType= MediaType.parse("application/json");
            RequestBody body= RequestBody.create(mediaType, data);
            Request request = new Request.Builder()
                    .url(HOST+"/v2/J4ipt__b_exS1cez4CO9KhRkXEYWxUcJ")
                    .post(body)
                    .addHeader("Accept", "application/json")
//                    .addHeader("X-API-KEY", "null")
                    .build();
            Response response = client.newCall(request).execute();
            System.out.println(response.body().string());
        }
    }
}
