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

import com.app.barcodeclient3.DataModelInterface;
import com.app.barcodeclient3.DataModelOnline;
import com.google.gson.Gson;

import org.apache.commons.dbutils.DbUtils;
import org.json.JSONArray;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import adapter.DatabaseHelper;
import scanworkingactivity.ScanSettingsActivity;
import startactivity.MainActivity;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.json.JSONObject;

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
    public static int SETTINGS_STATE = 416;



    public static String databaseIP="";
    public static String databasePath="";
    public static boolean onlineMode=false;

    public static Handler h = new Handler();

    private static DataModelInterface dataModel;

    @Override
    public void onCreate() {
        super.onCreate();


        refreshOptions(MainApplication.this);



        readyDBHandler = new ReadyDBHandler();
        LoadDBThread loadDBThread = new LoadDBThread();
        loadDBThread.start();


    }


    public static void refreshOptions(Context context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        onlineMode = prefs.getBoolean("ONLINE_MODE",false);
        databaseIP = prefs.getString("IP","");
        databasePath = prefs.getString("PATH","");

        Log.d("my","Main App refresh options we get path"+databasePath);

        int scanType = prefs.getInt(ScanSettingsActivity.scan_type, 0);
        ScannerConstants.CURRENT_SCAN_MODE=scanType;
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

    public  static  DataModelInterface getApi()
    {
        if(dataModel==null){
            dataModel = DataModelOnline.getInstance();
        }

        return dataModel;
    }

    private static Connection connection;

    private static Properties paramConnection;

    public static void disconnect()
    {
        if(connection!=null) try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        connection=null;
    }

    public static Connection initConnect()
    {
        if(connection==null)   connect();
       return  connection;
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



    public static boolean isConnect()
    {
        if(connection==null) return false;
        else return true;
    }

    public static JSONArray executeStatement(String statement)
    {
        JSONArray jsonArray = null;
        try
        {
            if(connection == null){
                String msg = "Error in connection with SQL server";
                Log.d("my","query statement conn err "+msg);
                return jsonArray;
            }
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(statement);
            ResultSetMetaData rsmd = rs.getMetaData();
            int cols = rsmd.getColumnCount();
            Properties connInfo=new Properties();
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
                Log.d("my","executeSt jsonArr err = "+e.toString());
            }

            rs.close();
            st.close();



        } catch (SQLException e)
        {
            Log.d("my","KFDB.There are problems with the query ******" );
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

