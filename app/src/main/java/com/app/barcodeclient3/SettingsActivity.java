package com.app.barcodeclient3;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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
import main.MainApplication;

import static main.MainApplication.databaseIP;
import static main.MainApplication.databasePath;
import static main.MainApplication.onlineMode;
import static main.MainApplication.refreshOptions;

public class SettingsActivity extends AppCompatActivity {

    @BindView(R.id.onlineCheclBox)CheckBox onlineCheckBox;
    @BindView(R.id.connectLayout)LinearLayout connectLayout;
    @BindView(R.id.ipTV)EditText ipTV;
    @BindView(R.id.pathTV)EditText pathTV;



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

        onlineCheckBox.setChecked(onlineMode);
        if(onlineMode) connectLayout.setVisibility(View.VISIBLE);
        else connectLayout.setVisibility(View.GONE);

        onlineCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) connectLayout.setVisibility(View.VISIBLE);
                else connectLayout.setVisibility(View.GONE);
            }
        });

        ipTV.setText(databaseIP);
        pathTV.setText(databasePath);


    }

    private void saveOptions()
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        String ip = ipTV.getText().toString();
        String path=pathTV.getText().toString();
        boolean onlineMode = onlineCheckBox.isChecked();
        editor.putString("IP", ip);
        editor.putString("PATH", path);
        editor.putBoolean("ONLINE_MODE",onlineMode);
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
