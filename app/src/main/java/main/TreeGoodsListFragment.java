package main;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.barcodeclient3.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.HashSet;

import adapter.MyArrayAdapter;
import essences.Good;
import essences.Inventory;
import requests.RequestGoodsList;
import scanworkingactivity.ScanWorkingActivity;
import startactivity.MainActivity;

/**
 * Список товаров в дереве категорий
 */
public class TreeGoodsListFragment extends ListFragment {
	
	public TreeGoodsListFragment() {

		//data.add("222");
		//data.add("777");
	//	 adapter = new ArrayAdapter<String>(cont,
	//		        android.R.layout.simple_list_item_1, data);
	}

	public void setInvId(int id)
	{
		invId = id;
	}

 // String data[] = new String[] { "one", "two", "three", "four","cip" };
	ArrayList<String> data = new ArrayList<>();
	ArrayAdapter<String> adapterDoc;;
	static MyArrayAdapter<Good> adapter;
	
	 ViewGroup container;
	 ArrayList<Inventory> docList = new ArrayList<Inventory>();

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
     adapter = new MyArrayAdapter<Good>(getActivity(),
        R.layout.adapter_good_row, goodList);
    setListAdapter(adapter);
    
  }
  
  TreeGrpAndGoodsActivity treeGoodsActivity;
  
  
  View myRootView;
  
  RelativeLayout relFind;
  EditText findEditText;
  ImageButton clearfindButton;
  ImageButton docListBt;
  ImageButton keyBoardBt;
  ImageButton cameraButton;
  
  
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
	  myRootView = inflater.inflate(R.layout.my_list_fragment_view, null);
	  
	  relFind = (RelativeLayout) myRootView.findViewById(R.id.rel1);
	  if(relFind!=null) relFind.setVisibility(View.GONE);
	  
	  findEditText =(EditText) myRootView.findViewById(R.id.findEditText1);
	 
	  
	  findEditText.setFocusable(true);
	  
	  clearfindButton = (ImageButton) myRootView.findViewById(R.id.clearfindButton1);
	  clearfindButton.setOnClickListener(findButtonClickListener);
	  clearfindButton.setFocusable(false);
	  
	  keyBoardBt = (ImageButton) myRootView.findViewById(R.id.keyBoardButton1);
	  keyBoardBt.setOnClickListener(new View.OnClickListener() {
		public void onClick(View v) {
			InputMethodManager inputMethodManager =(InputMethodManager)
					getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
			inputMethodManager.showInputMethodPicker();
		}
	});
	  
	  cameraButton = (ImageButton) myRootView.findViewById(R.id.cameraButton1);
	  cameraButton.setOnClickListener(new View.OnClickListener() {
		  public void onClick(View v) {
			 // IntentIntegrator scanIntegrator = new IntentIntegrator(TreeGoodsListFragment.this);
			 // scanIntegrator.initiateScan();
		  }
	  });
	  
	  
	  docListBt = (ImageButton) myRootView.findViewById(R.id.docListButton1);
	  docListBt.setFocusable(false);
	  docListBt.setVisibility(View.GONE);
	//  docListBt.setOnClickListener(docListBtListener);
	  
	  
	  findEditText.addTextChangedListener(tw);
	  
	  findEditText.setOnKeyListener(findListener);
/*
	  findEditText.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (enter == 2) {
					enter = 0;
				} else {
					
				}

			}
		});
*/

	 // Inventory inv = docList.get(position);
	  //setListAdapter(null);
	  adapter = new MyArrayAdapter<Good>(getActivity(),
			  R.layout.adapter_good_row, goodList);
	  setListAdapter(adapter);
	  adapter.notifyDataSetChanged();

	  Log.d("my", "lalalala");
	  invId = ScanWorkingActivity.id;
	  if(relFind!=null) relFind.setVisibility(View.VISIBLE);
	  TreeGrpAndGoodsActivity.setDrawerEnabled(true);
	  TreeGrpAndGoodsActivity.openDriver();
	  
	  
    return  myRootView;
  }


  
