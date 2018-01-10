package expandablerecyclerview.sample;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;


import com.app.barcodeclient3.R;

import essences.Good;
import expandablerecyclerview.core.ExpandableView;
import json_process.GoodJSON;

/**
 * Created by andriipanasiuk on 19.09.15.
 */
public class SimpleItemView extends FrameLayout implements ExpandableView<SimpleItemView> {



    private GoodJSON good;

    private View mExpandedView;
    private TextView mExpandedContributionsText;
    private TextView mExpandedNameText;

    private View mCollapsedView;
    private TextView mCollapsedNameText;

    TextView barcodeTV;
    TextView unitTV;
    EditText priceTV;
    EditText quantityTV;

    public SimpleItemView(Context context) {
        super(context);
    }

    public SimpleItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        Log.d("Expand", "onFinishInflate for " + this);
        mExpandedView = findViewById(R.id.expanded_container);
        mExpandedNameText = (TextView) findViewById(R.id.expanded_name);
        mExpandedContributionsText = (TextView) findViewById(R.id.contributions_text);
        mCollapsedView = findViewById(R.id.collapsed_container);
        mCollapsedNameText = (TextView) findViewById(R.id.collapsed_name);
        barcodeTV = findViewById(R.id.barcodeTV);
        unitTV = findViewById(R.id.unitTV);
        priceTV = findViewById(R.id.priceTV);
        quantityTV = findViewById(R.id.quantityTV);
    }

  /*  private static String buildExpandedText(Data data) {
        StringBuilder result = new StringBuilder();
        for (String contribution : data.repositories) {
            result.append(contribution);
            result.append("\n");
        }
        result.delete(result.length() - 1, result.length());
        return result.toString();
    }*/

    private static void bindExpanded(GoodJSON good, SimpleItemView view) {
        view.mExpandedContributionsText.setText("...\n...\n");
        view.mExpandedNameText.setText(good.getName());
        String barcode = good.getBarcode();
        if(barcode==null) barcode="";
        view.barcodeTV.setText(barcode);
    }

    public void bind(GoodJSON good, boolean isExpanded, int height) {
        this.good = good;
        if (isExpanded) {
            bindExpanded(good, this);
        } else {
            bindCollapsed(good);
        }
        getLayoutParams().height = height;
    }

    private void bindCollapsed(GoodJSON good) {
        mCollapsedNameText.setText(good.getName());
    }

    @Override
    public View getContainer() {
        return this;
    }

    @Override
    public View getExpandedStateView() {
        return mExpandedView;
    }

    @Override
    public View getCollapsedStateView() {
        return mCollapsedView;
    }

    @Override
    public void bindViews(boolean expanded) {
        if (expanded) {
            bindExpanded(good, this);
        } else {
            bindCollapsed(good);
        }
    }

    @Override
    public void bindExpandedState(SimpleItemView another) {
        another.mExpandedView.setVisibility(VISIBLE);
        another.mCollapsedView.setVisibility(GONE);
        bindExpanded(good, another);
        another.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
    }
}
