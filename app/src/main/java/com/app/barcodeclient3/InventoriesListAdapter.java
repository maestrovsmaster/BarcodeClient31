package com.app.barcodeclient3;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

        InventoryJSON inventory = inventories.get(position);

        numTV.setText(inventory.getNum());
        subdivisionVT.setText(inventory.getSubdivisionName());
        date.setText(inventory.getDateTime());

        if(inventory.getDoc_state()==0) lock.setVisibility(View.GONE);
        else lock.setVisibility(View.VISIBLE);

        v.setTag(inventory);

        return v;
    }
}
