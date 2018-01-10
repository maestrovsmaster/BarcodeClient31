package com.app.barcodeclient3;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import essences.Inventory;
import json_process.InventoryJSON;
import main.MainApplication;
import newmainscanner.NewScannerActivity;

import static main.MainApplication.CONNECT_OFFLINE;
import static main.MainApplication.connectMode;

/**
 * Created by MaestroVS on 19.12.2017.
 */

public class InventoriesListAdapter extends ArrayAdapter {

    ArrayList<InventoryJSON> inventories;


    public InventoriesListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<InventoryJSON> inventories) {
        super(context, resource, inventories);
        this.inventories=inventories;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.row_inventory_document, null);


        TextView numTV = v.findViewById(R.id.num);
        TextView subdivisionVT = v.findViewById(R.id.subdivision);
        TextView date =  v.findViewById(R.id.date);
        ImageView lock = v.findViewById(R.id.lock);
        ImageView delete = v.findViewById(R.id.delete);
        delete.setVisibility(connectMode==CONNECT_OFFLINE?View.VISIBLE:View.GONE);

        final InventoryJSON inventory = inventories.get(position);


        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = inventory.getId();
                 deleteInventory(view.getContext() ,id);
            }
        });



        numTV.setText(inventory.getNum());
        subdivisionVT.setText(inventory.getSubdivisionName());
        date.setText(inventory.getDateTime());

        if(inventory.getDoc_state()==0) lock.setVisibility(View.GONE);
        else lock.setVisibility(View.VISIBLE);

        v.setTag(inventory);

        return v;
    }


    private void deleteInventory(Context _context, int _id)
    {

        final int id = _id;
        final Context context = _context;

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        MainApplication.dbHelper.clear_INVENTORY(id);

                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        if(MainApplication.getAppContext()==null){
            Log.d("my","app context is null!!!");
        }else {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(context.getString(R.string.delete_inventory_dialog)).setPositiveButton("Ok", dialogClickListener)
                    .setNegativeButton(context.getString(R.string.cancel), dialogClickListener).show();
        }
    }
}
