package com.example.hellobaidumap;

import com.baidu.mapapi.model.LatLng;

import java.io.Serializable;

/**
 * Created by yubin on 2016/10/9.
 */
public class FriendsInfo implements Serializable{

    private String name;

    private String number;

    private MyLatLng myLatLng = null;

    public void setMyLatLng(LatLng latLng) {
        if (latLng != null) {
            this.myLatLng = new MyLatLng(latLng);
        }
    }

    public LatLng getMyLatLng() {
        if (myLatLng != null) {
            return new LatLng(myLatLng.latitude, myLatLng.longitude);
        } else {
            return null;
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getNumber() {
        return this.number;
    }

    public FriendsInfo(String name, String number, LatLng latLng) {
        this.name = name;
        this.number = number;

        if (latLng != null) {
           myLatLng = new MyLatLng(latLng);
        }
    }

    class MyLatLng implements Serializable{
        private double latitude;

        private double longitude;

        public MyLatLng(LatLng latLng) {
            latitude = latLng.latitude;
            longitude = latLng.longitude;
        }
    }
}
