package ao.darknet.tunnel.core;

import android.net.VpnService;
import android.os.ParcelFileDescriptor;

import java.util.concurrent.atomic.AtomicBoolean;

import ao.darknet.tunnel.model.ServerConfig;

/**
 * Ponto de integração V2Ray/Xray.
 * Produção: adicione uma biblioteca/core compatível com a sua licença e chame o core aqui,
 * passando server.v2rayJson recebido do dashboard.
 */
public class V2RayEngine implements TunnelEngine {
    private final AtomicBoolean running = new AtomicBoolean(false);
    private String status = "Parado";

    @Override
    public void start(VpnService service, ParcelFileDescriptor tun, ServerConfig server) {
        running.set(true);
        status = "V2Ray preparado para " + server.name;
    }

    @Override public void stop() { running.set(false); status = "Parado"; }
    @Override public boolean isRunning() { return running.get(); }
    @Override public String status() { return status; }
}
