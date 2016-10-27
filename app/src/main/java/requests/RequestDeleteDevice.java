package requests;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

import essences.Device;
import startactivity.MainActivity;

public class RequestDeleteDevice {


    Device device=null;



    public void deleteDevice(Device device)
    {

        this.device=device;

        request();


    }
    




	public void request() {
			String  returnString="";
    		String urlStr= MainActivity.mainURL+"/DeleteDeviceServlet";

    		
    		 try{
                 URL url = new URL(urlStr);
                 URLConnection connection = url.openConnection();

                 String inputString ="";// Integer.toString(grp_id);
                 //inputString = URLEncoder.encode(inputString, "UTF-8");
                 JSONObject jsonObject = new JSONObject();

                 jsonObject.put("device_id", device.getId());
                 jsonObject.put("device_name", device.getName());
                 
                 inputString= jsonObject.toString();

               //  Log.d("my", inputString);

                 connection.setDoOutput(true);
                 OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
                 
                 
                 out.write(inputString);
                 out.close();

                 Log.d("my","ok del");

                 BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                 returnString = in.readLine();


                 in.close();


                 }catch(Exception e)
                 {
                     Log.d("my","err="+e.toString());
                 }
    		
    	}

    
    
 
    }