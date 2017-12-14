package main;

import android.app.Application;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import adapter.DatabaseHelper;
import scanworkingactivity.ScanSettingsActivity;
import start.WellcomeActivity2;
import startactivity.MainActivity;

/**
 * Created by userd088 on 28.04.2016.
 */
public class MainApplication extends Application {

    public static boolean firstStart=true;

    public static SQLiteDatabase sdb;
    public static DatabaseHelper dbHelper = null;

    public static String CAMERA_STATE="CAMERA_STATE";
    public static String CAMERA_ON="CAMERA_ON";
    public static String CAMERA_OFF="CAMERA_OFF";
    public static int UPDATE_STATE = 18;
    private SharedPreferences prefs;

    @Override
    public void onCreate() {
        super.onCreate();


        prefs = getSharedPreferences("com.app.barcodeclient3", MODE_PRIVATE);
        int scanType = prefs.getInt(ScanSettingsActivity.scan_type, 0);
        ScannerConstants.CURRENT_SCAN_MODE=scanType;

        readyDBHandler = new ReadyDBHandler();
        LoadDBThread loadDBThread = new LoadDBThread();
        loadDBThread.start();


    }





    class LoadDBThread extends Thread {
        @Override
        public void run() {
            dbHelper = new DatabaseHelper(getApplicationContext(), "mydatabase.db", null, 1);
            sdb = dbHelper.getReadableDatabase();
            Log.d("my", "SDB + " + dbHelper.getReadableDatabase().toString());
            ContentValues newValues = new ContentValues();
            newValues.put(dbHelper.PARAMETER, "1");
            newValues.put(dbHelper.VALUE, "1");

            boolean startSettings = false;

            String ip = dbHelper.getOption("IP");//getServerIp();
            if (ip == null) {
                Log.d("my", "ip =null");


                MainActivity.serverIP = "192.168.0.100";
                dbHelper.insertOrReplaceOption("IP", MainActivity.serverIP);//setServerIp(serverIP);
                startSettings = true;

            } else {
                Log.d("my", "ip =" + ip);
                MainActivity.serverIP = ip;
            }

            String port = dbHelper.getOption("PORT");//getServerPort();
            if (port == null) {
                Log.d("my", "port =null");
                MainActivity.serverPort = "8080";
                dbHelper.insertOrReplaceOption("PORT",port);//.setServerPort(serverPort);
                startSettings = true;

            } else {
                Log.d("my", "port =" + port);
                MainActivity.serverPort = port;
            }
            MainActivity.serverPort ="8080";
            MainActivity. mainURL = "http://" + MainActivity.serverIP + ":" + MainActivity.serverPort + "/" + MainActivity.serverName;



            String offlineMode = dbHelper.getOption("OFFLINE_MODE");
            if(offlineMode==null) dbHelper.insertOrReplaceOption("OFFLINE_MODE", "1");
            Log.d("my","1 OFFLINE MODE from DB = "+offlineMode);
            offlineMode = dbHelper.getOption("OFFLINE_MODE");
            Log.d("my","2 OFFLINE MODE from DB = "+offlineMode);
            if(offlineMode.contains("1")) MainActivity.OFFLINE_MODE=true;
            else MainActivity.OFFLINE_MODE=false;
            //startSettings = true; !!!!/////////////////////
            if (startSettings) {
              //  Intent intent2 = new Intent(MainActivity.this, WellcomeActivity2.class);

              //  startActivity(intent2);

            } else {
                readyDBHandler.sendEmptyMessage(0);
            }
        }
    }

    ReadyDBHandler readyDBHandler = null;


    class ReadyDBHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
         /*   Log.d("my", "db ready, server url = " + mainURL);
            if(!OFFLINE_MODE)
                if (serverIP != null && serverPort != null) mainFragment.updateStatus();*/
        }

    }



}

