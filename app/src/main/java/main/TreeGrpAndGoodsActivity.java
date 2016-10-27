package main;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.app.barcodeclient3.R;

import java.util.ArrayList;

import adapter.GoodsGrpListAdapter;
import essences.GoodGRP;
import requests.RequestGoodsGRP;
import startactivity.MainActivity;

public class TreeGrpAndGoodsActivity extends Activity{
	
	private ActionBarDrawerToggle mDrawerToggle;
	
	private static DrawerLayout mDrawerLayout;
	private static ListView mDrawerList;
	
	private static ArrayList<GoodGRP> grpList;
	private static GoodsGrpListAdapter adapter;
	public static ActionBar mainActionBar;
	private CharSequence mTitle;
	private CharSequence mDrawerTitle;
	//GoodsListFragment2 goodsListFragment;
	
	public static final int BILL_IN=0;
	public static final int INVENTORY_ACT=1;
	private int docType=1;
	private int dockId=0;
	public static final String DOC_TYPE = "docType";
	
	
	TreeGoodsListFragment goodsListFragment;


	 @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	      // requestWindowFeature(Window.FEATURE_NO_TITLE);
	        Intent iin= getIntent();
			Bundle b = iin.getExtras();
			 int doc =-1;

		        if(b!=null)
		        {
		        	doc =(int) b.get(DOC_TYPE);

		        }
	             Log.d("my", "doc type= " + doc);
			
	    
	        if(doc!=-1) docType=1;



	        setContentView(R.layout.goods_main_activity);
	        
	        mTitle = mDrawerTitle = getTitle();

		 mainActionBar = getActionBar(); //main bar
		 mainActionBar.setDisplayShowCustomEnabled(true);
		 mainActionBar.setDisplayShowHomeEnabled(false);
		 // mainActionBar.hide();
		 //  mainActionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor( R.color.indigo_700)));

		 mainActionBar.setCustomView(R.layout.abar_tree_goods);

		 ImageButton closeBt = (ImageButton) mainActionBar.getCustomView().findViewById(R.id.closeButton);
		 TextView abarTitle = (TextView) mainActionBar.getCustomView().findViewById(R.id.abarTitle);
		 ImageView listIcon = (ImageView) mainActionBar.getCustomView().findViewById(R.id.listIcon);

		 closeBt.setOnClickListener(new OnClickListener() {
			 @Override
			 public void onClick(View view) {
				 finish();
			 }
		 });

		 listIcon.setOnClickListener(new OnClickListener() {
			 @Override
			 public void onClick(View view) {
				 if(mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) closeDriver();
				 else openDriver();
			 }
		 });


		 abarTitle.setText("Переучет");


				 //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	       
	        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
			mDrawerList = (ListView) findViewById(R.id.list_slidermenu);
	        
			grpList = new ArrayList<GoodGRP>();
		//	grpList.add(new GoodGRPDrawerItem("/"));
			adapter = new GoodsGrpListAdapter(getApplicationContext(),
					currentGrpList, groupClickListener);

			mDrawerList.setAdapter(adapter);///////////!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
			
			setDrawerEnabled(false);
			
			
			ActionBar ab = null;
			try {
				ab = getActionBar();
			} catch (Exception e) {
				Log.d("my", " a bar error = " + e.toString());
			}

			try {
				ab.setDisplayHomeAsUpEnabled(true);
			} catch (Exception e) {
				Log.d("my", " a bar error = " + e.toString());
			}

			getActionBar().setDisplayHomeAsUpEnabled(true);
			getActionBar().setHomeButtonEnabled(true);
			
			mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
					R.drawable.ic_menu_white_36dp, // nav menu toggle icon
					R.string.app_name, // nav drawer open - description for accessibility
					R.string.app_name // nav drawer close - description for accessibility
			) {
				public void onDrawerClosed(View view) {
					invalidateOptionsMenu();
				}
				public void onDrawerOpened(View drawerView) {}
			};
			mDrawerLayout.setDrawerListener(mDrawerToggle);
			
			
			 goodsListFragment = new TreeGoodsListFragment();
			 goodsListFragment.setTreeGoodsActivity(this);
			
			//goodsListFragment = new GoodsListFragment2();
			FragmentManager fragmentManager = getFragmentManager();
			FragmentTransaction ft = fragmentManager.beginTransaction();
			ft.add(R.id.frame_container, goodsListFragment).commit();
			
			



			 if(MainActivity.OFFLINE_MODE)
			 {
				 Log.d("my",""+getClass().toString()+" offline ...");
				// ArrayList<GoodGRP> grpList = new ArrayList<>();
				 grpList = MainApplication.dbHelper.getGoodsGRPList();
				 if(grpList!=null)
					 if(grpList.size()>0)
					 {

						 buildList();
					 }
			 }
			 else{
				 Log.d("my", "" + getClass().toString() + " online ...");


				// showProgressBar(false, getString(R.string.loading_goods) );

				 LoadGoodsGRPThread loadGoodsThread = new LoadGoodsGRPThread();
				 loadGoodsThread.start();
			 }

	    }


	 
	 public  static void setDrawerEnabled(Boolean enabled)
	 {
		 if(enabled){
		   mDrawerList.setEnabled(true);
		   mDrawerList.setVisibility(View.VISIBLE);
		 }
		 else{
			mDrawerList.setEnabled(false);
			mDrawerList.setVisibility(View.GONE);
		 }
	 }
	 
	 
	 public static void openDriver()
	 {
		 new Handler().postDelayed(openDrawerRunnable(), 100);
	 }
	 
	 private static Runnable openDrawerRunnable() {
		    return new Runnable() {

		        
		        public void run() {
		            mDrawerLayout.openDrawer(Gravity.RIGHT);
		        }
		    };
		};

	public static void closeDriver()
	{
		new Handler().postDelayed(closeDrawerRunnable(), 100);
	}

	private static Runnable closeDrawerRunnable() {
		return new Runnable() {


			public void run() {
				mDrawerLayout.closeDrawer(Gravity.RIGHT);
			}
		};
	};





	 
	 OnClickListener groupClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Log.d("my", "my id ");
			GoodGRP grp = (GoodGRP)v.getTag();
			int id = grp.getId();


			for(GoodGRP goodGRP:currentGrpList)
			{
				goodGRP.setCurrent(false);
			}
			grp.setCurrent(true);
			
			if(grp.isParent())
			{
				Log.d("my","CURRRENT");

				int parentId =  grp.getParentGrpId();
				currentRootId=parentId;
				buildList();
				
			}else{
			Log.d("my","my id = "+id);
			currentRootId = id;
			 buildList();
			}
		}
	};
	 
	 
	 class LoadGoodsGRPThread extends Thread
	    {
	    	
	    	@Override
	    	public void run() {
	    		
	    		 RequestGoodsGRP requestServer = new RequestGoodsGRP();
	    		grpList = requestServer.getGoodsGRPList(-1);
	    		Log.d("my","thread: goodsGRPlist sz = "+grpList.size());

				MainApplication.dbHelper.clearDic_Goods_GRP();
				for(GoodGRP goodGRP:grpList)
					MainApplication.dbHelper.writeGoodGRP(goodGRP.getId(),goodGRP.getParentGrpId(),goodGRP.getName());


	    		readyGoodsGRPHandler.post(new Runnable() {
					@Override
					public void run() {
						buildList();
					}
				});
	    		
	    	}
	    	
	    }
	    
	    Handler readyGoodsGRPHandler=new Handler();



	ArrayList<GoodGRP> currentGrpList = new ArrayList<>();

	private int rootId=0;
	private int currentRootId=rootId;
	 
	 /**
	  *
	  */
	 private void buildList()
	 {

		 Log.d("my", "build list");
		 ArrayList<GoodGRP> grpl = getListByParentId(currentRootId);
		 if(grpl.size()>1) {
			 currentGrpList.clear();

			 currentGrpList.addAll(grpl);
		 }
		else{
			 closeDriver();
			 goodsListFragment.setGoodsByGrpId(currentRootId);
		 }

		 adapter.notifyDataSetChanged();

	 }




	 private ArrayList<GoodGRP> getListByParentId(int parentId)
	 {
		 ArrayList<GoodGRP> goodGRPlist = new ArrayList<>();


		 if(parentId==0&&currentRootId==0){ goodGRPlist.add(new GoodGRP(-1,-1,"home"));}
		 else{
			 GoodGRP grp = findGroupById(parentId);
			 if(grp!=null) {
				 grp.setParent(true);
				 goodGRPlist.add(grp);
			 }
		 }


		 goodGRPlist.addAll(findGroupByParentId(parentId));


		return goodGRPlist;
	 }

	private GoodGRP findGroupById(int id)
	{
		for(GoodGRP grp: grpList)
		{
			if(grp.getId()==id) return  grp;
		}
		return null;
	}

	private ArrayList<GoodGRP> findGroupByParentId(int id)
	{
		Log.d("my","find by parent id = "+id);
		ArrayList<GoodGRP> grpLst = new ArrayList<>();

		for(GoodGRP grp: grpList)
		{
			if(grp.getParentGrpId()==id){

				if(grp.getId()==0&&grp.getParentGrpId()==0){}
				else {
					grp.setParent(false);
					grpLst.add(grp);}
			}
		}

		Log.d("my","finded= "+grpLst.size());
		return grpLst;
	}


	 
	 
	 ProgressDialog dialogGoToBookDetails;
		
		public void showProgressBar(boolean cancelable,String text )
		{
			
			dialogGoToBookDetails = new ProgressDialog(TreeGrpAndGoodsActivity.this);
			dialogGoToBookDetails.setMessage(text);
			dialogGoToBookDetails.setIndeterminate(true);
			dialogGoToBookDetails.setCancelable(cancelable);
			dialogGoToBookDetails.show();
		}
		
		public  void hideProgressBar()
		{
			if(dialogGoToBookDetails!=null) dialogGoToBookDetails.hide();
		}
	 
	 
	 
	 
	 
	 
	/* @Override
		public boolean onOptionsItemSelected(MenuItem item) {

			// toggle nav drawer on selecting action bar app icon/title
			if (mDrawerToggle.onOptionsItemSelected(item)) {
				return true;
			}
			// Handle action bar actions click
			switch (item.getItemId()) {
			case R.id.action_settings:

				return true;
			default:
				return super.onOptionsItemSelected(item);

			}
		}*/

	 @Override
		protected void onPostCreate(Bundle savedInstanceState) {
		 super.onPostCreate(savedInstanceState);
			// Sync the toggle state after onRestoreInstanceState has occurred.
			mDrawerToggle.syncState();
		}

	 @Override
		public void onConfigurationChanged(Configuration newConfig) {
			super.onConfigurationChanged(newConfig);
			// Pass any configuration change to the drawer toggls
			mDrawerToggle.onConfigurationChanged(newConfig);
			super.onConfigurationChanged(newConfig);
			 /*  if(newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO) {

			     ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE))
			                                   .showInputMethodPicker();
			     Toast.makeText(this, "Barcode Scanner detected. Please turn OFF Hardware/Physical keyboard to enable softkeyboard to function.",
			    		 Toast.LENGTH_LONG).show();
			   }*/
		}

	

}
