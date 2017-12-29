package expandablerecyclerview.sample;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.app.barcodeclient3.R;

import expandablerecyclerview.core.ExpandableRecyclerView;


/**
 * Created by andriipanasiuk on 19.09.15.
 */
public class SimpleExpandableRecyclerView extends ExpandableRecyclerView<SimpleItemView,
        SimpleItemView, SimpleItemHolder> {




    @Override
    protected SimpleItemView createMeasurableView(Context context, ViewGroup parent) {

        return (SimpleItemView) LayoutInflater.from(context).inflate(R.layout.expandable_item, parent, false);
    }

    public SimpleExpandableRecyclerView(Context context, AttributeSet set) {
        super(context, set);
    }
}
