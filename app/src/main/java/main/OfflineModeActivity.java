package main;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.app.barcodeclient3.R;

import java.util.ArrayList;

import essences.Good;
import essences.GoodGRP;
import requests.RequestGoodsCount;
import requests.RequestGoodsGRP;
import requests.RequestGoodsList;
import startactivity.MainActivity;


public class OfflineModeActivity extends Activity  {



	ImageButton backButton = null;

	CheckBox checkOffline=null;
	Button importBt;
	Button clearBt;
	ImageView offlineIcon;
	ProgressBar progressBar;
	ProgressBar updateProgress;

	TextView mobileGoodsCnt;
	TextView serverGoodsCnt;
	CheckBox noCreateGoodcheckBox;


	boolean notCreateGood;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		Log.d("my", "settings1");
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		
		setContentView(R.layout.activity_offline_mode);
		Log.d("my", "settings12");
		backButton =(ImageButton) findViewById(R.id.settingsBackBt);
		backButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				if (load) {
					AlertDialog.Builder builder = new AlertDialog.Builder(OfflineModeActivity.this);
					builder.setTitle("Выйти?");
					builder.setMessage("Загрузка будет прекращена");
					builder.setCancelable(true);
					builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() { // Кнопка ОК
						@Override
						public void onClick(DialogInterface dialog, int which) {
							load = false;
							dialog.dismiss();
							finish();
						}
					});
					builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() { // Кнопка ОК
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss(); // Отпускает диалоговое окно
						}
					});
					AlertDialog dialog = builder.create();
					dialog.show();
				} else {
					finish();
				}

			}
		});
		offlineIcon = (ImageView)findViewById(R.id.offlineIcon);
		progressBar = (ProgressBar)findViewById(R.id.progressBar);
		updateProgress =(ProgressBar)findViewById(R.id.updateProgress);
		updateProgress.setVisibility(View.GONE);

		mobileGoodsCnt = (TextView)findViewById(R.id.mobileGoodsCnt);
		serverGoodsCnt = (TextView)findViewById(R.id.serverGoodsCnt);


		checkOffline =(CheckBox) findViewById(R.id.checkOffline);
		checkOffline.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
				if(b){
					offlineIcon.setVisibility(View.VISIBLE);
					MainActivity.OFFLINE_MODE=true;
					MainApplication.dbHelper.insertOrReplaceOption("OFFLINE_MODE","1");
				}
				else{
					offlineIcon.setVisibility(View.GONE);
					MainActivity.OFFLINE_MODE=false;
					MainApplication.dbHelper.insertOrReplaceOption("OFFLINE_MODE","0");
				}
			}
		});

		if(MainActivity.OFFLINE_MODE) {
			offlineIcon.setVisibility(View.VISIBLE);
			checkOffline.setChecked(true);
		}else{
			offlineIcon.setVisibility(View.GONE);
			checkOffline.setChecked(false);
		}


		importBt = (Button)findViewById(R.id.importBt);
		importBt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Log.d("my","server good count = "+serverGoodsCount);
				if (serverGoodsCount > 0) {
					h.post(new Runnable() {
						@Override
						public void run() {
							progressBar.setMax(serverGoodsCount);
							progressBar.setProgress(0);
							updateProgress.setVisibility(View.VISIBLE);
							LoadGoodsByBarcThread load = new LoadGoodsByBarcThread();
							load.start();
						}
					});

				}
			}
		});

		clearBt = (Button)findViewById(R.id.clearBt);
		clearBt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				AlertDialog.Builder builder = new AlertDialog.Builder(OfflineModeActivity.this);
				builder.setTitle("Удалить?");
				builder.setMessage("Все справочники  будут удалены");
				builder.setCancelable(true);
				builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() { // Кнопка ОК
					@Override
					public void onClick(DialogInterface dialog, int which) {
						EmptyTable emptyTable = new EmptyTable();
						emptyTable.start();
						dialog.dismiss();
					}
				});
				builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() { // Кнопка ОК
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss(); // Отпускает диалоговое окно
					}
				});
				AlertDialog dialog = builder.create();
				dialog.show();
			}
		});

		noCreateGoodcheckBox = (CheckBox)findViewById(R.id.noCreateGoodcheckBox);



		noCreateGoodcheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				String ns="0";
				if(isChecked) ns="1";
				MainApplication.dbHelper.insertOrReplaceOption(MainActivity.not_ask_create_good,ns);
			}
		});


		LoadGoodsMobileCount loadGoodsMobileCount = new LoadGoodsMobileCount();
		loadGoodsMobileCount.start();


	}
	Handler checkBoxHandler= new Handler();

	int serverGoodsCount =0;
	int mobileGoodsCount =0;

	Handler  h=new Handler();

	class LoadGoodsMobileCount extends Thread {
		@Override
		public void run() {

			String value = MainApplication.dbHelper.getOption(MainActivity.not_ask_create_good);
			if(value==null){
				MainApplication.dbHelper.insertOrReplaceOption(MainActivity.not_ask_create_good,"0");
				value = MainApplication.dbHelper.getOption(MainActivity.not_ask_create_good);
			}
			if(value.equals("1")) {
				notCreateGood = true;

			}
			else {
				notCreateGood = false;

			}

			checkBoxHandler.post(new Runnable() {
				@Override
				public void run() {
					noCreateGoodcheckBox.setChecked(notCreateGood);
				}
			});



			int goodsCnt = MainApplication.dbHelper.countOfGoods();
			Log.d("my", "goodsCnt = " + goodsCnt);
			mobileGoodsCount = goodsCnt;
			h.post(new Runnable() {
				@Override
				public void run() {
					mobileGoodsCnt.setText(""+ mobileGoodsCount);

					LoadGoodsServerCount loadGoodsCount = new LoadGoodsServerCount();
					loadGoodsCount.start();;
				}
			});

		}
	}


	class LoadGoodsServerCount extends Thread {
		@Override
		public void run() {

				RequestGoodsCount requestGoodsCount = new RequestGoodsCount();
				int goodsCnt = requestGoodsCount.getGoodsCount();
				Log.d("my", "goodsCnt = " + goodsCnt);
				serverGoodsCount = goodsCnt;
				h.post(new Runnable() {
					@Override
					public void run() {
						serverGoodsCnt.setText(""+ serverGoodsCount);
					}
				});

		}
	}

	int offset=0;
	int limit=100;

	int currentLoadedGoodCount=0;

	boolean load = false;

	class LoadGoodsByBarcThread extends Thread {
		@Override
		public void run() {
			if(OfflineModeActivity.this!=null){
			RequestGoodsList requestServer = new RequestGoodsList();

			load = true;
				h.post(new Runnable() {
					@Override
					public void run() {
						importBt.setEnabled(false);

					}
				});


				currentLoadedGoodCount=0;
			offset=0;
			Log.d("my", "Start!");
			while (load) {

				ArrayList<Good> goodsList = requestServer.getGoodsList(-1,-1,offset,limit);

				h.post(new Runnable() {
					@Override
					public void run() {
						progressBar.setProgress(offset);

					}
				});

				if(goodsList.size()==0){
					Log.d("my", "end");
					load=false;
					h.post(new Runnable() {
						@Override
						public void run() {

						//	Toast.makeText(OfflineModeActivity.this, "End", Toast.LENGTH_LONG).show();
						//	updateProgress.setVisibility(View.GONE);

							LoadGoodsGRPThread loadGRP = new LoadGoodsGRPThread();
							loadGRP.start();

						}
					});


				}
				else{
					Log.d("my", "get " + goodsList.size());
					//Log.d("my","g= "+goodsList.get(0).getName());

					for(Good good:goodsList)
					{
						int gid = good.getId();
						int grp_id = good.getGrpId();
						String gname=good.getName();
						if(gname.contains("'")) gname=gname.replace("'", "''");
						String article=good.getArticle();
						//if(gname.contains("'")) gname=gname.replace("'", "''");
						String unit=good.getUnit();
						//if(gname.contains("'")) gname=gname.replace("'", "''");
						String barcode="";
							if(	good.getBarcodes()!=null)
								if(good.getBarcodes().size()>0)
									barcode=good.getBarcodes().get(0);
						double out_price = good.getOut_price();

						if(gname.contains("'")) gname=gname.replace("'", "''");

						MainApplication.dbHelper.writeGood(gid,grp_id, gname, article, unit , barcode ,out_price);
					}

					offset+=limit;

					currentLoadedGoodCount+=goodsList.size();

					h.post(new Runnable() {
						@Override
						public void run() {
							mobileGoodsCnt.setText(""+currentLoadedGoodCount);
						}
					});
				}
			}


		}
	}}



	class LoadGoodsGRPThread extends Thread
	{

		@Override
		public void run() {

			RequestGoodsGRP requestServer = new RequestGoodsGRP();
			ArrayList<GoodGRP> goodsList = requestServer.getGoodsGRPList(-1);
			Log.d("my", "thread: goods GRP list sz = "+goodsList.size());
			for(GoodGRP goodGRP:goodsList){

				MainApplication.dbHelper.writeGoodGRP(goodGRP.getId(), goodGRP.getParentGrpId(), goodGRP.getName());
			}
			readyGoodsGRPHandler.post(new Runnable() {
				@Override
				public void run() {

					importBt.setEnabled(true);

					Toast.makeText(OfflineModeActivity.this, "End", Toast.LENGTH_LONG).show();
						updateProgress.setVisibility(View.GONE);
				}
			});

		}

	}

	Handler readyGoodsGRPHandler=new Handler();



	class EmptyTable extends Thread {
		@Override
		public void run() {


			MainApplication.dbHelper.clearDic_Goods();
			MainApplication.dbHelper.clearDic_Goods_GRP();


			h.post(new Runnable() {
				@Override
				public void run() {
					mobileGoodsCnt.setText("" + mobileGoodsCount);

					LoadGoodsMobileCount loadGoodsMobileCount = new LoadGoodsMobileCount();
					loadGoodsMobileCount.start();
				}
			});

		}
	}





}
