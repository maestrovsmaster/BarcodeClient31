package main;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;

import com.app.barcodeclient3.*;
import com.rollbar.android.Rollbar;

import org.json.JSONArray;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import adapter.DatabaseHelper;
import scanworkingactivity.ScanSettingsActivity;

import org.json.JSONObject;

/**
 * Created by userd088 on 28.04.2016.
 */
public class MainApplication extends Application {


    public static boolean firstStart = true;

    public static SQLiteDatabase sdb;
    public static DatabaseHelper dbHelper = null;

    public static String CAMERA_STATE = "CAMERA_STATE";
    public static String CAMERA_ON = "CAMERA_ON";
    public static String CAMERA_OFF = "CAMERA_OFF";
    public static int UPDATE_STATE = 18;
    public static int SETTINGS_STATE = 416;


    public static String databaseIP = "";
    public static String databasePath = "";

    public static final String OPT_IP = "IP";
    public static final String OPT_PATH = "PATH";
    public static final String OPT_CONNECT_MODE = "CONNECT_MODE";
    //public static final String OPT_CONNECT_OFFLINE = "CONNECT_OFFLINE";
    // public static final String OPT_CONNECT_SERVER = "CONNECT_SERVER";
    // public static final String OPT_CONNECT_ONLINE = "CONNECT_ONLINE";
    public static final int CONNECT_OFFLINE = 0;
    public static final int CONNECT_SERVER = 1;
    public static final int CONNECT_ONLINE = 2;

    public static int connectMode = CONNECT_OFFLINE;


    public static Handler h = new Handler();


    public static String serverIP = "";
    public static String serverPort = "";
    public static String serverName = "BarcodeServer3";
    public static boolean OFFLINE_MODE = false;
    public static String mainURL = ""; //"http://192.168.65.156:8080/BarcodeServer3";//Main !!!!!!!!!!!!!!
    public static String WEIGTH_BARCODE_MASK = "";
    public static String WEIGTH_BARCODE = "";

    private static DataModelInterface dataModel;

    private String rollbarId = "69b56ad85ef34390a56d40727dedf010";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("my", "hello MainApplication!");

        Rollbar.init(this, rollbarId, "production");

        Rollbar.reportMessage("A test message Hello inventory!", "debug");


        context = getApplicationContext();


        Log.d("my", "hello MainApplication! 1");

        refreshOptions(MainApplication.this);

        Log.d("my", "hello MainApplication! 2");


        {

            refreshOldDBOptions();
        }

