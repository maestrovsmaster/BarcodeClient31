package essences;

/**
 * Created by userd088 on 28.04.2016.
 */
public class User extends DicTable {

    public static final int ENABLE=1;
    private static final int NOT_AVIALABLE=0;

    String password;
    int isEnabled=1;

    public User(int id, int grpId, String login, String password, int isEnabled) {
        super(id, grpId, login);
        this.password=password;
        this.isEnabled=isEnabled;
    }

    public String getLogin()
    {
        return getName();
    }

    public String getPassword() {
        return password;
    }

    public int getIsEnabled() {
        return isEnabled;
    }
}
