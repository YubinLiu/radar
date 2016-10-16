package com.example.hellobaidumap;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static double latitude;

    public static double longitude;

    private MapView mapView;

    private Button enemies;

    private Button friends;

    public LocationClient mLocationClient = null;

    public BDLocationListener myListener = new MyLocationListener();

    private boolean isFirstLocation = true;

    private BaiduMap mBaiduMap;

    private double lat;

    private double lon;

    private Button refresh;

    private Button locate;

    private Button danger;

    private Button help;

    private ImageView scanner;

    private Animation operatingAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if ((ArrayList<FriendsInfo>) MainActivity.getObject("data.dat", this) != null ) {
            FriendsActivity.listItem = (ArrayList<FriendsInfo>) MainActivity.getObject("data.dat", this);
        } else {
            FriendsActivity.listItem = new ArrayList<>();
        }

        if ((ArrayList<EnemiesInfo>) MainActivity.getObject("edata.dat", this) != null) {
            EnemiesActivity.enemiesListItem = (ArrayList<EnemiesInfo>) MainActivity.getObject("edata.dat", this);
        } else {
            EnemiesActivity.enemiesListItem = new ArrayList<>();
        }

        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        mapView = (MapView) findViewById(R.id.map_view);

        //获取BaiduMap对象
        mBaiduMap = mapView.getMap();
        mBaiduMap.setMyLocationEnabled(true);

        //声明LocationClient类
        mLocationClient = new LocationClient(getApplicationContext());
        //注册监听函数
        mLocationClient.registerLocationListener( myListener );

        //配置定位参数
        initLocation();

        //开始定位
        mLocationClient.start();

        MyLocationConfiguration.LocationMode mCurrentMode
                = MyLocationConfiguration.LocationMode.FOLLOWING;
        mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(mCurrentMode, true, null));

        scanner = (ImageView) findViewById(R.id.scanner);

        operatingAnim
                = AnimationUtils.loadAnimation(this, R.anim.rotate_indefinitely);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);

        enemies = (Button) findViewById(R.id.enemies);
        enemies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =
                        new Intent(MainActivity.this, EnemiesActivity.class);
                startActivity(intent);
            }
        });

        friends = (Button) findViewById(R.id.friends);
        friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FriendsActivity.class);
                startActivity(intent);
            }
        });

        refresh = (Button) findViewById(R.id.refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (operatingAnim != null) {
                    scanner.startAnimation(operatingAnim);
                }

                SmsManager smsManager = SmsManager.getDefault();
                for (int i = 0; i < FriendsActivity.listItem.size(); i++) {
                    smsManager.sendTextMessage(FriendsActivity.listItem.get(i).getNumber()
                            , null, "where are you"
                            , null, null);
                }
            }

        });

        locate = (Button) findViewById(R.id.locate);
        locate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanner.clearAnimation();
                if (FriendsActivity.listItem.size() > 0) {
                    for (int i = 0; i < FriendsActivity.listItem.size(); i++) {
                        if (FriendsActivity.listItem.get(i).getMyLatLng() != null) {
                            //定义Maker坐标点
                            LatLng point = new LatLng(
                                    FriendsActivity.listItem.get(i).getMyLatLng().latitude,
                                    FriendsActivity.listItem.get(i).getMyLatLng().longitude);
                            //构建Marker图标
                            BitmapDescriptor bitmap = BitmapDescriptorFactory
                                    .fromResource(R.drawable.friend_marker);

                            Bundle bundle = new Bundle();

                            bundle.putString("friendsName",
                                    FriendsActivity.listItem.get(i).getName());

                            //构建MarkerOption，用于在地图上添加Marker
                            OverlayOptions option = new MarkerOptions()
                                    .position(point)
                                    .icon(bitmap).extraInfo(bundle);
                            //在地图上添加Marker，并显示
                            mBaiduMap.addOverlay(option);

                            // 构造折线点坐标
                            List<LatLng> points = new ArrayList<LatLng>();
                            points.add(new LatLng(MainActivity.latitude, MainActivity.longitude));
                            points.add(point);

                            //构建分段颜色索引数组
                            List<Integer> colors = new ArrayList<>();
                            colors.add(Integer.valueOf(Color.GREEN));

                            OverlayOptions ooPolyline = new PolylineOptions().width(10)
                                    .colorsValues(colors).points(points);
                            //添加在地图中
                            mBaiduMap.addOverlay(ooPolyline);
                        }
                    }
                }
                if (EnemiesActivity.enemiesListItem.size() > 0) {
                    for (int i = 0; i < EnemiesActivity.enemiesListItem.size(); i++) {
                        if (EnemiesActivity.enemiesListItem.get(i).getEnLatLng() != null) {
                            //定义Maker坐标点
                            LatLng point = new LatLng(
                                    EnemiesActivity.enemiesListItem.get(i).getEnLatLng().latitude,
                                    EnemiesActivity.enemiesListItem.get(i).getEnLatLng().longitude);
                            //构建Marker图标
                            BitmapDescriptor bitmap = BitmapDescriptorFactory
                                    .fromResource(R.drawable.enemy_marker);

                            Bundle bundle = new Bundle();

                            bundle.putString("enemiesName",
                                    EnemiesActivity.enemiesListItem.get(i).getName());

                            //构建MarkerOption，用于在地图上添加Marker
                            OverlayOptions option = new MarkerOptions()
                                    .position(point)
                                    .icon(bitmap)
                                    .extraInfo(bundle);
                            //在地图上添加Marker，并显示
                            mBaiduMap.addOverlay(option);

                            // 构造折线点坐标
                            List<LatLng> points = new ArrayList<LatLng>();
                            points.add(new LatLng(MainActivity.latitude, MainActivity.longitude));
                            points.add(point);

                            //构建分段颜色索引数组
                            List<Integer> colors = new ArrayList<>();
                            colors.add(Integer.valueOf(Color.RED));

                            OverlayOptions ooPolyline = new PolylineOptions().width(10)
                                    .colorsValues(colors).points(points);
                            //添加在地图中
                            mBaiduMap.addOverlay(ooPolyline);
                        }
                    }
                }
            }
        });

        danger = (Button) findViewById(R.id.danger);
        danger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (operatingAnim != null) {
                    scanner.startAnimation(operatingAnim);
                }

                SmsManager smsManager = SmsManager.getDefault();
                for (int i = 0; i < EnemiesActivity.enemiesListItem.size(); i++) {
                    smsManager.sendTextMessage(EnemiesActivity.enemiesListItem.get(i).getNumber()
                            , null, "where are you"
                            , null, null);
                }
            }
        });

        //对 marker 添加点击相应事件
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker arg0) {
                // TODO Auto-generated method stub
                String name = arg0.getExtraInfo().getString("friendsName");
                String e_name = arg0.getExtraInfo().getString("enemiesName");
                if (name != null) {
                    Toast.makeText(getApplicationContext(),
                            "friends: " + name, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "enemies: " + e_name, Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });

        help = (Button) findViewById(R.id.help);
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("说明")
                        .setMessage("左上角按钮表示定位\n左下角按钮表示给朋友发送短信\n" +
                                "右上角按钮表示给敌人发送短信")
                        .setPositiveButton("确定", null)
                        .show();
            }
        });
    }

    /**
     * 添加marker
     */
    private void setMarker() {
        Log.v("pcw","setMarker : lat : "+ lat+" lon : " + lon);
        //定义Maker坐标点
        LatLng point = new LatLng(lat, lon);
    }

    /**
     * 设置中心点
     */
    private void setUserMapCenter() {
        Log.v("pcw","setUserMapCenter : lat : "+ lat+" lon : " + lon);
        LatLng cenpt = new LatLng(lat,lon);
        //定义地图状态
        MapStatus mMapStatus = new MapStatus.Builder()
                .target(cenpt)
                .zoom(18.0f)
                .build();
        //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        //改变地图状态
        mBaiduMap.setMapStatus(mMapStatusUpdate);
    }

    /**
     * 配置定位参数
     */
    private void initLocation(){
        LocationClientOption option = new LocationClientOption();

        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span=1000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要

        mLocationClient.setLocOption(option);
    }

    /**
     * 实现定位监听 位置一旦有所改变就会调用这个方法
     * 可以在这个方法里面获取到定位之后获取到的一系列数据
     */
    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();

            StringBuffer sb = new StringBuffer(256);
            sb.append("time : ");
            sb.append(location.getTime());
            sb.append("\nerror code : ");
            sb.append(location.getLocType());
            sb.append("\nlatitude : ");
            sb.append(location.getLatitude());
            sb.append("\nlontitude : ");
            sb.append(location.getLongitude());
            sb.append("\nradius : ");
            sb.append(location.getRadius());
            lat = location.getLatitude();
            lon = location.getLongitude();
            if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                sb.append("\nspeed : ");
                sb.append(location.getSpeed());// 单位：公里每小时
                sb.append("\nsatellite : ");
                sb.append(location.getSatelliteNumber());
                sb.append("\nheight : ");
                sb.append(location.getAltitude());// 单位：米
                sb.append("\ndirection : ");
                sb.append(location.getDirection());// 单位度
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                sb.append("\ndescribe : ");
                sb.append("gps定位成功");

            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                //运营商信息
                sb.append("\noperationers : ");
                sb.append(location.getOperators());
                sb.append("\ndescribe : ");
                sb.append("网络定位成功");
            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                sb.append("\ndescribe : ");
                sb.append("离线定位成功，离线定位结果也是有效的");
            } else if (location.getLocType() == BDLocation.TypeServerError) {
                sb.append("\ndescribe : ");
                sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                sb.append("\ndescribe : ");
                sb.append("网络不同导致定位失败，请检查网络是否通畅");
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                sb.append("\ndescribe : ");
                sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
            }
            sb.append("\nlocationdescribe : ");
            sb.append(location.getLocationDescribe());// 位置语义化信息
            List<Poi> list = location.getPoiList();// POI数据
            if (list != null) {
                sb.append("\npoilist size = : ");
                sb.append(list.size());
                for (Poi p : list) {
                    sb.append("\npoi= : ");
                    sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
                }
            }

            lat = location.getLatitude();
            lon = location.getLongitude();

            MyLocationData myLocationData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    .direction(location.getDirection())
                    .latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();

            mBaiduMap.setMyLocationData(myLocationData);

            //这个判断是为了防止每次定位都重新设置中心点和marker
            if(isFirstLocation){
                isFirstLocation = false;
                setMarker();
                setUserMapCenter();
            }

            Log.v("pcw","lat : " + lat+" lon : " + lon);
            Log.i("BaiduLocationApiDem", sb.toString());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("onResume", " what");
        mapView.onResume();
    }

    public static void saveObject(String name, Context context){
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        if (name.equals("data.dat")) {
            try {
                fos = context.openFileOutput(name, MODE_PRIVATE);
                oos = new ObjectOutputStream(fos);
                oos.writeObject(FriendsActivity.listItem);
            } catch (Exception e) {
                e.printStackTrace();
                //这里是保存文件产生异常
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        //fos流关闭异常
                        e.printStackTrace();
                    }
                }
                if (oos != null) {
                    try {
                        oos.close();
                    } catch (IOException e) {
                        //oos流关闭异常
                        e.printStackTrace();
                    }
                }
            }
        } else if (name.equals("edata.dat")) {
            try {
                fos = context.openFileOutput(name, MODE_PRIVATE);
                oos = new ObjectOutputStream(fos);
                oos.writeObject(EnemiesActivity.enemiesListItem);
            } catch (Exception e) {
                e.printStackTrace();
                //这里是保存文件产生异常
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        //fos流关闭异常
                        e.printStackTrace();
                    }
                }
                if (oos != null) {
                    try {
                        oos.close();
                    } catch (IOException e) {
                        //oos流关闭异常
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static Object getObject(String name, Context context){
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = context.openFileInput(name);
            ois = new ObjectInputStream(fis);
            return ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            //这里是读取文件产生异常
        } finally {
            if (fis != null){
                try {
                    fis.close();
                } catch (IOException e) {
                    //fis流关闭异常
                    e.printStackTrace();
                }
            }
            if (ois != null){
                try {
                    ois.close();
                } catch (IOException e) {
                    //ois流关闭异常
                    e.printStackTrace();
                }
            }
        }
        //读取产生异常，返回null
        return null;
    }
}
