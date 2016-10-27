package devicesList;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.app.barcodeclient3.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import essences.Device;

/**
 * Created by userd088 on 23.10.2015.
 */
public class DevicesListActivity extends ListActivity {

    String devList="";

    boolean wrong_date=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setContentView(R.layout.activity_devices_list);

        Intent iin = getIntent();

        Bundle bundle =  iin.getExtras();

        try {
             devList =  bundle.getString("list");

        } catch (Exception e) {
            Log.d("my", " no id =((( ");
        }

        TextView message = (TextView) findViewById(R.id.message);




            Log.d("my","list="+devList);

        if(devList.contains("wrong_date")) wrong_date=true; // Oshibka nesovpadeniya dat

        ArrayList<Device> devListStr = new ArrayList<>();

        try{
            JSONArray devListArray = new JSONArray(devList);

            for(int i=0;i<devListArray.length();i++)
            {
                if(devListArray.get(i) instanceof JSONObject)
                {
                    JSONObject jsObj = (JSONObject) devListArray.get(i);

                    try{
                        String device_id = jsObj.getString("device_id");
                        String device_name = jsObj.getString("device_name");

                        Log.d("my","LIST = "+device_name+" "+device_id);

                        devListStr.add(new Device(device_id, device_name));


                    }catch (Exception e)
                    {

                    }
                }
            }



        }catch (Exception e){}

        if(wrong_date){
            message.setText("Период срока действия лицензии не прошел проверку!");
        }
        else message.setText("Максимальное кол-во устройств по Вашей лицензии = "+devListStr.size()+"\n\r"+" Чтобы активировать текущее андроид-устройство,выберите из" +
                " списка телефон, который следует удалить.");

       DevicesListAdapter<String> adapter = new DevicesListAdapter<String>(this, R.layout.row_device, devListStr);
        setListAdapter(adapter);


        super.onCreate(savedInstanceState);
    }
}
