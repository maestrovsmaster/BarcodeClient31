package connect;

public abstract class ConnectsFactory {

	public abstract Firebird createFirebird(String driver, String url, String login, String pass, String charset);
	
	public abstract MySql createMySql();

}
