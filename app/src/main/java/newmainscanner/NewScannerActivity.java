package newmainscanner;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
//import android.support.v7.widget.Toolbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
//import android.widget.Toolbar;

import com.app.barcodeclient3.R;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import docklist.DockListActivity;
import essences.Good;
import essences.Inventory;
import excel.CreateExportFile;
import excel.FileLoader;
import main.MainApplication;
import main.ScannerConstants;
import main.TreeGrpAndGoodsActivity;
import requests.RequestGoodsList;
import requests.RequestInventoryDtEditCnt;
import requests.RequestScanSettings;
import scanworkingactivity.CreateGoodDialog;
import scanworkingactivity.GoodsListAdapter;
import scanworkingactivity.ScanSettingsActivity;
import scanworkingactivity.TotalGoodsListActivity;
import oldbarcodestartactivity.MainActivity;

import static main.ScannerConstants.CURRENT_SCAN_MODE;

/**
 * Custom Scannner Activity extending from Activity to display a custom layout form scanner view.
 */
public class NewScannerActivity extends AppCompatActivity implements
        DecoratedBarcodeView.TorchListener {

    // private CaptureManager capture;
    private DecoratedBarcodeView barcodeScannerView;
    private ImageView switchFlashlightButton;
    private ImageButton hideCameraButton;


    private static DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;




    ArrayList<String> data = new ArrayList<>();
    ArrayAdapter<String> adapterDoc;
    GoodsListAdapter<Good> adapter;

    ViewGroup container;
    LayoutInflater inflater;
    View myRootView;
    private String titletxt = "";

    public static int id = 0;
    int docType = 0;
    String date = "";
    String subdiv = "";

    ImageButton closeButton;
    TextView abarTitle;
    ImageView offlineIcon;

    ListView list;


    EditText searchEditText;
    // EditText searchEditTextNew;

    ImageButton searchBt;

    RelativeLayout listRelLayout;

    RelativeLayout goodDetailsRelLayout;
    TextView goodsName;
    TextView goodsBarcode;
    TextView goodsArticle;
    TextView goodsPrice;

    RelativeLayout cntRelLayout;
    EditText goodsCntEditText;
    TextView factCntTextView;
    TextView plusTextView;
    Button plusButton;
    Button minusButton;

    LinearLayout calculatorLinLayout;
    Button c0;
    Button c1;
    Button c2;
    Button c3;
    Button c4;
    Button c5;
    Button c6;
    Button c7;
    Button c8;
    Button c9;
    Button cDot;
    Button cClear;

    LinearLayout applyLinLayout;
    Button btCancel;
    Button btOk;

    ProgressBar exportProgress;


    LinearLayout subRelLayout;
    ImageButton docListButton1;
    ImageButton keyBoardButton1;

    ImageButton scansettingsButton;

    CheckBox searchArticle;

    NumberPicker numberPicker;


    // private ActionBar mainActionBar;

    boolean notCreateGood = false;


    Button testButton;

    int enter = 0;
    boolean newScan = true;
    public static ArrayList<Good> goodList = new ArrayList<>();
    ArrayList<Good> originGoodListSaver = new ArrayList<>();
    boolean isEnabledFindButton = true;

    String barcode = "";
    String article = "";
    String name = "";

    Good currentGood = null;

    public static android.support.v7.app.ActionBar mainActionBar;


    Dialog dialog;

    private void showCamera() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View view = getLayoutInflater().inflate(R.layout.dialog_camera, null);
        switchFlashlightButton =  view.findViewById(R.id.switch_flashlight);
        switchFlashlightButton.setTag("0");
        switchFlashlightButton.setImageResource(R.drawable.ic_highlight_white_36dp);
        if (!hasFlash()) {
            switchFlashlightButton.setVisibility(View.GONE);
        }
        ImageView closeBt = view.findViewById(R.id.closeBt);
        closeBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        barcodeScannerView = (DecoratedBarcodeView) view.findViewById(R.id.zxing_barcode_scanner);
        barcodeScannerView.setTorchListener(this);
        barcodeScannerView.decodeContinuous(callback);
        barcodeScannerView.setVisibility(View.VISIBLE);
        barcodeScannerView.resume();
        barcodeScannerView.setStatusText("");

        builder.setView(view);
        builder.setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialog.dismiss();
            }
        });

        dialog = builder.create();
        dialog.show();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_new_scanner);
        Log.d("my", "hello book list");


        // if the device does not have flashlight in its camera,
        // then remove the switch flashlight button...
        searchArticle = (CheckBox) findViewById(R.id.searchArticle);


        hideCameraButton = (ImageButton) findViewById(R.id.hide_camera);
        hideCameraButton.setTag("0");
       // hideCameraButton.setImageResource(R.drawable.ic_close_white_36dp);
        hideCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* if((  (String)hideCameraButton.getTag() ).equals("0") ){
                    v.setTag("1");
                    hideCameraButton.setImageResource(R.drawable.ic_camera_alt_white_36dp);
                   // barcodeScannerView.setVisibility(View.GONE);
                  //  switchFlashlightButton.setVisibility(View.INVISIBLE);
                    //capture.onPause();
                    barcodeScannerView.pause();
                    MainApplication.dbHelper.insertOrReplaceOption(MainApplication.CAMERA_STATE,MainApplication.CAMERA_OFF);

                }
                else*/
                {
                    v.setTag("0");
                    startCamera();
                }
            }
        });


       /* capture = new CaptureManager(this, barcodeScannerView);
        capture.initializeFromIntent(getIntent(), savedInstanceState);
        capture.decode();*/


        //setProgressBarIndeterminateVisibility(true);

        String value = MainApplication.dbHelper.getOption(MainActivity.not_ask_create_good);
        if (value == null) {
            notCreateGood = false;
        } else {
            if (value.equals("1")) notCreateGood = true;
            else notCreateGood = false;
        }


        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_menu_white_36dp, // nav menu toggle icon
                R.string.app_name, // nav drawer open - description for accessibility
                R.string.app_name // nav drawer close - description for accessibility
        ) {
            public void onDrawerClosed(View view) {
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);


        MenuAdapter menuAdapter = new MenuAdapter();

        mDrawerList.setAdapter(menuAdapter);///////////!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!


       /* mainActionBar = getActionBar(); //main bar
        mainActionBar.setDisplayShowCustomEnabled(true);
        mainActionBar.setDisplayShowHomeEnabled(false);
        mainActionBar.setCustomView(R.layout.abar_scan_settings);*/


        mainActionBar = getSupportActionBar();
        mainActionBar.setDisplayOptions(getSupportActionBar().getDisplayOptions() | android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        mainActionBar.setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);

        mainActionBar.setCustomView(R.layout.abar_scan_settings);
        View abarView = mainActionBar.getCustomView();
        final Toolbar parent = (Toolbar) abarView.getParent();
        parent.setPadding(0, 0, 0, 0);//for tab otherwise give space in tab
        parent.setContentInsetsAbsolute(0, 0);


        closeButton = (ImageButton) mainActionBar.getCustomView().findViewById(R.id.closeButton);
        abarTitle = (TextView) mainActionBar.getCustomView().findViewById(R.id.abarTitle);
        TextView offlineTW = (TextView) mainActionBar.getCustomView().findViewById(R.id.offlineIcon);
        if (MainApplication.OFFLINE_MODE) {
            //offlineTW.setVisibility(View.VISIBLE);//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        } else {
            offlineTW.setVisibility(View.GONE);
        }

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (exportProgress.getVisibility() != View.VISIBLE) {
                    finish();
                } else {
                    new AlertDialog.Builder(NewScannerActivity.this)
                            .setTitle("Идет передача данных!")
                            .setMessage("Отменить?")
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    export = false;
                                    //exportProgress.setVisibility(View.GONE);
                                    //finish();
                                }
                            })
                            .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            }
        });

        ImageView listIcon = (ImageView) mainActionBar.getCustomView().findViewById(R.id.listIcon);
        listIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) closeDriver();
                else openDriver();
            }
        });

        Intent iin = getIntent();

        Bundle bundle = iin.getExtras();

        try {
            docType = bundle.getInt("docType");
            id = bundle.getInt("id");
            date = bundle.getString("date");
            subdiv = bundle.getString("subdiv");
        } catch (Exception e) {
            Log.d("my", " no id =((( ");
        }

        Log.d("my", "myId = " + id);
        if (id == -1) {
            selectOrCreateDocument();
        }

        if (docType == Inventory.INVENTORY) {
            abarTitle.setText(R.string.inventory);
        }
        if (docType == Inventory.BILL_IN) {
            abarTitle.setText(R.string.bill_in);
        }

        list = (ListView) findViewById(R.id.list);

        searchEditText = (EditText) mainActionBar.getCustomView().findViewById(R.id.searchEditText);
        searchBt = (ImageButton) mainActionBar.getCustomView().findViewById(R.id.searchBt);
        listRelLayout = (RelativeLayout) findViewById(R.id.listRelLayout);
        goodDetailsRelLayout = (RelativeLayout) findViewById(R.id.goodDetailsRelLayout);
        goodsName = (TextView) findViewById(R.id.goodsName);
        goodsBarcode = (TextView) findViewById(R.id.goodsBarcode);
        goodsArticle = (TextView) findViewById(R.id.goodsArticle);
        goodsPrice = (TextView) findViewById(R.id.goodsPrice);
        cntRelLayout = (RelativeLayout) findViewById(R.id.cntRelLayout);
        goodsCntEditText = (EditText) findViewById(R.id.goodsCnt);
        factCntTextView = (TextView) findViewById(R.id.factCnt);
        plusTextView = (TextView) findViewById(R.id.plusTextView);
        plusButton = (Button) findViewById(R.id.plusButton);
        minusButton = (Button) findViewById(R.id.minusButton);


        calculatorLinLayout = (LinearLayout) findViewById(R.id.calculatorLinLayout);
        c0 = (Button) findViewById(R.id.c0);
        c0.setOnClickListener(c0_Click);
        c1 = (Button) findViewById(R.id.c1);
        c1.setOnClickListener(c1c9_Click);
        c2 = (Button) findViewById(R.id.c2);
        c2.setOnClickListener(c1c9_Click);
        c3 = (Button) findViewById(R.id.c3);
        c3.setOnClickListener(c1c9_Click);
        c4 = (Button) findViewById(R.id.c4);
        c4.setOnClickListener(c1c9_Click);
        c5 = (Button) findViewById(R.id.c5);
        c5.setOnClickListener(c1c9_Click);
        c6 = (Button) findViewById(R.id.c6);
        c6.setOnClickListener(c1c9_Click);
        c7 = (Button) findViewById(R.id.c7);
        c7.setOnClickListener(c1c9_Click);
        c8 = (Button) findViewById(R.id.c8);
        c8.setOnClickListener(c1c9_Click);
        c9 = (Button) findViewById(R.id.c9);
        c9.setOnClickListener(c1c9_Click);
        cDot = (Button) findViewById(R.id.cDot);
        cDot.setOnClickListener(cDot_Click);
        cClear = (Button) findViewById(R.id.cClear);
        cClear.setOnClickListener(cClear_Click);
        applyLinLayout = (LinearLayout) findViewById(R.id.applyLinLayout);
        btCancel = (Button) findViewById(R.id.btCancel);
        btOk = (Button) findViewById(R.id.btOk);
        subRelLayout = (LinearLayout) findViewById(R.id.subRelLayout);
        docListButton1 = (ImageButton) findViewById(R.id.docListButton1);
        keyBoardButton1 = (ImageButton) findViewById(R.id.keyBoardButton1);

        scansettingsButton = (ImageButton) findViewById(R.id.scansettingsButton);

        exportProgress = (ProgressBar) findViewById(R.id.exportProgress);


        keyBoardButton1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                InputMethodManager inputMethodManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showInputMethodPicker();
            }
        });


        keyBoardButton1.setFocusable(false);

        scansettingsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(NewScannerActivity.this, ScanSettingsActivity.class);
                startActivityForResult(intent, 0);
            }
        });


        testButton = (Button) findViewById(R.id.testButton);
        //testButton.setVisibility(View.VISIBLE);
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                test();
            }
        });


        searchEditText.setOnKeyListener(findListener);
        searchEditText.setOnClickListener(searchEditTextListener);
        searchEditText.setOnTouchListener(clearTouchListener);

        searchBt.setOnClickListener(searchButtonListener);

        goodsCntEditText.setOnClickListener(goodsCntListener);
        goodsCntEditText.setOnTouchListener(backspaceTouchListener);
        goodsCntEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {


            @Override
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                int result = actionId & EditorInfo.IME_MASK_ACTION;
                Log.d("my","CURRENT SCAN MODE = "+CURRENT_SCAN_MODE);
                switch (result) {
                    case EditorInfo.IME_ACTION_DONE:
                        // done stuff
                        hideKeyboard();
                        if (CURRENT_SCAN_MODE == 0 || CURRENT_SCAN_MODE == 1) {
                            Log.d("my", "DONE!!!");
                            sendInvDt();
                        }
                        if (CURRENT_SCAN_MODE == 2) {
                            cntPlusFactShow();
                        }
                        break;
                    case EditorInfo.IME_ACTION_NEXT:
                        // next stuff
                        break;
                }
                return true;
            }

            ;
        });
        //InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        //imm.hideSoftInputFromWindow(goodsCntEditText.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);

        plusButton.setOnClickListener(plusMinusClickListener);
        minusButton.setOnClickListener(plusMinusClickListener);


		  /*numberPicker =  (NumberPicker) findViewById(R.id.numberPicker);
          numberPicker.setMinValue(0);
		  numberPicker.setMaxValue(10);
		  numberPicker.setWrapSelectorWheel(true);*/


        btCancel.setOnClickListener(clearClickListener);
        btOk.setOnClickListener(commitInvClickListener);

        docListButton1.setOnClickListener(goodsListListener);


        Log.d("my", "  onCreateView scanwork");

        adapter = new GoodsListAdapter<Good>(NewScannerActivity.this,
                R.layout.adapter_good_row, goodList, id);
        list.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        ThreadScanSettings threadScanSettings = new ThreadScanSettings();
        threadScanSettings.start();


    }

    private void startCamera() {

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.d("my", "get gallery android 6+");


            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                Log.d("my", "get gallery no granted");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 0);
            } else {
                Log.d("my", "get gallery granted!");
                //takePicture();
                //startCameraIntegrator();
                showCamera();

            }
        } else {
            Log.d("my", "get gallery android 4,5");
            //takePicture();
            //startCameraIntegrator();
            showCamera();
        }


    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @android.support.annotation.NonNull final String[] permissions, @android.support.annotation.NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("my", "permissions = " + permissions[0]);


        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                //takePicture();
                if (permissions[0].equals(Manifest.permission.CAMERA))
                    showCamera(); //startCameraIntegrator();
                if (permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE))
                    exportInvToXLS1();

            }
        }
    }


    /*private void startCameraIntegrator(){

        hideCameraButton.setImageResource(R.drawable.ic_close_white_36dp);
        barcodeScannerView.setVisibility(View.VISIBLE);
        switchFlashlightButton.setVisibility(View.VISIBLE);
        //capture.onResume();
        barcodeScannerView.resume();
        MainApplication.dbHelper.insertOrReplaceOption(MainApplication.CAMERA_STATE,MainApplication.CAMERA_ON);
    }*/


    ArrayList<Good> accGoods = new ArrayList<>();

    class CheckOffineEditDocument extends Thread {
        @Override
        public void run() {
            accGoods.clear();
            ArrayList<Good> alg = MainApplication.dbHelper.getGoodsCountListAcc(id);
            accGoods.addAll(alg);

            if (accGoods.size() > 0) {
                ht.post(new Runnable() {
                    @Override
                    public void run() {
                        exportDataToServerDialog();
                    }
                });
            }

        }
    }

    ;

    class ThreadScanSettings extends Thread {
        @Override
        public void run() {
            if (!MainApplication.OFFLINE_MODE) {
                RequestScanSettings requestScanSettings = new RequestScanSettings();
                JSONObject scanJSON = requestScanSettings.getScanSettings();
                Log.d("my", "scan settings = " + scanJSON.toString());
                if (scanJSON.length() > 0) {
                    try {
                        MainApplication.WEIGTH_BARCODE = scanJSON.getString("WEIGTH_BARCODE");
                    } catch (Exception e) {
                    }
                    try {
                        MainApplication.WEIGTH_BARCODE_MASK = scanJSON.getString("WEIGTH_BARCODE_MASK");
                    } catch (Exception e) {
                    }
                }
            }
            ht.post(new Runnable() {
                @Override
                public void run() {
                    CheckOffineEditDocument checkOffineEditDocument = new CheckOffineEditDocument();
                    checkOffineEditDocument.start();
                }
            });
        }
    }

    ;

    Handler ht = new Handler();


    private void checkWeigthBarcode(String barcode) {
        if (MainApplication.WEIGTH_BARCODE.length() >= 11 && MainApplication.WEIGTH_BARCODE_MASK.length() >= 2) {

            //String pref1=MainActivity.WEIGTH_BARCODE.substring(2,4);
            //String pref2=MainActivity.WEIGTH_BARCODE.substring(2,4);
            //String pref3=MainActivity.WEIGTH_BARCODE.substring(2,4);
            getScanPrefixs(MainApplication.WEIGTH_BARCODE);

        }
    }

    String[] getScanPrefixs(String sampleString) {
        sampleString = "fgh(101|203|405)dchh";
        String[] stringArray = sampleString.split("|");
        int[] intArray = new int[stringArray.length];
        for (int i = 0; i < stringArray.length; i++) {
            String numberAsString = stringArray[i];
            intArray[i] = Integer.parseInt(numberAsString);
        }
        System.out.println("Number of integers: " + intArray.length);
        System.out.println("The integers are:");
        for (int number : intArray) {
            System.out.println(number);

        }
        Log.d("my", stringArray.toString());
        return stringArray;
    }


    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.RESULT_HIDDEN);//!!!!!!!!!!!!!!!!!!!!!!!!
            //to show soft keyboard
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

