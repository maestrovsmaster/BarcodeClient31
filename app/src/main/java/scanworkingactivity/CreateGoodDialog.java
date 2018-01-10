package scanworkingactivity;


import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.app.barcodeclient3.R;

import java.util.ArrayList;

import adapter.CustomizedSpinnerAdapter;
import main.MainApplication;
import newmainscanner.NewScannerActivity;
import oldbarcodestartactivity.MainActivity;

/**
 * Created by userd088 on 20.07.2016.
 */
public class CreateGoodDialog extends DialogFragment  {

    final String LOG_TAG = "myLogs";

    Spinner unitSpinner;
    EditText editUnit;
    Button okUnitBt;
    CustomizedSpinnerAdapter adapter1;
    EditText barcodeET;
    EditText nameEt;
    Button createGoodBt;
    CheckBox notAskMore;

    ArrayList<String> unitsList = new ArrayList<>();

    String good="";

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow()
                .setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().setTitle(getString(R.string.create_new_good));

        good = getArguments().getString("good");


        View v = inflater.inflate(R.layout.dialog_create_good, null);
        unitSpinner= (Spinner) v.findViewById(R.id.unitSpinner);

        editUnit = (EditText)  v.findViewById(R.id.editUnit);
        okUnitBt = (Button)  v.findViewById(R.id.okUnitBt);

        okUnitBt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditUnit(false);

                String unit = editUnit.getText().toString();
                unit.trim();
                if(unit.length()>0)
                {
                    Log.d("my","unit = "+unit);

                    MainApplication.dbHelper.getOrInsertUnitFromDB(unit);

                   // ArrayList<String> unitsList = MainActivity.dbHelper.getUnits();
                  //  Log.d("my","units list = "+unitsList.toString());
                }


                unitsList = MainApplication.dbHelper.getUnits();

                String[] data = new String[unitsList.size()];
                data=unitsList.toArray(data);
                adapter1 = new CustomizedSpinnerAdapter(
                        CreateGoodDialog.this.getActivity(), android.R.layout.simple_spinner_item,
                        data);

                unitSpinner.setAdapter(adapter1);
                int position = unitsList.size()-1;
                if(position>=0)  unitSpinner.setSelection(position);
                unitSpinner.refreshDrawableState();

            }
        });

        barcodeET = (EditText)  v.findViewById(R.id.barcodeET);
        nameEt = (EditText) v.findViewById(R.id.nameET);

        Log.d("my","GOOD = "+good);
        try {
            Double.parseDouble(good);
            barcodeET.setText(good );
           // barcodeET.setFocusable(false);
            nameEt.setFocusable(true);

        }catch (Exception e){
            nameEt.setText(good);
            barcodeET.setFocusable(true);
            //nameEt.setFocusable(false);
        }


       /* ArrayList<String> data0 = new ArrayList<>();
        data0.add("kg");
        data0.add("litr");
        data0.add("st");*/
        unitsList =MainApplication.dbHelper.getUnits();
        if(unitsList.size()==0){


            MainApplication.dbHelper.getOrInsertUnitFromDB(getString(R.string.sht));
            MainApplication.dbHelper.getOrInsertUnitFromDB(getString(R.string.kg));
            MainApplication.dbHelper.getOrInsertUnitFromDB(getString(R.string.but));
            unitsList = MainApplication.dbHelper.getUnits();
        }



        String[] data = new String[unitsList.size()];
        data=unitsList.toArray(data);
        adapter1 = new CustomizedSpinnerAdapter(
                CreateGoodDialog.this.getActivity(), android.R.layout.simple_spinner_item,
                data);
      /*  adapter1.setOnItemClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/
        unitSpinner.setAdapter(adapter1);

        unitSpinner.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {


                showEditUnit(true);
                return false;
            }
        });


        unitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                showEditUnit(false);
                Log.d("my","select 1");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d("my","select 0");
            }
        });


/*        unitSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showEditUnit(false);
            }
        });*/

        Button cancelGood = (Button) v.findViewById(R.id.cancelGood);
        cancelGood.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                NewScannerActivity sw = (NewScannerActivity) getActivity();
                sw.onClickGoodDialogCancel();
                dismiss();
            }
        });

        //

        createGoodBt = (Button)v.findViewById(R.id.createGood);
        createGoodBt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String barc = barcodeET.getText().toString().trim();
                String name = nameEt.getText().toString();
                String unit = unitSpinner.getSelectedItem().toString();

                int maxId = MainApplication.dbHelper.getMaxGoodId();
                Log.d("my","maxGoodId = "+maxId);
                int id=MainActivity.GOOD_ID_CONST+maxId+1;
                int grpId=MainActivity.GRP_OFFLINE_GOODS;

                if(name.length()==0) name="__";

                MainApplication.dbHelper.writeGood(id,grpId, name, barc, unit , barc ,0);

                NewScannerActivity sw = (NewScannerActivity) getActivity();
                boolean notAsk = notAskMore.isChecked();

                String ns="0";
                if(notAsk) ns="1";
                MainApplication.dbHelper.insertOrReplaceOption(MainActivity.not_ask_create_good,ns);

                sw.onClickGoodDialog(name,notAsk);
                dismiss();
            }
        });

        notAskMore = (CheckBox) v.findViewById(R.id.notAskMore);


        return v;
    }

    private void showEditUnit(boolean edit)
    {
        if(edit)
        {
            unitSpinner.setVisibility(View.GONE);
            editUnit.setVisibility(View.VISIBLE);
            okUnitBt.setVisibility(View.VISIBLE);
            //adapter1.setOnItemClickListener(adapterClickListener);
        }else{
            unitSpinner.setVisibility(View.VISIBLE);
            editUnit.setVisibility(View.GONE);
            okUnitBt.setVisibility(View.GONE);
            //adapter1.setOnItemClickListener(null);
        }
    }

    View.OnClickListener adapterClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            TextView tv = (TextView) v;
            String str = tv.getText().toString();
            editUnit.setText(str);
        }
    };


   /* public void onClick(View v) {
        Log.d(LOG_TAG, "Dialog 1: " + ((Button) v).getText());
        dismiss();
    }

    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        Log.d(LOG_TAG, "Dialog 1: onDismiss");
    }

    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        Log.d(LOG_TAG, "Dialog 1: onCancel");
    }*/
}