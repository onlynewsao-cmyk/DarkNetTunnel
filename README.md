# DarkNet Tunnel

Aplicativo Android VPN profissional para **Dark Net AO**, com identidade visual escura/dourada, servidores Free/Premium e configuração remota por dashboard para HTTP, TLS/SNI, V2Ray, WS e SSH.

## O que foi criado

- Projeto Android nativo em Java.
- Ícone do app usando `uploads/ic_launcher.png`.
- Tela inicial profissional com conexão, servidor selecionado, lista Free/Premium e badges.
- `VpnService` configurado com permissão oficial do Android.
- Repositório para carregar servidores a partir de dashboard HTTPS.
- Modelos para HTTP, TLS, V2Ray, WS, SSH.
- Motores `HttpTlsEngine` e `V2RayEngine` como pontos limpos de integração.
- Exemplo de JSON do dashboard.
- Preview visual em HTML (`preview_darknet_tunnel.html`).

## Estrutura

```text
DarkNetTunnel/
  app/src/main/java/ao/darknet/tunnel/
    ui/MainActivity.java
    core/DarkNetVpnService.java
    core/HttpTlsEngine.java
    core/V2RayEngine.java
    data/ConfigRepository.java
    model/*.java
  config/dashboard-response-example.json
  docs/API_DASHBOARD.md
```

## Como abrir

1. Abra a pasta `DarkNetTunnel` no Android Studio.
2. Deixe o Android Studio sincronizar o Gradle.
3. Em `app/build.gradle`, altere `DASHBOARD_URL` para o endpoint real do seu dashboard.
4. Compile e instale em um telemóvel Android.

## Importante sobre o motor VPN

O Android exige um `VpnService` para capturar o tráfego. Para produção, é necessário integrar um motor real, por exemplo:

- `tun2socks` para encaminhar pacotes TUN para SOCKS/HTTP/TLS.
- Core V2Ray/Xray compatível com a licença do seu produto.
- Cliente TLS/WS/SSH conforme seus servidores.

Os arquivos `HttpTlsEngine.java` e `V2RayEngine.java` foram criados como camada profissional de integração, para você ligar as bibliotecas nativas/cores que escolher sem reescrever a UI ou a arquitetura.

## Próximos módulos recomendados

- Login/registro no dashboard.
- Pagamento e ativação Premium no backend.
- Medição real de latência.
- Atualização obrigatória.
- Assinatura criptográfica da configuração remota.
- Proteção anti-tamper/anti-debug para publicação.
