package ao.darknet.tunnel.model;

public class ServerConfig {
    public String id;
    public String name;
    public String country;
    public String host;
    public int port;
    public TunnelMode mode;
    public PlanType plan;
    public int latencyMs;
    public String payload;
    public String sni;
    public String v2rayJson;

    public ServerConfig(String id, String name, String country, String host, int port, TunnelMode mode, PlanType plan) {
        this.id = id;
        this.name = name;
        this.country = country;
        this.host = host;
        this.port = port;
        this.mode = mode;
        this.plan = plan;
        this.latencyMs = 0;
    }

    public String subtitle() {
        return country + " • " + mode.name() + " • " + (plan == PlanType.PREMIUM ? "Premium" : "Free");
    }
}
