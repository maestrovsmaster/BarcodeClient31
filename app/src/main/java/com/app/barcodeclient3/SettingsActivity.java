package com.app.barcodeclient3;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

import static main.MainApplication.CONNECT_OFFLINE;
import static main.MainApplication.CONNECT_ONLINE;
import static main.MainApplication.CONNECT_SERVER;
import static main.MainApplication.OPT_CONNECT_MODE;
import static main.MainApplication.OPT_IP;
import static main.MainApplication.OPT_PATH;
import static main.MainApplication.databaseIP;
import static main.MainApplication.databasePath;
import static main.MainApplication.connectMode;
import static main.MainApplication.refreshOptions;

public class SettingsActivity extends AppCompatActivity {

    @BindView(R.id.offlineCheckBox)CheckBox offlineCheckBox;

    @BindView(R.id.onlineCheclBox)CheckBox onlineCheckBox;
    @BindView(R.id.connectLayout)LinearLayout connectLayout;
    @BindView(R.id.ipTV)EditText ipTV;
    @BindView(R.id.pathTV)EditText pathTV;

    @BindView(R.id.barcodeCheckBox)CheckBox barcodeCheckBox;
    @BindView(R.id.connectBarcLayout)LinearLayout connectBarcLayout;
    @BindView(R.id.ipbarcodeTV)EditText ipbarcodeTV;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ButterKnife.bind(this);

        //----
        getSupportActionBar().setDisplayOptions(getSupportActionBar().getDisplayOptions() | android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);

        getSupportActionBar().setCustomView(R.layout.action_bar_main);
        View abarView = getSupportActionBar().getCustomView();


        TextView titleBar = abarView.findViewById(R.id.abarTitle);
        titleBar.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        titleBar.setText(getString(R.string.settings));

        ImageView statusButton = abarView.findViewById(R.id.statusButton);
        statusButton.setVisibility(View.GONE);

        ImageView settingsButton = abarView.findViewById(R.id.settingsButton);
        settingsButton.setVisibility(View.INVISIBLE);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //!!!!!!!!!!!!!!!!!!!!!!!!!!!!! BACK ICON!!!!!!!!!!!!!!!!!
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setIcon(android.R.color.transparent);


        final Toolbar parent = (Toolbar) abarView.getParent();
        parent.setPadding(0, 0, 0, 0);//for tab otherwise give space in tab
        parent.setContentInsetsAbsolute(0, 0);

        offlineCheckBox.setChecked(connectMode==CONNECT_OFFLINE?true:false);
        barcodeCheckBox.setChecked(connectMode==CONNECT_SERVER?true:false);
        onlineCheckBox.setChecked(connectMode==CONNECT_ONLINE?true:false);
        connectLayout.setVisibility(onlineCheckBox.isChecked()?View.VISIBLE:View.GONE);
        connectBarcLayout.setVisibility(barcodeCheckBox.isChecked()?View.VISIBLE:View.GONE);


        offlineCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    connectLayout.setVisibility(View.GONE);
                    connectBarcLayout.setVisibility(View.GONE);
                    barcodeCheckBox.setChecked(false);
                    onlineCheckBox.setChecked(false);
                }

            }
        });


        onlineCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    connectLayout.setVisibility(View.VISIBLE);
                    connectBarcLayout.setVisibility(View.GONE);
                    barcodeCheckBox.setChecked(false);
                    offlineCheckBox.setChecked(false);
                }
                else connectLayout.setVisibility(View.GONE);
            }
        });

        ipTV.setText(databaseIP);
        pathTV.setText(databasePath);


        //---



        ipbarcodeTV.setText(databaseIP);
        barcodeCheckBox.setChecked(connectMode==CONNECT_SERVER?true:false);
        barcodeCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    connectBarcLayout.setVisibility(View.VISIBLE);
                    connectLayout.setVisibility(View.GONE);
                    onlineCheckBox.setChecked(false);
                    offlineCheckBox.setChecked(false);
                }
                else connectBarcLayout.setVisibility(View.GONE);
            }
        });


    }

    private void saveOptions()
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        String ip = ipTV.getText().toString();
        if(barcodeCheckBox.isChecked()) ip= ipbarcodeTV.getText().toString();
        String path=pathTV.getText().toString();

        editor.putString(OPT_IP, ip);
        editor.putString(OPT_PATH, path);
        int mode= CONNECT_OFFLINE;
        if(onlineCheckBox.isChecked()) mode=CONNECT_ONLINE;
        if(barcodeCheckBox.isChecked()) mode = CONNECT_SERVER;

        Log.d("my","save options: mode = "+mode);
        editor.putInt(OPT_CONNECT_MODE,mode);
        editor.commit();
        refreshOptions(SettingsActivity.this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            // do something on back.

            saveOptions();

            Intent returnIntent = new Intent();
            returnIntent.putExtra("result", 11114);
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                saveOptions();
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", 11114);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
                return true;
        }


        return super.onOptionsItemSelected(item);

    }
}
