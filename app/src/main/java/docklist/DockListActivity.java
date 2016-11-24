package docklist;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.barcodeclient3.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;

import essences.BillIn;
import essences.Employee;
import essences.Inventory;
import essences.JorHeadTable;
import essences.Organization;
import essences.Subdivision;
import main.Dom;
import main.MainApplication;
import requests.Request;
import requests.RequestInventoryList;
import startactivity.MainActivity;


public class DockListActivity extends AppCompatActivity //ListActivity
{

    private LinkedList<String> mListItems;
    ListView listView;

    private ActionBar mainActionBar;

    TextView title;

    ImageView offlineIcon;

    ArrayList<String> data = new ArrayList<>();
    ArrayAdapter<String> adapterDoc;
    static DoksListAdapter<JorHeadTable> adapter;

    ViewGroup container;


    LayoutInflater inflater;
    View myRootView;


    private String titletxt = "";

    int docType = 0;

    Dialog dialog;
    EditText idEditText;
    EditText nameEditText;

    String createInvId="";
    int crInvId=999000;
    String createInvName="";
    String currentDateTimeString="";
    Handler createInvH=new Handler();


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //	 <include layout="@layout/activity_docs_list" />

        setContentView(R.layout.main_activity_docs_list);
        Log.d("my", "hello dock list");



