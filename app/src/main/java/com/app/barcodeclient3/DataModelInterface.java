package com.app.barcodeclient3;





import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Map;

import essences.Inventory;

/**
 * Created by MaestroVS on 19.12.2017.
 */

public interface DataModelInterface {

    String NAME="name";
    String ARTICLE="article";
    String BARCODE="barcode";

    void getInventoryList(Callback callback);

    void getDatabaseVersion(Callback callback);

    /**
     *
     * @param options  Search by: 'name','article','barcode'
     * @param callback
     */
    void getGoodsList(Map<String, String> options, Callback callback);

    String createInventory(int id, String num, String datetime, String subdiv, int doc_type );
}
