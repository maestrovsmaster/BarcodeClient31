package com.app.barcodeclient3;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.sql.Connection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.zip.Inflater;

import butterknife.BindView;
import butterknife.ButterKnife;
import json_process.InventoryJSON;
import main.MainApplication;
import newmainscanner.NewScannerActivity;

import static main.MainApplication.CONNECT_OFFLINE;
import static main.MainApplication.CONNECT_ONLINE;
import static main.MainApplication.CONNECT_SERVER;
import static main.MainApplication.SETTINGS_STATE;
import static main.MainApplication.UPDATE_STATE;
import static main.MainApplication.databaseIP;
import static main.MainApplication.databasePath;
import static main.MainApplication.disconnect;
import static main.MainApplication.getApi;
import static main.MainApplication.isConnect;
import static main.MainApplication.connectMode;


public class MainActivity extends AppCompatActivity {

    @BindView(R.id.list)
    ListView list;
    @BindView(R.id.progress)
    ProgressBar progressBar;
    @BindView(R.id.fab)
    FloatingActionButton fab;

    ImageView statusButton;
    ImageView settingsButton;
    TextView titleBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        Log.d("my", "Hello MainActivity new!");


        //----
        getSupportActionBar().setDisplayOptions(getSupportActionBar().getDisplayOptions() | android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);

        getSupportActionBar().setCustomView(R.layout.action_bar_main);
        View abarView = getSupportActionBar().getCustomView();


