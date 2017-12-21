package com.app.barcodeclient3;

import android.support.annotation.Nullable;

/**
 * Created by MaestroVS on 19.12.2017.
 */

public class Response<T>  {

    private T body=null;

    public Response(T body){
        this.body=body;
    }


    public  T body() {
        return body;
    }
}
