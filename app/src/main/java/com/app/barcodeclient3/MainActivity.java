package com.app.barcodeclient3;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import essences.Inventory;
import json_process.InventoryJSON;
import main.MainApplication;

import static main.MainApplication.SETTINGS_STATE;
import static main.MainApplication.databaseIP;
import static main.MainApplication.databasePath;
import static main.MainApplication.disconnect;
import static main.MainApplication.isConnect;
import static main.MainApplication.onlineMode;


public class MainActivity extends AppCompatActivity {

    @BindView(R.id.list) ListView list;
    @BindView(R.id.progress) ProgressBar progressBar;

    ImageView statusButton;
    ImageView settingsButton;
    TextView titleBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);



        //----
        getSupportActionBar().setDisplayOptions(getSupportActionBar().getDisplayOptions() | android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);

        getSupportActionBar().setCustomView(R.layout.action_bar_main);
        View abarView = getSupportActionBar().getCustomView();


        titleBar =  abarView.findViewById(R.id.abarTitle);
        statusButton = abarView.findViewById(R.id.statusButton);
        settingsButton =  abarView.findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stop=true;//Останавливаем поток автообновления
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivityForResult(intent,SETTINGS_STATE);
            }
        });




        getSupportActionBar().setDisplayHomeAsUpEnabled(false); //!!!!!!!!!!!!!!!!!!!!!!!!!!!!! BACK ICON!!!!!!!!!!!!!!!!!
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setIcon(android.R.color.transparent);


        final Toolbar parent = (Toolbar) abarView.getParent();
        parent.setPadding(0, 0, 0, 0);//for tab otherwise give space in tab
        parent.setContentInsetsAbsolute(0, 0);

        //--------------









        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        fab.setVisibility(View.GONE);


        if(list==null) Log.d("my"," List is null");

        ConnectThread thread = new ConnectThread();
        thread.start();

    }




    String dbVersion=null;



    class ConnectThread extends Thread
    {
        @Override
        public void run() {

            if(disconnect){
                disconnect=false;
                disconnect();
            }
            Connection connection = MainApplication.initConnect();//!!!!!!!!!!!!!!!!!!!!!!!
            if(connection==null){
                Log.d("my","connection is null");
            }else{
                Log.d("my","connection is ok");


            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(isConnect()) {
                        Log.d("my","connection is ok2");
                        //getDatabaseVersion();
                       // updateInventories();
                        UpdateStateThread updateStateThread = new UpdateStateThread();
                        updateStateThread.start();

                    }
                    else updateStatus();
                }
            });
            //callback.sendResult(null);

        }
    }

    private long timesleep=2000;
    private boolean stop=false;
    private boolean lock=false;

    class UpdateStateThread extends Thread
    {
        @Override
        public void run() {

            while (!stop){
                if(!lock) {
                    getDatabaseVersion();
                    Log.d("my","----------------------------------------------------------------------------------------------------------tick!!!!!!!!!!!!!!!!!!!!!");
                    try {
                        sleep(timesleep);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
            Log.d("my","----------------------------------------------------------------------------------------------------------STOP ~~~~~~!!!!!!!!!!!!!!!!!!!!!");
        }
    }


    private void getDatabaseVersion()
    {
        lock=true;////Блокируем процесс автообновления

        MainApplication.getApi().getDatabaseVersion(new Callback<String>() {
            @Override
            public boolean sendResult(Response<String> response) {

                if(response!=null){
                    Log.d("my","<<<<"+response.body());
                    dbVersion=response.body();
                }else {
                    Log.d("my","<<<<null");
                }
                updateStatus();

                updateInventories();

                return false;
            }
        });
    }


    private void updateStatus(){

        if(onlineMode){

            String textErr = "Can't connect to "+databaseIP+":"+databasePath+" !";
            titleBar.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            statusButton.setVisibility(View.VISIBLE);
            if(isConnect()){
                if(dbVersion==null) dbVersion="";
                if(dbVersion.length()>0){
                    statusButton.setImageDrawable(getResources().getDrawable(R.drawable.tint_status_online));
                    String text = "Connected to "+databaseIP+":"+databasePath+"\n"+"ver "+dbVersion;

                    titleBar.setText(text);
                }
                else{
                    String textW = "Connected to "+databaseIP+":"+databasePath+"\n Сбой получения версии базы !";
                    titleBar.setText(textW);
                    statusButton.setImageDrawable(getResources().getDrawable(R.drawable.tint_status_online_warning));
                }
            } else{
                titleBar.setText(textErr);
                statusButton.setImageDrawable(getResources().getDrawable(R.drawable.tint_status_offline));
            }

        }else{
            titleBar.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
            titleBar.setText(getString(R.string.inventory));
            statusButton.setVisibility(View.INVISIBLE);
        }
    };

    InventoriesListAdapter adapter=null;


    ArrayList<InventoryJSON> invlist=new ArrayList<>();


    private void updateInventories()
    {
        MainApplication.getApi().getInventoryList(new Callback<ArrayList<InventoryJSON>>() {
            @Override
            public boolean sendResult(Response<ArrayList<InventoryJSON>> response) {

                invlist = response.body();
                if(list!=null) Log.d("my","inv list!=null "+invlist.size());
                else Log.d("my","list is null");

                if(adapter==null) {
                    adapter = new InventoriesListAdapter(MainActivity.this, R.layout.row_inventory_document, invlist);
                    list.setAdapter(adapter);
                }
                else{
                    Log.d("my","notifydataChanged");
                    adapter.clear();
                    adapter.addAll(invlist);
                    adapter.notifyDataSetChanged();
                }

               // getDatabaseVersion();

                lock=false;//разблокируем процесс автообновления

                return false;
            }
        });
    }


    private boolean disconnect=false;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==SETTINGS_STATE){
            Log.d("my","we must update settings!");
            disconnect=true;

            stop=false; //Запускаем поток автообновления
            lock=false;
            ConnectThread thread = new ConnectThread();
            thread.start();

        }
    }
}