        // custom dialog
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.new_inventory_dialog);
        dialog.setTitle(getString(R.string.create_inventory));

        // set the custom dialog components - text, image and button
        idEditText = (EditText) dialog.findViewById(R.id.idEditText);
        nameEditText=(EditText) dialog.findViewById(R.id.nameEditText);


        Button cancelButton = (Button) dialog.findViewById(R.id.cancelBt);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        Button createButton = (Button) dialog.findViewById(R.id.createBt);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //create inventory...
                //dialog.dismiss();

                createInvId=idEditText.getText().toString();
                createInvName=nameEditText.getText().toString();

                createInvName+=" - offline document";
                //currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                DateFormat dateFormatter = new SimpleDateFormat("dd/mm/yyyy hh:mm:ss");
                dateFormatter.setLenient(false);
                java.util.Date today = new java.util.Date();
                currentDateTimeString = dateFormatter.format(today);




                try{
                    crInvId=Integer.parseInt(createInvId);
                }catch (Exception e){}


                if(createInvId.length()>0)
                new Thread( ){
                    @Override
                    public void run() {
                        MainApplication.dbHelper.insertInventoryDoc(crInvId, createInvId, currentDateTimeString,createInvName , docType);
                        createInvH.post(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();
                                loadDocListOffline();
                            }
                        });
                    }
                }.start();
            }
        });





        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //  Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
              //          .setAction("Action", null).show();
                dialog.show();
            }
        });


        Intent iin = getIntent();

        Bundle bundle = iin.getExtras();

        try {
            docType = (Integer) bundle.getInt("type");


        } catch (Exception e) {
            Log.d("my", " no dockType =((( ");
            docType=0; ///!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        }


        Log.d("my", "my shelf 2  onCreateView ");


        title = (TextView) findViewById(R.id.title);
        View v = findViewById(R.id.closeBt);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        listView = (ListView) findViewById(R.id.list_view);
        // Set a listener to be invoked when the list should be refreshed.
        // listView.setOnRefreshListener

        //  mListItems = new LinkedList<String>();
        //  mListItems.addAll(Arrays.asList(mStrings));


        countDocksDB = (TextView) findViewById(R.id.countDocksDB);

        countDocksDB.setText("OfflineMode = "+MainActivity.OFFLINE_MODE);

        switch (docType) {
            case 0:
                Log.d("my","Переучет");
                title.setText(getResources().getString(R.string.inventory));
                if(MainActivity.OFFLINE_MODE) loadDocListOffline();
                 else   loadDocList();
                break;
            case 1:
                title.setText(getResources().getString(R.string.bill_in));
                loadBillInList();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //docList.clear();
       /* switch (docType) {
            case 0:
                Log.d("my","Переучет");
                title.setText(getResources().getString(R.string.inventory));
                if(MainActivity.OFFLINE_MODE) loadDocListOffline();
                else   loadDocList();
                break;
            case 1:
                title.setText(getResources().getString(R.string.bill_in));
                loadBillInList();
                break;
        }*/

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //if (data == null) {return;}
        Log.d("my"," RESULT CODE = "+resultCode+" request code = "+requestCode);
        switch (docType) {
            case 0:
                Log.d("my","Переучет");
                title.setText(getResources().getString(R.string.inventory));
                if(MainActivity.OFFLINE_MODE) loadDocListOffline();
                else   loadDocList();
                break;
            case 1:
                title.setText(getResources().getString(R.string.bill_in));
                loadBillInList();
                break;
        }
    }



    TextView countDocksDB;

    ArrayList<JorHeadTable> docList = new ArrayList<>();

    /////////////inventory

    public void loadDocList() {

        LoadDocumentsThread loadDocumentsThread = new LoadDocumentsThread();
        loadDocumentsThread.start();
    }


    class LoadDocumentsThread extends Thread {

        @Override
        public void run() {


            RequestInventoryList requestInventoryList = new RequestInventoryList();

            if (!MainActivity.OFFLINE_MODE){
                Log.d("my"," CLEAR=====================");
                docList.clear();/////!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            }
            docList.addAll(requestInventoryList.getInventoryList());
            Log.d("my", "!!!!thread: invlist sz = " + docList.size());

            //for(Inventory inv:goodsList){
            //MainActivityOld.goodGRPList.put(goodGRP.getId(), goodGRP);
            //}
            if(docList.size()>0) {
                writeDocksToSQLLite(docList);

                h.post(readyLoadDocks);
            }
            else{
                hOffline.post(new Runnable() {
                    @Override
                    public void run() {
                        loadDocListOffline();
                    }
                });
            }
        }
    }

    Handler hOffline = new Handler();


    public void loadDocListOffline() {
        LoadDocumentsThreadOffline loadDocumentsThreadOffline = new LoadDocumentsThreadOffline();
        loadDocumentsThreadOffline.start();
    }

    class LoadDocumentsThreadOffline extends Thread {

        @Override
        public void run() {
            ArrayList<Inventory> invList = MainApplication.dbHelper.getInventoryList();
            docList.clear();
            if (invList.size() > 0) docList.addAll(invList);
            Log.d("my", "!!!!thread: invlist sz = " + docList.size());

            h.post(readyLoadDocks);
        }
    }


    Handler h = new Handler();

    Runnable readyLoadDocks = new Runnable() {
        @Override
        public void run() {
            countDocksDB.setText("OfflineMode = " + MainActivity.OFFLINE_MODE + "\n\r" +
                    "count of Docks in DB=" + docList.size());

            if(docList.size()==0){
                dialog.show();
            }

            adapter = new DoksListAdapter<JorHeadTable>(DockListActivity.this, R.layout.row_doc, docList);
            listView.setAdapter(adapter);
        }
    };
    String ans="";

    private void writeDocksToSQLLite(ArrayList<JorHeadTable> invList) {
        //if(invList.size()>0) MainActivity.dbHelper.clear_INVENTORIES();

        for (JorHeadTable inv : invList) {
            Log.d("my", "insert to db num activity= " + inv.getNum());
            Subdivision subdiv = new Subdivision(0,0,inv.getSubdivision().getName());
            int docType =0;
            if(inv instanceof BillIn) docType=1;
            ans = MainApplication.dbHelper.insertInventoryDoc(inv.getId(), inv.getNum(), inv.getDatetime(),subdiv.getName() , docType);

            h00.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(DockListActivity.this, ans,
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    Handler h00 = new Handler();


    ////////////////// bill in

    public void loadBillInList() {

        LoadBillInThread loadDocumentsThread = new LoadBillInThread();
        loadDocumentsThread.start();
    }


    class LoadBillInThread extends Thread {

        @Override
        public void run() {
            Log.d("my", "load bill in!!!");
            Request requestInventoryList = new Request();
            String query = getResources().getString(R.string.query_jor_bill_in);
            JSONObject jsonObject = requestInventoryList.getJSON(query);
            Log.d("my", "json Bill = " + jsonObject.toString());
            parseBillInJSon(jsonObject);

            //if(!MainActivity.OFFLINE_MODE) docList.clear();
            //docList.addAll( requestInventoryList.getInventoryList());
            Log.d("my", "!!!!thread: invlist sz = " + docList.size());

            //for(Inventory inv:goodsList){
            //MainActivityOld.goodGRPList.put(goodGRP.getId(), goodGRP);
            //}
           // writeDocksToSQLLite(docList);

            h.post(readyLoadDocks);
        }
    }

    private void parseBillInJSon(JSONObject jsonObject) {
        try {
            String result = jsonObject.getString("status");

            if (result.contains("success")) {
                Log.d("my", "success !!!");
                JSONArray array = jsonObject.getJSONArray("data");

                docList.clear();

                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    //Log.d("my","obj = "+obj.toString());

                    java.lang.String date = (java.lang.String) obj.get(Dom.DATE_TIME);
                    int id = obj.getInt(Dom.ID);
                    int grpId=obj.getInt(Dom.GRP_ID);
                    String num ="";
                    try{
                        num= obj.getString(Dom.NUM);
                    }catch (Exception e){}

                    String supplierNum="";
                    try{
                    supplierNum=obj.getString(Dom.SUPPLIER_NUM);
                    }catch (Exception e){}


                    Organization organization =null;
                    try{
                    String orgName= obj.getString(Dom.ORGANIZATION);
                    int orgId = obj.getInt(Dom.ORG_ID);
                    organization = new Organization(orgId,0,orgName);
                    }catch (Exception e){}

                    Employee employee=null;
                    try{
                        int employeeID = obj.getInt(Dom.EMPLOYEE_ID);
                        String employeeName = obj.getString(Dom.EMPLOYEE);
                        employee = new Employee(employeeID,0,employeeName);
                    }catch (Exception e){}


                    String subdivName = obj.getString(Dom.SUBDIVISION_NAME);
                    int subdivId = obj.getInt(Dom.SUBDIVISION_ID);
                    Subdivision subdivision = new Subdivision(subdivId,0,subdivName);
                    int isTaxIn = obj.getInt(Dom.IS_TAX_IN);
                    int docState = obj.getInt(Dom.DOC_STATE);
                    int isReturn = obj.getInt(Dom.IS_RETURN);

                    BillIn billIn = new BillIn(id,grpId,num, date,supplierNum,subdivision,docState,employee,organization,isTaxIn,isReturn);

                    docList.add(billIn);

                    Log.d("my", " date = " + date + " num =" + num);
                }

               // writeDocksToSQLLite(docList);

            }
            if (result.contains("error")) {
                Log.d("my", "error !!!");
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("my", "parse err = " + e.toString());
        }

    }


}
