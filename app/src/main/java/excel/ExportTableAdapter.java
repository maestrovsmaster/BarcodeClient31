package excel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.barcodeclient3.R;

import java.util.ArrayList;

import essences.Good;


public class ExportTableAdapter<T> extends ArrayAdapter<ArrayList<String>> {


    private ArrayList<ArrayList<String>> goods;
    Context context;


    public ExportTableAdapter(Context context, int textViewResourceId, ArrayList<ArrayList<String>> objects) {
        super(context, textViewResourceId, objects);
        this.goods = objects;
        this.context = context;
    }


    TextView idEx;


    View v;

    LinearLayout linearLayout;

    @SuppressLint("NewApi")
    public View getView(int position, final View convertView, ViewGroup parent) {

        Log.d("my", "*--");
        v = convertView;


        //if (v == null)

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.row_excel_adapter, null);
        Log.d("my", "*--1");

        linearLayout = (LinearLayout) v.findViewById(R.id.linearLayout);

        if(position>goods.size()) return v;
        Log.d("my", "*--2");
        ArrayList<String> list = goods.get(position);
        if(list==null) return v;
        Log.d("my", "*--3");

        for(String str :list)
        {
            TextView tv = new TextView(context);
            tv.setText(str);
            tv.setTextColor(context.getResources().getColor(R.color.primary_text));

            tv.setWidth(250);
            tv.setLines(1);
            tv.setMaxLines(1);
            tv.setEllipsize(TextUtils.TruncateAt.END);

            linearLayout.addView(tv);
        }

        Log.d("my", "*--!44");



        return v;

    }


}
