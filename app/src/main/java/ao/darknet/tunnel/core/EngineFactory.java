package ao.darknet.tunnel.core;

import ao.darknet.tunnel.model.ServerConfig;
import ao.darknet.tunnel.model.TunnelMode;

public class EngineFactory {
    public static TunnelEngine create(ServerConfig server) {
        if (server.mode == TunnelMode.V2RAY) return new V2RayEngine();
        return new HttpTlsEngine();
    }
}
