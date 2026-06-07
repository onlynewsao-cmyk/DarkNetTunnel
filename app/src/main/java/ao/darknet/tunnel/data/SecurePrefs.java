package ao.darknet.tunnel.data;

import android.content.Context;
import android.content.SharedPreferences;

public class SecurePrefs {
    private static final String FILE = "darknet_secure_prefs";
    private final SharedPreferences prefs;

    public SecurePrefs(Context context) {
        prefs = context.getSharedPreferences(FILE, Context.MODE_PRIVATE);
    }

    public boolean isPremium() { return prefs.getBoolean("premium", false); }
    public void setPremium(boolean value) { prefs.edit().putBoolean("premium", value).apply(); }
    public String selectedServerId() { return prefs.getString("server_id", "free-ao-1"); }
    public void setSelectedServerId(String id) { prefs.edit().putString("server_id", id).apply(); }
}
