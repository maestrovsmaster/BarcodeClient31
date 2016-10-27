package requests;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

import startactivity.MainActivity;

public class RequestScanSettings {
	
	//private static  String mainURL = "http://"+StaticParameterNames.SERVER_IP+":"+StaticParameterNames.SERVER_PORT+"/BarcodeServer3";
 

    
   // ArrayList<Inventory> jsonObject = new ArrayList<>();

    
    JSONObject ansJSON=null;
    String ver="";
    
    
    /**
     *
     * @return
     */
    public JSONObject getScanSettings()
    {

        request();

		return ansJSON;
    }
   



    public void request() {

            String  returnString="";


    		
    		 try{
                 URL url = new URL(MainActivity.mainURL+"/WeigthParamsServlet");
                 URLConnection connection = url.openConnection();
                 
                 JSONObject  jsonObject = new JSONObject();
                 jsonObject.put("device_id", MainActivity.device_id);
                 jsonObject.put("device_name", MainActivity.device_name);


                 String inputString = jsonObject.toString();
                 //inputString = URLEncoder.encode(inputString, "UTF-8");

                 Log.d("my", inputString);

                 connection.setDoOutput(true);
                 OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
                 out.write(inputString);
                 out.close();
                 
               
               
                 BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
              
               
                
                 
                 returnString = in.readLine();
                 
                 JSONObject jsonObj = new JSONObject(returnString);
                 
               
                 Log.d("my","scan settings request = "+jsonObj.toString());
                 
              ansJSON=jsonObj;
                	
                	//int id = obj.getInt("id");
                	//String num = obj.getString("num");
                	//String datetime = obj.getString("datetime");
                	//String subdivision = obj.getString("subdivision");
                	
                	//Inventory inventory = new Inventory(id, num, datetime, subdivision);
                	//jsonObject.add(inventory);
                
                 in.close();
                 


                 }catch(Exception e)
                 {
                     Log.d("my", "err=" + e.toString());
                     ansJSON = new JSONObject();

                     try {
                         ansJSON.put("err",e.toString());
                     }catch (Exception e2){}



                 }
    		

    		
    	}

    
    
 
    }