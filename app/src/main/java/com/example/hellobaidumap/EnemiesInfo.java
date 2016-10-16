package com.example.hellobaidumap;

import com.baidu.mapapi.model.LatLng;

import java.io.Serializable;

/**
 * Created by yubin on 2016/10/11.
 */
public class EnemiesInfo implements Serializable{
    private String enemiesName;

    private String enemiesNumber;

    private EnLatLng enLatLng = null;

    public void setEnLatLng(LatLng latLng) {
        if (latLng != null) {
            this.enLatLng = new EnLatLng(latLng);
        }
    }

    public LatLng getEnLatLng() {
        if (enLatLng != null) {
            return new LatLng(enLatLng.latitude, enLatLng.longitude);
        } else {
            return null;
        }
    }

    public void setName(String enemiesName) {
        this.enemiesName = enemiesName;
    }

    public String getName() {
        return this.enemiesName;
    }

    public void setNumber(String enemiesNumber) {
        this.enemiesNumber = enemiesNumber;
    }

    public String getNumber() {
        return this.enemiesNumber;
    }

    public EnemiesInfo(String enemiesName, String enemiesNumber, LatLng latLng) {
        this.enemiesName = enemiesName;
        this.enemiesNumber = enemiesNumber;

        if (latLng != null) {
            enLatLng = new EnLatLng(latLng);
        }
    }

    class EnLatLng implements Serializable {
        private double latitude;

        private double longitude;

        public EnLatLng(LatLng latLng) {
            latitude = latLng.latitude;
            longitude = latLng.longitude;
        }
    }
}
