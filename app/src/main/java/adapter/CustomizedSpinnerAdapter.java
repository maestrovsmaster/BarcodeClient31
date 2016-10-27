package adapter;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.app.barcodeclient3.R;

/**
 * Created by userd088 on 20.07.2016.
 */
public class CustomizedSpinnerAdapter extends ArrayAdapter<String> {

    private Activity context;
    String[] data = null;

    public CustomizedSpinnerAdapter(Activity context, int resource, String[] data2)
    {
        super(context, resource, data2);
        this.context = context;
        this.data = data2;
    }


    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent)
    {
        View row = convertView;
        if(row == null)
        {
            //inflate your customlayout for the textview
            LayoutInflater inflater = context.getLayoutInflater();
            row = inflater.inflate(R.layout.spinner_layout, parent, false);
        }

        //put the data in it
        String item = data[position];
        if(item != null)
        {
            TextView text1 = (TextView) row.findViewById(R.id.rowText);
            text1.setTextColor(Color.BLUE);
            text1.setText(item);
            //if(onItemClickListener!=null)
               // text1.setOnClickListener(onItemClickListener);
        }

        return row;
    }

    View.OnClickListener onItemClickListener;

    public void setOnItemClickListener(View.OnClickListener onItemClickListener)
    {
       this.onItemClickListener=onItemClickListener;
    }

}