//to hide it, call the method again
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }
    }


    View.OnClickListener searchButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            search();
        }
    };

    private void search()//////!!!!!!!!!!!!!!!!!!!!!!
    {
        Log.d("my", "search bt!");

        hideKeyboard();

        String barc = searchEditText.getText().toString();

        if (barc.trim().length() > 0) {
            //	clearfindButton.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.close_32));
            originGoodListSaver.clear();
            originGoodListSaver.addAll(goodList);
            enter = 0;
            newScan = true;
            searchEditText.setFocusable(true);
            //isEnabledFindButton=false;

            setGoodsByBarc(barc, barc, barc);

        }
    }


    View.OnTouchListener clearTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            final int DRAWABLE_LEFT = 0;
            final int DRAWABLE_TOP = 1;
            final int DRAWABLE_RIGHT = 2;
            final int DRAWABLE_BOTTOM = 3;

            TextView tv = (TextView) v;

            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (tv.getRight() - tv.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    Log.d("my", "clear");
                    tv.setText("");
                    return true;
                }
            }
            return false;
        }
    };

    View.OnTouchListener backspaceTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            final int DRAWABLE_LEFT = 0;
            final int DRAWABLE_TOP = 1;
            final int DRAWABLE_RIGHT = 2;
            final int DRAWABLE_BOTTOM = 3;

            TextView tv = (TextView) v;

            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (tv.getRight() - tv.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    // your action here
                    Log.d("my", "buckspace");

                    String current_text = tv.getText().toString();
                    if (current_text.length() > 0) {
                        String new_text = current_text.substring(0, current_text.length() - 1);
                        tv.setText(new_text);
                    }

                    return true;
                }
            }
            return false;
        }
    };


    View.OnKeyListener findListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            //if(!blockScanner){
            Log.d("my", "onKey");

            if (keyCode == 66) {
                enter++;
                Log.d("my", "key=" + enter);
            }
            if (enter == 2) {

                //clearfindButton.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.close_32));
				/*originGoodListSaver.clear();
				originGoodListSaver.addAll(goodList);
				enter=0;
				newScan=true;
				searchEditText.setFocusable(true);
				//isEnabledFindButton=false;
				String barc = searchEditText.getText().toString();*/
                //setGoodsByBarc(barc,barc);

                search();
            }

            //}
            return false;
        }

    };

    class LoadGoodsByBarcThread extends Thread {
        @Override
        public void run() {
            RequestGoodsList requestServer = new RequestGoodsList();
            ArrayList<Good> goodsList = new ArrayList<>();
            Log.d("my", "IS checked = " + searchArticle.isChecked());
            if (!MainApplication.OFFLINE_MODE) {
                if (!searchArticle.isChecked()) {
                    goodsList = requestServer.getGoodsListByBarcode(id, barcode, article, name);
                } else {
                    goodsList = requestServer.getGoodsListByBarcode(id, "", article, "");
                }
            }
            if (MainApplication.OFFLINE_MODE) {
                if (!searchArticle.isChecked()) {
                    goodsList = MainApplication.dbHelper.getGoodList(name, article, barcode, id);
                } else {
                    goodsList = MainApplication.dbHelper.getGoodList("", article, "", id);
                }
            }


            goodList.clear();
            goodList.addAll(goodsList);
            Log.d("my", "otpravili tovar");
            h.post(readyGoodsHandler);
        }
    }

    Handler h = new Handler();

    Handler h3 = new Handler();

    Runnable readyGoodsHandler = new Runnable() {


        public void run() {

            Log.d("my", "thread: goodslist sz = " + goodList.size());// buildList();
            Log.d("my", "build rows goods!!!");
            adapter.notifyDataSetChanged();
            hideProgressBar();
            goodDetailsRelLayout.setOnClickListener(null);
            int size = goodList.size();
            switch (size) {
                case 0:
                    clearGoodInfo();
                    searchFocusable(true);
                    if (MainApplication.OFFLINE_MODE) {//dialog. Tovar ne naiden sozdat' ?
					/*	createGoodDialogBuilder = new AlertDialog.Builder(ScanWorkingActivity.this);
						createGoodDialogBuilder.setMessage(getString(R.string.good_not_found_create)).setPositiveButton(getString(R.string.create), createGoodDialogClickListener)
								.setNegativeButton(getString(R.string.cancel), createGoodDialogClickListener);
						createGoodDialog = createGoodDialogBuilder.create();
						createGoodDialog.show();*/


                        if (!notCreateGood) {


                            CreateGoodDialog goodDialog = new CreateGoodDialog();


                            Bundle args = new Bundle();
                            String good = barcode;//searchEditText.getText().toString();
                            args.putString("good", good);
                            goodDialog.setArguments(args);

                            goodDialog.show(getFragmentManager(), "Hello!");
                        }
                    }

                    break;
                case 1:
                    Good good = goodList.get(0);
                    currentGood = good;
                    showOneGood(currentGood);
                    searchFocusable(false);
                    break;
                default:
                    if (adapter != null) {


                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                for (Good good1 : goodList) {
                                    //double cnt = MainActivity.dbHelper.getGoodCountAcc(good1.getId(), id);
                                    Log.d("my", "---*** cnt = " + good1.getFcnt());
                                    //good1.setFcnt(cnt);
                                }
                                h3.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Log.d("my", "adapter no null");
                                        adapter.notifyDataSetChanged();

                                        Log.d("my", "222");
                                        adapter.setGoodClickListener(goodClickListener);
                                        adapter.notifyDataSetChanged();
                                        list.setAdapter(adapter);
                                        adapter.notifyDataSetChanged();/////!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! CHeck it !!!!
                                        showGoodsList(true);
                                        adapter.notifyDataSetChanged();/////!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! CHeck it !!!!
                                    }
                                });
                            }
                        }).start();


                    }

                    break;
            }
        }
    };


    private void clearGoodInfo() {
        currentGood = null;
        goodsName.setText("");
        goodsArticle.setText("");
        goodsBarcode.setText("");
        goodsPrice.setText("");
        goodsCntEditText.setText("");
        searchEditText.setText("");
        factCntTextView.setTextColor(getResources().getColor(R.color.dark_gray));

        ////

        adapter.notifyDataSetChanged();

    }


    private void showOneGood(Good good) {

        hideKeyboard();
        //Good good = goodList.get(0);
        String name = good.getName();
        String article = good.getArticle();
        ArrayList<String> barcodes = good.getBarcodes();
        Log.d("my", "barcodes &&&=" + barcodes);
        String barcodStr = "";
        for (int i = 0; i < barcodes.size(); i++) {
            barcodStr += barcodes.get(i);
            if (i < barcodes.size() - 2) barcodStr += "\n";
        }
        double out_price = good.getOut_price();

        goodsName.setText(name);
        goodsArticle.setText(article);
        goodsBarcode.setText(barcodStr);
        goodsPrice.setText("" + out_price);

        double fcnt = good.getFcnt();
        setFactCntTextView(fcnt);

    }


    private void showGoodsList(boolean show) {
        if (show) {
            listRelLayout.setVisibility(View.VISIBLE);

            goodDetailsRelLayout.setVisibility(View.GONE);
            cntRelLayout.setVisibility(View.GONE);
            calculatorLinLayout.setVisibility(View.GONE);
            applyLinLayout.setVisibility(View.GONE);
            subRelLayout.setVisibility(View.GONE);
            //typeCntRelLayout.setVisibility(View.GONE);
        } else {

            listRelLayout.setVisibility(View.GONE);

            goodDetailsRelLayout.setVisibility(View.VISIBLE);
            cntRelLayout.setVisibility(View.VISIBLE);
            calculatorLinLayout.setVisibility(View.VISIBLE);
            applyLinLayout.setVisibility(View.VISIBLE);
            subRelLayout.setVisibility(View.VISIBLE);
            //typeCntRelLayout.setVisibility(View.VISIBLE);
        }
    }


    private void cntPlusFactShow() {
        String strFcnt = factCntTextView.getText().toString();
        String strCnt = goodsCntEditText.getText().toString();
        double sum = 0;

        if (strCnt.length() > 0) {

            double factcnt = 0;
            double cnt = 0;

            try {
                factcnt = Double.parseDouble(strFcnt);
                Log.d("my", "01");
            } catch (Exception e) {
                factcnt = 0;
                Log.d("my", "e1");
            }

            try {
                cnt = Double.parseDouble(strCnt);
                sum = cnt + factcnt;
                factCntTextView.setText("");
                goodsCntEditText.setText("" + sum);
                Log.d("my", "02");
            } catch (Exception e) {
                Log.d("my", "e2");

            }

        }

    }

    View.OnClickListener goodClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            Object tag = view.getTag();
            if (tag != null) {
                if (tag instanceof Good) {
                    Good good = (Good) tag;
                    String name = good.getName();
                    Log.d("my", "name = " + name);

                    showGoodsList(false);
                    currentGood = good;
                    showOneGood(currentGood);
                    goodDetailsRelLayout.setOnClickListener(goodDetailsRelLayoutListener);
                    searchFocusable(false);
                }
            }

        }
    };


    public void setGoodsByBarc(String barcode, String article, String name) {

        searchEditText.setText("");


        this.barcode = barcode;
        this.article = article;
        this.name = name;

        //showProgressBar(true,getString(R.string.loading_goods)+ " by BARCODE...");
        LoadGoodsByBarcThread loadGoodsThread = new LoadGoodsByBarcThread();
        loadGoodsThread.start();
    }

    View.OnClickListener searchEditTextListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            searchFocusable(true);
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);//!!!!!!
            if (imm != null) {
                imm.showSoftInput(searchEditText, InputMethodManager.SHOW_IMPLICIT);
            }
        }
    };

    View.OnClickListener goodsCntListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            searchFocusable(false);

            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(goodsCntEditText.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
            }
            //hideKeyboard(); ///!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        }
    };

    private void searchFocusable(boolean searchFocusable) {
        if (searchFocusable) {
            //searchEditText.setFocusable(true);
            searchEditText.requestFocus();
            //InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            //imm.showSoftInput(searchEditText, InputMethodManager.SHOW_IMPLICIT);
        } else {
            //goodsCntEditText.setFocusable(true);
            goodsCntEditText.requestFocus();

            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(goodsCntEditText.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
            }
            hideKeyboard();

            //InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);//!!!!!!!!!!!!!!!!!!!!!!!!
            //imm.showSoftInput(goodsCntEditText, InputMethodManager.SHOW_IMPLICIT);


        }
    }

    View.OnClickListener goodDetailsRelLayoutListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            showGoodsList(true);
        }
    };

   /* public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        //retrieve scan result
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);

        Log.d("my","SCANNED!");
        if(dialog!=null) dialog.dismiss();


        if (scanningResult != null) {


            //we have a result
            String scanContent = scanningResult.getContents();
            String scanFormat = scanningResult.getFormatName();
            //formatTxt.setText("FORMAT: " + scanFormat);
            if (scanContent != null) {

                Log.d("my", "scanContent" + scanContent);
                searchEditText.setText(scanContent);


                if (!scanContent.equals("")) {
                    //	clearfindButton.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.close_32));
                    originGoodListSaver.clear();
                    originGoodListSaver.addAll(goodList);
                    enter = 0;
                    newScan = true;
                    searchEditText.setFocusable(true);
                    //isEnabledFindButton=false;
                    String barc = searchEditText.getText().toString();
                    setGoodsByBarc(barc, barc, barc);

                }
            }
        } else {
            if (requestCode != 911) {
                Toast toast = Toast.makeText(getApplicationContext(),
                        "No scan data received!", Toast.LENGTH_SHORT);
                //toast.show();!!!!!/////////////////////////////////////////
            }
        }

        Log.d("my", "update scan_mode");
        CURRENT_SCAN_MODE = ScannerConstants.CURRENT_SCAN_MODE;
        switch (CURRENT_SCAN_MODE) {
            case 0:
                plusTextView.setText("");
                break;
            case 2:
                plusTextView.setText("+");
                break;
            case 1:
                plusTextView.setText("+");
                break;
            default:
                plusTextView.setText("");
                break;
        }

        Log.d("my", "activity result: requestCode =" + requestCode + " result Code = " + resultCode);

    }*/

    View.OnClickListener goodsListListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //if(!MainActivity.OFFLINE_MODE)
            {
                Intent intent = new Intent(NewScannerActivity.this, TreeGrpAndGoodsActivity.class);
                startActivity(intent);
            }
            closeDriver();
        }
    };


    private void setFactCntTextView(double fcnt) {
        double fcntPlusOne = fcnt + 1;
        goodsCntEditText.setSelection(goodsCntEditText.getText().length());
        switch (CURRENT_SCAN_MODE) {
            case 0:
                factCntTextView.setText("");
                goodsCntEditText.setText("" + fcnt);
                goodsCntEditText.setSelectAllOnFocus(true);
                break;
            case 1:
                factCntTextView.setText("" + fcnt);
                goodsCntEditText.setText("" + fcntPlusOne);
                goodsCntEditText.setSelectAllOnFocus(true);
                sendInvDt();
                break;
            case 2:
                factCntTextView.setText("" + fcnt);
                goodsCntEditText.setText("0.00");
                goodsCntEditText.setSelectAllOnFocus(true);
                break;
        }
    }

    View.OnClickListener plusMinusClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view instanceof Button) {
                if (currentGood != null) {
                    Button bt = (Button) view;
                    String stD = bt.getText().toString();

                    String goodsCntStr = goodsCntEditText.getText().toString();

                    double cnt = 0;

                    try {
                        cnt = Double.parseDouble(goodsCntStr);
                    } catch (Exception e) {
                        cnt = 0;
                    }


                    if (stD.contains("+")) {
                        cnt += 1;
                        goodsCntEditText.setText("" + cnt);
                    } else {
                        cnt -= 1;
                        double fcnt = currentGood.getFcnt();
                        double anti_fcnt = fcnt * -1;
                        if (cnt < 0) {
                            if (CURRENT_SCAN_MODE == 0) {
                                cnt = 0;
                            } else {
                                if (cnt < anti_fcnt) cnt = 0;
                            }
                        }
                        goodsCntEditText.setText("" + cnt);
                    }
                }
            }

        }
    };


    ProgressDialog dialogGoToBookDetails;

    public void showProgressBar(boolean cancelable, String text) {

        dialogGoToBookDetails = new ProgressDialog(this);
        dialogGoToBookDetails.setMessage(text);
        dialogGoToBookDetails.setIndeterminate(true);
        dialogGoToBookDetails.setCancelable(cancelable);
        dialogGoToBookDetails.show();
    }

    public void hideProgressBar() {
        if (dialogGoToBookDetails != null) dialogGoToBookDetails.hide();
    }

    View.OnClickListener clearClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            clearGoodInfo();
            // barcodeScannerView.resume();
            //barcodeScannerView.setStatusText("");
        }
    };


    View.OnClickListener commitInvClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            // barcodeScannerView.resume();
            // barcodeScannerView.setStatusText("");

            sendInvDt();
        }
    };


    private void sendInvDt() {
        String strcnt = "";
        Log.d("my", "sendInvDt()");

		/*if(CURRENT_SCAN_MODE==ScannerConstants.NORMAL_SCAN||CURRENT_SCAN_MODE==ScannerConstants.PLUS_ONE_SCAN) {
			goodsCntEditText.getText().toString();
		}*/
        if (CURRENT_SCAN_MODE == ScannerConstants.PLUS_FACT_SCAN) {
            Log.d("my", "CURRENT_SCAN_MODE==ScannerConstants.PLUS_FACT_SCAN");
            cntPlusFactShow();
        }
        strcnt = goodsCntEditText.getText().toString();

        if (strcnt.length() > 0) {
            try {
                double cnt = Double.parseDouble(strcnt);
                SendInvDt sendInvDt = new SendInvDt(id, currentGood.getId(), cnt);
                sendInvDt.start();

            } catch (Exception e) {
            }

        }
    }

