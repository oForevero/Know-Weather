package com.forever.weather.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Copyright (C), 2015-2019
 * FileName: NetWorkUtils
 * Author: Forever
 * Date: 2019/3/19 17:49
 * Description: Utilsclass for send POST or GET request and get response data.
 */
public class NetWorkUtils {

    public static String sendResponseByOkHttp(URL url){
        String result = null;
        OkHttpClient oc = new OkHttpClient.Builder().
                connectTimeout(5000, TimeUnit.SECONDS)
                .readTimeout(5000, TimeUnit.SECONDS)
                .writeTimeout(5000, TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("accept", "*/*")
                .build();
        try {
            Response response = oc.newCall(request).execute();
            result = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * @Description: Method for get Data by https://www.heweather.com/ server
     * @Author: Forever
     * @Date: 2019/3/19 17:53
     */ 
    public static String sendResponseByHttpUrlConnection(URL url){
        String responsedata = null;
        HttpURLConnection hc = null;
        try {
             hc = (HttpURLConnection) url.openConnection();
             setConnectionType(hc);
             responsedata = getResponseData(hc);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responsedata;
    }

    /**
     * @Description: Method for read Jsondata.
     * @Author: Forever
     * @Date: 2019/3/19 18:09
     */ 
    public static JSONObject jsonReader(String jsonData){
        JSONObject js = null;
        try {
            js = new JSONObject(jsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return js;
    }
    
    /**
     * @Description: Method for read JsonArrayData.
     * @Author: Forever
     * @Date: 2019/3/19 18:12
     */ 
    public static JSONArray jsonArrayReader(JSONObject jsonObject,String name){
        JSONArray jsonArray = null;
        try {
            jsonArray = jsonObject.getJSONArray(name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonArray;
    }

    /**
     * @Description: Method for set HttpUrlConnection type.
     * @Author: Forever
     * @Date: 2019/3/19 18:04
     */
    private static void setConnectionType(HttpURLConnection hc){
        hc.setConnectTimeout(5000);
        hc.setReadTimeout(5000);
        hc.setDoOutput(true);
        hc.setDoInput(true);
        hc.setRequestProperty("accept", "*/*");
        hc.setRequestProperty("connection", "Keep-Alive");
        hc.setRequestProperty("user-agent","Android/1.0");
        hc.setRequestProperty("Content-Type", "application/octet-stream;charset=UTF-8");
        try {
            hc.setRequestMethod("GET");
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * @Description: Method for get response data .
     * @Author: Forever
     * @Date: 2019/3/19 18:04
     */ 
    private static String getResponseData(HttpURLConnection hc){
        String responsedata = null;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(hc.getInputStream(),"utf-8"));
            String line = null;
            while ((line = br.readLine()) != null){
                if(responsedata == null){
                    responsedata = line;
                }else {
                    responsedata += line;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(br != null){
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return responsedata;
    }
}
