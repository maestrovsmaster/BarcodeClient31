package docklist;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import essences.BillIn;
import essences.Inventory;
import essences.JorHeadTable;
import main.MainApplication;
import newmainscanner.NewScannerActivity;


public class DoksListAdapter<T> extends ArrayAdapter<JorHeadTable> {

	
		private ArrayList<JorHeadTable> inventories;

	DockListActivity context;

		public DoksListAdapter(DockListActivity context, int textViewResourceId, ArrayList<JorHeadTable> objects) {
			super(context, textViewResourceId, objects);
			this.context=context;
			this.inventories = objects;
		}


		TextView numTV;
		TextView dateTV;
		TextView subdivTV;
		RelativeLayout rowRel;

		
		View v ;
		
		@SuppressLint("NewApi")
		public View getView(int position, final View convertView, ViewGroup parent){

			//Log.d("my", "*--");
			 v = convertView;

			
			//if (v == null)
			{
				LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = inflater.inflate(R.layout.row_doc, null);

				rowRel = (RelativeLayout) v.findViewById(R.id.rowRel);
				
				numTV = (TextView) v.findViewById(R.id.message);
				dateTV = (TextView) v.findViewById(R.id.date);
				subdivTV = (TextView) v.findViewById(R.id.subdiv);


				rowRel.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Log.d("my", "pik");
						Object tag = v.getTag();
						if(tag instanceof Inventory){
							Log.d("my", "pik Inventory");
						Inventory inv = (Inventory)tag;
						int id = inv.getId();
						String date= inv.getDatetime();
						String subdiv = inv.getSubdivision().getName();
						int docType = inv.getDocType();

						
						//Intent intent = new Intent(v.getContext(),	ScanWorkingActivity.class);//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
							Intent intent = new Intent(v.getContext(), NewScannerActivity.class);
					
						Bundle arg = new Bundle();
						arg.putInt("id", id);
						arg.putString("date", date);
						arg.putString("subdiv", subdiv);
						arg.putInt("docType", docType);
						
						intent.putExtras(arg);
							context.startActivityForResult(intent, MainApplication.UPDATE_STATE);
						}

						if(tag instanceof BillIn){
							Log.d("my", "pik Bill In");
							BillIn inv = (BillIn)tag;
							int id = inv.getId();
							String date= inv.getDatetime();
							String subdiv = inv.getSubdivision().getName();
							String num = inv.getNum();
							int docType = Inventory.BILL_IN;


							Intent intent = new Intent(v.getContext(),	DockDetailsActivity.class);

							Bundle arg = new Bundle();
							arg.putInt("hd_id", id);
							arg.putString("date", date);
							arg.putString("num", num);
							arg.putString("subdiv", subdiv);
							arg.putInt("docType", docType);

							intent.putExtras(arg);

							v.getContext().startActivity(intent);
						}



					//	inventories.add(0,new Inventory(0,"num","2000","lala",0));
					//	notifyDataSetChanged();
					}
				});
				


				
			}


			JorHeadTable inv = inventories.get(position);
			rowRel.setTag(inv);

			if (inv != null) {



				Log.d("my", "my num = " + inv.getNum());
				numTV.setText(inv.getNum());

				dateTV.setText(inv.getDatetime());

				subdivTV.setText(inv.getSubdivision().getName());

				int docState = inv.getDocState();
				if(docState==1)subdivTV.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_lock_grey600_24dp, 0);




			}


			// the view must be returned to our activity
			return v;

		}
		


}
