package ao.darknet.tunnel.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.VpnService;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.*;

import java.util.ArrayList;
import java.util.List;

import ao.darknet.tunnel.R;
import ao.darknet.tunnel.core.DarkNetVpnService;
import ao.darknet.tunnel.data.ConfigRepository;
import ao.darknet.tunnel.data.SecurePrefs;
import ao.darknet.tunnel.model.AppConfig;
import ao.darknet.tunnel.model.PlanType;
import ao.darknet.tunnel.model.ServerConfig;

public class MainActivity extends Activity {
    private final List<ServerConfig> servers = new ArrayList<>();
    private ServerConfig selected;
    private SecurePrefs prefs;
    private TextView status, selectedServer, planBadge, message;
    private Button connectButton;
    private LinearLayout serverList;
    private boolean connected = false;

    private static final int GOLD = Color.rgb(214,166,75);
    private static final int TEXT = Color.rgb(247,232,200);
    private static final int CARD = Color.rgb(34,23,13);

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = new SecurePrefs(this);
        if (Build.VERSION.SDK_INT >= 33 && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 12);
        }
        buildUi();
        loadConfig();
    }

    private void buildUi() {
        ScrollView scroll = new ScrollView(this);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(18), dp(18), dp(18), dp(24));
        root.setBackgroundResource(R.drawable.bg_gradient);
        scroll.addView(root);

        LinearLayout header = row(); header.setGravity(Gravity.CENTER_VERTICAL);
        ImageView logo = new ImageView(this);
        logo.setImageResource(R.mipmap.ic_launcher);
        header.addView(logo, new LinearLayout.LayoutParams(dp(58), dp(58)));
        LinearLayout titleBox = new LinearLayout(this); titleBox.setOrientation(LinearLayout.VERTICAL); titleBox.setPadding(dp(12),0,0,0);
        TextView title = txt("DarkNet Tunnel", 26, true); title.setTextColor(GOLD);
        TextView sub = txt("by Dark Net AO", 13, false); sub.setAlpha(.85f);
        titleBox.addView(title); titleBox.addView(sub);
        header.addView(titleBox, new LinearLayout.LayoutParams(0, -2, 1));
        planBadge = badge(prefs.isPremium() ? "PREMIUM" : "FREE");
        header.addView(planBadge);
        root.addView(header);

        message = txt("A carregar servidores...", 14, false); message.setPadding(0, dp(18),0, dp(8)); root.addView(message);

        LinearLayout hero = card(); hero.setGravity(Gravity.CENTER); hero.setOrientation(LinearLayout.VERTICAL);
        status = txt("Desligado", 18, true); status.setTextColor(GOLD); status.setGravity(Gravity.CENTER);
        selectedServer = txt("Seleciona um servidor", 14, false); selectedServer.setGravity(Gravity.CENTER); selectedServer.setPadding(0, dp(8),0, dp(18));
        connectButton = new Button(this); connectButton.setText("LIGAR"); connectButton.setTextColor(Color.BLACK); connectButton.setTypeface(Typeface.DEFAULT_BOLD); connectButton.setTextSize(18); connectButton.setAllCaps(false); connectButton.setBackgroundResource(R.drawable.button_gold);
        connectButton.setOnClickListener(v -> toggleVpn());
        hero.addView(status); hero.addView(selectedServer); hero.addView(connectButton, new LinearLayout.LayoutParams(-1, dp(56)));
        root.addView(hero);

        TextView section = txt("Servidores", 18, true); section.setTextColor(GOLD); section.setPadding(0, dp(18),0,dp(8)); root.addView(section);
        serverList = new LinearLayout(this); serverList.setOrientation(LinearLayout.VERTICAL); root.addView(serverList);

        TextView tools = txt("Ferramentas: HTTP • TLS/SNI • V2Ray • WS • Free/Premium • Configuração remota", 13, false);
        tools.setAlpha(.75f); tools.setPadding(0, dp(18),0,0); root.addView(tools);
        setContentView(scroll);
    }

    private void loadConfig() {
        new ConfigRepository().load((config, remote) -> {
            servers.clear(); servers.addAll(config.servers);
            message.setText(config.message + (remote ? "" : "  (modo offline/demo)"));
            String saved = prefs.selectedServerId();
            selected = servers.get(0);
            for (ServerConfig s : servers) if (s.id.equals(saved)) selected = s;
            renderServers(); updateSelected();
        });
    }

    private void renderServers() {
        serverList.removeAllViews();
        for (ServerConfig s : servers) {
            LinearLayout item = card(); item.setGravity(Gravity.CENTER_VERTICAL); item.setPadding(dp(14),dp(12),dp(14),dp(12));
            TextView left = txt(s.name + "\n" + s.subtitle(), 15, true); left.setLineSpacing(4,1); left.setTextColor(TEXT);
            TextView right = badge(s.plan == PlanType.PREMIUM ? "PREMIUM" : "FREE");
            item.addView(left, new LinearLayout.LayoutParams(0, -2, 1)); item.addView(right);
            item.setOnClickListener(v -> {
                if (s.plan == PlanType.PREMIUM && !prefs.isPremium()) {
                    Toast.makeText(this, "Servidor Premium: ativa a tua subscrição no dashboard.", Toast.LENGTH_LONG).show();
                    return;
                }
                selected = s; prefs.setSelectedServerId(s.id); updateSelected(); renderServers();
            });
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2); lp.setMargins(0,0,0,dp(10));
            serverList.addView(item, lp);
        }
    }

    private void updateSelected() {
        if (selected == null) return;
        selectedServer.setText(selected.name + " • " + selected.host + ":" + selected.port + " • " + selected.mode.name());
    }

    private void toggleVpn() {
        if (selected == null) return;
        if (connected) { disconnect(); return; }
        Intent prepare = VpnService.prepare(this);
        if (prepare != null) startActivityForResult(prepare, 99); else connect();
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 99 && resultCode == RESULT_OK) connect();
    }

    private void connect() {
        Intent i = new Intent(this, DarkNetVpnService.class);
        i.setAction(DarkNetVpnService.ACTION_CONNECT);
        i.putExtra(DarkNetVpnService.EXTRA_ID, selected.id);
        i.putExtra(DarkNetVpnService.EXTRA_NAME, selected.name);
        i.putExtra(DarkNetVpnService.EXTRA_COUNTRY, selected.country);
        i.putExtra(DarkNetVpnService.EXTRA_HOST, selected.host);
        i.putExtra(DarkNetVpnService.EXTRA_PORT, selected.port);
        i.putExtra(DarkNetVpnService.EXTRA_MODE, selected.mode.name());
        i.putExtra(DarkNetVpnService.EXTRA_PLAN, selected.plan.name());
        if (Build.VERSION.SDK_INT >= 26) startForegroundService(i); else startService(i);
        connected = true; status.setText("Ligado"); connectButton.setText("DESLIGAR");
    }

    private void disconnect() {
        Intent i = new Intent(this, DarkNetVpnService.class); i.setAction(DarkNetVpnService.ACTION_DISCONNECT); startService(i);
        connected = false; status.setText("Desligado"); connectButton.setText("LIGAR");
    }

    private LinearLayout row(){ LinearLayout l=new LinearLayout(this); l.setOrientation(LinearLayout.HORIZONTAL); return l; }
    private LinearLayout card(){ LinearLayout l=row(); l.setBackgroundResource(R.drawable.card); l.setPadding(dp(14),dp(14),dp(14),dp(14)); return l; }
    private TextView txt(String s, int sp, boolean bold){ TextView v=new TextView(this); v.setText(s); v.setTextSize(sp); v.setTextColor(TEXT); if(bold)v.setTypeface(Typeface.DEFAULT_BOLD); return v; }
    private TextView badge(String s){ TextView v=txt(s, 11, true); v.setTextColor(Color.BLACK); v.setGravity(Gravity.CENTER); v.setPadding(dp(10),dp(5),dp(10),dp(5)); v.setBackgroundResource(R.drawable.button_gold); return v; }
    private int dp(int v){ return (int)(v*getResources().getDisplayMetrics().density+.5f); }
}
