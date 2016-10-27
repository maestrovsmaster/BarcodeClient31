package startactivity;


import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

import com.app.barcodeclient3.R;


public class AboutAppActivity extends Activity  {
	
	

ImageButton backButton = null;

	TextView versionTextView;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		Log.d("my", "settings1");
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		
		setContentView(R.layout.activity_about_app);
		Log.d("my", "settings12");
		backButton =(ImageButton) findViewById(R.id.settingsBackBt);
		backButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});

		versionTextView= (TextView) findViewById(R.id.versionTextView);

		String ver = "Version "+getVersion();
		Log.d("my","VER = "+ver);

		versionTextView.setText(ver);

	}
	

	private String getVersion()
	{
		try {
		//	PackageInfo packageInfo = getPackageManager().getPackageInfo("com.app.barcodeclient3", 0);
		//	return packageInfo.versionName;
			PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			String version = pInfo.versionName;
			return  version;

		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return  "";
	}
	



}
