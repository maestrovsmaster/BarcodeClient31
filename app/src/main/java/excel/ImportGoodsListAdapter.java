package excel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.app.barcodeclient3.R;

import java.util.ArrayList;

import essences.Good;


public class ImportGoodsListAdapter<T> extends ArrayAdapter<Good> {


    private ArrayList<Good> goods;
    Context context;


    public ImportGoodsListAdapter(Context context, int textViewResourceId, ArrayList<Good> objects) {
        super(context, textViewResourceId, objects);
        this.goods = objects;
        this.context = context;
    }


    TextView idEx;
    TextView name;
    TextView article;
    TextView unit;
    TextView barcode;


    View v;

    @SuppressLint("NewApi")
    public View getView(int position, final View convertView, ViewGroup parent) {

        Log.d("my", "*--");
        v = convertView;


        //if (v == null)

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.row_excel_good, null);


        idEx = (TextView) v.findViewById(R.id.idEx);
        name = (TextView) v.findViewById(R.id.nameEx);
        article = (TextView) v.findViewById(R.id.articleEx);
        unit = (TextView) v.findViewById(R.id.unitEx);
        barcode = (TextView) v.findViewById(R.id.barcodeEx);


     int id = goods.get(position).getId();
        Log.d("my","id = "+id);
        String sid= ""+id;
//        idEx.setText(goods.get(position).getId());
        name.setText(goods.get(position).getName());
        article.setText(goods.get(position).getArticle());
        unit.setText(goods.get(position).getUnit());
        ArrayList<String> barcs = goods.get(position).getBarcodes();
        String barc="";
        if(barcs.size()>0) barc = barcs.get(0);
        barcode.setText(barc);



        return v;

    }


}
