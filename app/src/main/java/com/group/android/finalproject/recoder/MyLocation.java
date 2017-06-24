package com.group.android.finalproject.recoder;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by root on 16-12-9.
 */
public class MyLocation {
    private static String provider;//位置提供器
    private static Location location;
    private static String data = null;
    private LOCATION result = new LOCATION();

    public MyLocation(LocationManager locationManager) {
        provider = judgeProvider(locationManager);
        if (provider != null) {//有位置提供器的情况
            location = locationManager.getLastKnownLocation(provider);
            if (location != null) {
                setLocation(location);
            }
        }
    }

    private String judgeProvider(LocationManager locationManager) {
        List<String> prodiverlist = locationManager.getProviders(true);
        if (prodiverlist.contains(LocationManager.NETWORK_PROVIDER)) {
            return LocationManager.NETWORK_PROVIDER;
        } else if (prodiverlist.contains(LocationManager.GPS_PROVIDER)) {
            return LocationManager.GPS_PROVIDER;
        } else {
            return null;
        }
    }

    public LOCATION getLocation() {
        return result;
    }

    /**
     * 得到当前经纬度并开启线程去反向地理编码
     */
    private void setLocation(Location location) {
        String latitude = location.getLatitude() + "";
        String longitude = location.getLongitude() + "";
        final String url = "http://api.map.baidu.com/geocoder/v2/";  // url
        final String values = "ak=P5wf8jK7uzecOPCb5CXeiBh8onVpnBV1&callback=renderReverse&location=" + latitude + "," + longitude + "&output=json&pois=0";  // 查询参数
        new Thread() {
            @Override
            public void run() {
                super.run();
                Message msg = handler.obtainMessage();
                msg.what = 0;
                data = Http.startSearch(url, values);
                handler.sendMessage(msg);
            }
        }.start();
    }

    // the new thread
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    if (data == null) {
                        data = " {\"status\":0,\"result\":{\"location\"" +
                                ":{\"lng\":113.38762099999998,\"lat\":23.060835009861603}" +
                                ",\"formatted_address\":\"广东省广州市番禺区大学城中环西路\"" +
                                ",\"business\":\"大学城\",\"addressComponent\":{\"country\"" +
                                ":\"中国\",\"country_code\":0,\"province\":\"广东省\",\"city" +
                                "\":\"广州市\",\"district\":\"番禺区\",\"adcode\":\"440113" +
                                "\",\"street\":\"大学城中环西路\",\"street_number\":\"\",\"" +
                                "direction\":\"\",\"distance\":\"\"},\"pois\":[],\"poiRegions\"" +
                                ":[{\"direction_desc\":\"内\",\"name\":\"华南师范大学大学城校" +
                                "区\",\"tag\":\"教育培训\"}],\"sematic_description\":\"华南" +
                                "师范大学大学城校区内,华南师范大学(大学城校区)东北229米\",\"" +
                                "cityCode\":257}}";
                    }
                    parsePostion(data);
                    break;
            }
        }
    };

    public String getData() {
        return data;
    }

    private void parsePostion(String str) {
        try {
            JSONObject jsonObject = new JSONObject(str);
            if (jsonObject.getInt("status") == 0) {
                JSONObject address = jsonObject.getJSONObject("result");
                result.setAddress(address.getString("formatted_address"));
                result.setCityCode(address.getInt("cityCode"));

                JSONObject addressComponent = new JSONObject(address.getString("addressComponent"));
                result.setCountry(addressComponent.getString("country"));
                result.setCity(addressComponent.getString("city"));
                result.setProvince(addressComponent.getString("province"));
                result.setDistrict(addressComponent.getString("district"));

                // 获取当前时间
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
                Date curDate = new Date(System.currentTimeMillis());
                String time = formatter.format(curDate);
                result.setDate(time);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public class LOCATION {
        private String country = "中国";
        private String province = "广东省";
        private String city = "深圳市";
        private String district = "福田区";
        private String address = "广东省深圳市福田区桂罗路86号";
        private int cityCode = 440300;
        private String date = "2016年12月18日 01:28:14";
        public void setCountry(String _c) {
            this.country = _c;
        }
        public void setProvince(String _p) {
            this.province = _p;
        }
        public void setCity(String _c) {
            this.city = _c;
        }
        public void setDistrict(String _d) {
            this.district = _d;
        }
        public void setAddress(String _a) {
            this.address = _a;
        }
        public void setCityCode(int _c) {
            this.cityCode = _c;
        }
        public void setDate(String _d) {
            this.date = _d;
        }
        public String getCountry() {
            return this.country;
        }
        public String getProvince() {
            return this.province;
        }
        public String getCity() {
            return this.city;
        }
        public String getDistrict() {
            return this.district;
        }
        public String getAddress() {
            return  this.address;
        }
        public int getCityCode() {
            return this.cityCode;
        }
        public String getDate() {
            return this.date;
        }
    }
}