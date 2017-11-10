package scanworkingactivity;


import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.barcodeclient3.R;

import main.ScannerConstants;


public class ScanSettingsActivity extends AppCompatActivity {

    private android.support.v7.app.ActionBar mainActionBar;
    ImageButton closeButton;
    TextView abarTitle;

    RelativeLayout typeCntRelLayout;
    RadioGroup scanModeRadioGroup;
    RadioButton normalScanButton;
    RadioButton plusFactScanButton;
    RadioButton plusOneScanButton;


    SharedPreferences prefs = null;

    public static final String scan_type = "scan_type";
    int scanType = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.d("my", "scan settings1");
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_scan_settings);
        Log.d("my", "settings12");


		/*mainActionBar = getActionBar(); //main bar
        mainActionBar.setDisplayShowCustomEnabled(true);
		mainActionBar.setDisplayShowHomeEnabled(false);
		// mainActionBar.hide();
		mainActionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.indigo_700)));

		mainActionBar.setCustomView(R.layout.abar_scan_settings);*/


        mainActionBar = getSupportActionBar();
        mainActionBar.setDisplayOptions(getSupportActionBar().getDisplayOptions() | android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        mainActionBar.setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        mainActionBar.setTitle(getString(R.string.settings));

        getSupportActionBar().setCustomView(R.layout.action_bar_book);
        // getSupportActionBar().setTitle(getResources().getString(R.string.my_orders));
        View abarView = getSupportActionBar().getCustomView();

        TextView titleBar = (TextView) abarView.findViewById(R.id.abarTitle);

        titleBar.setText(getString(R.string.settings));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //!!!!!!!!!!!!!!!!!!!!!!!!!!!!! BACK ICON!!!!!!!!!!!!!!!!!
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setIcon(android.R.color.transparent);


        final Toolbar parent = (Toolbar) abarView.getParent();
        parent.setPadding(0, 0, 0, 0);//for tab otherwise give space in tab
        parent.setContentInsetsAbsolute(0, 0);


        prefs = getSharedPreferences("com.app.barcodeclient3", MODE_PRIVATE);
        scanType = prefs.getInt(scan_type, 0);


        typeCntRelLayout = (RelativeLayout) findViewById(R.id.typeCntRelLayout);
        scanModeRadioGroup = (RadioGroup) findViewById(R.id.scanModeRadioGroup);
        normalScanButton = (RadioButton) findViewById(R.id.normalScanButton);
        plusFactScanButton = (RadioButton) findViewById(R.id.plusFactScanButton);
        plusOneScanButton = (RadioButton) findViewById(R.id.plusOneScanButton);
        scanModeRadioGroup.setOnCheckedChangeListener(checkRadioListener);

        switch (scanType) {
            case 0:
                normalScanButton.setChecked(true);
                plusFactScanButton.setChecked(false);
                plusOneScanButton.setChecked(false);
                break;
            case 1:
                normalScanButton.setChecked(false);
                plusFactScanButton.setChecked(false);
                plusOneScanButton.setChecked(true);
                break;
            case 2:
                normalScanButton.setChecked(false);
                plusFactScanButton.setChecked(true);
                plusOneScanButton.setChecked(false);
                break;


        }
    }

    RadioGroup.OnCheckedChangeListener checkRadioListener = new RadioGroup.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            // TODO Auto-generated method stub
            switch (checkedId) {
                case -1:
                    //Toast.makeText(getApplicationContext(), "No choice", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.normalScanButton:
                    prefs.edit().putInt(scan_type, 0).commit();
                    ScannerConstants.CURRENT_SCAN_MODE = 0;
                    //plusTextView.setText("");
                    break;
                case R.id.plusFactScanButton:
                    prefs.edit().putInt(scan_type, 2).commit();
                    ScannerConstants.CURRENT_SCAN_MODE = 2;
                    //plusTextView.setText("+");
                    break;
                case R.id.plusOneScanButton:
                    prefs.edit().putInt(scan_type, 1).commit();
                    ScannerConstants.CURRENT_SCAN_MODE = 1;
                    //plusTextView.setText("+");
                    break;

                default:
                    prefs.edit().putInt(scan_type, 0).commit();
                    ScannerConstants.CURRENT_SCAN_MODE = 0;
                    //plusTextView.setText("");
                    break;
            }

			/*if(currentGood!=null) {
				double fcnt = currentGood.getFcnt();
				goodsCntEditText.setSelectAllOnFocus(true);
				setFactCntTextView(fcnt);
			}*/
        }
    };


    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {

            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }


}
