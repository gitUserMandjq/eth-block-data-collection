package com.eth.test;

import okhttp3.*;

import java.io.IOException;

public class Test {
    public static void main(String[] args) throws IOException {
        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\"id\":1,\"jsonrpc\":\"2.0\",\"method\":\"alchemy_getTokenMetadata\",\"params\":[\"0xb8c77482e45f1f44de1745f52c74426c631bdd52\"]}");
        Request request = new Request.Builder()
                .url("https://eth-mainnet.g.alchemy.com/v2/KFIeu3YumP6QUXwQa0y_qnBrYCEkD3dq")
                .post(body)
                .addHeader("accept", "application/json")
                .addHeader("content-type", "application/json")
                .build();

        Response response = client.newCall(request).execute();
        System.out.println(response.body().string());
    }
}
