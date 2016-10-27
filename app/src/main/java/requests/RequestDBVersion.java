package requests;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

import startactivity.MainActivity;

public class RequestDBVersion {
	
	//private static  String mainURL = "http://"+StaticParameterNames.SERVER_IP+":"+StaticParameterNames.SERVER_PORT+"/BarcodeServer3";
 

    
   // ArrayList<Inventory> jsonObject = new ArrayList<>();

    
    JSONObject ansJSON=null;
    String ver="";
    
    
    /**
     *
     * @return
     */
    public JSONObject getDBVersion ()
    {

        request();

		return ansJSON;
    }
   

    public static final int CONNECT_TIMEOUT=15000;


    public void request() {

            String  returnString="";


    		
    		 try{


                 URL url = new URL(MainActivity.mainURL+"/StateServlet");
                 URLConnection connection = url.openConnection();

                 JSONObject  jsonObject = new JSONObject();

                 String inputString = jsonObject.toString();
                 //inputString = URLEncoder.encode(inputString, "UTF-8");

                 Log.d("my", inputString);

                 connection.setDoOutput(true);
                 //connection.setReadTimeout(5000);
                 connection.setConnectTimeout(CONNECT_TIMEOUT);
                 Log.d("my", "buffer read in...............3-1");
                 OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
                 Log.d("my","buffer read in...............3-2");
                 out.write(inputString);

                 out.close();

                 BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                 
                 returnString = in.readLine();
                 
                 JSONObject jsonObj = new JSONObject(returnString);
                 
               
                 Log.d("my","===ver=="+jsonObj.toString());
                 
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