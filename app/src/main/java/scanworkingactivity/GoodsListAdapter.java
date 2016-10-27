package scanworkingactivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.barcodeclient3.R;

import java.util.ArrayList;

import essences.Good;
import startactivity.MainActivity;


public class GoodsListAdapter<T> extends ArrayAdapter<Good> {


    private ArrayList<Good> goods;
    int docNum;


    public GoodsListAdapter(Context context, int textViewResourceId, ArrayList<Good> objects, int docNum ) {
        super(context, textViewResourceId, objects);
        this.goods = objects;
        this.docNum=docNum;
    }


    TextView goodNameTW;

    TextView goodInfoTW;
    TextView priceTW;
    TextView cntTW;


    RelativeLayout rowRel;


    View v;

    @SuppressLint("NewApi")
    public View getView(int position, final View convertView, ViewGroup parent) {

        Log.d("my", "*--");
        v = convertView;

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.row_good_details, null);

        rowRel = (RelativeLayout) v.findViewById(R.id.rowRel);

        goodNameTW = (TextView) v.findViewById(R.id.goodNameTW);
        if (goodClickListener != null) rowRel.setOnClickListener(goodClickListener);

        goodInfoTW = (TextView) v.findViewById(R.id.goodInfoTW);
        priceTW = (TextView) v.findViewById(R.id.priceTW);
        cntTW = (TextView) v.findViewById(R.id.cntTW);


        Good good = goods.get(position);
        rowRel.setTag(good);

        if (good != null) {
            goodNameTW.setText(good.getName());

            int id = good.getId();
            String unit = good.getUnit();
            String article = good.getArticle();
            String art = getContext().getString(R.string.art);
            String bark= getContext().getString(R.string.barc);
            ArrayList<String> barks = good.getBarcodes();
            String barcode = "";
            if(barks.size()>0) barcode=barks.get(0);
            String cnt="";
            double dcnt = good.getFcnt();
            cnt = Double.toString(dcnt);
            Log.d("my","goods adapter: cnt = "+cnt);


            String price = "";

            if(article.length()>0||bark.length()>0)
            {
                goodInfoTW.setVisibility(View.VISIBLE);
                String info = art + " "+article;
                if(barcode.length()>0) info+=" "+bark+" "+barcode;
                goodInfoTW.setText(info);
            }

            if(price.length()>0)
            {
                priceTW.setText(price+""+"грн");
                priceTW.setVisibility(View.VISIBLE);

            }

            if(cnt.length()>0)
            {
                cntTW.setText(cnt+"\n"+unit);
                cntTW.setVisibility(View.VISIBLE);
            }

        }

        return v;

    }

    OnClickListener goodClickListener = null;

    public OnClickListener getGoodClickListener() {
        return goodClickListener;
    }

    public void setGoodClickListener(OnClickListener goodClickListener) {
        this.goodClickListener = goodClickListener;
    }
}
