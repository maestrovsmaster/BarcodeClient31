package newmainscanner;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.app.barcodeclient3.Callback;
import com.app.barcodeclient3.MainActivity;
import com.app.barcodeclient3.R;
import com.app.barcodeclient3.Response;
import com.app.barcodeclient3.SettingsActivity;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import essences.Good;
import expandablerecyclerview.sample.SimpleAdapter;
import expandablerecyclerview.sample.SimpleExpandableRecyclerView;
import expandablerecyclerview.sample.SimpleItemView;
import json_process.GoodJSON;
import main.MainApplication;

import static com.app.barcodeclient3.DataModelInterface.ARTICLE;
import static com.app.barcodeclient3.DataModelInterface.BARCODE;
import static com.app.barcodeclient3.DataModelInterface.NAME;
import static main.MainApplication.SETTINGS_STATE;

/**
 * Created by MaestroVS on 10.11.2017.
 */

public class ScannerOfflineActivity extends AppCompatActivity implements
        DecoratedBarcodeView.TorchListener {


    @BindView(R.id.searchBt) ImageView searchBt;
    @BindView(R.id.searchEt)EditText searchEt;
    @BindView(R.id.list)ListView mList;
    @BindView(R.id.cameraBt)ImageView cameraBt;
    @BindView(R.id.checkArticle)CheckBox checkArticle;
    @BindView(R.id.progress) ProgressBar progress;
    @BindView(R.id.resultLb)TextView resultLb;

    private int inv_id = -1;
    private String inv_subdiv = "";
    private String inv_date = "";
    private String inv_num="";
    private int inv_subdiv_id=-1;

    ImageView statusButton;
    ImageView settingsButton;
    TextView titleBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_offline_scanner);

        ButterKnife.bind(this);

        /*Window window = getWindow();
// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
// finally change the color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.primary_dark));
        }*/

        //----
        getSupportActionBar().setDisplayOptions(getSupportActionBar().getDisplayOptions() | ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

        getSupportActionBar().setCustomView(R.layout.action_bar_main);
        View abarView = getSupportActionBar().getCustomView();


        titleBar =  abarView.findViewById(R.id.abarTitle);
        statusButton = abarView.findViewById(R.id.statusButton);
        statusButton.setVisibility(View.GONE);
        progress.setVisibility(View.GONE);
        settingsButton =  abarView.findViewById(R.id.settingsButton);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settingsButton.setImageDrawable(getResources().getDrawable(R.drawable.tint_ic_more,null));
        }else{
            settingsButton.setBackground( getResources().getDrawable(R.drawable.tint_ic_more));
        }
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //!!!!!!!!!!!!!!!!!!!!!!!!!!!!! BACK ICON!!!!!!!!!!!!!!!!!
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setIcon(android.R.color.transparent);


        final Toolbar parent = (Toolbar) abarView.getParent();
        parent.setPadding(0, 0, 0, 0);//for tab otherwise give space in tab
        parent.setContentInsetsAbsolute(0, 0);

        //--------------


        Intent iin = getIntent();
        Bundle bundle = iin.getExtras();


        try {
            inv_id =  bundle.getInt("id");
            inv_subdiv =  bundle.getString("name");
            inv_date = bundle.getString("date");
            inv_num = bundle.getString("num");
            inv_subdiv_id = bundle.getInt("subdiv_id");

        } catch (Exception e) {
            // TODO: handle exception
            Log.d("my","e err ="+e.toString());
            finish();
        }

        String text = ""+getString(R.string.inventory)+" "+inv_num+" , "+inv_subdiv+"\n"+inv_date;
        titleBar.setTextColor(getResources().getColor(R.color.secondary_text));
        titleBar.setText(text);

        cameraBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setTag("0");
                startCamera();
            }
        });


        searchBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("my","search CLICK!");
                String str = searchEt.getText().toString();
                boolean art = checkArticle.isChecked();
                if(str==null) str="";
                if(str.trim().length()==0) return;
                progress.setVisibility(View.VISIBLE);
                hideKeyboard();
                searchGood(str,art);
            }
        });

       // SimpleAdapter adapter = new SimpleAdapter(this);
       // adapter.setData(buildData());
       // mList.setAdapter(adapter);

        //searchGood("масло",false);

    }



    ArrayList<GoodJSON> goodsList=new ArrayList<>();

    private void searchGood(String str, boolean article)
    {
        Map<String, String> data = new HashMap<>();
        if(article){
            data.put(ARTICLE, str);
        }else{
            data.put(NAME, str);
            data.put(BARCODE, str);
        }


        MainApplication.getApi().getGoodsList(data, new Callback<ArrayList<GoodJSON>>() {
            @Override
            public boolean sendResult(Response<ArrayList<GoodJSON>> response) {

                progress.setVisibility(View.GONE);
                goodsList.clear();

                ArrayList<GoodJSON> list =response.body();
                if(list==null) return false;

                Log.d("my","we get goods list !!!! "+list.size());
                resultLb.setText("Найдено товаров: "+list.size());

                GoodJSON good = new GoodJSON();
                good.setBarcode("--------------");
               // goodsList.add(good);

                goodsList.addAll(list);


                 //SimpleAdapter adapter = new SimpleAdapter(ScannerOfflineActivity.this);
                // adapter.setData(goodsList);

                GoodsListAdapter adapter = new GoodsListAdapter(ScannerOfflineActivity.this, R.layout.row_expandable_item , goodsList);
                 mList.setAdapter(adapter);

                return false;
            }
        });
    }






    Dialog dialog;
    private DecoratedBarcodeView barcodeScannerView;
    private ImageView switchFlashlightButton;
    private ImageView closeBt;

    private void showCamera() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View view = getLayoutInflater().inflate(R.layout.dialog_camera, null);
        switchFlashlightButton = view.findViewById(R.id.switch_flashlight);
        closeBt = view.findViewById(R.id.closeBt);
        closeBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        switchFlashlightButton.setTag("0");
        switchFlashlightButton.setImageResource(R.drawable.ic_highlight_white_36dp);
        if (!hasFlash()) {
            switchFlashlightButton.setVisibility(View.GONE);
        }

        barcodeScannerView = (DecoratedBarcodeView) view.findViewById(R.id.zxing_barcode_scanner);
        barcodeScannerView.setTorchListener(this);
        barcodeScannerView.decodeContinuous(callback);
        barcodeScannerView.setVisibility(View.VISIBLE);
        barcodeScannerView.resume();
        barcodeScannerView.setStatusText("");

        builder.setView(view);
       /* builder.setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialog.dismiss();
            }
        });*/

        dialog = builder.create();
        dialog.show();
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
                if (permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    //  exportInvToXLS1();
                }

            }
        }
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

              /*  searchEditText.setText(scanContent);


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

                }*/
            }


            //Added preview of scanned barcode
            // ImageView imageView = (ImageView) findViewById(R.id.barcodePreview);
            // imageView.setImageBitmap(result.getBitmapWithResultPoints(Color.YELLOW));
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
        }
    };

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






    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:

                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", 11114);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
                return true;
        }


        return super.onOptionsItemSelected(item);

    }

    private void hideKeyboard()
    {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
