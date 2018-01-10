package com.app.barcodeclient3;

import android.util.Log;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

import essences.Inventory;
import json_process.InventoryJSON;
import main.MainApplication;
import requests.RequestDBVersion;
import requests.RequestInventoryList;

import static main.MainApplication.h;

/**
 * Created by MaestroVS on 10.01.2018.
 */

public class DataModelOffline implements DataModelInterface{

    private static  DataModelOffline ourInstance = new DataModelOffline();

    public static DataModelOffline getInstance() {
        return ourInstance;
    }

    @Override
    public void getInventoryList(Callback callback) {

        final Callback call1=callback;
        Log.d("my","getInventoryList offline...");

        Thread thread = new Thread(){
            @Override
            public void run() {
                ArrayList<InventoryJSON> inventories = new ArrayList<>();

                Log.d("my","inv get is connect ok");
                ArrayList<Inventory> invlst = MainApplication.dbHelper.getInventoryList();
                //RequestInventoryList requestInventoryList = new RequestInventoryList();
                //ArrayList<Inventory> invlst = requestInventoryList.getInventoryList();

                Log.d("my","inv get inv lst ="+invlst);


                for(Inventory inv : invlst){
                    InventoryJSON inventoryJSON = new InventoryJSON(inv.getId(),
                            inv.getNum(),inv.getDatetime(),
                            inv.getSubdivision().getId(),inv.getSubdivision().getName(),inv.getDocState());
                    inventories.add(inventoryJSON);
                }



                final Response response = new Response(inventories);
                Log.d("my","inv send resp...");
                h.post(new Runnable() {
                    @Override
                    public void run() {
                        call1.sendResult(response);
                    }
                });

            }
        };
        thread.start();

    }

    @Override
    public void getDatabaseVersion(Callback callback) {
        final Callback call1=callback;
        final Response response = new Response("offline");
        call1.sendResult(response);


    }

    @Override
    public void getGoodsList(Map<String, String> options, Callback callback) {

    }

    public String createInventory(int id, String num, String datetime, String subdiv, int doc_type ){
       return MainApplication.dbHelper.insertInventoryDoc(id, num, datetime,subdiv , doc_type);
    }
}