//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%// KEYBOARD CALC


    private EditText display_currentTextView = null;
    boolean rule = false;

    public View.OnClickListener c1c9_Click = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.d("my", "cifra");
            getCurrentFocusTextView();
            if (display_currentTextView != null) {

                String currentTxt = display_currentTextView.getText().toString();
                Button bt = (Button) view;
                String touchC = bt.getText().toString();
                if (rule) {
                    if (currentTxt.equals("0")) currentTxt = touchC;
                    else currentTxt += touchC;
                } else currentTxt += touchC;
                display_currentTextView.setText(currentTxt);
                display_currentTextView.setSelection(0);


            }
        }
    };

    public View.OnClickListener c0_Click = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            getCurrentFocusTextView();
            if (display_currentTextView != null) {
                String currentTxt = display_currentTextView.getText().toString();
                Button bt = (Button) view;
                String touchC = bt.getText().toString();
                if (rule) {
                    if (currentTxt.equals("0")) currentTxt = "0";
                    else currentTxt += touchC;
                } else currentTxt += touchC;
                display_currentTextView.setText(currentTxt);
                display_currentTextView.setSelection(0);
            }
        }
    };

    public View.OnClickListener cDot_Click = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            getCurrentFocusTextView();
            if (display_currentTextView != null) {
                String currentTxt = display_currentTextView.getText().toString();
                Button bt = (Button) view;
                String touchC = bt.getText().toString();
                if (rule) {
                    if (currentTxt.equals("")) currentTxt = "0" + touchC;
                    else {
                        if (!currentTxt.contains(touchC))
                            currentTxt += touchC;
                    }
                } else currentTxt += touchC;
                display_currentTextView.setText(currentTxt);
                display_currentTextView.setSelection(0);
            }
        }
    };

    public View.OnClickListener cClear_Click = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            getCurrentFocusTextView();
            if (display_currentTextView != null) {

                display_currentTextView.setText("");
                display_currentTextView.setSelection(0);
            }
        }
    };


    private void getCurrentFocusTextView() {
        if (goodsCntEditText.isFocused()) {
            Log.d("my", " good cnt focusable");
            display_currentTextView = goodsCntEditText;
            rule = true;
        }
        if (searchEditText.isFocused()) {
            Log.d("my", "search focusable");
            display_currentTextView = searchEditText;
            rule = false;
        }

        if (display_currentTextView != null) {
            boolean isSelected = display_currentTextView.isSelected();
            String current_text = display_currentTextView.getText().toString();
            int startSelection = display_currentTextView.getSelectionStart();
            int endSelection = display_currentTextView.getSelectionEnd();
            String selectedText = current_text.
                    substring(startSelection, endSelection);
            current_text = current_text.replace(selectedText, "");
            display_currentTextView.setText(current_text);
            Log.d("my", "text selected = " + isSelected);

        }
    }


    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ INVENTORY SEND


    class SendInvDt extends Thread {

        int invId;
        int goodId;
        double cnt;

        public SendInvDt(int invId, int goodId, double cnt) {
            this.invId = invId;
            this.goodId = goodId;
            this.cnt = cnt;

            tempcnt = cnt;
            tempGoodId = goodId;
        }

        @Override
        public void run() {

            if (MainApplication.OFFLINE_MODE) {
                MainApplication.dbHelper.insertGoodsAcCnt(invId, goodId, cnt);
                tempOfflineAnsCnt = MainApplication.dbHelper.getGoodCountAcc(invId, goodId);
                h2.post(readyOffLineInvWriteCnt);

            } else {
                RequestInventoryDtEditCnt requestInventoryDtEditCnt = new RequestInventoryDtEditCnt(NewScannerActivity.this);
                ansJSON = requestInventoryDtEditCnt.sendEditInventoryDt(invId, goodId, cnt);

                //MainActivity.dbHelper.insertGoodsAcCnt(invId, goodId, cnt); ///!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                h2.post(readySendInvId);
            }
        }
    }

    ;

    Handler h2 = new Handler();


    Runnable readyOffLineInvWriteCnt = new Runnable() {
        @Override
        public void run() {

            searchFocusable(true);
            clearGoodInfo();

            if (tempOfflineAnsCnt >= 0) {
                factCntTextView.setText("" + tempOfflineAnsCnt);
                factCntTextView.setTextColor(getResources().getColor(R.color.gold));

                Log.d("my", "ans4 ok");
                Toast toast = Toast.makeText(getApplicationContext(),
                        R.string.labelled, Toast.LENGTH_SHORT);
                toast.show();
                playDone();
            } else {
                new AlertDialog.Builder(NewScannerActivity.this)
                        .setTitle("Error")
                        .setMessage("Ошибка чтения/записи SQLLite")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                playError();
            }

        }
    };


    double tempOfflineAnsCnt = 0;
    JSONObject ansJSON = null;
    double tempcnt = 0;
    int tempGoodId = 0;


    Runnable readySendInvId = new Runnable() {

        public void run() {

            searchFocusable(true);
            clearGoodInfo();

            if (ansJSON != null) {
                String status = "err";
                try {
                    status = ansJSON.getString("status");
                    if (status.equals("ok")) {
                        Log.d("my", "inv status inv dt ok!");
                        Object objcnt = null;
                        try {
                            objcnt = ansJSON.get("cnt");
                            Log.d("my", "inv ans=" + ansJSON.toString());
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        Log.d("my", "ans2");
                        double dcnt = -1;
                        if (objcnt instanceof Double) dcnt = (double) objcnt;
                        if (objcnt instanceof Integer) {
                            int icnt = (int) objcnt;
                            dcnt = icnt;
                        }
                        Log.d("my", "ans3");
                        factCntTextView.setText("" + dcnt);
                        factCntTextView.setTextColor(getResources().getColor(R.color.green));

                        Log.d("my", "ans4 ok");
                        Toast toast = Toast.makeText(getApplicationContext(),
                                R.string.saved, Toast.LENGTH_SHORT);
                        toast.show();

                        playDone();

                    } else {
                        try {

                            String details = ansJSON.getString("details");
                            new AlertDialog.Builder(NewScannerActivity.this)
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
                        playError();
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    playError();
                }


            }
            ;

        }
    };


    private void exportDataToServerDialog() {
        String msg = getString(R.string.exist_acc);
        String message = "" + msg + " \n Непереданных товаров: " + accGoods.size();
        int max = 8;
        int mx = max;
        if (max > accGoods.size()) mx = accGoods.size();
        for (int i = 0; i < mx; i++) {

            message += " " + accGoods.get(i).getName();
            if (mx > 2 && i < mx - 1) message += " ,\n ";
        }

        if (max < accGoods.size()) message += "...";

        //exportInvToXLS()

        String expType = "";
        if (!MainApplication.OFFLINE_MODE) {
            expType = getString(R.string.export_server);
        } else {
            expType = getString(R.string.export_file);
        }

        new AlertDialog.Builder(NewScannerActivity.this)
                .setTitle(R.string.message)
                .setMessage(message)
                .setPositiveButton(expType, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        if (MainApplication.OFFLINE_MODE) {
                            exportInvToXLS();
                        } else {
                            exportProgress.setVisibility(View.VISIBLE);
                            exportProgress.setMax(accGoods.size());
                            exportProgress.setProgress(0);
                            ExportData exportData = new ExportData();
                            exportData.start();
                        }
                    }
                })
                .setNegativeButton(R.string.clear, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ClearOfflineData clearOfflineData = new ClearOfflineData();
                        clearOfflineData.start();
                    }
                })
                .setNeutralButton(R.string.ignore, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();

    }

    ArrayList<String> errList = new ArrayList<>();
    int successSendedGoods = 0;
    int prog = 0;

    boolean export = true;


    String tempErrStr = "";

    class ExportData extends Thread {
        public void run() {
            RequestInventoryDtEditCnt requestInventoryDtEditCnt = new RequestInventoryDtEditCnt(NewScannerActivity.this);

            ArrayList<JSONObject> ansList = new ArrayList<>();
            export = true;

            prog = 0;
            for (Good good : accGoods) {
                if (!export) break;
                JSONObject ansJSON = requestInventoryDtEditCnt.sendEditInventoryDt(id, good.getId(), good.getFcnt());

                prog++;
                hprog.post(new Runnable() {
                    @Override
                    public void run() {
                        exportProgress.setProgress(prog);
                    }
                });

                ansList.add(ansJSON);
                if (ansJSON != null) {
                    try {
                        String status = ansJSON.getString("status");
                        if (status.equals("ok")) {
                            MainApplication.dbHelper.clearGoodForDocID_AC_GOODS_CNT(id, good.getId());
                        }
                    } catch (Exception e) {
                        try {
                            String rusErr = "";
                            String error = ansJSON.getString("error");
                            if (error.contains("network un"))
                                rusErr = getString(R.string.error_connect);

                            tempErrStr = rusErr + "\\n\\r" + error;

                            ht.post(new Runnable() {
                                @Override
                                public void run() {


                                    new AlertDialog.Builder(NewScannerActivity.this)
                                            .setTitle(R.string.error_connect)
                                            .setMessage(tempErrStr)
                                            .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {


                                                public void onClick(DialogInterface dialog, int which) {

                                                    exportProgress.setVisibility(View.VISIBLE);
                                                    exportProgress.setMax(accGoods.size());
                                                    exportProgress.setProgress(0);
                                                    ExportData exportData = new ExportData();
                                                    exportData.start();
                                                }
                                            }).setNeutralButton(R.string.ignore, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    })
                                            .setIcon(android.R.drawable.ic_dialog_info)
                                            .show();

                                }
                            });


                            export = false;
                            break;
                        } catch (Exception e2) {

                        }
                    }
                }

            }


            successSendedGoods = 0;

            for (JSONObject ansJSON : ansList) {

                if (ansJSON != null) {
                    String status = "err";
                    try {
                        status = ansJSON.getString("status");
                        if (status.equals("ok")) {
                            successSendedGoods++;
                        } else {
                            try {
                                String details = ansJSON.getString("details");

                                errList.add("\\n\\r");
                                errList.add(details);

                            } catch (Exception e) {
                                // TODO: handle exception
                            }

                        }
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        playError();
                    }
                }
            }

            if (export)
                ht.post(new Runnable() {
                    @Override
                    public void run() {
                        String msg = "";
                        if (successSendedGoods == accGoods.size())
                            msg = getString(R.string.success_send) + "\n\r" + getString(R.string.export_count) + " " + successSendedGoods;
                        Log.d("my", "acc = " + accGoods.size() + " succ = " + successSendedGoods);

                        if (errList.size() > 0)
                            msg = getString(R.string.error_send) + "\n\r" + getString(R.string.export_count) + " " + successSendedGoods;

                        ScrollView scroll = new ScrollView(NewScannerActivity.this);
                        LinearLayout lin = new LinearLayout(NewScannerActivity.this);
                        TextView txt = new TextView(NewScannerActivity.this);
                        lin.addView(txt);
                        scroll.addView(lin);

                        String errMsg = "";
                        for (String str : errList) {
                            errMsg += str;
                        }

                        txt.setText(errMsg);


                        new AlertDialog.Builder(NewScannerActivity.this)
                                .setTitle(R.string.ready)
                                .setMessage(msg)
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        exportProgress.setVisibility(View.GONE);
                                    }
                                })

                                .setIcon(android.R.drawable.ic_dialog_info)
                                .setView(scroll)
                                .show();
                    }
                });
        }
    }

    Handler hprog = new Handler();


    private class ClearOfflineData extends Thread {
        @Override
        public void run() {
            MainApplication.dbHelper.clearAllForDocID_AC_GOODS_CNT(id);

            h.post(new Runnable() {
                @Override
                public void run() {
                    new AlertDialog.Builder(NewScannerActivity.this)
                            .setTitle(R.string.ready)
                            .setMessage("")
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })

                            .setIcon(android.R.drawable.ic_dialog_info)
                            .show();
                }
            });
        }
    }


    private void playDone() {
        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.done);
        mp.start();
    }

    private void playError() {
        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.error_sound);
        mp.start();
    }


    ProgressDialog progress;

    AlertDialog.Builder adb;
    AlertDialog ad;
    Handler deleteH = new Handler();
    int deletingDockId = -1;

    private class MenuAdapter extends BaseAdapter {

        ArrayList<View> imagesList = new ArrayList<>();

        public MenuAdapter() {


            View view;
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.scan_work_menu_item_folder, null);

            Log.d("my1", "view class =" + view.getClass());

            Button folder = (Button) view.findViewById(R.id.folder);
            folder.setOnClickListener(goodsListListener);
            if (MainApplication.OFFLINE_MODE) folder.setVisibility(View.GONE);

            view.findViewById(R.id.list).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(NewScannerActivity.this,
                            TotalGoodsListActivity.class);
                    Bundle arg = new Bundle();
                    arg.putInt("id", id);
                    intent.putExtras(arg);
                    startActivity(intent);
                    closeDriver();
                }
            });

            view.findViewById(R.id.back_to_invs).setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.putExtra("", "");
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });

            view.findViewById(R.id.key).setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    InputMethodManager inputMethodManager = (InputMethodManager)
                            getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.showInputMethodPicker();
                    closeDriver();
                }
            });

            view.findViewById(R.id.import_excel).setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(NewScannerActivity.this,
                            excel.AndroidReadExcelActivity.class);
                    startActivity(intent);
                    closeDriver();
                }
            });


            view.findViewById(R.id.settings).setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Log.d("my", "select settings...");
                    Intent intent = new Intent(NewScannerActivity.this, ScanSettingsActivity.class);
                    startActivityForResult(intent, 0);
                    closeDriver();
                }
            });

            view.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    //Yes button clicked
                                    MainApplication.dbHelper.clear_INVENTORY(id);
                                    finish();
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    //No button clicked
                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(NewScannerActivity.this);
                    builder.setMessage(getString(R.string.delete_inventory_dialog)).setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show();
                    closeDriver();
                }
            });

            view.findViewById(R.id.review).setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Uri uri = Uri.parse("market://details?id=" + getPackageName());
                    Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                    // To count with Play market backstack, After pressing back button,
                    // to taken back to our application, we need to add following flags to intent.
                    goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                            Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                            Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                    try {
                        startActivity(goToMarket);
                    } catch (ActivityNotFoundException e) {
                        startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
                    }
                    closeDriver();
                }
            });


            view.findViewById(R.id.export).setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    exportInvToXLS();
                }
            });

            imagesList.add(view);


        }


        @Override
        public int getCount() {
            return imagesList.size();
        }

        @Override

        public Object getItem(int position) {
            return imagesList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            return imagesList.get(position);

        }

    }

    public static int dpToPx(Context context, int dp) {

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        Log.d("my", "dp =" + dp + " dp=" + px);

        return px;
    }


    static String filename = "";

    private void exportInvToXLS() {

        if (Build.VERSION.SDK_INT >= 23) {
            // Here, thisActivity is the current activity
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);


            } else { //permission is automatically granted on sdk<23 upon installation
                Log.d("my", "Permission is granted");
                exportInvToXLS1();

            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.d("my", "Permission is granted");
            exportInvToXLS1();

        }
    }


    private void exportInvToXLS1() {
        //Intent intent = new Intent(ScanWorkingActivity.this, ScanSettingsActivity.class);
        //startActivityForResult(intent, 0);

        progress = new ProgressDialog(NewScannerActivity.this);
       /* progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.show();*/
// To dismiss the dialog


        //setProgressBarIndeterminateVisibility(true);
        closeDriver();

        if (date == null) date = "_";
        if (subdiv == null) subdiv = "market";

        date = date.replaceAll(":", "_");
        date = date.replaceAll("/", "-");
        int offl = subdiv.indexOf(" - offline");
        if (offl > 0) subdiv = subdiv.substring(0, offl);
        //date=date.replaceAll("offline document","");
        filename = "inv" + "_" + date + "_" + subdiv;
        Log.d("my", "filename = " + filename);


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Export to file");

// Set up the input
        final EditText input = new EditText(this);
        input.setText(filename);
        input.setSelected(true);
        input.setSelectAllOnFocus(true);

// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton("CSV", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                filename = input.getText().toString();

                ExportExcelThread exportExcelThread = new ExportExcelThread("csv");
                exportExcelThread.start();
            }
        });


        builder.setNeutralButton("XLS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                filename = input.getText().toString();

                ExportExcelThread exportExcelThread = new ExportExcelThread("xls");
                exportExcelThread.start();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();


    }


    boolean success = false;

    class ExportExcelThread extends Thread {

        String type = "xls";

        ExportExcelThread(String type) {
            this.type = type;
        }

        @Override
        public void run() {
            ArrayList<Good> goodsAccList = MainApplication.dbHelper.getALLGoodsCountListAcc(id);
            //success = CreateExportFile.exportGoodsXLS(goodsAccList,filename, id);


            if (type.contains("csv"))
                success = CreateExportFile.exportGoodsCSV(goodsAccList, filename + ".csv", id);
            if (type.contains("xls"))
                success = CreateExportFile.exportGoodsXLS(goodsAccList, filename + ".xls", id);

            impH.post(new Runnable() {
                @Override
                public void run() {
                    progress.dismiss();


                    String title = "";
                    String details = "";
                    if (success) {
                        title = "";
                        details = getString(R.string.export_success);
                    } else {
                        title = getString(R.string.error);
                        details = getString(R.string.export_error);
                    }

                    AlertDialog alertDialog = new AlertDialog.Builder(NewScannerActivity.this).create();
                    alertDialog.setTitle(title);
                    alertDialog.setMessage(details);
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    openFolder();

                                }
                            });
                    alertDialog.show();
                }
            });

            //


        }
    }

    Handler impH = new Handler();

    public void openFolder() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getPath()
                + "/" + FileLoader.DIR_NAME);
        intent.setDataAndType(uri, "xls/text/csv");
        startActivity(Intent.createChooser(intent, "Open folder"));
    }


    public static void openDriver() {
        new Handler().postDelayed(openDrawerRunnable(), 100);
    }

    private static Runnable openDrawerRunnable() {
        return new Runnable() {


            public void run() {
                mDrawerLayout.openDrawer(Gravity.LEFT);
            }
        };
    }

    ;

    public static void closeDriver() {
        new Handler().postDelayed(closeDrawerRunnable(), 100);
    }

    private static Runnable closeDrawerRunnable() {
        return new Runnable() {


            public void run() {
                mDrawerLayout.closeDrawer(Gravity.LEFT);
            }
        };
    }

    ;


    private ArrayList<Good> testGoodList = new ArrayList<>();

    private void test() {
        testGoodList.clear();
        new Thread() {
            @Override
            public void run() {
                Log.d("my", "loading test goods...");
                testGoodList = MainApplication.dbHelper.getGoodList("", "", "", id);
                Log.d("my", "test: goods list size = " + testGoodList);
                testHandler1.post(new Runnable() {
                    @Override
                    public void run() {

                        new Thread() {
                            @Override
                            public void run() {
                                Log.d("my", "writing cnt...");
                                for (Good testgood : testGoodList) {
                                    //Log.d("my","id="+testgood.getId()+" : "+testgood.getName());
                                    MainApplication.dbHelper.insertGoodsAcCnt(id, testgood.getId(), 33);
                                    tempOfflineAnsCnt = MainApplication.dbHelper.getGoodCountAcc(id, testgood.getId());
                                }
                                Log.d("my", "ready write fcnt 33...");
                            }
                        }.start();
                    }
                });
            }
        }.start();


    }

    Handler testHandler1 = new Handler();

    AlertDialog createGoodDialog;


    AlertDialog.Builder createGoodDialogBuilder;


    DialogInterface.OnClickListener createGoodDialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    //Yes button clicked
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    createGoodDialog.hide();
                    break;
            }
        }
    };

    public void onClickGoodDialog(String val, boolean notAskMore) {
        Log.d("my", "val = " + val);
        //barcodeScannerView.resume();
        // barcodeScannerView.setStatusText("");
        setGoodsByBarc(val, val, val);
        notCreateGood = notAskMore;
    }

    public void onClickGoodDialogCancel() {

        // barcodeScannerView.resume();
        // barcodeScannerView.setStatusText("");

    }


    ////////CAMERA = @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

    @Override
    protected void onResume() {
        super.onResume();
        //capture.onResume();
        // barcodeScannerView.resume();

        String state = MainApplication.dbHelper.getOption(MainApplication.CAMERA_STATE);
        if (state != null)
            if (state.equals(MainApplication.CAMERA_OFF)) {
                hideCameraButton.setTag("1");
                hideCameraButton.setImageResource(R.drawable.ic_camera_alt_white_36dp);
                // barcodeScannerView.setVisibility(View.GONE);
                // switchFlashlightButton.setVisibility(View.INVISIBLE);
                //capture.onPause();
                // barcodeScannerView.pause();
                MainApplication.dbHelper.insertOrReplaceOption(MainApplication.CAMERA_STATE, MainApplication.CAMERA_OFF);
            }
    }

    @Override
    protected void onPause() {
        Log.d("camr", "onPause");
        super.onPause();
        //capture.onPause();
        //   barcodeScannerView.pause();
    }

    @Override
    protected void onDestroy() {
        Log.d("camr", "kadfsasdf");
        super.onDestroy();
        //capture.onDestroy();
        //  barcodeScannerView.pause();
    }

    /*@Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        capture.onSaveInstanceState(outState);
    }*/

   /* @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d("camr","keyDown");

        return barcodeScannerView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }*/


    public void pause(View view) {
        //barcodeScannerView.pause();
    }

    public void resume(View view) {
        //  barcodeScannerView.resume();
    }

    public void triggerScan(View view) {

        //barcodeScannerView.decodeSingle(callback);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        finish();
        return true;//barcodeScannerView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

    /**
     * Check if the device's camera has a Flashlight.
     *
     * @return true if there is Flashlight, otherwise false.
     */
    private boolean hasFlash() {
        return getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    public void switchFlashlight(View view) {
        if (((String) switchFlashlightButton.getTag()).equals("0")) {
            if (barcodeScannerView != null) barcodeScannerView.setTorchOn();
        } else {
            if (barcodeScannerView != null) barcodeScannerView.setTorchOff();
        }
    }

    @Override
    public void onTorchOn() {

        switchFlashlightButton.setTag("1");

        switchFlashlightButton.setImageResource(R.drawable.ic_highlight_off_white_36dp);

    }

    @Override
    public void onTorchOff() {

        switchFlashlightButton.setTag("0");
        switchFlashlightButton.setImageResource(R.drawable.ic_highlight_white_36dp);
    }


    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if (result.getText() != null) {

                if (dialog != null) dialog.dismiss();
                if(barcodeScannerView!=null) barcodeScannerView.pause();

                //  barcodeScannerView.pause();
                //  barcodeScannerView.setStatusText(result.getText());
                String scanContent = result.getText();
                Log.d("my", scanContent);
                Log.d("my", "finish!!!");

                searchEditText.setText(scanContent);


                if (!scanContent.equals("")) {
                    //	clearfindButton.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.close_32));
                    originGoodListSaver.clear();
                    originGoodListSaver.addAll(goodList);
                    enter = 0;
                    newScan = true;
                    searchEditText.setFocusable(true);
                    //isEnabledFindButton=false;
                    String barc = searchEditText.getText().toString();
                    setGoodsByBarc(barc, barc, barc);

                }
            }


            //Added preview of scanned barcode
            // ImageView imageView = (ImageView) findViewById(R.id.barcodePreview);
            // imageView.setImageBitmap(result.getBitmapWithResultPoints(Color.YELLOW));
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
        }
    };


    Dialog creadeInvDialog;
    EditText idEditText;
    EditText nameEditText;

    String createInvId = "";
    int crInvId = 999000;
    String createInvName = "";
    String currentDateTimeString = "";
    Handler createInvH = new Handler();


    private void selectOrCreateDocument() {
        Log.d("my", "selectOrCreateDocument");

        ArrayList<Inventory> invList = MainApplication.dbHelper.getInventoryList();
        if (invList.size() == 1) {

            Inventory inv = invList.get(0);
            id = inv.getId();
            date = inv.getDatetime();
            subdiv = inv.getSubdivision().getName();
            docType = inv.getDocType();

            Toast.makeText(NewScannerActivity.this, "inv " + id + " " + subdiv,
                    Toast.LENGTH_SHORT).show();

        } else {


            // custom dialog
            creadeInvDialog = new Dialog(this);
            creadeInvDialog.setContentView(R.layout.new_inventory_dialog);
            creadeInvDialog.setTitle(getString(R.string.create_inventory));

            // set the custom dialog components - text, image and button
            idEditText = (EditText) creadeInvDialog.findViewById(R.id.idEditText);
            nameEditText = (EditText) creadeInvDialog.findViewById(R.id.nameEditText);

            Button cancelButton = (Button) creadeInvDialog.findViewById(R.id.cancelBt);
            Button createButton = (Button) creadeInvDialog.findViewById(R.id.createBt);

            //idEditText.setFocusable(false);


            nameEditText.setFocusable(true);
            nameEditText.setSelectAllOnFocus(true);
            if (invList.size() == 0) {
                idEditText.setText("" + 1);
                nameEditText.setText("Market");
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        creadeInvDialog.dismiss();
                    }
                });
            } else {
                // int maxId = MainApplication.dbHelper.getMaxInventoryId()+1;
                // Log.d("my","maxId = "+maxId);
                // idEditText.setText(""+maxId);

                Inventory inv = invList.get(invList.size() - 1);
                id = inv.getId();
                date = inv.getDatetime();
                subdiv = inv.getSubdivision().getName();
                docType = inv.getDocType();

                idEditText.setText("" + id);
                nameEditText.setText(subdiv);

                createButton.setText("OK");
                cancelButton.setText(R.string.inventory_list);

                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        Intent intent = new Intent(NewScannerActivity.this,
                                DockListActivity.class);
                        Bundle b = new Bundle();
                        b.putInt("type", ScannerConstants.INVENTORY);
                        intent.putExtras(b);
                        startActivity(intent);


                        finish();
                    }
                });
            }


            createButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //create inventory...
                    //dialog.dismiss();

                    createInvId = idEditText.getText().toString();
                    createInvName = nameEditText.getText().toString();

                    createInvName += " - offline document";
                    //currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                    DateFormat dateFormatter = new SimpleDateFormat("dd/mm/yyyy hh:mm:ss");
                    dateFormatter.setLenient(false);
                    java.util.Date today = new java.util.Date();
                    currentDateTimeString = dateFormatter.format(today);


                    try {
                        crInvId = Integer.parseInt(createInvId);
                    } catch (Exception e) {
                    }


                    if (createInvId.length() > 0)
                        new Thread() {
                            @Override
                            public void run() {
                                MainApplication.dbHelper.insertInventoryDoc(crInvId, createInvId, currentDateTimeString, createInvName, docType);
                                createInvH.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        id = crInvId;
                                        date = currentDateTimeString;
                                        subdiv = createInvName;

                                        creadeInvDialog.dismiss();

                                        Toast.makeText(NewScannerActivity.this, "inv " + id + " " + subdiv,
                                                Toast.LENGTH_SHORT).show();
                                        // loadDocListOffline();
                                    }
                                });
                            }
                        }.start();
                }
            });

            creadeInvDialog.show();
        }
    }


}
