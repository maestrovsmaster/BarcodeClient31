package com.app.barcodeclient3;

import android.os.Handler;
import android.os.Message;

/**
 * Created by MaestroVS on 19.12.2017.
 */

public interface Callback<T>  {

     boolean sendResult(Response<T> response);
}
