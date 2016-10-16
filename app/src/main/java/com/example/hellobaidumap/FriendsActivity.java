package com.example.hellobaidumap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by yubin on 2016/10/7.
 */

public class FriendsActivity extends Activity {

    public static ArrayList<FriendsInfo> listItem;

    private FriendsInfo friendsInfo;

    private Button add;

    private Button edit;

    private ListView friendsList;

    private EditText name;

    private EditText number;

    private TextView delete_friend_name;

    private boolean isDelete = false;

    private Button radar;

    MyAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.friends_listview);

        mAdapter = new MyAdapter(FriendsActivity.this);//得到一个MyAdapter对象

        add = (Button) findViewById(R.id.add);

        edit = (Button) findViewById(R.id.edit);

        friendsList = (ListView) findViewById(R.id.friends_list);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater inflater = getLayoutInflater();
                final View layout = inflater.inflate(R.layout.add_dialog,
                        (ViewGroup) findViewById(R.id.add_dialog));

                name = (EditText)layout.findViewById(R.id.inputName);
                number = (EditText)layout.findViewById(R.id.inputNumber);

                AlertDialog.Builder builder =
                        new AlertDialog.Builder(FriendsActivity.this);

                builder.setTitle("添加朋友").setView(layout);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //为动态数组添加数据

                        if(!name.getText().toString().equals("")
                               || !number.getText().toString().equals("")) {
                            friendsInfo = new FriendsInfo(name.getText().toString(),
                                number.getText().toString(), null);
                            listItem.add(friendsInfo);
                            MainActivity.saveObject("data.dat", FriendsActivity.this);
                        }
                    }
                });
                builder.setNegativeButton("取消", null).show();
            }
        });

        friendsList.setAdapter(mAdapter);//为ListView绑定Adapter

        radar = (Button) findViewById(R.id.back_to_radar);
        radar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /*新建一个类继承BaseAdapter，实现视图与数据的绑定*/
    private class MyAdapter extends BaseAdapter {
        private LayoutInflater mInflater;//得到一个LayoutInfalter对象用来导入布局

        private int currentItem = -1; //用于记录点击的 Item 的 position，是控制 item 展开的核心

        /*构造函数*/
        public MyAdapter(Context context) {
            super();
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return listItem.size();    //返回数组的长度
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            //观察convertView随ListView滚动情况

            Log.v("MyListViewBase", "getView " + position + " " + convertView);
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item_for_friends, null);
                holder = new ViewHolder();

                /*得到各个控件的对象*/

                holder.showArea = (RelativeLayout)
                        convertView.findViewById(R.id.layout_showArea);

                holder.greenImage =
                        (ImageView) convertView.findViewById(R.id.green_image);
                holder.nameAndNumber =
                        (TextView) convertView.findViewById(R.id.friends_name);
                holder.delete =
                        (Button) convertView.findViewById(R.id.delete_button);

                edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isDelete) {
                            isDelete = false;
                            mAdapter.notifyDataSetChanged();
                        } else {
                            isDelete = true;
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                });

                holder.nameHiddenInput = (TextView)
                        convertView.findViewById(R.id.nameHiddenInput);
                holder.numberHiddenInput = (TextView)
                        convertView.findViewById(R.id.numberHiddenInput);
                holder.latitudeHiddenInput = (TextView)
                        convertView.findViewById(R.id.latitudeHiddenInput);
                holder.longitudeHiddenInput = (TextView)
                        convertView.findViewById(R.id.longitudeHiddenInput);

                holder.hideArea = (RelativeLayout)
                        convertView.findViewById(R.id.layout_hideArea);

                convertView.setTag(holder);//绑定ViewHolder对象
            } else {
                holder = (ViewHolder) convertView.getTag();//取出ViewHolder对象                  }
            }

            holder.showArea.setTag(position);

            if (isDelete) {
                holder.delete.setVisibility(View.VISIBLE);
            } else {
                holder.delete.setVisibility(View.GONE);
            }

            //根据 currentItem 记录的点击位置来设置"对应Item"的可见性（在list依次加载列表数据时，每加载一个时都看一下是不是需改变可见性的那一条）
            if (currentItem == position) {
                holder.hideArea.setVisibility(View.VISIBLE);
            } else {
                holder.hideArea.setVisibility(View.GONE);
            }

            holder.showArea.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    //用 currentItem 记录点击位置
                    int tag = (Integer) view.getTag();
                    if (tag == currentItem) { //再次点击
                        currentItem = -1; //给 currentItem 一个无效值
                    } else {
                        currentItem = tag;
                    }
                    //通知adapter数据改变需要重新加载
                    notifyDataSetChanged(); //必须有的一步
                }
            });

            //设置TextView显示的内容，即我们存放在动态数组中的数据
            holder.nameAndNumber.
                    setText(listItem.get(position).getName() + " "
                        + listItem.get(position).getNumber());

            holder.nameHiddenInput.setText(listItem.get(position).getName());
            holder.numberHiddenInput.setText(listItem.get(position).getNumber());

            if (listItem.get(position).getMyLatLng() != null) {
                holder.latitudeHiddenInput.setText(
                        listItem.get(position).getMyLatLng().latitude+"");
                holder.longitudeHiddenInput.setText(
                        listItem.get(position).getMyLatLng().longitude+"");
            }

            //为Button添加点击事件
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LayoutInflater inflater = getLayoutInflater();
                    final View layout = inflater.inflate(R.layout.delete_dialog,
                            (ViewGroup) findViewById(R.id.delete_dialog));

                    delete_friend_name =
                            (TextView) layout.findViewById(R.id.delete_name);

                    AlertDialog.Builder builder =
                            new AlertDialog.Builder(FriendsActivity.this);

                    delete_friend_name.setText(listItem.get(position).getName());

                    builder.setTitle("删除朋友").setView(layout);
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            listItem.remove(position);
                            friendsList.setAdapter(mAdapter);
                            MainActivity.saveObject("data.dat", FriendsActivity.this);
                        }
                    });
                    builder.setNegativeButton("取消", null).show();
                }
            });

            return convertView;
        }
    }

    /*存放控件*/
    public final class ViewHolder{
        public RelativeLayout showArea;

        public ImageView greenImage;
        public TextView nameAndNumber;
        public Button delete;

        public TextView nameHiddenInput;
        public TextView numberHiddenInput;
        public TextView latitudeHiddenInput;
        public TextView longitudeHiddenInput;

        public RelativeLayout hideArea;
    }
}
