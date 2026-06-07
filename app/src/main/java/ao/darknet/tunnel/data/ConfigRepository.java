package ao.darknet.tunnel.data;

import android.os.Handler;
import android.os.Looper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ao.darknet.tunnel.BuildConfig;
import ao.darknet.tunnel.model.AppConfig;
import ao.darknet.tunnel.model.PlanType;
import ao.darknet.tunnel.model.ServerConfig;
import ao.darknet.tunnel.model.TunnelMode;

public class ConfigRepository {
    public interface Callback { void onLoaded(AppConfig config, boolean remote); }

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler main = new Handler(Looper.getMainLooper());

    public void load(Callback callback) {
        executor.execute(() -> {
            AppConfig config = null;
            boolean remote = false;
            try {
                URL url = new URL(BuildConfig.DASHBOARD_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(8000);
                conn.setReadTimeout(8000);
                conn.setRequestProperty("Accept", "application/json");
                if (conn.getResponseCode() == 200) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) sb.append(line);
                    config = parse(sb.toString());
                    remote = true;
                }
            } catch (Exception ignored) { }
            AppConfig finalConfig = config == null ? AppConfig.fallback() : config;
            boolean finalRemote = remote;
            main.post(() -> callback.onLoaded(finalConfig, finalRemote));
        });
    }

    private AppConfig parse(String json) throws Exception {
        JSONObject root = new JSONObject(json);
        AppConfig config = new AppConfig();
        config.message = root.optString("message", config.message);
        config.forceUpdate = root.optBoolean("forceUpdate", false);
        config.updateUrl = root.optString("updateUrl", "");
        JSONArray servers = root.optJSONArray("servers");
        if (servers != null) {
            for (int i = 0; i < servers.length(); i++) {
                JSONObject s = servers.getJSONObject(i);
                ServerConfig server = new ServerConfig(
                        s.getString("id"),
                        s.getString("name"),
                        s.optString("country", "Global"),
                        s.getString("host"),
                        s.optInt("port", 443),
                        TunnelMode.valueOf(s.optString("mode", "TLS").toUpperCase()),
                        PlanType.valueOf(s.optString("plan", "FREE").toUpperCase())
                );
                server.latencyMs = s.optInt("latencyMs", 0);
                server.payload = s.optString("payload", "");
                server.sni = s.optString("sni", "");
                server.v2rayJson = s.optJSONObject("v2ray") == null ? s.optString("v2rayJson", "") : s.optJSONObject("v2ray").toString();
                config.servers.add(server);
            }
        }
        if (config.servers.isEmpty()) return AppConfig.fallback();
        return config;
    }
}
