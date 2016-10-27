package scanworkingactivity;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.barcodeclient3.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;


import essences.DicTable;
import essences.Good;
import main.MainApplication;
import startactivity.MainActivity;

/**
 * Created by userd088 on 13.07.2016.
 */
public class TotalGoodsListActivity     extends ListActivity {


    int id=-1;

        ImageButton backButton = null;

    private TotalGoodsListAdapter adapter;
    private GestureDetector mGestureDetector;
    private List<Object[]> alphabet = new ArrayList<Object[]>();
    private HashMap<String, Integer> sections = new HashMap<String, Integer>();
    private int sideIndexHeight;
    private static float sideIndexX;
    private static float sideIndexY;
    private int indexListSize;
    private ViewGroup container;
    private LayoutInflater inflater;
    View rootView;
    //private UniversalTableList booksShTableListDynamic;
    ArrayList<String> namesList = new ArrayList<>();


    class SideIndexGestureListener extends
            GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            sideIndexX = sideIndexX - distanceX;
            sideIndexY = sideIndexY - distanceY;

            if (sideIndexX >= 0 && sideIndexY >= 0) {
               // displayListItem();  //@@@@@@@@@@@@!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            }

            return super.onScroll(e1, e2, distanceX, distanceY);
        }
    }

    HashMap<String, Good> goodsMap = new HashMap<>();
    RelativeLayout relativeL;
    ProgressBar loadProgress;



    @Override
        protected void onCreate(Bundle savedInstanceState) {

        Log.d("my", "settings1");
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);


        setContentView(R.layout.activity_total_goods_list);
        Log.d("my", "settings12");


        Intent iin = getIntent();

        Bundle bundle =  iin.getExtras();

        try {
            //docType =  bundle.getInt("docType");
            id = bundle.getInt("id");

            Log.d("my", " ==== id = "+id);
            //date =  bundle.getString("date");
           // subdiv =  bundle.getString("subdiv");
        } catch (Exception e) {
            Log.d("my", " no id =((( ");
        }
        adapter = new TotalGoodsListAdapter(id, getApplicationContext());

        backButton =(ImageButton) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });




        mGestureDetector = new GestureDetector(TotalGoodsListActivity.this.getBaseContext(),
                new SideIndexGestureListener());

        relativeL = (RelativeLayout) findViewById(R.id.relative);

        loadProgress= (ProgressBar) findViewById(R.id.loadBar1);
        loadProgress.setVisibility(View.GONE);


        listView = (ListView) findViewById(android.R.id.list);

        //goodsList.clear();

        LoadThread loadThread = new LoadThread();
        loadThread.start();




    }


    ListView listView ;





    ArrayList<DicTable> goodsList = new ArrayList<>();

    class LoadThread extends  Thread  {

        public void run() {

            goodsList.clear();

            if(MainActivity.OFFLINE_MODE) {



               goodsList.addAll( MainApplication.dbHelper.getGoodList());





                Log.d("my", "author list");



            }


           // goodsList.addAll(inputList);

            Log.d("my", "list ok size = "+ goodsList.size());
            myHandler.post(new MyRunnable());


        }
    };

    Handler myHandler = new Handler();

    class MyRunnable implements  Runnable
    {

        @Override
        public void run() {
           // if(isResumed())
            {
                buildList();

                updateList();

                if (adapter != null) {
                    try {
                        setListAdapter(adapter);
                    } catch (Exception e) {

                    }
                } else Log.d("my", "adapter null");
            }
        }
    }



    private void buildList() {
        Log.d("my", "build list");



        for (DicTable auth : goodsList) {
            Good good = (Good) auth;

            if(id>=0){
               double cnt =  MainApplication.dbHelper.getGoodCountAcc(id,good.getId());
               good.setFcnt(cnt);
            }

            int id = good.getId();
            String name = good.getName();
            String str = "";

                str = name;
            str.trim();

            namesList.add(str);

            goodsMap.put(str, good);
        }

        // Collections.sort(countries);

        Collections.sort(namesList,new SortIgnoreCase());
        Log.d("my","collection namelist= "+namesList);

        List<TotalGoodsListAdapter.Row> rows = new ArrayList<TotalGoodsListAdapter.Row>();
        int start = 0;
        int end = 0;
        String previousLetter = null;
        Object[] tmpIndexItem = null;
        Pattern numberPattern = Pattern.compile("[0-9]");

        alphabet.clear();
        for (String country : namesList) {
            String firstLetter = country.substring(0, 1);
            Locale locale = getResources().getConfiguration().locale;
            firstLetter = firstLetter.toUpperCase(locale/*Locale.UK*/);

            // Group numbers together in the scroller
            if (numberPattern.matcher(firstLetter).matches()) {
                firstLetter = "#";
            }

            // If we've changed to a new letter, add the previous letter to the
            // alphabet scroller
            if (previousLetter != null && !firstLetter.equals(previousLetter)) {
                end = rows.size() - 1;
                tmpIndexItem = new Object[3];
                tmpIndexItem[0] = previousLetter;//.toUpperCase(Locale.UK);
                tmpIndexItem[1] = start;
                tmpIndexItem[2] = end;
                alphabet.add(tmpIndexItem);

                start = end + 1;
            }

            // Check if we need to add a header row
            if (!firstLetter.equals(previousLetter)) {
                rows.add(new TotalGoodsListAdapter.Section(firstLetter));
                sections.put(firstLetter, start);
            }

            // Add the country to the list
            rows.add(new TotalGoodsListAdapter.Item(country));
            previousLetter = firstLetter;
        }

        if (previousLetter != null) {
            // Save the last letter
            tmpIndexItem = new Object[3];
            tmpIndexItem[0] = previousLetter;//.toUpperCase(Locale.UK);
            tmpIndexItem[1] = start;
            tmpIndexItem[2] = rows.size() - 1;
            alphabet.add(tmpIndexItem);
        }

        Log.d("my","authors map="+ goodsMap);
        adapter.setRows(rows);
        adapter.setHashMap(goodsMap);


        ready = true;


    }

    public class SortIgnoreCase implements Comparator<Object> {
        public int compare(Object o1, Object o2) {
            String s1 = (String) o1;
            String s2 = (String) o2;
            return s1.toLowerCase().compareTo(s2.toLowerCase());
        }
    }



    boolean ready = true;

    public boolean onTouchEvent(MotionEvent event) {
        if (mGestureDetector.onTouchEvent(event)) {
            return true;
        } else {
            return false;
        }
    }

    public void updateList() {
        Log.d("my","888");
        LinearLayout sideIndex = (LinearLayout) findViewById(R.id.sideIndex);
        sideIndex.removeAllViews();
        indexListSize = alphabet.size();
        if (indexListSize < 1) {
            return;
        }

        int indexMaxSize = (int) Math.floor(sideIndex.getHeight() / 20);
        int tmpIndexListSize = indexListSize;
        while (tmpIndexListSize > indexMaxSize) {
            tmpIndexListSize = tmpIndexListSize / 2;
        }
        double delta;
        if (tmpIndexListSize > 0) {
            delta = indexListSize / tmpIndexListSize;
        } else {
            delta = 1;
        }

        TextView tmpTV;
        for (double i = 1; i <= indexListSize; i = i + delta) {
            Object[] tmpIndexItem = alphabet.get((int) i - 1);
            String tmpLetter = tmpIndexItem[0].toString();

            tmpTV = new TextView(TotalGoodsListActivity.this);
            tmpTV.setText(tmpLetter);
            tmpTV.setTextColor(getResources().getColor(R.color.navy));
            tmpTV.setAllCaps(false);

            tmpTV.setGravity(Gravity.CENTER);
            tmpTV.setTextSize(15);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT, 1);
            tmpTV.setLayoutParams(params);
            sideIndex.addView(tmpTV);
        }

        sideIndexHeight = sideIndex.getHeight();
        Log.d("my","889");

        sideIndex.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // now you know coordinates of touch
                sideIndexX = event.getX();
                sideIndexY = event.getY();

                // and can display a proper item it country list
                displayListItem();//@@@@@@@@@@@@@@@@@@@@@!!!!!!!!!!!!!!!!!!!!!!!!!!!!2222222222222222222222222222222

                return false;
            }
        });
    }

    public void displayListItem() {
        LinearLayout sideIndex = (LinearLayout) findViewById(R.id.sideIndex);
        sideIndexHeight = sideIndex.getHeight();
        // compute number of pixels for every side index item
        double pixelPerIndexItem = (double) sideIndexHeight / indexListSize;

        // compute the item index for given event position belongs to
        int itemPosition = (int) (sideIndexY / pixelPerIndexItem);
        if(sections==null) Log.d("my","alphabet = null");
        //if(sections==null) Log.d("my","sections = null");
        // get the item (we can do it since we know item index)
        if (itemPosition < alphabet.size()) {
            Object[] indexItem = alphabet.get(itemPosition);

            if(indexItem==null) Log.d("my","index item = null");
            Log.d("my","ssssss");
            Log.d("my","index size = "+indexItem.length);
            Log.d("my","sections size = "+sections.size());

          //  if(sideIndex==null) Log.d("my","sideIndex = null");
           // if(sections==null) Log.d("my","sections = null");

            Log.d("my"," ind item = "+indexItem[0]);
            Log.d("my","section = "+sections.toString());
            String indi = (String) indexItem[0];

            //indi=indi.toLowerCase();
            Log.d("my","indi = "+indi);
            int subitemPosition = sections.get(indi);

           // int subitemPosition = sections.get(indexItem[0]);
           // if(sections.get(indexItem[0])==null) Log.d("my","sections.get(indexItem[0]) = null");

            // ListView listView = (ListView) findViewById(android.R.id.list);
            getListView().setSelection(subitemPosition);
        }
    }



    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        Log.d("my","on resume");
    }

    public void  clickEvent(View v)
    {
//Code to implement
    }







}
