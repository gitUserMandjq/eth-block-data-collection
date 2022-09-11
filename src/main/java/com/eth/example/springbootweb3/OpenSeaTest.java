package com.eth.example.springbootweb3;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;

public class OpenSeaTest {
    public static void main(String[] args) throws IOException {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        // 设置代理地址
        SocketAddress sa = new InetSocketAddress("127.0.0.1", 60959);
        builder.proxy(new Proxy(Proxy.Type.HTTP, sa));
        OkHttpClient client = builder.build();
        {
            Request request = new Request.Builder()
                    .url("https://api.opensea.io/api/v1/assets?order_direction=desc&limit=20&include_orders=false")
                    .get()
                    .addHeader("Accept", "application/json")
//                    .addHeader("X-API-KEY", "null")
                    .build();
            Response response = client.newCall(request).execute();
            System.out.println(response.body().string());
        }
        {
            Request request = new Request.Builder()
                    .url("https://api.opensea.io/api/v1/collections?offset=0&limit=300")
                    .get()
                    .addHeader("Accept", "application/json")
//                    .addHeader("X-API-KEY", "null")
                    .build();
            Response response = client.newCall(request).execute();
            System.out.println(response.body().string());
        }
    }
}
