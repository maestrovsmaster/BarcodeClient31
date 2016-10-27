package connect;

import java.util.ArrayList;



public interface ConnectListener {

	public void setSelectedRow(ArrayList<Object> row);
	
	public void addConnectListener(ConnectListener listener);
	
}
