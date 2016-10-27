package main;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.barcodeclient3.R;

import org.json.JSONException;
import org.json.JSONObject;

import essences.Good;
import requests.RequestInventoryDtEditCnt;
import startactivity.MainActivity;

public  class MyDialogFragment extends DialogFragment {
    int mNum;
    
    View clickView;
    Good good;
    

    /**
     * Create a new instance of MyDialogFragment, providing "num"
     * as an argument.
     */
    static MyDialogFragment newInstance(int num) {
        MyDialogFragment f = new MyDialogFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("num", num);
        f.setArguments(args);
        //f.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
     

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNum = getArguments().getInt("num");

        // Pick a style based on the num.
        int style = DialogFragment.STYLE_NORMAL, theme = 0;
        switch ((mNum-1)%6) {
            case 1: style = DialogFragment.STYLE_NO_TITLE; break;
            case 2: style = DialogFragment.STYLE_NO_FRAME; break;
            case 3: style = DialogFragment.STYLE_NO_INPUT; break;
            case 4: style = DialogFragment.STYLE_NORMAL; break;
            case 5: style = DialogFragment.STYLE_NORMAL; break;
            case 6: style = DialogFragment.STYLE_NO_TITLE; break;
            case 7: style = DialogFragment.STYLE_NO_FRAME; break;
            case 8: style = DialogFragment.STYLE_NORMAL; break;
        }
        switch ((mNum-1)%6) {
            case 4: theme = android.R.style.Theme_Holo; break;
            case 5: theme = android.R.style.Theme_Holo_Light_Dialog; break;
            case 6: theme = android.R.style.Theme_Holo_Light; break;
            case 7: theme = android.R.style.Theme_Holo_Light_Panel; break;
            case 8: theme = android.R.style.Theme_Holo_Light; break;
        }
        //setStyle(style, theme);
       setStyle(DialogFragment.STYLE_NO_TITLE, 0);
       
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.calculator_layout, container, false);
       // View tv = v.findViewById(R.id.text);
     //   ((TextView)tv).setText("Dialog #" + mNum + ": using style "
        //        + getNameForNum(mNum));

        // Watch for button clicks.
       /* Button button = (Button)v.findViewById(R.id.show);
        button.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // When button is clicked, call up to owning activity.
               // ((FragmentDialog)getActivity()).showDialog();
            }
        });*/

        parseCalculator(v);
        return v;
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
	
	TextView goodNameView;
	TextView goodArticleView;
	TextView goodPriceView;
	
	
	
	private void parseCalculator(View rootView)
	{
		 good = TreeGoodsListFragment.currentClickGood;
		
		display =  (EditText) rootView.findViewById(R.id.displayText);
		titleText =  (TextView) rootView.findViewById(R.id.titleText);
		
		goodNameView =  (TextView) rootView.findViewById(R.id.calcGoodName);
		goodArticleView =  (TextView) rootView.findViewById(R.id.calcArticulView);
		goodPriceView =  (TextView) rootView.findViewById(R.id.calcPriceView);
		

		if(good!=null){
			goodNameView.setText(good.getName());
			goodArticleView.setText(good.getArticle());
			goodPriceView.setText(""+good.getOut_price()+" "+getString(R.string.uah));
		}
		
		btCancel = (Button) rootView.findViewById(R.id.btCancel);
		btCancel.setOnClickListener(new OnClickListener() {public void onClick(View v) {
				//calculatorLayout.setVisibility(View.GONE);
			
			dismiss();
				}});
		
		btOk = (Button) rootView.findViewById(R.id.btOk);
		btOk.setOnClickListener(new OnClickListener() {public void onClick(View v) {
			
			View view = TreeGoodsListFragment.currentClickView;
			TextView bt = (TextView) view.findViewById(R.id.goodCntRow);
			
			
			//String unit = good.getUnit();
			
			//String sum = displayString+" "+unit;
			
			//bt.setText(sum);
			//calculatorLayout.setVisibility(View.GONE);
			
			
			int invId=-1;
			int goodId=-1;
			double cnt =-1;
			
			if(good!=null)
			{
				invId= TreeGoodsListFragment.invId;
				goodId=good.getId();
				try {
				cnt = Double.parseDouble(displayString);	
				} catch (Exception e) {}
				if(invId>=0&&goodId>=0&&cnt>=0)
				{
					tempView = bt;
					SendInvDt sendInvDt = new SendInvDt( invId, goodId, cnt);
					sendInvDt.start();
				}
				
			}
			dismiss();
			
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
			tempGoodId=goodId;
		}

		@Override
		public void run() {
			if(MainActivity.OFFLINE_MODE)
			{
				MainApplication.dbHelper.insertGoodsAcCnt(invId,goodId,cnt);
				h.post(new Runnable() {
					@Override
					public void run() {
						TreeGoodsListFragment.changeUpdateGoodId(tempGoodId, tempcnt);
						TreeGoodsListFragment.changedGoodsIdCollections.add(tempGoodId);
					}
				});


			}else {
				RequestInventoryDtEditCnt requestInventoryDtEditCnt = new RequestInventoryDtEditCnt(MyDialogFragment.this.getActivity());
				ansJSON = requestInventoryDtEditCnt.sendEditInventoryDt(invId, goodId, cnt);
				h.post(readySendInvId);
			}
		}
	};

	Handler h = new Handler();

	View tempView;
	JSONObject ansJSON=null;
	double tempcnt=0;
	int tempGoodId=0;


	Runnable readySendInvId = new Runnable() {

		public void run() {
		
		if(ansJSON!=null)
		{
			String status="err";
			try {
				status = ansJSON.getString("status");
				if(status.equals("ok"))
				{
					Log.d("my","inv status inv dt ok!");
					Object objcnt = null;
					try {
						objcnt = ansJSON.get("cnt");
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					double dcnt = -1;
					if(objcnt instanceof Double) dcnt = (double) objcnt;
					if(objcnt instanceof Integer) {
						int icnt = (int) objcnt;
						dcnt=icnt;
					}
					
					View rel = TreeGoodsListFragment.currentClickView;
					TextView tw = (TextView) rel.findViewById(R.id.goodCntRow);
					
					if(tw!=null)
					{
						Log.d("my","v=ok"+tempView.getId());
						
						String strCnt = "";
						if (tempcnt == (int) tempcnt)
							strCnt = String.format("%d", (int) dcnt);
						else
							strCnt = String.format("%s", dcnt);
						
						String str = strCnt+" "+good.getUnit();
						
						//tw.setText(str);
						//tw.setTextColor(getResources().getColor(R.color.green));
						
						TreeGoodsListFragment.changeUpdateGoodId(tempGoodId, tempcnt);
						
						TreeGoodsListFragment.changedGoodsIdCollections.add(tempGoodId);
					}
					
					
				}
				else
				{
					Log.d("my", "status inv not ok =(");
					try {
						
				String details = 	ansJSON.getString("details");
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
			
			


    
}}};}