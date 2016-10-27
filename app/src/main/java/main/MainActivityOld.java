package main;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;

import com.app.barcodeclient3.R;

import java.util.HashMap;

import essences.Good;
 
public class MainActivityOld extends Activity  {
	
	
	public static HashMap<Integer, Good> goodList = null;
	//public static HashMap<Integer,GoodGRP> goodGRPList = null;

 
   
	ImageButton goodsButton =null;
	ImageButton settingsButton =null;
	ImageButton exitButton=null;
	ImageButton infoButton=null;
	ImageButton billlButton=null;
	ImageButton inventoryButton=null;
 
	int dur=180;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main_activity_old);
 
        goodsButton = (ImageButton) findViewById(R.id.goodsButton);
        goodsButton.setOnClickListener(goodsBtListener);
        
        
        settingsButton =(ImageButton)findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(settingsBtListener);
        
        exitButton =(ImageButton)findViewById(R.id.exitButton);
        exitButton .setOnClickListener(exitBtListener);
        
        billlButton =(ImageButton)findViewById(R.id.billInButton);
        inventoryButton =(ImageButton)findViewById(R.id.inventoryButton);
        infoButton =(ImageButton)findViewById(R.id.infoButton);
        

        
        

        
    }
    

    

    
    android.view.View.OnClickListener goodsBtListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
			Intent intent = new Intent(MainActivityOld.this, TreeGrpAndGoodsActivity.class);
			intent.putExtra(TreeGrpAndGoodsActivity.DOC_TYPE, 1);
		     startActivity(intent);
			
			
			
		}
	};
	
	
	 android.view.View.OnClickListener settingsBtListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivityOld.this, SettingsActivity.class);
				//Intent intent = new Intent(MainActivityOld.this, FragmentActivity.class);
			     startActivity(intent);
				
				
				
			}
		};
		
android.view.View.OnClickListener exitBtListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
				android.os.Process.killProcess(android.os.Process.myPid());
			}
		};
	
	
    
   
 
    }