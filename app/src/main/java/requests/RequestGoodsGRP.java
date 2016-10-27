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

public class RequestGoodsGRP {
	
	//private static  String mainURL = "http://"+StaticParameterNames.SERVER_IP+":"+StaticParameterNames.SERVER_PORT+"/BarcodeServer3";
 
    EditText inputValue=null;
    Integer doubledValue =0;
    Button doubleMe;
 
    

    
    ArrayList<GoodGRP> goodsGRPList = new ArrayList<GoodGRP>();
    
    int grp_id=-1;
    
    public ArrayList<GoodGRP> getGoodsGRPList(int grp_id)
    {
    	goodsGRPList.clear();

    	this.grp_id=grp_id;

        request();
    	
		return goodsGRPList;
    }
   
    


    	public void request() {

            String  returnString="";
    		
    		 try{
                 URL url = new URL(MainActivity.mainURL+"/GoodsGRPListServlet");
                 URLConnection connection = url.openConnection();

                 String inputString = Integer.toString(grp_id);
                 //inputString = URLEncoder.encode(inputString, "UTF-8");

                 Log.d("my", inputString);

                 connection.setDoOutput(true);
                 OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
                 out.write(inputString);
                 out.close();
                 
               
               
                 BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
              
               
                 doubledValue =0;
                 
                 returnString = in.readLine();
                 
                 JSONArray jsonArray = new JSONArray(returnString);
                 
               
                 Log.d("my","==="+jsonArray.length());
                 
                for(int i=0;i<jsonArray.length();i++)
                {
                	JSONObject obj = jsonArray.getJSONObject(i);
                	
                	int id = obj.getInt("id");
                	int grp_id = obj.getInt("grp_id");
                	String name = obj.getString("name");
                	
                	GoodGRP goodGRP = new GoodGRP(id, grp_id, name);
                	goodsGRPList.add(goodGRP);
                }
                
                 in.close();
                 


                 }catch(Exception e)
                 {
                     Log.d("my","err="+e.toString());
                 }
    		

    		
    	}

    
    
 
    }