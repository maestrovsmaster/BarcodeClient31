package start;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.barcodeclient3.R;

/**
 * Created by userd088 on 30.03.2016.
 */
public class BluetoothFragment4 extends Fragment {

    LayoutInflater inflater;
    ViewGroup container;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("my", "9999999999");

        this.inflater = inflater;
        this.container = container;
        View	rootView = inflater.inflate(R.layout.wellcome_fragment_4, container, false);

        //   Button bt = (Button) rootView.findViewById(R.id.button);

        return rootView;
    }



}
