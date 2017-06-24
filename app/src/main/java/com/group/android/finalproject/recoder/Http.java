package com.group.android.finalproject.recoder;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by root on 16-11-25.
 */
public class Http {
//    private static final String url = "http://ws.webxml.com.cn/WebServices/WeatherWS.asmx/getWeather";
    private static final int TIME = 8000;
    private static HttpURLConnection connection = null;

    public static String startSearch(String url, String req) {
        try {
            // set Connection
            Log.e("TAG", "begin connection");
            connection = (HttpURLConnection) ((new URL(url.toString()).openConnection()));
            connection.setRequestMethod("POST");
            connection.setReadTimeout(TIME);
            connection.setConnectTimeout(TIME);

            // start connect
            connection.connect();

            // start request
            Log.e("TAG", "start request");
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());

//            if (hasChinese) {
//                req = URLEncoder.encode(req, "utf-8");
//            }
            Log.e("Req", req);

            out.writeBytes(req);
            out.flush(); // sent and clear cache
            out.close();

            // start get response
            Log.e("TAG", "start get response");
            InputStream in = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            in.close();

            connection.disconnect();

            Log.e("Result>>", response.toString());

            return response.toString();
        } catch (ProtocolException e) {
            e.printStackTrace();
            return null;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