        // readyDBHandler = new ReadyDBHandler();
        // LoadDBThread loadDBThread = new LoadDBThread();
        // loadDBThread.start();


    }

    private static Context context;

    public static Context getAppContext() {

        return MainApplication.context;
    }

    public static void refreshOptions(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        connectMode = prefs.getInt(OPT_CONNECT_MODE, CONNECT_OFFLINE);
        databaseIP = prefs.getString(OPT_IP, "");
        serverIP=databaseIP;
        databasePath = prefs.getString(OPT_PATH, "");
        Log.d("my", "Main App refresh options we get CONNECT_MODE = " + connectMode);
        Log.d("my", "Main App refresh options we get path" + databasePath);
        if (connectMode == CONNECT_OFFLINE) OFFLINE_MODE = true;
        else OFFLINE_MODE = false;

        int scanType = prefs.getInt(ScanSettingsActivity.scan_type, 0);
        ScannerConstants.CURRENT_SCAN_MODE = scanType;
    }

    private void refreshOldDBOptions() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();

        dbHelper = new DatabaseHelper(getApplicationContext(), "mydatabase.db", null, 1);
        sdb = dbHelper.getReadableDatabase();
        Log.d("my", "SDB + " + dbHelper.getReadableDatabase().toString());
        ContentValues newValues = new ContentValues();
        newValues.put(dbHelper.PARAMETER, "1");
        newValues.put(dbHelper.VALUE, "1");
        String ip = dbHelper.getOption("IP");//getServerIp();
        if (ip == null) {
            Log.d("my", "ip =null");
           /* serverIP = "";
            dbHelper.insertOrReplaceOption("IP", serverIP);//setServerIp(serverIP);
            startSettings = true;*/

        } else {
            Log.d("my", "ip =" + ip);
            serverIP = ip;
            connectMode = 1;
            OFFLINE_MODE = false;

            editor.putString(OPT_IP, ip);
            editor.putInt(OPT_CONNECT_MODE, CONNECT_SERVER);
            editor.commit();
            refreshOptions(getAppContext());

        }


        String offlineMode = dbHelper.getOption("OFFLINE_MODE");
        if (offlineMode != null) {
            if (offlineMode.contains("1")) {
                OFFLINE_MODE = true;
                connectMode = CONNECT_OFFLINE;

                editor.putString(OPT_IP, "");
                editor.putInt(OPT_CONNECT_MODE, CONNECT_OFFLINE);
                editor.commit();
                refreshOptions(getAppContext());

            } else OFFLINE_MODE = false;
        }

        serverPort = "8080";
        mainURL = "http://" + serverIP + ":" + serverPort + "/" + serverName;

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


                serverIP = "";
                dbHelper.insertOrReplaceOption("IP", serverIP);//setServerIp(serverIP);
                startSettings = true;

            } else {
                Log.d("my", "ip =" + ip);
                serverIP = ip;
            }

            String port = dbHelper.getOption("PORT");//getServerPort();
            if (port == null) {
                Log.d("my", "port =null");
                serverPort = "8080";
                dbHelper.insertOrReplaceOption("PORT", port);//.setServerPort(serverPort);
                startSettings = true;

            } else {
                Log.d("my", "port =" + port);
                serverPort = port;
            }
            serverPort = "8080";
            mainURL = "http://" + serverIP + ":" + serverPort + "/" + serverName;


            String offlineMode = dbHelper.getOption("OFFLINE_MODE");
            if (offlineMode == null) dbHelper.insertOrReplaceOption("OFFLINE_MODE", "1");
            Log.d("my", "1 OFFLINE MODE from DB = " + offlineMode);
            offlineMode = dbHelper.getOption("OFFLINE_MODE");
            Log.d("my", "2 OFFLINE MODE from DB = " + offlineMode);
            if (offlineMode.contains("1")) OFFLINE_MODE = true;
            else OFFLINE_MODE = false;
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

    public static DataModelInterface getApi() {
        if (dataModel == null) {
            switch (connectMode) {
                case CONNECT_OFFLINE:
                    dataModel = DataModelOffline.getInstance();
                    break;
                case CONNECT_SERVER:
                    dataModel = DataModelBarcodeServerConnect.getInstance();
                    break;
                case CONNECT_ONLINE:
                    dataModel = DataModelOnline.getInstance();
                    break;
                default:
                    break;
            }

        }

        return dataModel;
    }

    private static Connection connection;

    private static Properties paramConnection;

    public static void disconnect() {
        if (connection != null) try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        connection = null;
    }

    public static Connection initConnect() {
        if (connection == null) connect();
        return connection;
    }

    private static void connect() {


        paramConnection = new Properties();
        paramConnection.setProperty("user", "SYSDBA");
        paramConnection.setProperty("password", "masterkey");
        paramConnection.setProperty("encoding", "UNICODE_FSS");
        String sCon = "jdbc:firebirdsql:" + MainApplication.databaseIP + "/3050:" + MainApplication.databasePath;//E:/Unisystem/DB/DB.FDB";
        try {
            // register Driver
            Class.forName("org.firebirdsql.jdbc.FBDriver");
            // Get connection
            connection = DriverManager.getConnection(sCon, paramConnection);
            //And if you remove the comment in a row
            paramConnection.setProperty("encoding", "WIN1251");
            Log.d("my", "conn ok= " + connection.toString());


        } catch (Exception e) {
            Log.d("my", "conn err= " + e.toString());

        }

    }


    public static boolean isConnect() {
        if (connection == null) return false;
        else return true;
    }

    public static JSONArray executeStatement(String statement) {
        JSONArray jsonArray = null;
        try {
            if (connection == null) {
                String msg = "Error in connection with SQL server";
                Log.d("my", "query statement conn err " + msg);
                return jsonArray;
            }
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(statement);
            ResultSetMetaData rsmd = rs.getMetaData();
            int cols = rsmd.getColumnCount();
            Properties connInfo = new Properties();
            connInfo.put("charSet", "UNICODE_FSS");



           /* while (rs.next())
            {
                ArrayList<Object> newRow = new ArrayList<>();
                for (int i = 1; i <= cols; i++)
                {
                    newRow.add(rs.getObject(i));
                    Log.d("my","rs ="+rs.getObject(i).toString());

                }

            }*/

            try {
                jsonArray = convertToJSON(rs);
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("my", "executeSt jsonArr err = " + e.toString());
            }

            rs.close();
            st.close();


        } catch (SQLException e) {
            Log.d("my", "KFDB.There are problems with the query ******");
            e.printStackTrace();
        }

        return jsonArray;
    }

    public static JSONArray convertToJSON(ResultSet resultSet)
            throws Exception {
        JSONArray jsonArray = new JSONArray();
        while (resultSet.next()) {
            int total_rows = resultSet.getMetaData().getColumnCount();
            JSONObject obj = new JSONObject();
            for (int i = 0; i < total_rows; i++) {
                obj.put(resultSet.getMetaData().getColumnLabel(i + 1).toLowerCase(), resultSet.getObject(i + 1));
            }
            jsonArray.put(obj);
        }
        return jsonArray;
    }


}

