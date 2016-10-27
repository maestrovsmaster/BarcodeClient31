package devicesList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.app.barcodeclient3.R;

import java.util.ArrayList;

import essences.Device;
import requests.RequestDeleteDevice;


public class DevicesListAdapter<T> extends ArrayAdapter<Device> {


		private ArrayList<Device> devices;
	Context context;


		public DevicesListAdapter(Context context, int textViewResourceId, ArrayList<Device> objects) {
			super(context, textViewResourceId, objects);
			this.devices = objects;
			this.context=context;
		}


		TextView devName;
		Button deleteButton;


		
		View v ;
		
		@SuppressLint("NewApi")
		public View getView(int position, final View convertView, ViewGroup parent){

			Log.d("my", "*--");
			 v = convertView;

			
			//if (v == null)
			{
				LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = inflater.inflate(R.layout.row_device, null);



				devName = (TextView) v.findViewById(R.id.devName);
				deleteButton = (Button) v.findViewById(R.id.deleteButton);



				deleteButton.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Log.d("my", "pik");
						Device dev  = (Device)v.getTag();

						DeleteDeviceThread delThr = new DeleteDeviceThread(dev);
						delThr.start();


					}
				});

			}


			Device device = devices.get(position);
			deleteButton.setTag(device);

			if (device != null)
				devName.setText(device.getName());

			return v;

		}
		
	class DeleteDeviceThread extends  Thread
	{
		Device dev = null;

		public DeleteDeviceThread(Device dev) {
			this.dev = dev;
		}

		@Override
		public void run() {
			RequestDeleteDevice req = new RequestDeleteDevice();
			req.deleteDevice(dev);
			Log.d("my","del .");

			h.post(new Runnable() {
				@Override
				public void run() {
					Activity activity = (Activity) context;
					activity.finish();
				}
			});
		}
	}

	Handler h = new Handler();

}
