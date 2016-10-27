package connect;

import java.util.ArrayList;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public abstract class ConnectConstants {

	public static String DB_IP = "192.168.65.156";
	public static String DB_PATH = "D:\\ClientsDB\\Hotel\\DB.fdb";

	public static String DB_LOGIN = "SYSDBA";
	public static String DB_PASS = "masterkey";

	public static ArrayList<JSONObject> devicesList = new ArrayList<>();

	public static int MAX_DEVICES_COUNT = 0;

	public static String getConnect(String ip, String pathDB, String login, String pass) {
		if (pathDB.length() == 0)
			return "";

		String ver = "";

		ConnectDBFactory connects = new ConnectDBFactory();

		connects.deleteConnectFirebird();
		Firebird conn = connects.createFirebird(ConnectConstants.DB_IP, ConnectConstants.DB_PATH);
		if (conn != null) {
			System.out.println("conn ok !!!");
			Query query = Query.getQuery(conn);

			JSONArray goodsArray = new JSONArray();
			// goodsArray =
			ver = query.getDataBaseVersion();
		} else {
			System.out.println("conn null!!!");
			// ver+=ConnectFirebird.getErrorMsg();
			ver = "";
		}

		System.out.println("ver=" + ver);
		return ver;
	}


	
}
