package connect;

public class ConnectDBFactory extends ConnectsFactory{


	public Firebird createFirebird(String ip, String pathdb, String login, String pass, String charset) {
		
		return new ConnectFirebird("org.firebirdsql.jdbc.FBDriver", "jdbc:firebirdsql:" + ip + "/3050:" +pathdb,  login, pass,  charset);
	}
	
	private static ConnectFirebird  connectFirebird = null;

	public Firebird createFirebird(String ip, String pathdb) {
		if(connectFirebird==null)
	
			connectFirebird = new ConnectFirebird("org.firebirdsql.jdbc.FBDriver","jdbc:firebirdsql:" + ip + "/3050:" +pathdb,  "SYSDBA", "masterkey",  "UNICODE_FSS");
	
	return connectFirebird ;
	
	}
	
	public void deleteConnectFirebird()
	{
		if(connectFirebird!=null) connectFirebird=null;
	}

	
	public MySql createMySql() {
		
		return new ConnectSql();
	}

}
