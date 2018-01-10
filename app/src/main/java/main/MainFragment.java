package main;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.barcodeclient3.R;

import org.json.JSONObject;

import docklist.DockListActivity;
//import excel.AndroidReadExcelActivity;
import requests.RequestDBVersion;
import requests.RequestUniversalQuery;
import scanworkingactivity.TotalGoodsListActivity;
import oldbarcodestartactivity.MainActivity;

import static main.MainApplication.OFFLINE_MODE;
import static main.MainApplication.mainURL;
import static main.MainApplication.serverIP;
import static main.MainApplication.serverPort;

/**
 * Created by userd088 on 14.09.2015.
 */
public class MainFragment extends Fragment{

    static View rootView;

    ViewGroup container = null;

    public  MainFragment()
    {
    }

    RelativeLayout statusRelative;
    ImageView statusImageView;
    ImageView refreshImageView;
    TextView statusTextView;
    ProgressBar progressBar1;

    ImageView settingsImageView;
    TextView ipConnectTextView;
    TextView errorTextView;

    TextView autonomTextView;

    EditText hrefEditText;

    Button inventoryButton;
    Button billInButton;
    Button exitButton;
    RelativeLayout connectRelative;

    WebView webView;

    String ver = "";
    String ans = "";
    String err  ="";
    JSONObject ansJSON=null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        LayoutInflater mInflater = LayoutInflater.from(getActivity());



        this.container = container;
        // requestWindowFeature(Window.FEATURE_NO_TITLE);
        rootView = inflater
                .inflate(R.layout.main_fragment, container, false);
        // setContentView(R.layout.library);
        Log.d("my", "main");

        statusRelative = (RelativeLayout)rootView.findViewById(R.id.statusRelative);
        statusImageView= (ImageView) rootView.findViewById(R.id.statusImageView);
        refreshImageView=(ImageView) rootView.findViewById(R.id.refreshImageView);
        statusTextView=(TextView)rootView.findViewById(R.id.statusTextView);
        progressBar1=(ProgressBar)rootView.findViewById(R.id.progressBar1);
        settingsImageView=(ImageView)rootView.findViewById(R.id.settingsImageView);
        ipConnectTextView=(TextView)rootView.findViewById(R.id.ipConnectTextView);
        errorTextView=(TextView)rootView.findViewById(R.id.errorTextView);
        hrefEditText=(EditText) rootView.findViewById(R.id.hrefEditText);
        webView = (WebView) rootView.findViewById(R.id.webView);
        connectRelative=(RelativeLayout)rootView.findViewById(R.id.connectRelative);

        autonomTextView=(TextView) rootView.findViewById(R.id.autonomTextView);

