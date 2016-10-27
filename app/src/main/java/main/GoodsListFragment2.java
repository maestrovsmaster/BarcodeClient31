package main;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnSystemUiVisibilityChangeListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.barcodeclient3.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import essences.Good;
import essences.Inventory;
import requests.RequestGoodsList;
import requests.RequestInventoryDtEditCnt;

public class GoodsListFragment2 extends ListFragment {

	private View rootView;

//	static LinearLayout linearLayout;
	LayoutInflater inflater;
	ViewGroup container;
	
	TextView label1;
	//TextView headDocumentLabel;
	
	ArrayAdapter<String> adapter;
	ArrayList<String> data = new ArrayList<>();

	public GoodsListFragment2() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d("my","9999999999");

		this.inflater = inflater;
		this.container = container;
	//	rootView = inflater.inflate(R.layout.goods_list_fragment, container, false);

		//linearLayout = (LinearLayout) rootView.findViewById(R.id.goodLinear);
		//label1 = (TextView) rootView.findViewById(R.id.titleTW);
		//headDocumentLabel = (TextView) rootView.findViewById(R.id.headLabel2);
		
		data.add("dfgdfgdf");
		data.add("gdf");
		data.add("df89gdf");
		data.add("ettfgdf");
		
		 adapter = new ArrayAdapter<String>(getActivity(),
		        android.R.layout.simple_list_item_1, data);
		    setListAdapter(adapter);
		
		
		parseCalculator();

