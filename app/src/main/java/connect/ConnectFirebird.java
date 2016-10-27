package connect;

import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;



public class ConnectFirebird implements Firebird{
	
	private  static Connection con = null;
    private static ConnectFirebird connectDB = null;
    
    private static String errorMsg="";
    
    
    public static ConnectFirebird getConnectFirebird(String driver, String url, String login, String pass, String charset) {
		 if (connectDB == null) {
	            synchronized (ConnectFirebird.class) {
	                if (connectDB == null) {
	                	connectDB= new ConnectFirebird(driver,url,login,pass,charset);
	                }
	            }
	        }
	        return connectDB;
		
	}
    
    
    
    @SuppressWarnings("unused")
	private ConnectFirebird() {}

    ConnectFirebird(String driver, String url, String login, String pass, String charset)
    {
		Log.d("my", "create singleton of connectDB...");
    	errorMsg="";
        try
        {
            Class.forName(driver);
            
            Properties connParam = new Properties();
            connParam.put("user",login);
            connParam.put("password",pass);
           // connParam.put("lc_ctype", charset);
         
            con = DriverManager.getConnection(url, connParam);
            if(con == null){
				Log.d("my","con = null");
			}
            
        } catch (ClassNotFoundException ex)
        {
			Log.d("my", "KFDB.Cannot find this db driver classes.");
            ex.printStackTrace();
            errorMsg=ex.toString();
        } catch (SQLException e)
        {
			Log.d("my", "KFDB.Cannot connect to this db.");
           e.printStackTrace();
           errorMsg=e.toString();
        }         
    }

	/**
	 * �������� ������ �� ������� �� ����
	 */
	public ArrayList<ArrayList<Object>> getNomen(String query) {
		ArrayList<ArrayList<Object>> retList = new ArrayList<ArrayList<Object>>();
        try
        {
           if(con == null){
			   Log.d("my", "con = null");
        	   return null;
           }
        	Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(query);
            ResultSetMetaData rsmd = rs.getMetaData();
            int cols = rsmd.getColumnCount();
            Properties connInfo=new Properties();
           // connInfo.put("charSet", "UNICODE_FSS");
            
            
            
			while (rs.next())
			{
				ArrayList<Object> newRow = new ArrayList<Object>();
				for (int i = 1; i <= cols; i++)
				{
					newRow.add(rs.getObject(i));
					
					 
				}
				retList.add(newRow);
				for(int j=0;j<listeners.size();j++)
				{
				ConnectListener listener = (ConnectListener)listeners.get(j);
	               listener.setSelectedRow(newRow);
				}
			}
			rs.close();
            st.close();
            
            
            
         } catch (SQLException e)
         {
			 Log.d("my", "KFDB.There are problems with the query ******" + query);
            e.printStackTrace();
         }    
        
        for(int j=0;j<listeners.size();j++)
		{
		ConnectListener listener = (ConnectListener)listeners.get(j);
           listener.setSelectedRow(null);
		}
        
    	return retList;
	}
	
	/**
	 * ��������� ������ � ������ � ���������� ������������ ����������
	 * @param query
	 */
	public void getFireNomen(String query)
	{
		FireNomen fn = new FireNomen(query);
		fn.start();
	}
	
	class FireNomen extends Thread
	{
		String query="";
		
		public FireNomen(String query) {
			this.query=query;
		}
		
		public void run()
		{
			 try
		        {
		           if(con == null)  System.out.println("connect = null");
		        	Statement st = con.createStatement();
		            ResultSet rs = st.executeQuery(query);
		            ResultSetMetaData rsmd = rs.getMetaData();
		            int cols = rsmd.getColumnCount();
		            Properties connInfo=new Properties();
		            connInfo.put("charSet", "UNICODE_FSS");
					while (rs.next())
					{
						ArrayList<Object> newRow = new ArrayList<Object>();
						for (int i = 1; i <= cols; i++)
						{
							newRow.add(rs.getObject(i));
						}
						for(int j=0;j<listeners.size();j++)
						{
						ConnectListener listener = (ConnectListener)listeners.get(j);
			               listener.setSelectedRow(newRow);
						}
						
					}
					rs.close();
		            st.close();
		         } catch (SQLException e)
		         {
					 Log.d("my", "KFDB.There are problems with the query ******" + query);
		            e.printStackTrace();
		         }    	
		}
	}
	

	/**
	 * ��������� ������
	 */
	public String setNomen(String query) {
			try {
    		
			Statement st = con.createStatement();
			int q = st.executeUpdate(query);
			String msg="";
			msg = Integer.toString(q);
			return msg;
			
			} catch (SQLException e) {
				Log.d("my", "KFDB.There are problems with the query ****** " + query);
				e.printStackTrace();
				String err =e.getMessage();
				return err;
			}
	}
	
	private ArrayList listeners = new ArrayList();
	 
    public void addListener(ConnectListener listener){
         this.listeners.add(listener);
    }



	public static String getErrorMsg() {
		return errorMsg;
	}
    
    
	

}//end of class
