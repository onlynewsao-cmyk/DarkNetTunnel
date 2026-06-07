package ao.darknet.tunnel.model;

import java.util.ArrayList;
import java.util.List;

public class AppConfig {
    public String message = "Bem-vindo ao DarkNet Tunnel";
    public boolean forceUpdate = false;
    public String updateUrl = "";
    public final List<ServerConfig> servers = new ArrayList<>();

    public static AppConfig fallback() {
        AppConfig config = new AppConfig();
        ServerConfig free = new ServerConfig("free-ao-1", "Angola Free 01", "Angola", "free1.example.com", 443, TunnelMode.TLS, PlanType.FREE);
        free.sni = "www.example.com";
        free.payload = "CONNECT [host_port] HTTP/1.1[crlf]Host: [host][crlf]User-Agent: DarkNetTunnel[crlf][crlf]";
        config.servers.add(free);

        ServerConfig premium = new ServerConfig("premium-eu-1", "Europa Premium 01", "Portugal", "premium1.example.com", 443, TunnelMode.V2RAY, PlanType.PREMIUM);
        premium.v2rayJson = "{\"protocol\":\"vmess\",\"remark\":\"Configure no dashboard\"}";
        config.servers.add(premium);
        return config;
    }
}