        titleBar = abarView.findViewById(R.id.abarTitle);
        statusButton = abarView.findViewById(R.id.statusButton);
        settingsButton = abarView.findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stop = true;//Останавливаем поток автообновления
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivityForResult(intent, SETTINGS_STATE);
            }
        });


        getSupportActionBar().setDisplayHomeAsUpEnabled(false); //!!!!!!!!!!!!!!!!!!!!!!!!!!!!! BACK ICON!!!!!!!!!!!!!!!!!
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setIcon(android.R.color.transparent);


        final Toolbar parent = (Toolbar) abarView.getParent();
        parent.setPadding(0, 0, 0, 0);//for tab otherwise give space in tab
        parent.setContentInsetsAbsolute(0, 0);

        //--------------


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (connectMode) {
                    case CONNECT_OFFLINE:
                        createInvDialog();
                        break;
                    case CONNECT_SERVER:
                        getDatabaseVersion();
                        break;
                    case CONNECT_ONLINE:

                        break;

                }
            }
        });
        // fab.setVisibility(View.GONE);


        if (list == null) Log.d("my", " List is null");

        buildActivityState();


    }

    private void buildActivityState() {
        switch (connectMode) {
            case CONNECT_OFFLINE:
                fab.setImageResource(R.drawable.ic_add_white_36dp);

               // updateStatus();
                getDatabaseVersion();
                break;
            case CONNECT_SERVER:
                fab.setImageResource(R.drawable.ic_refresh_white_36dp);

                getDatabaseVersion();
                break;
            case CONNECT_ONLINE:
                fab.setImageResource(R.drawable.ic_refresh_white_36dp);
                fab.setVisibility(View.GONE);
                disconnect = true;
                stop = false; //Запускаем поток автообновления
                lock = false;
                ConnectThread thread = new ConnectThread();
                thread.start();
                break;

        }
    }


    String dbVersion = null;


    class ConnectThread extends Thread {
        @Override
        public void run() {

            if (disconnect) {
                disconnect = false;
                disconnect();
            }
            Connection connection = MainApplication.initConnect();//!!!!!!!!!!!!!!!!!!!!!!!
            if (connection == null) {
                Log.d("my", "connection is null");
            } else {
                Log.d("my", "connection is ok");


            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (isConnect()) {
                        Log.d("my", "connection is ok2");
                        //getDatabaseVersion();
                        // updateInventories();
                        UpdateStateThread updateStateThread = new UpdateStateThread();
                        updateStateThread.start();

                    } else updateStatus();
                }
            });
            //callback.sendResult(null);

        }
    }

    private long timesleep = 2000;
    private boolean stop = false;
    private boolean lock = false;

    class UpdateStateThread extends Thread {
        @Override
        public void run() {

            while (!stop) {
                if (!lock) {
                    getDatabaseVersion();
                    Log.d("my", "----------------------------------------------------------------------------------------------------------tick!!!!!!!!!!!!!!!!!!!!!");
                    try {
                        sleep(timesleep);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
            Log.d("my", "----------------------------------------------------------------------------------------------------------STOP ~~~~~~!!!!!!!!!!!!!!!!!!!!!");
        }
    }


    private void getDatabaseVersion() {
        lock = true;////Блокируем процесс автообновления
        Log.d("my", "MainActivity getDatabaseVersion");

        MainApplication.getApi().getDatabaseVersion(new Callback<String>() {
            @Override
            public boolean sendResult(Response<String> response) {

                if (response != null) {
                    Log.d("my", "<<<<" + response.body());
                    dbVersion = response.body();
                } else {
                    Log.d("my", "<<<<null");
                }
                updateStatus();

                updateInventories();

                return false;
            }
        });
    }


    private void updateStatus() {
        Log.d("my", "update status  connectMode=" + connectMode);

        titleBar.setTextColor(getResources().getColor(R.color.primary_dark));

        if (connectMode == CONNECT_ONLINE) {

            String textErr = "Can't connect to " + databaseIP + ":" + databasePath + " !";
            titleBar.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            statusButton.setVisibility(View.VISIBLE);
            if (isConnect()) {
                if (dbVersion == null) dbVersion = "";
                if (dbVersion.length() > 0) {
                    statusButton.setImageDrawable(getResources().getDrawable(R.drawable.tint_status_online));
                    String text = "Connected to " + databaseIP + ":" + databasePath + "\n" + "ver " + dbVersion;

                    titleBar.setText(text);
                } else {
                    String textW = "Connected to " + databaseIP + ":" + databasePath + "\n Сбой получения версии базы !";
                    titleBar.setText(textW);
                    statusButton.setImageDrawable(getResources().getDrawable(R.drawable.tint_status_online_warning));
                }
            } else {
                titleBar.setText(textErr);
                statusButton.setImageDrawable(getResources().getDrawable(R.drawable.tint_status_offline));
            }

        }

        if (connectMode == CONNECT_SERVER) {

            String textErr = "Can't connect to " + databaseIP + ":" + databasePath + " !";
            titleBar.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            statusButton.setVisibility(View.VISIBLE);

            if (dbVersion == null) {
                titleBar.setText(textErr);
                statusButton.setImageDrawable(getResources().getDrawable(R.drawable.tint_status_offline));
                return;
            }

            if (dbVersion.length() > 0) {
                statusButton.setImageDrawable(getResources().getDrawable(R.drawable.tint_status_online));
                String text = "Connected to " + databaseIP + ":8080/BarcodeServer3" + "\n" + "ver " + dbVersion;

                titleBar.setText(text);
            } else {
                String textW = "Connected to " + databaseIP + ":" + databasePath + "\n Сбой получения версии базы !";
                titleBar.setText(textW);
                statusButton.setImageDrawable(getResources().getDrawable(R.drawable.tint_status_online_warning));
            }
        }


        if (connectMode == CONNECT_OFFLINE) {
            titleBar.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
            titleBar.setText(getString(R.string.inventory));
            statusButton.setVisibility(View.INVISIBLE);
        }
    }

    ;

    InventoriesListAdapter adapter = null;


    ArrayList<InventoryJSON> invlist = new ArrayList<>();


    private void updateInventories() {
        Log.d("my","updateInventories...");
        MainApplication.getApi().getInventoryList(new Callback<ArrayList<InventoryJSON>>() {
            @Override
            public boolean sendResult(Response<ArrayList<InventoryJSON>> response) {

                invlist = response.body();
                if (list != null) Log.d("my", "inv list!=null " + invlist.size());
                else Log.d("my", "list is null");

                if (adapter == null) {
                    adapter = new InventoriesListAdapter(MainActivity.this, R.layout.row_inventory_document, invlist);
                    list.setAdapter(adapter);
                    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            Log.d("my", "click inv!");
                            stop = true;
                            InventoryJSON inventory = (InventoryJSON) view.getTag();
                            if (inventory.getDoc_state() == 0) {
                                if (connectMode == CONNECT_ONLINE) {
                                    Intent intent = new Intent(MainActivity.this, newmainscanner.ScannerOfflineActivity.class);
                                    intent.putExtra("id", inventory.getId());
                                    intent.putExtra("name", inventory.getSubdivisionName());
                                    intent.putExtra("date", inventory.getDateTime());
                                    intent.putExtra("num", inventory.getNum());
                                    intent.putExtra("subdiv_id", inventory.getSubdivisionId());
                                    startActivity(intent);
                                }
                                if (connectMode == CONNECT_SERVER||connectMode==CONNECT_OFFLINE) {
                                    Intent intent = new Intent(MainActivity.this, NewScannerActivity.class);

                                    Bundle arg = new Bundle();
                                    arg.putInt("id", inventory.getId());
                                    arg.putString("date", inventory.getDateTime());
                                    arg.putString("subdiv", inventory.getSubdivisionName());
                                    arg.putInt("docType", 0);

                                    intent.putExtras(arg);
                                    //startActivity(intent);
                                    startActivityForResult(intent, UPDATE_STATE);
                                }
                            }
                        }
                    });
                } else {
                    Log.d("my", "notifydataChanged");
                    adapter.clear();
                    adapter.addAll(invlist);
                    adapter.notifyDataSetChanged();
                }

                // getDatabaseVersion();

                lock = false;//разблокируем процесс автообновления

                return false;
            }
        });
    }

    Dialog newInventoryDialog;


    private void createInvDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.new_inventory_dialog, null);
        builder.setView(view);
        builder.setTitle(getString(R.string.create_inventory));

        /*newInventoryDialog = new Dialog(this);
        newInventoryDialog.setContentView(R.layout.new_inventory_dialog);
        newInventoryDialog.setTitle(getString(R.string.create_inventory));*/

        // set the custom newInventoryDialog components - text, image and button
        final EditText idEditText = view.findViewById(R.id.idEditText);
        final EditText nameEditText = view.findViewById(R.id.nameEditText);


        Button cancelButton =  view.findViewById(R.id.cancelBt);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newInventoryDialog.dismiss();
            }
        });

        Button createButton = view.findViewById(R.id.createBt);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //create inventory...
                //newInventoryDialog.dismiss();

                final String createInvId = idEditText.getText().toString();

                String strnm = nameEditText.getText().toString();
                //strnm += " - offline document";
                final String createInvName = strnm;
                //currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                DateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
                dateFormatter.setLenient(false);
                java.util.Date today = new java.util.Date();
                final String currentDateTimeString = dateFormatter.format(today);
                 int crInvId =0;
                try {
                     crInvId = Integer.parseInt(createInvId);

                } catch (Exception e) {
                }


                if (createInvId.length() > 0){
                    getApi().createInventory(crInvId, createInvId, currentDateTimeString, createInvName, 0);
                }

                updateInventories();
                newInventoryDialog.dismiss();

            }
        });
        newInventoryDialog = builder.create();
        newInventoryDialog.show();
    }


    @Override
    protected void onResume() {
        super.onResume();
        stop = false;
    }

    private boolean disconnect = false;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SETTINGS_STATE||requestCode==UPDATE_STATE) {


            Log.d("my", "we must update settings!");
            buildActivityState();


        }
    }
}
