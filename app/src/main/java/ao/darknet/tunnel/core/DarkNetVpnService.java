package ao.darknet.tunnel.core;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.VpnService;
import android.os.Build;
import android.os.ParcelFileDescriptor;

import ao.darknet.tunnel.R;
import ao.darknet.tunnel.model.PlanType;
import ao.darknet.tunnel.model.ServerConfig;
import ao.darknet.tunnel.model.TunnelMode;
import ao.darknet.tunnel.ui.MainActivity;

public class DarkNetVpnService extends VpnService {
    public static final String ACTION_CONNECT = "ao.darknet.tunnel.CONNECT";
    public static final String ACTION_DISCONNECT = "ao.darknet.tunnel.DISCONNECT";
    public static final String EXTRA_ID = "id";
    public static final String EXTRA_NAME = "name";
    public static final String EXTRA_COUNTRY = "country";
    public static final String EXTRA_HOST = "host";
    public static final String EXTRA_PORT = "port";
    public static final String EXTRA_MODE = "mode";
    public static final String EXTRA_PLAN = "plan";

    private static final String CHANNEL_ID = "darknet_vpn";
    private ParcelFileDescriptor tun;
    private TunnelEngine engine;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) return START_NOT_STICKY;
        if (ACTION_DISCONNECT.equals(intent.getAction())) {
            stopTunnel();
            stopSelf();
            return START_NOT_STICKY;
        }
        if (ACTION_CONNECT.equals(intent.getAction())) {
            startForeground(7, notification("A ligar..."));
            ServerConfig server = fromIntent(intent);
            startTunnel(server);
        }
        return START_STICKY;
    }

    private void startTunnel(ServerConfig server) {
        try {
            stopTunnel();
            Builder builder = new Builder()
                    .setSession("DarkNet Tunnel")
                    .setMtu(1500)
                    .addAddress("10.88.0.2", 32)
                    .addDnsServer("1.1.1.1")
                    .addDnsServer("8.8.8.8")
                    .addRoute("0.0.0.0", 0)
                    .setBlocking(true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) builder.setMetered(false);
            tun = builder.establish();
            engine = EngineFactory.create(server);
            if (tun != null) engine.start(this, tun, server);
            startForeground(7, notification("Ligado: " + server.name));
        } catch (Exception e) {
            startForeground(7, notification("Erro: " + e.getMessage()));
            stopTunnel();
        }
    }

    private ServerConfig fromIntent(Intent i) {
        return new ServerConfig(
                i.getStringExtra(EXTRA_ID),
                i.getStringExtra(EXTRA_NAME),
                i.getStringExtra(EXTRA_COUNTRY),
                i.getStringExtra(EXTRA_HOST),
                i.getIntExtra(EXTRA_PORT, 443),
                TunnelMode.valueOf(i.getStringExtra(EXTRA_MODE)),
                PlanType.valueOf(i.getStringExtra(EXTRA_PLAN))
        );
    }

    private Notification notification(String text) {
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel ch = new NotificationChannel(CHANNEL_ID, "DarkNet Tunnel VPN", NotificationManager.IMPORTANCE_LOW);
            nm.createNotificationChannel(ch);
        }
        Intent open = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, open, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder b = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? new Notification.Builder(this, CHANNEL_ID) : new Notification.Builder(this);
        return b.setContentTitle("DarkNet Tunnel")
                .setContentText(text)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pi)
                .setOngoing(true)
                .build();
    }

    private void stopTunnel() {
        try { if (engine != null) engine.stop(); } catch (Exception ignored) {}
        try { if (tun != null) tun.close(); } catch (Exception ignored) {}
        engine = null; tun = null;
    }

    @Override public void onDestroy() { stopTunnel(); super.onDestroy(); }
}