		return super.onCreateView(inflater, container, savedInstanceState);
	}

	int grpId = -1;

	public void setGoodsByGrpId(int grpId) {
		Log.d("my", "GRPID====" + grpId);
		this.grpId = grpId;

		readyGoodsHandler = new ReadyGoodsHandler();

		showProgressBar(false, getString(R.string.loading_goods));

		LoadGoodsThread loadGoodsThread = new LoadGoodsThread();
		loadGoodsThread.start();
	}

	ArrayList<Good> goodList = new ArrayList<>();

	class LoadGoodsThread extends Thread {

		@Override
		public void run() {
			RequestGoodsList requestServer = new RequestGoodsList();
			ArrayList<Good> goodsList = requestServer.getGoodsList(grpId,-1);
			Log.d("my", "thread: goodslist sz = " + goodsList.size());
			goodList.clear();
			goodList.addAll(goodsList);
			readyGoodsHandler.sendEmptyMessage(1);

		}

	}

	ReadyGoodsHandler readyGoodsHandler = null;

	class ReadyGoodsHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {

			// buildList();
			Log.d("my", "build rows goods!!!");
			readyRowHandler = new ReadyRowHandler();

			/*linearLayout.removeAllViews();
			CreateRowThread createRowThread = new CreateRowThread();
			createRowThread.start();*/
		}
	}

	class CreateRowThread extends Thread {
		@Override
		public void run() {

			for (Good good : goodList) {
				//RelativeLayout rl = createRow(good);
				//TextView goodNameTW = (TextView) rl
				//		.findViewById(R.id.goodNameTW);
				//String name = good.getName();
				//goodNameTW.setText(name);

				Message msg = new Message();
			//	msg.obj = rl;
			//	msg.what = 0;
				readyRowHandler.sendMessage(msg);
			}
			readyRowHandler.sendEmptyMessage(1);
		}
	}

	ReadyRowHandler readyRowHandler;

	class ReadyRowHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {

			if (msg.what == 0) {
				RelativeLayout rl = (RelativeLayout) msg.obj;
				//linearLayout.addView(rl);
			}
			if (msg.what == 1) {
				//hideProgressBar();
			}
		}
	}
	
	
	

	int btW = 50;
	boolean rb = true;


	
	//public static final int TAG_ID=0;
	//public static final int TAG_CNT=1;
	//public static final int TAG_NAME=2;
	

	
	
	
	
	
	

	/**
	 * �������� ������ ��������� ��� ����������
	 */
	@SuppressLint("NewApi")
	public void createDocsList(ArrayList<Inventory> docList, int docType) {
	//	linearLayout.removeAllViews();
		//linearLayout.setPaddingRelative(10, 10, 10, 10);

		if(docType== TreeGrpAndGoodsActivity.BILL_IN){
			label1.setText("������ ������");
		}
		if(docType== TreeGrpAndGoodsActivity.INVENTORY_ACT){
			//label1.setText("�������� ������");
		}
		
		for (Inventory doc : docList) {
		//	linearLayout.addView(createDocRow(doc));
		}
	}

	@SuppressLint("NewApi")
	private RelativeLayout createDocRow(Inventory doc) {

		RelativeLayout rowRel = new RelativeLayout(getActivity());
		rowRel.setId(doc.getId());
		rowRel.setTag(doc);
		
		
		rowRel.setId(R.id.rowRel);
		rowRel.setPadding(15, 0, 15, 0);
		if (rb)
			rowRel.setBackgroundResource(R.drawable.transparent_nobord_button);
	//	rowRel.setBackgroundColor(getResources().getColor(R.color.gold));
		LayoutParams layout_845 = new LayoutParams(0, 0);
		layout_845.width = LayoutParams.MATCH_PARENT;
		layout_845.height = LayoutParams.WRAP_CONTENT;
		rowRel.setLayoutParams(layout_845);

		// /////////////////////////////// rowRel.addView(minusCntBt);
		String doctype = "";
		if(doc instanceof Inventory) doctype = "�������� ";
		

		TextView headNameTW = new TextView(getActivity());
		headNameTW.setId(R.id.goodNameTW);
	//	headNameTW.setBackgroundResource(R.drawable.transparent_nobord_button);
		headNameTW.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
		// goodNameTW.setPadding(int left, int top,
		// (15/getApplicationContext().getResources().getDisplayMetrics().density),
		// int bottom);
		
		
		String text = doctype+"  "+doc.getSubdivision();
		headNameTW.setText( text);
		headNameTW.setTypeface(null, Typeface.BOLD);

		headNameTW.setTextColor(getResources().getColor(R.color.dark_gray));
		LayoutParams layout_119 = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		layout_119.width = LayoutParams.MATCH_PARENT;
		layout_119.height = LayoutParams.WRAP_CONTENT;
		headNameTW.setLayoutParams(layout_119);
		//headNameTW.setPadding(10, 10, 10, 10);
		
		TextView detailNameTW = new TextView(getActivity());
		//detailNameTW.setId(R.id.goodNameTW);
		detailNameTW.setTypeface(null, Typeface.NORMAL);
		//detailNameTW.setBackgroundResource(R.drawable.transparent_nobord_button);
		detailNameTW.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
		detailNameTW.setLayoutParams(layout_119);
		// goodNameTW.setPadding(int left, int top,
		// (15/getApplicationContext().getResources().getDisplayMetrics().density),
		// int bottom);
		 text = "� "+doc.getNum()+" �� "+doc.getDatetime();
		 detailNameTW.setText( text);

		 detailNameTW.setTextColor(getResources().getColor(R.color.dark_gray));
		
		
		 View row = new View(getActivity());
		 row.setBackgroundColor(getResources().getColor(R.color.dark_gray));
		 LayoutParams rowlayout = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		 row.setLayoutParams(rowlayout);
		 
		 LinearLayout linear = new LinearLayout(getActivity());
		 linear.setOrientation(LinearLayout.VERTICAL);
		 linear.setPadding(0, 0, 0, 0);
		// linear.setBackgroundColor(getResources().getColor(R.color.orange_logout));
		 
		 linear.addView(headNameTW);
		 linear.addView(detailNameTW);

		RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		layoutParams1.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		
		RelativeLayout.LayoutParams layoutHeadParams = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, 80);
		layoutHeadParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		//RelativeLayout.ALIGN_PARENT_RIGHT
		// layoutInfoParams.addRule(Gravity.CENTER_VERTICAL);
		layoutHeadParams.setMargins(10, 0, 0, 0);
		
		
		
		RelativeLayout.LayoutParams layoutViewParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, 1);
		layoutViewParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		//layoutViewParams.setMarginStart(15);
		
		
		rowRel.addView(linear, layoutHeadParams);
		//rowRel.addView(detailNameTW, layoutDetailParams);
		
		rowRel.addView(row, layoutViewParams);
		
		rowRel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				/*Inventory doc = (Inventory) v.getTag();
				String text = doc.getDatetime()+" �"+doc.getNum()+"  "+doc.getSubdivision();
				headDocumentLabel.setText(text);
				headDocumentLabel.setTag(doc);
				linearLayout.removeAllViews();
				linearLayout.setTag(doc);*/
			}
		});

		return rowRel;

	}

	ProgressDialog dialogGoToBookDetails;

	public void showProgressBar(boolean cancelable, String text) {

		dialogGoToBookDetails = new ProgressDialog(rootView.getContext());
		dialogGoToBookDetails.setMessage(text);
		dialogGoToBookDetails.setIndeterminate(true);
		dialogGoToBookDetails.setCancelable(cancelable);
		dialogGoToBookDetails.show();
	}

	public void hideProgressBar() {
		dialogGoToBookDetails.hide();
	}
	
	String displayString = "";
    boolean isDot=false;
	RelativeLayout calculatorLayout;
	TextView titleText;
	EditText display;
	Button bt0;
	Button bt1;
	Button bt2;
	Button bt3;
	Button bt4;
	Button bt5;
	Button bt6;
	Button bt7;
	Button bt8;
	Button bt9;
	
	Button btDel;
	Button btDot;
	
	Button btOk;
	Button btCancel;
	
	private void parseCalculator()
	{
		calculatorLayout = (RelativeLayout) rootView.findViewById(R.id.calculatorLayout);
		calculatorLayout.setOnSystemUiVisibilityChangeListener(new OnSystemUiVisibilityChangeListener() {
			public void onSystemUiVisibilityChange(int visibility) {
				displayString="";
				display.setText(displayString);
			}
		});
		
		display =  (EditText) rootView.findViewById(R.id.displayText);
		titleText =  (TextView) rootView.findViewById(R.id.titleText);
		
		btCancel = (Button) rootView.findViewById(R.id.btCancel);
		btCancel.setOnClickListener(new OnClickListener() {public void onClick(View v) {
				calculatorLayout.setVisibility(View.GONE);}});
		
		btOk = (Button) rootView.findViewById(R.id.btOk);
		btOk.setOnClickListener(new OnClickListener() {public void onClick(View v) {
			
			Button bt = (Button) calculatorLayout.getTag();
			bt.setText(displayString);
			calculatorLayout.setVisibility(View.GONE);
			
			int invId=-1;
			int goodId=-1;
			double cnt =-1;
			
			Object tag =null;// linearLayout.getTag();
			if(tag!=null)
			{
				
				try {
					Inventory inventory = (Inventory) tag;
					 invId=inventory.getId();
				} catch (Exception e) {}
			}
			
			Object tag2 = calculatorLayout.getTag();
			if(tag2!=null)
			{
				try {
					Good good = (Good) bt.getTag();
					goodId=good.getId();
				} catch (Exception e) {}
			}
			
			try {
				cnt = Double.parseDouble(display.getText().toString());
			} catch (Exception e) {}
			
			if(invId>=0&&goodId>=0&&cnt>=0)
			{
				SendInvDt sendInvDt = new SendInvDt(invId, goodId, cnt);
				tempView =bt;
				sendInvDt.start();
			}
			
		}});
		
		
		bt1 = (Button) rootView.findViewById(R.id.bt1);
		bt1.setOnClickListener(new OnClickListener() {public void onClick(View v) {displayString+="1";display.setText(displayString);}});
		bt2 = (Button) rootView.findViewById(R.id.bt2);
		bt2.setOnClickListener(new OnClickListener() {public void onClick(View v) {displayString+="2";display.setText(displayString);}});
		bt3 = (Button) rootView.findViewById(R.id.bt3);
		bt3.setOnClickListener(new OnClickListener() {public void onClick(View v) {displayString+="3";display.setText(displayString);}});
		bt4 = (Button) rootView.findViewById(R.id.bt4);
		bt4.setOnClickListener(new OnClickListener() {public void onClick(View v) {displayString+="4";display.setText(displayString);}});
		bt5 = (Button) rootView.findViewById(R.id.bt5);
		bt5.setOnClickListener(new OnClickListener() {public void onClick(View v) {displayString+="5";display.setText(displayString);}});
		bt6 = (Button) rootView.findViewById(R.id.bt6);
		bt6.setOnClickListener(new OnClickListener() {public void onClick(View v) {displayString+="6";display.setText(displayString);}});
		bt7 = (Button) rootView.findViewById(R.id.bt7);
		bt7.setOnClickListener(new OnClickListener() {public void onClick(View v) {displayString+="7";display.setText(displayString);}});
		bt8 = (Button) rootView.findViewById(R.id.bt8);
		bt8.setOnClickListener(new OnClickListener() {public void onClick(View v) {displayString+="8";display.setText(displayString);}});
		bt9 = (Button) rootView.findViewById(R.id.bt9);
		bt9.setOnClickListener(new OnClickListener() {public void onClick(View v) {displayString+="9";display.setText(displayString);}});
		bt0 = (Button) rootView.findViewById(R.id.bt0);
		bt0.setOnClickListener(new OnClickListener() {public void onClick(View v) {
    		 displayString+="0";display.setText(displayString);}});
		
		btDot= (Button) rootView.findViewById(R.id.btDot);
		btDot.setOnClickListener(new OnClickListener() {public void onClick(View v) {			if(!isDot){
			if(!displayString.equals("")){
				isDot = true;
			displayString+=".";
			display.setText(displayString);
			}
			else{
				isDot = true;
				displayString+="0.";
				display.setText(displayString);
			}}}});
		
		btDel= (Button) rootView.findViewById(R.id.btDel);
		btDel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				isDot=false;
				displayString="";
				display.setText(displayString);
				Log.d("my","CE"+displayString+"=");
			}
		});
		
	}

	class SendInvDt extends Thread
	{

		int invId;
		int goodId;
		double cnt;

		public SendInvDt(int invId, int goodId, double cnt) {
			this.invId = invId;
			this.goodId = goodId;
			this.cnt = cnt;

			tempcnt=cnt;
		}

		@Override
		public void run() {
			RequestInventoryDtEditCnt requestInventoryDtEditCnt = new RequestInventoryDtEditCnt(GoodsListFragment2.this.getActivity());
			 ansJSON = requestInventoryDtEditCnt.sendEditInventoryDt(invId, goodId, cnt);
			h.post(readySendInvId);
		}
	};

	Handler h = new Handler();

	View tempView;
	JSONObject ansJSON=null;
	double tempcnt=0;

	Runnable readySendInvId = new Runnable() {

		public void run() {


			if (ansJSON != null) {
				String status = "err";
				try {
					status = ansJSON.getString("status");
					if (status.equals("ok")) {
						Object objcnt = null;
						try {
							objcnt = ansJSON.get("cnt");
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						double dcnt = -1;
						if (objcnt instanceof Double) dcnt = (double) objcnt;
						if (objcnt instanceof Integer) {
							int icnt = (int) objcnt;
							dcnt = icnt;
						}
						if (tempView instanceof Button) {
							Button bt = (Button) tempView;
							String strCnt = "";
							if (tempcnt == (int) tempcnt)
								strCnt = String.format("%d", (int) dcnt);
							else
								strCnt = String.format("%s", dcnt);
							bt.setText(strCnt);
							bt.setTextColor(getResources().getColor(R.color.green));
						}
					} else {
						try {

							String details = ansJSON.getString("details");
							new AlertDialog.Builder(getActivity())
									.setTitle("Error")
									.setMessage(details)
									.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog, int which) {
											// continue with delete
										}
									})
									.setIcon(android.R.drawable.ic_dialog_alert)
									.show();
						} catch (Exception e) {
							// TODO: handle exception
						}
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}


			}
		}
	};

}
