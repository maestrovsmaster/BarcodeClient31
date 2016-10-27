package docklist;

import android.annotation.SuppressLint;
import android.app.ActionBar;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.app.barcodeclient3.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;

import essences.BillIn;
import essences.BillInDt;
import essences.Employee;
import essences.Good;
import essences.Inventory;
import essences.JorDtTable;
import essences.JorHeadTable;
import essences.Organization;
import essences.Subdivision;
import essences.Unit;
import main.Dom;
import main.MainApplication;
import requests.Request;
import requests.RequestInventoryList;
import startactivity.MainActivity;


public class DockDetailsActivity extends AppCompatActivity //ListActivity
{

    private LinkedList<String> mListItems;
    ListView listView;

    private ActionBar mainActionBar;

    TextView title;

    ImageView offlineIcon;


    static DoksDetailsAdapter<JorDtTable> adapter;

    ViewGroup container;


    LayoutInflater inflater;
    View myRootView;


    private String titletxt = "";

    int docType = 0;
    int hdId=0;
    String num="";
    String subdiv="";
    String date="";


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //	 <include layout="@layout/activity_docs_list" />

        setContentView(R.layout.main_activity_dock_details);
        Log.d("my", "hello dock details");


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        Intent iin = getIntent();

        Bundle bundle = iin.getExtras();

        try {
            docType =  bundle.getInt("docType");
            hdId =  bundle.getInt("hd_id");
            num = bundle.getString("num");
            subdiv = bundle.getString("subdiv");
            date=bundle.getString("date");

        } catch (Exception e) {
            Log.d("my", " no id =((( ");
        }





        title = (TextView) findViewById(R.id.title);
        View v = findViewById(R.id.closeBt);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        listView = (ListView) findViewById(R.id.list_view);

        switch (docType) {
            case 0:
                title.setText(getResources().getString(R.string.inventory));
                loadDocList();
                break;
            case 1:
                String str1 = getResources().getString(R.string.bill);
                String title0 = str1+" №"+num+" "+subdiv+" "+date;
                title.setText(title0);
                loadBillInList();
                break;
        }


    }


    ArrayList<JorDtTable> docList = new ArrayList<>();

    /////////////inventory

    public void loadDocList() {

       // LoadDocumentsThread loadDocumentsThread = new LoadDocumentsThread();
       // loadDocumentsThread.start();
    }





    public void loadDocListOffline() {
        LoadDocumentsThreadOffline loadDocumentsThreadOffline = new LoadDocumentsThreadOffline();
        loadDocumentsThreadOffline.start();
    }

    class LoadDocumentsThreadOffline extends Thread {

        @Override
        public void run() {
           /* ArrayList<Inventory> invList = MainActivity.dbHelper.getInventoryList();
            docList.clear();
            if (invList.size() > 0) docList.addAll(invList);
            Log.d("my", "!!!!thread: invlist sz = " + docList.size());
            h.post(readyLoadDocks);*/
        }
    }


    Handler h = new Handler();

    Runnable readyLoadDocks = new Runnable() {
        @Override
        public void run() {
            adapter = new DoksDetailsAdapter<JorDtTable>(DockDetailsActivity.this, R.layout.row_doc, docList);
            listView.setAdapter(adapter);
        }
    };


    private void writeDocksToSQLLite(ArrayList<JorHeadTable> invList) {
       // if(invList.size()>0) MainActivity.dbHelper.clear_INVENTORIES();

        for (JorHeadTable inv : invList) {
            Log.d("my", "insert to db num activity= " + inv.getNum());
            Subdivision subdiv = new Subdivision(0,0,inv.getSubdivision().getName());
            int docType =0;
            if(inv instanceof BillIn) docType=1;
            MainApplication.dbHelper.insertInventoryDoc(inv.getId(), inv.getNum(), inv.getDatetime(),subdiv.getName() , docType);
        }
    }


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
            String query = getResources().getString(R.string.query_jor_bill_in_dt);
            query+=(" "+hdId);
            JSONObject jsonObject = requestInventoryList.getJSON(query);
            Log.d("my", "json BillDt = " + jsonObject.toString());
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
                Log.d("my","bill in dt data = "+array.toString());

                docList.clear();

                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    //Log.d("my","obj = "+obj.toString());


                    int id = obj.getInt(Dom.ID);

                    Unit unit =null;
                    try{
                    String unitName= obj.getString(Dom.UNIT);

                    unit = new Unit(0,0,unitName);
                    }catch (Exception e){}

                    Employee employee=null;
                    try{
                        int employeeID = obj.getInt(Dom.EMPLOYEE_ID);
                        String employeeName = obj.getString(Dom.EMPLOYEE);
                        employee = new Employee(employeeID,0,employeeName);
                    }catch (Exception e){}

                    Good good = null;
                    try{
                       int goodId = obj.getInt(Dom.GOOD_ID);
                       String article = obj.getString(Dom.ARTICLE);
                       String goodName = obj.getString(Dom.NAME);

                        good = new Good(goodId,0,goodName,unit,article);

                    }catch (Exception e){}



                    double cnt = 0;

                    if(obj.get(Dom.CNT) instanceof  Integer)
                    {
                        int cntInt = (java.lang.Integer) obj.get(Dom.CNT);
                        cnt=cntInt;
                    }
                    if(obj.get(Dom.CNT) instanceof  Double)
                    {
                        cnt=(Double) obj.get(Dom.CNT);

                    }

                    /*if(obj.get(Dom.CNT) instanceof  Float)
                    {
                        float cntf = (java.lang.Float) obj.get(Dom.CNT);
                        cnt=cntf;
                    }*/


                    double price = 0; //(Double) obj.get(Dom.PRICE);

                    if(obj.get(Dom.PRICE) instanceof  Integer)
                    {
                        int priceInt = (java.lang.Integer) obj.get(Dom.PRICE);
                        price=priceInt;
                    }
                    if(obj.get(Dom.PRICE) instanceof  Double)
                    {
                        price=(Double) obj.get(Dom.PRICE);

                    }

                   /* if(obj.get(Dom.PRICE) instanceof  Float)
                    {
                        float pricef = (java.lang.Float) obj.get(Dom.PRICE);
                        price=pricef;
                    }*/





                    BillInDt billInDt = new BillInDt(id,0,hdId,good,cnt,price);

                    docList.add(billInDt);

                   // Log.d("my", " date = " + date + " num =" + num);
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