        exitButton = (Button) rootView.findViewById(R.id.exitButton);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
                System.exit(0);
            }
        });


        inventoryButton  = (Button) rootView.findViewById(R.id.inventoryButton);
        inventoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("my","start inventory");
                Intent intent = new Intent(getActivity(),
                        DockListActivity.class);
                Bundle b = new Bundle();
                b.putInt("type", ScannerConstants.INVENTORY);
                intent.putExtras(b);
                startActivity(intent);
            }
        });

        billInButton  = (Button) rootView.findViewById(R.id.billInButton);
        billInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),
                        DockListActivity.class);
                Bundle b = new Bundle();
                b.putInt("type", ScannerConstants.BILL_IN);
                intent.putExtras(b);
                startActivity(intent);
            }
        });



        settingsImageView.setOnClickListener(settingsClickListener);
        refreshImageView.setOnClickListener(updateClickListener);


        Button excelButton = (Button) rootView.findViewById(R.id.excelButton);
        excelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),
                        excel.AndroidReadExcelActivity.class);
                startActivity(intent);
            }
        });

        //updateStatus();
      ///  MainActivity.OFFLINE_MODE=true;///!!!!!!!!!!!!!!!!!!!!!!!!!!!!

        ///////////////////////////!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
      /*  new Thread(new Runnable() {
            @Override
            public void run() {
                String ver = ConnectConstants.getConnect(ConnectConstants.DB_IP, ConnectConstants.DB_PATH, "SYSDBA", "masterkey");
                Log.d("my"," my ver = "+ver);
            }
        }).start();*/

        ////////////////////////////////////////////////////!!!!!


        final View debug = rootView.findViewById(R.id.debugButton);
        debug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               // DebugThread debugThread = new DebugThread();
                //debugThread.start();
                Intent intent = new Intent(getActivity(),
                        TotalGoodsListActivity.class);
                startActivity(intent);


            }
        });
        debug.setVisibility(View.GONE);


        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("my","ON START!!!!000");
        if(OFFLINE_MODE)
        {
            webView.setVisibility(View.GONE);
            errorTextView.setVisibility(View.GONE);
            autonomTextView.setVisibility(View.VISIBLE);
            statusTextView.setVisibility(View.GONE);
            //statusImageView.setVisibility(View.GONE);
            connectRelative.setVisibility(View.GONE);
            refreshImageView.setVisibility(View.GONE);
        }else{
            webView.setVisibility(View.VISIBLE);
            errorTextView.setVisibility(View.VISIBLE);
            autonomTextView.setVisibility(View.GONE);
            statusTextView.setVisibility(View.VISIBLE);
            //statusImageView.setVisibility(View.VISIBLE);
            connectRelative.setVisibility(View.VISIBLE);
            refreshImageView.setVisibility(View.VISIBLE);
        }
    }

    class DebugThread extends Thread
    {
        @Override
        public void run() {



            RequestUniversalQuery requestUniversalQuery = new RequestUniversalQuery();
            requestUniversalQuery.getQuery("select * from dic_goods");

        }
    }





    String stateUrl ="";

    boolean need_update =false;

    public  void updateStatus()
    {
        stateUrl = mainURL + "/StateServlet";
        err="";
        hrefEditText.setText("/StateServlet");
        //hrefEditText.setText("Login?login=&pass=&submit=Войти");
        //  errorTextView.setText(R.string.connecting);
        progressBar1.setVisibility(View.VISIBLE);
        getDBVersion=null;
        getDBVersion = new GetVersionThread();
        getDBVersion.start();
        Log.d("my", "servet url = " + stateUrl);
        webView.loadUrl(stateUrl);

    }


    GetVersionThread getDBVersion;


    Handler h = new Handler();

    class GetVersionThread extends  Thread
    {
        @Override
        public void run() {
            RequestDBVersion requestDBVersion =new RequestDBVersion();
            ansJSON = requestDBVersion.getDBVersion();
            h.post(readyGetDBVersion);
        }
    };

    Runnable readyGetDBVersion = new Runnable() {
        @Override
        public void run() {
            Log.d("my","!!!post handler ");
            progressBar1.setVisibility(View.GONE);
            //
            // statusImageView.setVisibility(View.VISIBLE);

            try {
                ans = ansJSON.getString("ans");
            }catch (Exception e){}
            try {
                ver = ansJSON.getString("ver");
            }catch (Exception e){}

            Log.d("my","hello: ans = "+ans+" ver = "+ver);

            if(ans.length()>0)
            {
                if(ver.length()>0)
                {
                   // MainActivity.OFFLINE_MODE=false;
                    Log.d("my","succ");
                    successAction();

                }
                else{
                    //MainActivity.OFFLINE_MODE=true;
                    Log.d("my","noDB");
                    noDBAnswerAction();

                }
            }
            else{
                Log.d("my","noAnswer");
                noServerAnswerAction();
            }

        }
    };



    private void successAction()
    {
        inventoryButton.setEnabled(true);
        statusRelative.setBackgroundColor(getResources().getColor(R.color.green));
        //statusImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_cast_connected_white_48dp));
        statusTextView.setText(R.string.connected);
        errorTextView.setText("");
    }

    private void noDBAnswerAction()
    {
       // inventoryButton.setEnabled(false);
        statusRelative.setBackgroundColor(getResources().getColor(R.color.gold));
        //statusImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_warning_white_48dp));
        statusTextView.setText(R.string.no_connect_to_database);
        errorTextView.setText("");
       // ipConnectTextView.setText(MainActivity.serverIP + ":" + MainActivity.serverPort);
       // errorTextView.setText(err);
    }

    private void noServerAnswerAction()
    {
       // inventoryButton.setEnabled(false);
        try {
            err = ansJSON.getString("err");
        }catch (Exception e){}

        statusRelative.setBackgroundColor(getResources().getColor(R.color.ligth_gray));
        //statusImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_error_red_48dp));
        statusTextView.setText(R.string.no_connect_to_server);
        ipConnectTextView.setText(serverIP + ":" + serverPort);
        errorTextView.setText(err);

        settingsImageView.setVisibility(View.VISIBLE);

    }



    View.OnClickListener settingsClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            need_update = true;
            Intent intent = new Intent(getActivity(),SettingsActivity.class);
            getActivity().startActivity(intent);
        }
    };


    View.OnClickListener updateClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            updateStatus();
        }
    };


    @Override
    public void onResume() {
        super.onResume();
        Log.d("my", "onResume fragment");

       need_update=true;///!!!!!!!!!!!!!!!!!!!!
        if(need_update) {
            Log.d("my", "update.");
            updateStatus();
        }
    }
}
