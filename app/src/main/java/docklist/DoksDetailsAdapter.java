package docklist;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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

import essences.BillInDt;
import essences.Good;
import essences.Inventory;
import essences.JorDtTable;
import main.MainApplication;
import scanworkingactivity.ScanWorkingActivity;


public class DoksDetailsAdapter<T> extends ArrayAdapter<JorDtTable> {


		private ArrayList<JorDtTable> detailsList;


		public DoksDetailsAdapter(Context context, int textViewResourceId, ArrayList<JorDtTable> objects) {
			super(context, textViewResourceId, objects);
			this.detailsList = objects;
		}


		TextView article;
		TextView name;
		TextView unit;
		TextView count;
		TextView price;


		RelativeLayout rowRel;

		
		View v ;

	AlertDialog.Builder adb;
	AlertDialog ad;
	Handler deleteH = new Handler();
	int deletingDockId =-1;

		@SuppressLint("NewApi")
		public View getView(int position, final View convertView, ViewGroup parent){

			Log.d("my", "*--");
			 v = convertView;

			
			//if (v == null)
			{
				LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = inflater.inflate(R.layout.row_doc_details, null);

				rowRel = (RelativeLayout) v.findViewById(R.id.rowRel);
				
				article = (TextView) v.findViewById(R.id.article);
				name = (TextView) v.findViewById(R.id.name);
				unit = (TextView) v.findViewById(R.id.unit);
				count = (TextView) v.findViewById(R.id.count);
				price = (TextView) v.findViewById(R.id.price);




				rowRel.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Log.d("my", "pik");
						Object tag = v.getTag();
						if(tag instanceof Inventory){
						Inventory inv = (Inventory)tag;
						int id = inv.getId();
						String date= inv.getDatetime();
						String subdiv = inv.getSubdivision().getName();
						int docType = inv.getDocType();

						
						Intent intent = new Intent(v.getContext(),	ScanWorkingActivity.class);
					
						Bundle arg = new Bundle();
						arg.putInt("id", id);
						arg.putString("date", date);
						arg.putString("subdiv", subdiv);
						arg.putInt("docType", docType);
						
						intent.putExtras(arg);
						
						v.getContext().startActivity(intent);
						}

					//	detailsList.add(0,new Inventory(0,"num","2000","lala",0));
					//	notifyDataSetChanged();
					}
				});


				name.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {

						Log.d("my", "long click...");

						Object tag = v.getTag();
						if (tag instanceof Inventory) {
							Inventory inv = (Inventory) tag;
							deletingDockId = inv.getId();

							adb = new AlertDialog.Builder(v.getContext());


							//adb.setView(alertDialogView);


							adb.setTitle(v.getContext().getString(R.string.delete_inventory_dialog));


							adb.setIcon(android.R.drawable.ic_dialog_alert);


							adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									new Thread() {
										@Override
										public void run() {
											MainApplication.dbHelper.clearAllForDocID_AC_GOODS_CNT(deletingDockId);
											MainApplication.dbHelper.clear_INVENTORY(deletingDockId);
											deleteH.post(new Runnable() {
												@Override
												public void run() {
													ad.cancel();
												}
											});
										}
									}.start();

								}
							});


							adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {

									ad.cancel();
								}
							});

							ad = adb.create();
							adb.show();

						}

					}
				});


			};


			JorDtTable detail= detailsList.get(position);
			rowRel.setTag(detail);

			if (detail != null) {


				Good good = detail.getGood();

				article.setText(good.getArticle());
				name.setText(good.getName());
				unit.setText(good.getUnit_().getName());
				count.setText(""+detail.getCount());

				if(detail instanceof BillInDt)
				{
					BillInDt billDt = (BillInDt) detail;
					price.setText(""+billDt.getInPrice());

				}




			}


			// the view must be returned to our activity
			return v;

		}
		


}
