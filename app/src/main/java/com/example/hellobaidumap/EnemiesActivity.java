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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by yubin on 2016/10/7.
 */
public class EnemiesActivity extends Activity {

    public static ArrayList<EnemiesInfo> enemiesListItem;

    private EnemiesInfo enemiesInfo;

    private Button add;

    private Button edit;

    private ListView enemiesList;

    private EditText enemiesName;

    private EditText enemiesNumber;

    private TextView delete_enemies_name;

    private boolean isDelete = false;

    private Button radar;

    private Button toFriends;

    EnAdapter enAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.enemies_listview);

        enAdapter = new EnAdapter(EnemiesActivity.this);//得到一个MyAdapter对象

        add = (Button) findViewById(R.id.enemies_add);

        edit = (Button) findViewById(R.id.enemies_edit);

        enemiesList = (ListView) findViewById(R.id.enemies_list);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater inflater = getLayoutInflater();
                final View layout = inflater.inflate(R.layout.add_dialog,
                        (ViewGroup) findViewById(R.id.add_dialog));

                enemiesName = (EditText)layout.findViewById(R.id.inputName);
                enemiesNumber = (EditText)layout.findViewById(R.id.inputNumber);

                AlertDialog.Builder builder =
                        new AlertDialog.Builder(EnemiesActivity.this);

                builder.setTitle("添加敌人").setView(layout);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //为动态数组添加数据

                        if(!enemiesName.getText().toString().equals("")
                                || !enemiesNumber.getText().toString().equals("")) {
                            enemiesInfo = new EnemiesInfo(enemiesName.getText().toString(),
                                    enemiesNumber.getText().toString(), null);
                            enemiesListItem.add(enemiesInfo);
                            MainActivity.saveObject("edata.dat", EnemiesActivity.this);
                        }
                    }
                });
                builder.setNegativeButton("取消", null).show();
            }
        });

        enemiesList.setAdapter(enAdapter);//为ListView绑定Adapter

        radar = (Button) findViewById(R.id.back_to_radar);
        radar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EnemiesActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        toFriends = (Button) findViewById(R.id.to_friends);
        toFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EnemiesActivity.this, FriendsActivity.class);
                startActivity(intent);
            }
        });
    }

    /*新建一个类继承BaseAdapter，实现视图与数据的绑定*/
    private class EnAdapter extends BaseAdapter {
        private LayoutInflater mInflater;//得到一个LayoutInfalter对象用来导入布局

        private int currentItem = -1; //用于记录点击的 Item 的 position，是控制 item 展开的核心

        /*构造函数*/
        public EnAdapter(Context context) {
            super();
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return enemiesListItem.size();    //返回数组的长度
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

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item_for_enemies, null);
                holder = new ViewHolder();

                /*得到各个控件的对象*/

                holder.e_showArea = (RelativeLayout)
                        convertView.findViewById(R.id.e_layout_showArea);

                holder.redImage =
                        (ImageView) convertView.findViewById(R.id.red_image);
                holder.e_nameAndNumber =
                        (TextView) convertView.findViewById(R.id.enemies_name);
                holder.e_delete =
                        (Button) convertView.findViewById(R.id.e_delete_button);

                edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isDelete) {
                            isDelete = false;
                            enAdapter.notifyDataSetChanged();
                        } else {
                            isDelete = true;
                            enAdapter.notifyDataSetChanged();
                        }
                    }
                });

                holder.e_nameHiddenInput = (TextView)
                        convertView.findViewById(R.id.e_nameHiddenInput);
                holder.e_numberHiddenInput = (TextView)
                        convertView.findViewById(R.id.e_numberHiddenInput);
                holder.e_latitudeHiddenInput = (TextView)
                        convertView.findViewById(R.id.e_latitudeHiddenInput);
                holder.e_longitudeHiddenInput = (TextView)
                        convertView.findViewById(R.id.e_longitudeHiddenInput);

                holder.e_hideArea = (RelativeLayout)
                        convertView.findViewById(R.id.e_layout_hideArea);

                convertView.setTag(holder);//绑定ViewHolder对象
            } else {
                holder = (ViewHolder) convertView.getTag();//取出ViewHolder对象                  }
            }

            holder.e_showArea.setTag(position);

            if (isDelete) {
                holder.e_delete.setVisibility(View.VISIBLE);
            } else {
                holder.e_delete.setVisibility(View.GONE);
            }

            //根据 currentItem 记录的点击位置来设置"对应Item"的可见性（在list依次加载列表数据时，每加载一个时都看一下是不是需改变可见性的那一条）
            if (currentItem == position) {
                holder.e_hideArea.setVisibility(View.VISIBLE);
            } else {
                holder.e_hideArea.setVisibility(View.GONE);
            }

            holder.e_showArea.setOnClickListener(new View.OnClickListener() {

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
            holder.e_nameAndNumber.
                    setText(enemiesListItem.get(position).getName() + " "
                            + enemiesListItem.get(position).getNumber());

            holder.e_nameHiddenInput.setText(enemiesListItem.get(position).getName());
            holder.e_numberHiddenInput.setText(enemiesListItem.get(position).getNumber());

            if (enemiesListItem.get(position).getEnLatLng() != null) {
                holder.e_latitudeHiddenInput.setText(
                        enemiesListItem.get(position).getEnLatLng().latitude+"");
                holder.e_longitudeHiddenInput.setText(
                        enemiesListItem.get(position).getEnLatLng().longitude+"");
            }

            //为Button添加点击事件
            holder.e_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LayoutInflater inflater = getLayoutInflater();
                    final View layout = inflater.inflate(R.layout.delete_dialog,
                            (ViewGroup) findViewById(R.id.delete_dialog));

                    delete_enemies_name =
                            (TextView) layout.findViewById(R.id.delete_name);

                    AlertDialog.Builder builder =
                            new AlertDialog.Builder(EnemiesActivity.this);

                    delete_enemies_name.setText(enemiesListItem.get(position).getName());

                    builder.setTitle("删除敌人").setView(layout);
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            enemiesListItem.remove(position);
                            enemiesList.setAdapter(enAdapter);
                            MainActivity.saveObject("edata.dat", EnemiesActivity.this);
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
        public RelativeLayout e_showArea;

        public ImageView redImage;
        public TextView e_nameAndNumber;
        public Button e_delete;

        public TextView e_nameHiddenInput;
        public TextView e_numberHiddenInput;
        public TextView e_latitudeHiddenInput;
        public TextView e_longitudeHiddenInput;

        public RelativeLayout e_hideArea;
    }
}
