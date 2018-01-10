package expandablerecyclerview.sample;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;


import com.app.barcodeclient3.R;

import java.util.List;

import expandablerecyclerview.core.ExpandableAdapter;
import json_process.GoodJSON;

/**
 * Created by andriipanasiuk on 19.09.15.
 */
public class SimpleAdapter extends ExpandableAdapter<SimpleItemView, SimpleItemView, SimpleItemHolder> {

    private List<GoodJSON> mData;

    private Context mContext;

    public SimpleAdapter(Context context) {
        this.mContext = context;
    }

    public void setData(List<GoodJSON> mData) {
        this.mData = mData;
        notifyDataSetChanged();
    }

    @Override
    protected boolean isExpandable(int viewType) {
        return true;
    }

    @Override
    protected SimpleItemHolder onCreateExpandableViewHolder(ViewGroup parent, int viewType) {
        SimpleItemView itemView = (SimpleItemView) LayoutInflater.from(mContext).inflate(R.layout.expandable_item, parent, false);
        return new SimpleItemHolder(itemView);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateUnexpandableViewHolder(ViewGroup parent, int viewType) {
        throw new RuntimeException("Unreachable cause we don't have nonexpandableitems");
    }

    @Override
    protected void onBindExpandableViewHolder(SimpleItemHolder holder, boolean isExpanded, int height, int position) {
        Log.d("my","Simple adapter: getView isExpanded="+isExpanded+" pos = "+position+" h="+height);
       // if(height==0) height=-2;
       // isExpanded=false;

       /* if(position==1){
            height=-2;
            isExpanded=true; //true //true
        }*/

        holder.getView().bind(mData.get(position), isExpanded, height);
    }

    @Override
    protected void onBindUnexpandableViewHolder(RecyclerView.ViewHolder holder, int position) {
        throw new RuntimeException("Unreachable cause we don't have nonexpandableitems");
    }

    @Override
    public int getItemCount() {
       // Log.d("my","Simple adapter: data cnt = "+mData.size());
        return mData.size();
    }
}
