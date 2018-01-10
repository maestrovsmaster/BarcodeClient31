package newmainscanner;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.barcodeclient3.R;

import java.util.ArrayList;
import java.util.List;

import json_process.GoodJSON;

/**
 * Created by MaestroVS on 05.01.2018.
 */

public class GoodsListAdapter<T> extends ArrayAdapter<GoodJSON> {

    ArrayList<GoodJSON> goods = new ArrayList<>();

    public GoodsListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<GoodJSON> objects) {
        super(context, resource, objects);
        this.goods = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View parentView = inflater.inflate(R.layout.row_expandable_item, null);

       /* final TextView mExpandedContributionsText;
        final TextView mExpandedNameText;

        final View mCollapsedView;
        final TextView mCollapsedNameText;

        TextView barcodeTV;
        TextView unitTV;
        EditText priceTV;
        EditText quantityTV;*/

       GoodJSON good = goods.get(position);
       if(good==null) return parentView;
        final com.silencedut.expandablelayout.ExpandableLayout collapsed_parent = parentView.findViewById(R.id.collapsed_parent);
        final RelativeLayout mExpandedView = parentView.findViewById(R.id.expanded_container);
        final TextView mExpandedNameText = parentView.findViewById(R.id.expanded_name);
        final TextView mExpandedContributionsText = (TextView) parentView.findViewById(R.id.contributions_text);
        final RelativeLayout mCollapsedView = parentView.findViewById(R.id.collapsed_container);
        final TextView mCollapsedNameText = (TextView) parentView.findViewById(R.id.collapsed_name);
        final TextView barcodeTV = parentView.findViewById(R.id.barcodeTV);
        final TextView unitTV = parentView.findViewById(R.id.unitTV);
        final EditText priceTV = parentView.findViewById(R.id.priceTV);
        final EditText quantityTV = parentView.findViewById(R.id.quantityTV);

        if(position==0) {
          Log.d("my","position is Expanded = "+collapsed_parent.isExpanded());
          collapsed_parent.setExpand(true);
            collapsed_parent.setExpandWithParentScroll(true);

        }


        mExpandedNameText.setText(good.getName());
        String barcode = good.getBarcode();
        if(barcode==null) barcode="";
        barcodeTV.setText(barcode);

        return parentView;
    }
}
