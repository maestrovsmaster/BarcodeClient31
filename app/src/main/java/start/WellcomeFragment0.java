package start;

import android.app.Fragment;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import com.app.barcodeclient3.R;

/**
 * Created by userd088 on 30.03.2016.
 */
public class WellcomeFragment0 extends Fragment {

    LayoutInflater inflater;
    ViewGroup container;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("my", "9999999999");

        this.inflater = inflater;
        this.container = container;
        View	rootView = inflater.inflate(R.layout.wellcome_fragment_0, container, false);

        //   Button bt = (Button) rootView.findViewById(R.id.button);

       RadioButton radioButton1= (RadioButton) rootView.findViewById(R.id.choose1);
        radioButton1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) WellcomeActivity2.autonom=true;
            }
        });

        RadioButton radioButton2= (RadioButton) rootView.findViewById(R.id.choose2);
        radioButton2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) WellcomeActivity2.autonom=false;
            }
        });





        return rootView;
    }



}
