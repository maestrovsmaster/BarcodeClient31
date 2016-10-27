package scanworkingactivity;


import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.barcodeclient3.R;

import main.ScannerConstants;


public class ScanSettingsActivity extends Activity  {

	private ActionBar mainActionBar;
	ImageButton closeButton;
	TextView abarTitle;
	
	RelativeLayout typeCntRelLayout;
	RadioGroup scanModeRadioGroup;
	RadioButton normalScanButton;
	RadioButton plusFactScanButton;
	RadioButton plusOneScanButton;


	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		Log.d("my", "scan settings1");
		super.onCreate(savedInstanceState);
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_scan_settings);
		Log.d("my", "settings12");


		mainActionBar = getActionBar(); //main bar
		mainActionBar.setDisplayShowCustomEnabled(true);
		mainActionBar.setDisplayShowHomeEnabled(false);
		// mainActionBar.hide();
		mainActionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.indigo_700)));

		mainActionBar.setCustomView(R.layout.abar_scan_settings);
		closeButton = (ImageButton) mainActionBar.getCustomView().findViewById(R.id.closeButton);
		abarTitle = (TextView) mainActionBar.getCustomView().findViewById(R.id.abarTitle);
		abarTitle.setText(R.string.scan_parameters);
		closeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent();
				setResult(911, intent);
				finish();
			}
		});

		typeCntRelLayout = (RelativeLayout) findViewById(R.id.typeCntRelLayout);
		scanModeRadioGroup = (RadioGroup) findViewById(R.id.scanModeRadioGroup);
		normalScanButton = (RadioButton) findViewById(R.id.normalScanButton);
		plusFactScanButton = (RadioButton) findViewById(R.id.plusFactScanButton);
		plusOneScanButton = (RadioButton) findViewById(R.id.plusOneScanButton);
		scanModeRadioGroup.setOnCheckedChangeListener(checkRadioListener);

		switch (ScannerConstants.CURRENT_SCAN_MODE) {
			case ScannerConstants.NORMAL_SCAN:
				normalScanButton.setChecked(true);
				plusFactScanButton.setChecked(false);
				plusOneScanButton.setChecked(false);
				break;
			case ScannerConstants.PLUS_FACT_SCAN:
				normalScanButton.setChecked(false);
				plusFactScanButton.setChecked(true);
				plusOneScanButton.setChecked(false);
				break;
			case ScannerConstants.PLUS_ONE_SCAN:
				normalScanButton.setChecked(false);
				plusFactScanButton.setChecked(false);
				plusOneScanButton.setChecked(true);
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
					ScannerConstants.CURRENT_SCAN_MODE=0;
					//plusTextView.setText("");
					break;
				case R.id.plusFactScanButton:
					ScannerConstants.CURRENT_SCAN_MODE=2;
					//plusTextView.setText("+");
					break;
				case R.id.plusOneScanButton:
					ScannerConstants.CURRENT_SCAN_MODE=1;
					//plusTextView.setText("+");
					break;

				default:
					ScannerConstants.CURRENT_SCAN_MODE=0;
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




}
