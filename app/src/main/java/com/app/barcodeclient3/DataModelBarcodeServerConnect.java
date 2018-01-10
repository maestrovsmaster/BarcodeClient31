package com.app.barcodeclient3;

import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

import essences.Inventory;
import json_process.InventoryJSON;
import requests.RequestDBVersion;
import requests.RequestInventoryList;

import static main.MainApplication.executeStatement;
import static main.MainApplication.h;
import static main.MainApplication.isConnect;

/**
 * Created by MaestroVS on 10.01.2018.
 */

public class DataModelBarcodeServerConnect implements DataModelInterface{

    private static  DataModelBarcodeServerConnect ourInstance = new DataModelBarcodeServerConnect();

    public static DataModelBarcodeServerConnect getInstance() {
        return ourInstance;
    }


    @Override
    public void getInventoryList(Callback callback) {

        final Callback call1=callback;

        Thread thread = new Thread(){
            @Override
            public void run() {
                ArrayList<InventoryJSON> inventories = new ArrayList<>();

                    Log.d("my","inv get is connect ok");
                RequestInventoryList requestInventoryList = new RequestInventoryList();
                ArrayList<Inventory> invlst = requestInventoryList.getInventoryList();

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

        Thread thread = new Thread()
        {
            @Override
            public void run() {

                String dbver=null;
                RequestDBVersion requestDBVersion =new RequestDBVersion();
                JSONObject ansJSON = requestDBVersion.getDBVersion();

               if(ansJSON!=null) {
                   try {
                       dbver = ansJSON.getString("ver");
                   } catch (Exception e) {
                   }
               }

                final Response response = new Response(dbver);

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
    public void getGoodsList(Map<String, String> options, Callback callback) {

    }

    @Override
    @Deprecated
    public String createInventory(int id, String num, String datetime, String subdiv, int doc_type) {
        return null;
    }


}
