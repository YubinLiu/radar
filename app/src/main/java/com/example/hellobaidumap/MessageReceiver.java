package com.example.hellobaidumap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

import com.baidu.mapapi.model.LatLng;

/**
 * Created by yubin on 2016/10/9.
 */
public class MessageReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent){
        Bundle bundle = intent.getExtras();
        Object[] pdus = (Object[]) bundle.get("pdus");  //提取短信消息
        SmsMessage[] messages = new SmsMessage[pdus.length];
        for(int i = 0; i < messages.length; i++){
            messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
        }
        String address = messages[0].getOriginatingAddress();   //获取发送方号码
        String fullMessage = "";
        for(SmsMessage message : messages){
            fullMessage += message.getMessageBody();    //获取短信内容
        }

        Log.e("address " , "is " + address);

        int i;

        int j;

        if (address.substring(0,3).equals("+86")) {
            address = address.substring(3, address.length());
        }

        if(fullMessage.equals("where are you")) {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(address
                    , null, MainActivity.latitude + "/" + MainActivity.longitude
                    , null, null);
        }

        for (i = 0; i < FriendsActivity.listItem.size(); i++) {
            if (address.equals(FriendsActivity.listItem.get(i).getNumber())) {
                break;
            }
        }

        for (j = 0; j < EnemiesActivity.enemiesListItem.size(); j++) {
            if (address.equals(EnemiesActivity.enemiesListItem.get(j).getNumber())) {
                break;
            }
        }

        if (fullMessage.matches("\\d+[.]\\d+/\\d+[.]\\d+")) {
            String[] str = fullMessage.split("/");
            double lat = Double.parseDouble(str[0]);
            double log = Double.parseDouble(str[1]);
            LatLng latLng = new LatLng(lat, log);

            if (FriendsActivity.listItem.size() != 0 && FriendsActivity.listItem.size() != i) {
                if (address.equals(FriendsActivity.listItem.get(i).getNumber())) {
                    FriendsActivity.listItem.get(i).setMyLatLng(latLng);
                }
            }
            if (EnemiesActivity.enemiesListItem.size() != 0 && EnemiesActivity.enemiesListItem.size() != j) {
                if (address.equals(EnemiesActivity.enemiesListItem.get(j).getNumber())) {
                    EnemiesActivity.enemiesListItem.get(j).setEnLatLng(latLng);
                }
            }
        }
    }
}
