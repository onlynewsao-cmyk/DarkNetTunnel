# API do Dashboard — DarkNet Tunnel

Configure `BuildConfig.DASHBOARD_URL` em `app/build.gradle` para apontar para o endpoint HTTPS do seu dashboard.

## Endpoint

`GET /api/v1/app-config`

## Resposta

Veja `config/dashboard-response-example.json`.

Campos principais:

- `message`: aviso exibido na tela inicial.
- `forceUpdate`: se `true`, o app deve bloquear uso até atualizar (já previsto no modelo; implemente a tela se desejar).
- `servers[]`: lista de servidores.
- `mode`: `HTTP`, `TLS`, `V2RAY`, `WS` ou `SSH`.
- `plan`: `FREE` ou `PREMIUM`.
- `payload`, `sni`, `v2ray`: configurações dinâmicas geridas no dashboard.

## Segurança recomendada

1. Use HTTPS com certificado válido.
2. Assine a resposta do dashboard ou use token Bearer para evitar alterações indevidas.
3. Não inclua chaves privadas no APK.
4. Controle servidores Premium no backend, não apenas no aplicativo.
5. Obedeça às regras das redes e provedores; use a VPN para privacidade, acesso corporativo e serviços autorizados.
