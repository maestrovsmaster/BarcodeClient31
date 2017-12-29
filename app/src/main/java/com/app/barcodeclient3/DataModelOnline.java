package com.app.barcodeclient3;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;


import json_process.GoodJSON;
import json_process.InventoryJSON;
import main.MainApplication;

import static main.MainApplication.executeStatement;
import static main.MainApplication.getAppContext;
import static main.MainApplication.h;
import static main.MainApplication.isConnect;

/**
 * Created by MaestroVS on 19.12.2017.
 */

public class DataModelOnline implements DataModelInterface{
    private static  DataModelOnline ourInstance = new DataModelOnline();

    public static DataModelOnline getInstance() {
        return ourInstance;
    }

    @SerializedName("uniqid")
    @Expose
    String str;


    private DataModelOnline() {
    }

    @Override
    public void getInventoryList(@NonNull Callback callback) {

        final Callback call1=callback;

        Thread thread = new Thread(){
            @Override
            public void run() {
                ArrayList<InventoryJSON> inventories = new ArrayList<>();
                if(isConnect()) {
                    Log.d("my","inv get is connect ok");
                    String statement = "select JB.ID, JB.NUM, JB.date_time, JB.DOC_STATE , "
                            + " (select DS.ID        from DIC_SUBDIVISION DS   where DS.ID = JB.subdivision_id) as SUBDIVISION_ID, "
                            + " (select DS.NAME         from DIC_SUBDIVISION DS   where DS.ID = JB.subdivision_id) as SUBDIVISION_NAME "
                            + " from jor_inventory_act JB "
                            + " 	where  JB.subdivision_id is not null "
                            + " order by JB.DATE_TIME ";
                    JSONArray jsonArray = executeStatement(statement);
                    Log.d("my","inv get stm ="+statement);
                    if (jsonArray != null) {
                        Log.d("my","inv list not null size = "+jsonArray.length());
                        Log.d("my", "inv list = " + jsonArray.toString());

                        //[{"RESULT":"2.1.186.0"}]
                        if (jsonArray.length() > 0) {
                           /* try {
                                JSONObject obj = jsonArray.getJSONObject(0);
                                dbver = obj.getString("RESULT");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }*/

                            for(int i=0;i<jsonArray.length();i++){
                                try {
                                    JSONObject obj=jsonArray.getJSONObject(i);
                                    Gson gson = new Gson();
                                    InventoryJSON inventory = gson.fromJson(obj.toString(),InventoryJSON.class);
                                    if(inventory!=null){
                                        Log.d("my","inventory ===+++= ok"+inventory.getSubdivisionName());
                                        inventories.add(inventory);
                                    }
                                    else Log.d("my","inventory =---err");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Log.d("my","inventory =---errtttt"+e.toString());
                                }
                            }

                        }
                    }else{
                        Log.d("my","inv get is jsonArr null");
                    }
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
    public void getDatabaseVersion(@NonNull Callback callback) {

        final Callback call1=callback;

            Thread thread = new Thread() {
                @Override
                public void run() {

                    String dbver = null;
                    if(isConnect()) {
                        String statement = "execute procedure sp_get_version";
                        JSONArray jsonArray = executeStatement(statement);
                        if (jsonArray != null) {

                            Log.d("my", "DB ver = " + jsonArray.toString());

                            //[{"RESULT":"2.1.186.0"}]
                            if (jsonArray.length() > 0) {
                                try {
                                    JSONObject obj = jsonArray.getJSONObject(0);
                                    dbver = obj.getString("result");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            Log.d("my", "DB ver dbver= " + dbver);
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
    public void getGoodsList(Map<String,String> map,@NonNull Callback callback) {

        final Callback call1=callback;

        Thread thread = new Thread(){
            @Override
            public void run() {
                ArrayList<GoodJSON> goodsList = new ArrayList<>();
                if(isConnect()) {
                    Log.d("my","goods get is connect ok");
                    String statement = getAppContext().getString(R.string.request_goods);
                    JSONArray jsonArray = executeStatement(statement);
                    Log.d("my","goods get stm ="+statement);
                    if (jsonArray != null) {
                        Log.d("my","goods list not null size = "+jsonArray.length());
                        Log.d("my", "goods list = " + jsonArray.toString());
                        if (jsonArray.length() > 0) {

                            for(int i=0;i<jsonArray.length();i++){
                                try {
                                    JSONObject obj=jsonArray.getJSONObject(i);
                                    Gson gson = new Gson();
                                    GoodJSON goodJSON = gson.fromJson(obj.toString(),GoodJSON.class);
                                    if(goodJSON!=null){
                                        Log.d("my","goods ===+++= ok"+goodJSON.getName());
                                        goodsList.add(goodJSON);
                                    }
                                    else Log.d("my","goods =---err");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Log.d("my","goods =---errtttt"+e.toString());
                                }
                            }

                        }
                    }else{
                        Log.d("my","goods get is jsonArr null");
                    }
                }
                final Response response = new Response(goodsList);
                Log.d("my","goods send resp...");
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




}
