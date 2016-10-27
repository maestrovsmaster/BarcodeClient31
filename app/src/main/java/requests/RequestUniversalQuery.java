package requests;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import essences.GoodGRP;
import startactivity.MainActivity;

public class RequestUniversalQuery {
	
	//private static  String mainURL = "http://"+StaticParameterNames.SERVER_IP+":"+StaticParameterNames.SERVER_PORT+"/BarcodeServer3";


    String query="";
    JSONArray jsonArray =null;

    public JSONArray getQuery(String query)
    {
    	this.query=query;

        request();
    	
		return jsonArray;
    }
   
    


    	public void request() {

            String  returnString="";
    		
    		 try{
                 URL url = new URL(MainActivity.mainURL+"/UniversalQueryServlet");
                 URLConnection connection = url.openConnection();
                 Log.d("my", query);

                 JSONObject  jsonObject = new JSONObject();
                 jsonObject.put("query", query);

                 jsonObject.put("device_id", MainActivity.device_id);
                 jsonObject.put("device_name", MainActivity.device_name);

                 String inputString = jsonObject.toString();

                 connection.setDoOutput(true);
                 OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
                 out.write(inputString);
                 out.close();
               
                 BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
              

                 
                 returnString = in.readLine();
                 Log.d("my","return String=== "+returnString);
                 
                  jsonArray = new JSONArray(returnString);


                 
               
                 //Log.d("my","==="+jsonArray.length());

                // Log.d("my","==="+jsonArray.toString());
                 

                
                 in.close();
                 


                 }catch(Exception e)
                 {
                     Log.d("my","err="+e.toString());
                 }
    		

    		
    	}

    
    
 
    }