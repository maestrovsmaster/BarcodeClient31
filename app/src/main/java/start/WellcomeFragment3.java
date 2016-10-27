package start;

import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.app.barcodeclient3.R;

import org.json.JSONObject;

import adapter.DatabaseHelper;
import adapter.Utils;
import main.MainApplication;
import requests.RequestDBVersion;
import startactivity.MainActivity;

/**
 * Created by userd088 on 30.03.2016.
 */
public class WellcomeFragment3 extends Fragment {

    LayoutInflater inflater;
    ViewGroup container;

    EditText ipAddressTextView;
    ProgressBar progressConnect;
    Button connectButton;
    TextView resultTextView;

    String stateUrl = "";
    String ver = "";
    String ans = "";
    String err  ="";
    JSONObject ansJSON=null;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("my", "9999999999");

        this.inflater = inflater;
        this.container = container;
        View	rootView = inflater.inflate(R.layout.wellcome_fragment_3, container, false);

        //   Button bt = (Button) rootView.findViewById(R.id.button);

        ipAddressTextView = (EditText)rootView.findViewById(R.id.ipAddressTextView);

        String myIp = Utils.getIPAddress(true);
        Log.d("my"," my ip = "+myIp);
        ipAddressTextView.setText(myIp);

        progressConnect = (ProgressBar)rootView.findViewById(R.id.progressConnect);
        connectButton = (Button) rootView.findViewById(R.id.connectButton);
        resultTextView = (TextView) rootView.findViewById(R.id.resultTextView);

        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard();
                LoadDBThread loadDBThread = new LoadDBThread();
                loadDBThread.start();
            }
        });


        return rootView;
    }


    class LoadDBThread extends Thread {
        @Override
        public void run() {
                Log.d("my", "ip write to db...");
                String serverIP = "192.168.0.1";
                String tempstr = ipAddressTextView.getText().toString().trim();
            tempstr.trim();
            if(tempstr.length()>7) {
                serverIP = tempstr;
                Log.d("my","our serv = "+serverIP);
                MainApplication.dbHelper.insertOrReplaceOption("IP", serverIP);//dbHelper.setServerIp(serverIP);
                MainActivity.serverIP = serverIP;

                MainActivity.mainURL = "http://" + MainActivity.serverIP + ":" + MainActivity.serverPort + "/" + MainActivity.serverName;
                h0.post(new Runnable() {
                    @Override
                    public void run() {
                        updateStatus();
                    }
                });
            }
        }
    }


Handler h0 = new Handler();

    private  void updateStatus()
    {

        err="";

        //  errorTextView.setText(R.string.connecting);
        progressConnect.setVisibility(View.VISIBLE);
        connectButton.setVisibility(View.GONE);
        resultTextView.setText("");
        getDBVersion=null;
        getDBVersion = new GetVersionThread();
        getDBVersion.start();
        stateUrl = MainActivity.mainURL + "/StateServlet";
        Log.d("my", "servet url = " + stateUrl);
        //webView.loadUrl(stateUrl);

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
            progressConnect.setVisibility(View.GONE);
            connectButton.setVisibility(View.VISIBLE);

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
                    MainActivity.OFFLINE_MODE=false;
                    successAction();

                }
                else{
                    MainActivity.OFFLINE_MODE=true;
                    noDBAnswerAction();

                }
            }
            else{
                noServerAnswerAction();
            }

        }
    };

    private void hideSoftKeyboard()
    {
        //InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        //imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
      /*  getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );*/

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput (InputMethodManager.SHOW_FORCED, InputMethodManager.RESULT_HIDDEN);

        //to show soft keyboard
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        Log.d("my","keyboard = "+imm.isActive());

//to hide it, call the method again
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

    }




    private void successAction()
    {


        resultTextView.setText(getString(R.string.db_version)+" "+ver);
        connectButton.setEnabled(false);
        connectButton.setText(getString(R.string.connected));
        connectButton.setBackgroundColor(getResources().getColor(R.color.green));
        ipAddressTextView.setEnabled(false);
       /* statusRelative.setBackgroundColor(getResources().getColor(R.color.green));
        statusImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_cast_connected_white_48dp));
        statusTextView.setText(R.string.connected);
        errorTextView.setText("");*/
    }

    private void noDBAnswerAction()
    {

        resultTextView.setText("сервер подключен, но нет связи с базой данных");
       /* statusRelative.setBackgroundColor(getResources().getColor(R.color.gold));
        statusImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_warning_white_48dp));
        statusTextView.setText(R.string.no_connect_to_database);
        errorTextView.setText("");*/
        // ipConnectTextView.setText(MainActivity.serverIP + ":" + MainActivity.serverPort);
        // errorTextView.setText(err);
    }

    private void noServerAnswerAction()
    {
        try {
            err = ansJSON.getString("err");
            resultTextView.setText(getString(R.string.server_error));
        }catch (Exception e){}

       /* statusRelative.setBackgroundColor(getResources().getColor(R.color.ligth_gray));
        statusImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_error_red_48dp));
        statusTextView.setText(R.string.no_connect_to_server);
        ipConnectTextView.setText(MainActivity.serverIP + ":" + MainActivity.serverPort);
        errorTextView.setText(err);

        settingsImageView.setVisibility(View.VISIBLE);*/

    }






}
