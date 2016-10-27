package startactivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.barcodeclient3.R;

import java.util.ArrayList;

import main.SettingsActivity;


public class MenuItemsListAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<String> navDrawerItems = new ArrayList<>();

	View vew=null;
	View autoriz=null;

	OnClickListener onClickListener=null;


	public MenuItemsListAdapter(Context context){
		this.context = context;

		navDrawerItems.add(context.getString(R.string.inventory));
		//navDrawerItems.add(context.getString(R.string.bill_in));
		//navDrawerItems.add(context.getString(R.string.bill_out));
		//navDrawerItems.add(context.getString(R.string.bill_move));
		//navDrawerItems.add(context.getString(R.string.write_off));
		navDrawerItems.add(context.getString(R.string.offline_mode));
		navDrawerItems.add(context.getString(R.string.settings));
		navDrawerItems.add(context.getString(R.string.about_app));


		Log.d("my","goods drawer adapter");
         
	}

	@Override
	public int getCount() {
		return navDrawerItems.size();
	}

	@Override
	
	public Object getItem(int position) {		
		return navDrawerItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		

            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
           int layoutId=R.layout.row_menu_item;
         convertView = mInflater.inflate(layoutId, null);

         
		ImageView imageView = (ImageView) convertView.findViewById(R.id.image);

		TextView textView = (TextView) convertView.findViewById(R.id.name);

		if(position==1||position==2||position==3||position==4){
			textView.setEnabled(false);//setVisibility(View.GONE);
		}

		String name = navDrawerItems.get(position);

		textView.setText(name);

		Drawable bm = null;
		switch (position)
		{


			case 0:
				bm = context.getResources().getDrawable(R.drawable.ic_view_list_white_36dp);
				break;

			case 1:
				bm = context.getResources().getDrawable(R.drawable.ic_phonelink_off_white_36dp);
				break;
			case 2:
				bm = context.getResources().getDrawable(R.drawable.ic_settings_applications_white_36dp);

				break;

			case 3:
				bm = context.getResources().getDrawable(R.drawable.ic_info_outline_white_36dp);
				break;
		}
		if(bm!=null) imageView.setImageDrawable(bm);
		
		
		return convertView;
		
	}
	
	OnClickListener settingsClick = new OnClickListener() {
		@Override
		public void onClick(View view) {
			Log.d("my", "set1");
			Intent intent = new Intent(context.getApplicationContext(),
					SettingsActivity.class);
			Log.d("my", "set12");
			try {
				context.getApplicationContext().startActivity(intent);
			}catch (Exception e){Log.d("my", "set err "+e.toString());}

			Log.d("my", "set13");
		}
	};
	
	


}
