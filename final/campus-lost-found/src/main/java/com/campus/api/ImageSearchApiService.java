package com.campus.api;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class ImageSearchApiService {

    private String getToken() throws Exception {
        Properties props = loadProperties();
        String apiKey = props.getProperty("baidu.api.key");
        String secret = props.getProperty("baidu.secret.key");

        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new Exception("未配置 baidu.api.key");
        }
        if (secret == null || secret.trim().isEmpty()) {
            throw new Exception("未配置 baidu.secret.key");
        }

        String tokenUrl = "https://aip.baidubce.com/oauth/2.0/token";
        String params = "grant_type=client_credentials"
                + "&client_id=" + URLEncoder.encode(apiKey, StandardCharsets.UTF_8)
                + "&client_secret=" + URLEncoder.encode(secret, StandardCharsets.UTF_8);

        String responseText = doPost(tokenUrl, params, "application/x-www-form-urlencoded");
        JSONObject tokenJson = JSON.parseObject(responseText);
        return tokenJson.getString("access_token");
    }

    public JSONObject search(String imageUrl) {
        try {
            if (imageUrl == null || imageUrl.trim().isEmpty()) {
                JSONObject resp = new JSONObject();
                resp.put("code", 400);
                resp.put("msg", "图片地址不能为空");
                return resp;
            }

            Properties props = loadProperties();
            String searchUrl = props.getProperty("baidu.image.search.url");
            if (searchUrl == null || searchUrl.trim().isEmpty()) {
                JSONObject resp = new JSONObject();
                resp.put("code", 500);
                resp.put("msg", "未配置 baidu.image.search.url");
                return resp;
            }

            String token = getToken();
            if (token == null || token.trim().isEmpty()) {
                JSONObject resp = new JSONObject();
                resp.put("code", 500);
                resp.put("msg", "获取百度 access_token 失败");
                return resp;
            }

            String params = "image_url=" + URLEncoder.encode(imageUrl, StandardCharsets.UTF_8)
                    + "&rn=5";

            String responseText = doPost(
                    searchUrl + "?access_token=" + URLEncoder.encode(token, StandardCharsets.UTF_8),
                    params,
                    "application/x-www-form-urlencoded"
            );

            JSONObject result = JSON.parseObject(responseText);
            JSONObject resp = new JSONObject();

            if (result.containsKey("error_code")) {
                resp.put("code", 500);
                resp.put("msg", result.getString("error_msg"));
            } else {
                resp.put("code", 200);
                resp.put("msg", "搜索成功");
                resp.put("data", result);
            }
            return resp;

        } catch (Exception e) {
            e.printStackTrace();
            JSONObject resp = new JSONObject();
            resp.put("code", 500);
            resp.put("msg", "搜索异常：" + e.getMessage());
            return resp;
        }
    }

    /**
     * 读取 resources 下的 api.properties
     */
    private Properties loadProperties() throws IOException {
        Properties props = new Properties();
        try (InputStream in = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream("api.properties")) {
            if (in == null) {
                throw new IOException("找不到配置文件 api.properties");
            }
            props.load(new InputStreamReader(in, StandardCharsets.UTF_8));
        }
        return props;
    }

    /**
     * 发送 POST 请求
     */
    private String doPost(String urlStr, String params, String contentType) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(30000);
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setRequestProperty("Content-Type", contentType + "; charset=UTF-8");

        try (OutputStream os = conn.getOutputStream()) {
            os.write(params.getBytes(StandardCharsets.UTF_8));
            os.flush();
        }

        int responseCode = conn.getResponseCode();
        InputStream inputStream = (responseCode >= 200 && responseCode < 300)
                ? conn.getInputStream()
                : conn.getErrorStream();

        return readStream(inputStream);
    }

    /**
     * 读取响应流
     */
    private String readStream(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
    }
}