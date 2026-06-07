package ao.darknet.tunnel.core;

import android.net.VpnService;
import android.os.ParcelFileDescriptor;

import ao.darknet.tunnel.model.ServerConfig;

public interface TunnelEngine {
    void start(VpnService service, ParcelFileDescriptor tun, ServerConfig server) throws Exception;
    void stop();
    boolean isRunning();
    String status();
}
