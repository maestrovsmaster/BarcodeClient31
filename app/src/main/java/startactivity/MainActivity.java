package startactivity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings.Secure;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.app.barcodeclient3.R;

import java.util.ArrayList;

import adapter.DatabaseHelper;
import docklist.DockListActivity;
import essences.GoodGRP;
import essences.Inventory;
import main.MainApplication;
import main.MainFragment;
import main.OfflineModeActivity;
import main.SettingsActivity;
import newmainscanner.NewScannerActivity;
import requests.RequestGoodsGRP;
import scanworkingactivity.ScanWorkingActivity;
import start.WellcomeActivity2;

public class MainActivity extends Activity {

//hi!
    public static String device_id = "";
    public static String device_name = "";

    public static final int GOOD_ID_CONST = 0; //smeshenie id dlia tovara, sozdannogo na tel
    public static final int GRP_OFFLINE_GOODS=-3; //grp id tovara, sozdannogo na tel
    public static final String not_ask_create_good="NOT_ASK_CREATE_GOOD";//option ne sprashivat bol'she sozdat novyi tovar

    private ActionBarDrawerToggle mDrawerToggle;

    private static DrawerLayout mDrawerLayout;
    private static ListView mDrawerList;
    private static MenuItemsListAdapter adapter;
    public static ActionBar mainActionBar;


    MainFragment mainFragment;

    int docType = 0;



    public static String serverIP = "";
    public static String serverPort = "";
    public static String serverName = "BarcodeServer3";

    public static boolean OFFLINE_MODE = false;

    public static String mainURL = ""; //"http://192.168.65.156:8080/BarcodeServer3";//Main !!!!!!!!!!!!!!

    public static String WEIGTH_BARCODE_MASK = "";
    public static String WEIGTH_BARCODE = "";

    private static final int SETTINGS_ACTIVITY_REQUEST_CODE = 11;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // requestWindowFeature(Window.FEATURE_NO_TITLE);


        setContentView(R.layout.start_activity);


        device_id = Secure.getString(getContentResolver(), Secure.ANDROID_ID);

        Log.d("my", "Android id = " + device_id);

        device_name = getDeviceName();

        Log.d("my", "device name = " + device_name);


        mainActionBar = getActionBar(); //main bar
        mainActionBar.setDisplayShowCustomEnabled(true);



        //mainActionBar.hide();

        mainActionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(
                R.color.indigo_600)));


        //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

       // mDrawerLayout.setVisibility(View.GONE);


        //	navDrawerItems.add(new GoodGRPDrawerItem("/"));
        adapter = new MenuItemsListAdapter(getApplicationContext());
        mDrawerList.setAdapter(adapter);///////////!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

        setDrawerEnabled(true);


        ActionBar ab = null;
        try {
            ab = getActionBar();
        } catch (Exception e) {
            Log.d("my", " a bar error = " + e.toString());
        }

        try {
            ab.setDisplayHomeAsUpEnabled(true);
        } catch (Exception e) {
            Log.d("my", " a bar error = " + e.toString());
        }

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_drawer, // nav menu toggle icon
                R.string.app_name, // nav drawer open - description for accessibility
                R.string.app_name // nav drawer close - description for accessibility
        ) {
            public void onDrawerClosed(View view) {
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mDrawerToggle.setDrawerIndicatorEnabled(true);



        mainFragment = new MainFragment();
        // mainFragment.setTreeGoodsActivity(this);

        //goodsListFragment = new GoodsListFragment2();




        //fragmentManager.beginTransaction().replace(R.id.frame_container, goodsListFragment).commit();

        //loadDocList();


        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.add(R.id.frame_container, mainFragment).commit();



        startHandler.postAtTime(new Runnable() {
            @Override
            public void run() {
                if(MainActivity.OFFLINE_MODE){

                    startNewScanActivity();

                }else {

                   /* FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction ft = fragmentManager.beginTransaction();
                    ft.add(R.id.frame_container, mainFragment).commit();*/
                }

            }
        },500);


    }


    Handler startHandler = new Handler();



    public static void setDrawerEnabled(Boolean enabled) {
        if (enabled) {
            mDrawerList.setEnabled(true);
            mDrawerList.setVisibility(View.VISIBLE);
        } else {
            mDrawerList.setEnabled(false);
            mDrawerList.setVisibility(View.GONE);
        }
    }


    private static Runnable openDrawerRunnable() {
        return new Runnable() {


            public void run() {
                mDrawerLayout.openDrawer(Gravity.LEFT);
            }
        };
    }



    ReadyGoodsGRPHandler readyGoodsGRPHandler = null;

    class ReadyGoodsGRPHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {

            hideProgressBar();

            buildList();

            //super.handleMessage(msg);
        }
    }


    /**
     *
     */
    private void buildList() {


    }


    private int rootId = 0;
    private int currentRootId = rootId;


    ProgressDialog dialogGoToBookDetails;

    public void showProgressBar(boolean cancelable, String text) {

        dialogGoToBookDetails = new ProgressDialog(MainActivity.this);
        dialogGoToBookDetails.setMessage(text);
        dialogGoToBookDetails.setIndeterminate(true);
        dialogGoToBookDetails.setCancelable(cancelable);
        dialogGoToBookDetails.show();
    }

    public void hideProgressBar() {
        if (dialogGoToBookDetails != null) dialogGoToBookDetails.hide();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // toggle nav drawer on selecting action bar app icon/title
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action bar actions click
        switch (item.getItemId()) {
            case R.id.action_settings:

                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
        super.onConfigurationChanged(newConfig);
             /*  if(newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO) {

			     ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE))
			                                   .showInputMethodPicker();
			     Toast.makeText(this, "Barcode Scanner detected. Please turn OFF Hardware/Physical keyboard to enable softkeyboard to function.", 
			    		 Toast.LENGTH_LONG).show();
			   }*/
    }


    private class SlideMenuClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            Log.d("my", "click");

            switch (position) {


                case 0:

                    Intent intent = new Intent(MainActivity.this,
                            DockListActivity.class);
                    startActivity(intent);

                    break;

                case 1:

                    Intent intent5 = new Intent(MainActivity.this,
                            OfflineModeActivity.class);
                    startActivity(intent5);

                    break;

                case 2:

                    Intent intent2 = new Intent(MainActivity.this,
                            SettingsActivity.class);
                    startActivityForResult(intent2, SETTINGS_ACTIVITY_REQUEST_CODE);

                    break;

                case 3:

                    Intent intent3 = new Intent(MainActivity.this,
                            AboutAppActivity.class);
                    startActivity(intent3);

                    break;

                default:

                    break;
            }

        }
    }


    /**
     * Returns the consumer friendly device name
     */
    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }
        return capitalize(manufacturer) + " " + model;
    }

    private static String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;
        String phrase = "";
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase += Character.toUpperCase(c);
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
            phrase += c;
        }
        return phrase;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("my"," result: requestCode code = "+requestCode);

        if(requestCode==SETTINGS_ACTIVITY_REQUEST_CODE)
        {
            if(MainApplication.dbHelper!=null) {

                //updateServerURL();
                mainFragment.updateStatus();
            }
        }
    }

    /*private void updateServerURL()
    {
        serverIP =   dbHelper.getServerIp();
        serverPort = "8080";
        mainURL = "http://" + serverIP + ":" + serverPort + "/" + serverName;
    }*/

    private void startNewScanActivity(){
       // ArrayList<Inventory> invList = MainApplication.dbHelper.getInventoryList();
       // if(invList.size()==0)

        Intent intent = new Intent(MainActivity.this, NewScannerActivity.class);

        Bundle arg = new Bundle();
        arg.putInt("id", -1);
       /* arg.putString("date", date);
        arg.putString("subdiv", subdiv);*/
        arg.putInt("docType", docType);

        intent.putExtras(arg);

        startActivity(intent);

    }


}
