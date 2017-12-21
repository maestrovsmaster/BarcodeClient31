package com.app.barcodeclient3;





import android.support.annotation.NonNull;

import java.util.ArrayList;

import essences.Inventory;

/**
 * Created by MaestroVS on 19.12.2017.
 */

public interface DataModelInterface {

    void getInventoryList(Callback callback);

    void getDatabaseVersion(Callback callback);
}