int enter=0;
boolean newScan=true;
  
  
 public static int INV=0;
 public static int BILL=1;
  public void setDocList(ArrayList<Inventory> docList2, int type) {
	  docList.clear();
	  data.clear();
	 // docList.addAll(docList2); !!!!!!!//////////////////////////////////////
	  for(Inventory inventory:docList)
		{
			String str = inventory.getDatetime()+" "+inventory.getSubdivision()+" �"+inventory.getNum();
			data.add(str);
		}
		
		adapterDoc = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, data);
		
		
		setListAdapter(adapterDoc);
		
		
  }
  
  
  public static HashSet<Integer> changedGoodsIdCollections = new HashSet<>();

 

  int grpId=0;
  public static int invId=-1;
  
  String barcode="";
  String article="";
  String name="";
  
  public void setGoodsByBarc(String barcode, String article,String name) {
	  
	  findEditText.setText("");
		readyGoodsHandler = new ReadyGoodsHandler();
		
		this.barcode=barcode;
		this.article=article;
		this.name=name;
		
		showProgressBar(true,getString(R.string.loading_goods)+ " by BARCODE...");
		LoadGoodsByBarcThread loadGoodsThread = new LoadGoodsByBarcThread();
		loadGoodsThread.start();
	}
  
  public void setGoodsByGrpId(int grpId) {
	 // isEnabledFindButton = true;
	  findEditText.setText("");
		Log.d("my", "GRPID====" + grpId);
		this.grpId = grpId;

		readyGoodsHandler = new ReadyGoodsHandler();
		Log.d("my", "3");
		showProgressBar(true, getString(R.string.loading_goods)); //Сюда добавить офлайн подгрузку товаров
		Log.d("my", "4");

		//setListAdapter(null);
	//	showProgressBar(true, "");
	//	setListShown(true);
		if(MainActivity.OFFLINE_MODE)
		{
			LoadGoodsOfflineThread load = new LoadGoodsOfflineThread();
			load.start();
		}else {
			LoadGoodsThread loadGoodsThread = new LoadGoodsThread();
			loadGoodsThread.start();
		}
	}

	public static ArrayList<Good> goodList = new ArrayList<>();
	
	public static void changeUpdateGoodId(int goodId, double cnt)
	{
		for(Good good:goodList)
		{
			if(good.getId()==goodId){ good.setFcnt(cnt);
			good.setChanged(true);
			Log.d("my","izmeneniaem kolvo");
			}
		}
		
		adapter.notifyDataSetChanged();
	}
	

	class LoadGoodsThread extends Thread {
		@Override
		public void run() {
			RequestGoodsList requestServer = new RequestGoodsList();
			ArrayList<Good> goodsList = requestServer.getGoodsList(grpId, invId);
			
			goodList.clear();
			goodList.addAll(goodsList);
			readyGoodsHandler.sendEmptyMessage(1);
		}
	}

	class LoadGoodsOfflineThread extends Thread {
		@Override
		public void run() {

			ArrayList<Good> goodsList = MainApplication.dbHelper.getGoodListByGrpId(grpId, invId);


			goodList.clear();
			goodList.addAll(goodsList);
			readyGoodsHandler.sendEmptyMessage(1);
		}
	}
	
	class LoadGoodsByBarcThread extends Thread {
		@Override
		public void run() {
			RequestGoodsList requestServer = new RequestGoodsList();
			ArrayList<Good> goodsList = requestServer.getGoodsListByBarcode(invId, barcode, article,name);
			
			goodList.clear();
			goodList.addAll(goodsList);
			readyGoodsHandler.sendEmptyMessage(1);
		}
	}

	ReadyGoodsHandler readyGoodsHandler = null;

	class ReadyGoodsHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {

			Log.d("my", "thread: goodslist sz = " + goodList.size());// buildList();
			Log.d("my", "build rows goods!!!");
			/*data.clear();
			for(Good good:goodList)
			{
				String name = good.getName();
				data.add(name);
			}*/
			
			if(adapter!=null){
				Log.d("my","adapter no null");
			

			    adapter.notifyDataSetChanged();
			    Log.d("my","222");
			    hideProgressBar();
			    setListAdapter(adapter);
			}
			else{
				Log.d("my","adapter is null");
				ListAdapter adapter = getListAdapter();
				if(adapter==null) Log.d("my","vse ravno null");
			}
		//	readyRowHandler = new ReadyRowHandler();

			////linearLayout.removeAllViews();
			//CreateRowThread createRowThread = new CreateRowThread();
			//createRowThread.start();
		}
	}
	
	
	

	ProgressDialog dialogGoToBookDetails;

	public void showProgressBar(boolean cancelable, String text) {

		dialogGoToBookDetails = new ProgressDialog(getActivity());
		dialogGoToBookDetails.setMessage(text);
		dialogGoToBookDetails.setIndeterminate(true);
		dialogGoToBookDetails.setCancelable(cancelable);
		dialogGoToBookDetails.show();
	}

	public void hideProgressBar() {
		if(dialogGoToBookDetails!=null) dialogGoToBookDetails.hide();
	}
	
	 /* (non-Javadoc)
     * @see android.app.ListFragment#onListItemClick(android.widget.ListView, android.view.View, int, long)
     */
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        
        
        Log.d("my", "[onListItemClick] Selected Position "+ position);
        
        if(v instanceof TextView){
        	Log.d("my","this is doc");
        	if(position<docList.size())
        	{
        		Inventory inv = docList.get(position);
        		//setListAdapter(null);
        		adapter = new MyArrayAdapter<Good>(getActivity(),
        		        R.layout.adapter_good_row, goodList);
        		    setListAdapter(adapter);
        		    adapter.notifyDataSetChanged();
        		
        		Log.d("my","lalalala");
        		invId = inv.getId();
        		if(relFind!=null) relFind.setVisibility(View.VISIBLE);
        		TreeGrpAndGoodsActivity.setDrawerEnabled(true);
        		TreeGrpAndGoodsActivity.openDriver();
        		
        	}
        }
        else{
        Object tag = v.getTag();
        if(tag instanceof Good){
        	Good good = (Good)tag;
        	currentClickGood=good;
        	currentClickView=v;
        	Log.d("my", good.getName());
        	showDialog();
        }
        }
     
    }
    
    int mStackLevel=0;
    int style=0;
    
    public static View currentClickView=null;
    public static Good currentClickGood=null;
    
    void showDialog() {
       // mStackLevel++;
    	

        // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        DialogFragment newFragment = MyDialogFragment.newInstance(mStackLevel);
        newFragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        
     
        newFragment.show(ft, "dialog");
    }
    
    
    ArrayList<Good> originGoodListSaver = new ArrayList<>();
    boolean isEnabledFindButton = true;
    
    OnKeyListener findListener = new View.OnKeyListener() {
		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			//if(!blockScanner){
			Log.d("my","onKey");
			
			if (keyCode == 66) {
				enter++;
				Log.d("my", "key=" + enter);
			}
			if (enter == 2) {
				
				clearfindButton.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.close_32));
				originGoodListSaver.clear();
				originGoodListSaver.addAll(goodList);
				enter=0;
				newScan=true;
				findEditText.setFocusable(true);
				//isEnabledFindButton=false;
				String barc = findEditText.getText().toString();
				//setGoodsByBarc(barc,barc);
			}

			//}
			return false;
		}

	};
	
	
	
	
	
	
	
	
	
    
    
    View.OnClickListener findButtonClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
			
			if(isEnabledFindButton==true){ 
			
			
			
			String filterText = findEditText.getText().toString().trim();
			
			Log.d("my","find text = "+filterText);
			Log.d("my","goodl size = = "+goodList.size());
			
			
			//originGoodListSaver.clear();
			//originGoodListSaver.addAll(goodList);
			
			
			
			ArrayList<Good> tempGood = new ArrayList<>();
			if(filterText.length()>=1)
			{
				inMemoryFindButton();
				
				enter=0;
				newScan=true;
				findEditText.setFocusable(true);
				//isEnabledFindButton=false;
				try {
					int a = Integer.parseInt(filterText);
					
					
					String barc = findEditText.getText().toString();
					setGoodsByBarc(barc,barc,"");
					
					
				} catch (Exception e) {
					///tempGood.addAll(findGood(filterText)); ��� ������ �� ������
					
					String name = findEditText.getText().toString().trim();
					Log.d("my","name="+name);
					
					setGoodsByBarc("","",name);
				}
			    
				
				goodList.clear();
				goodList.addAll(tempGood);
				
				
			}
			}
			else{
				outMemoryButton();
				
			}
			
			if(adapter!=null){
				Log.d("my","adapter no null");
			    adapter.notifyDataSetChanged();
			    Log.d("my","222");
			    hideProgressBar();
			    setListAdapter(adapter);
			}
			else{
				Log.d("my","adapter is null");
				ListAdapter adapter = getListAdapter();
				if(adapter==null) Log.d("my","vse ravno null");
			}
					
			isEnabledFindButton=!isEnabledFindButton;
		}
	};
	
	
	private void inMemoryFindButton()
	{
		clearfindButton.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.close_32));
		originGoodListSaver.clear();
		originGoodListSaver.addAll(goodList);
	}
	
	private void outMemoryButton()
	{
		clearfindButton.setImageDrawable(getActivity().
				getResources().getDrawable(R.drawable.find32b));
		findEditText.setText("");
		
		goodList.clear();
		goodList.addAll(originGoodListSaver);
	}
	
	
	
	private ArrayList<Good> findGood(String inname)
	{
		ArrayList<Good> tempGood = new ArrayList<>();
		for(Good good:goodList)
		{
			String name = good.getName();
			name=name.toLowerCase();
			if(name.contains(inname.toLowerCase())){
				tempGood.add(good);
				Log.d("my","ADDING good");
			}
		}
		return tempGood;
	}
	
	private ArrayList<Good> findArticle(String inname)
	{
		ArrayList<Good> tempGood = new ArrayList<>();
		for(Good good:goodList)
		{
			String name = good.getArticle();
			name=name.toLowerCase();
			if(name.contains(inname.toLowerCase())){
				tempGood.add(good);
				Log.d("my","ADDING good");
			}
		}
		return tempGood;
	}
	
	private void findBarcode()
	{
		
	}
	
	CharSequence sh="";
	
	private TextWatcher tw = new TextWatcher() {
	    public void afterTextChanged(Editable s){

	    }
	    public void  beforeTextChanged(CharSequence s, int start, int count, int after){
	      // you can check for enter key here
	    }
	    public void  onTextChanged (CharSequence s, int start, int before,int count) {
	    	//Log.d("my","----------  "+s);
	    } 
	};
	
	
	


public void setTreeGoodsActivity(TreeGrpAndGoodsActivity treeGoodsActivity) {
	this.treeGoodsActivity = treeGoodsActivity;
}
	
	 
public void onActivityResult(int requestCode, int resultCode, Intent intent) {
	
	//retrieve scan result
	IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
	
	
	if (scanningResult != null) {
		
		
		//we have a result
		String scanContent = scanningResult.getContents();
		String scanFormat = scanningResult.getFormatName();
		//formatTxt.setText("FORMAT: " + scanFormat);
		if(scanContent!=null){
		
		Log.d("my", scanContent); 
		findEditText.setText(scanContent);
		
		
		if(!scanContent.equals("")){
			clearfindButton.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.close_32));
			originGoodListSaver.clear();
			originGoodListSaver.addAll(goodList);
			enter=0;
			newScan=true;
			findEditText.setFocusable(true);
			//isEnabledFindButton=false;
			String barc = findEditText.getText().toString();
			setGoodsByBarc(barc,barc,"");
		
		}
		}
		}
	else{
	    Toast toast = Toast.makeText(getActivity().getApplicationContext(),
	        "No scan data received!", Toast.LENGTH_SHORT);
	    toast.show();
	}
	}
	 
	
}