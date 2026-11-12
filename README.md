# Roleparty
Roleparty is a cross-platform desktop TTRPG application designed for campaign management, community content sharing, and rich character tracking — built for both live table sessions and remote play.

## Prerequisites

- **Docker**, with Docker Compose, for Postgres and RabbitMQ. The integration tests need it too.
- **JDK 25**.
- **OpenSSL**, only to generate production keys.

You don't need to install Kotlin or Gradle: the `./kotlin` wrapper (`kotlin.bat` on Windows) downloads its own toolchain.

## Run locally

The commands below work in both `bash` and `zsh`. Keep the quotes around `'[environment]/...'`, as `zsh` would otherwise read the brackets as a pattern.

1. Start Postgres and RabbitMQ:

   ```sh
   docker compose -f '[environment]/local/docker-compose.yaml' up -d --wait
   ```

2. Start the backend on port 8080, with the local configuration:

   ```sh
   set -a; source '[environment]/local/backend.env'; set +a
   ./kotlin run -m application-backend
   ```

   It applies the database migrations on startup. Locally, emails aren't sent: they're written to the backend's logs, so that's where you'll find verification and password reset codes.

3. Start the desktop application in another terminal:

   ```sh
   ./kotlin run -m application-frontend
   ```

   It opens in fullscreen on the sign-in screen and uses the backend at `http://localhost:8080`. Set `ROLEPARTY_BACKEND_URL` to use another one. Press `ESC` and choose **Quit Roleparty** to leave.

   Create an account from there: the code to enter is in the backend's logs. Your session is stored encrypted, with its key in your system's keychain, so the application may ask for access to it.

| Local service | Address | Credentials |
|---|---|---|
| Backend | `http://localhost:8080` | |
| Postgres | `localhost:5438`, database `roleparty` | `postgres_user` / `postgres_password` |
| RabbitMQ | `localhost:5678` | `admin` / `admin` |
| RabbitMQ management | `http://localhost:15678` | `admin` / `admin` |

To stop the services, run the first command with `down` instead of `up -d --wait`. Adding `-v` also deletes their data.

### Try the authentication without the application

```sh
# Sign up, then look for the 8-character code in the backend's logs
curl -i -H 'Content-Type: application/json' \
  -d '{"emailAddress":"gimli@roleparty.app","password":"a-valid-password"}' \
  http://localhost:8080/v1/sign-up

# Confirm the address
curl -i -H 'Content-Type: application/json' \
  -d '{"emailAddress":"gimli@roleparty.app","code":"THE-CODE"}' \
  http://localhost:8080/v1/verify-email

# Sign in
curl -H 'Content-Type: application/json' \
  -d '{"emailAddress":"gimli@roleparty.app","password":"a-valid-password"}' \
  http://localhost:8080/v1/sign-in
```

## Run the tests

```sh
./kotlin test
```

Docker must be running, as integration tests start their own Postgres and RabbitMQ containers. No real email is sent.

The tabletop tests load the native WebRTC library. On Linux it needs these system libraries, which a desktop usually has but a bare server or CI image may not:

```sh
sudo apt-get install -y --no-install-recommends \
  libpulse0 libudev1 libx11-6 libxext6 libxfixes3 libxdamage1 libxtst6 libxrandr2 libxcomposite1 \
  libdbus-1-3 libglib2.0-0 libgbm1 libdrm2
```

## Deploy to production

Don't reuse anything from `[environment]/local/backend.env`: its secrets are public. Never set `EMAIL_SENDER=log` in production, as it writes verification and reset codes to the logs.

### 1. Generate the secrets

Keep these files out of git.

```sh
# Key pair signing the access tokens (ES256)
openssl ecparam -name prime256v1 -genkey -noout -out jwt-ec.pem
openssl pkcs8 -topk8 -nocrypt -in jwt-ec.pem -out jwt-private.pem
openssl ec -in jwt-ec.pem -pubout -out jwt-public.pem
rm jwt-ec.pem

# Tokens protecting the account and media RPC endpoints
openssl rand -base64 32 > account-rpc-token.txt
openssl rand -base64 32 > media-rpc-token.txt
```

### 2. Set up the email provider

Create a mailbox at [Hostinger](https://www.hostinger.com) and get its resource id and an API token. They become `HOSTINGER_MAILBOX_ID` and `HOSTINGER_API_TOKEN`.

### 3. Configure the backend

Each variable can instead be read from a file (a Docker secret, for example) by setting `<NAME>_FILE` to its path.

| Variable | Required | Description |
|---|---|---|
| `POSTGRES_HOST` | yes | Postgres host |
| `POSTGRES_PORT` | yes | Postgres port |
| `POSTGRES_USER` | yes | Postgres user |
| `POSTGRES_PASSWORD` | yes | Postgres password |
| `POSTGRES_DATABASE_NAME` | yes | Postgres database |
| `RABBITMQ_HOST` | yes | RabbitMQ host |
| `RABBITMQ_PORT` | yes | RabbitMQ port |
| `RABBITMQ_USER` | yes | RabbitMQ user |
| `RABBITMQ_PASSWORD` | yes | RabbitMQ password |
| `JWT_PRIVATE_KEY` | yes | Content of `jwt-private.pem` |
| `JWT_PUBLIC_KEY` | yes | Content of `jwt-public.pem` |
| `ACCOUNT_RPC_URL` | yes | WebSocket URL of the account RPC endpoint, ending in `/rpc/account`. It's served by this same backend, so use `ws://localhost:8080/rpc/account` if it stays on this machine, and `wss://` if it crosses a network |
| `ACCOUNT_RPC_TOKEN` | yes | Content of `account-rpc-token.txt`, at least 32 characters |
| `MEDIA_RPC_TOKEN` | yes | Content of `media-rpc-token.txt`, at least 32 characters. Protects the media RPC endpoint `/rpc/media` |
| `HOSTINGER_API_TOKEN` | yes | Hostinger API token |
| `HOSTINGER_MAILBOX_ID` | yes | Hostinger mailbox resource id |
| `ALERT_EMAIL_ADDRESS` | no | Receives an email when a dead-letter queue grows. An error is logged either way |
| `TRUST_PROXY` | no | Set to `true` only behind exactly one trusted reverse proxy, so that rate limits use the real client IP. Without it, all clients behind the proxy share one limit |
| `JWT_PREVIOUS_PUBLIC_KEY` | no | Previous public key, during a key rotation |
| `TABLETOP_ICE_SERVER_URLS` | no | Comma-separated STUN/TURN URLs the desktop apps use to reach each other at the tabletop. Defaults to `stun:stun.l.google.com:19302`; set it empty to stay on a local network. Without a TURN server, people behind a strict NAT can't join |
| `TABLETOP_ICE_SERVER_USERNAME` | no | Username for the TURN server, sent to the participants of a tabletop |
| `TABLETOP_ICE_SERVER_CREDENTIAL` | no | Credential for the TURN server, sent to the participants of a tabletop |

Postgres and RabbitMQ must be reachable when the backend starts. The port is set in `application/application-backend/resources/application.yaml`.

Expose the backend over HTTPS only, as passwords and tokens travel in its requests.

### 4. Point the desktop application at it

The application uses `ROLEPARTY_BACKEND_URL`, which defaults to `http://localhost:8080`. Set it to your backend's HTTPS address wherever the application runs.

The installed application doesn't need it: its address comes from `roleparty.backend.url` in `conveyor.conf`.

### 5. Check the deployment

- The logs must not contain `Account RPC is unreachable` after startup, which means `ACCOUNT_RPC_URL` or `ACCOUNT_RPC_TOKEN` is wrong.
- Sign up with a real address and check that the code arrives.

### Rotate the signing key

1. Generate a new key pair.
2. Set `JWT_PREVIOUS_PUBLIC_KEY` to the current public key, then `JWT_PRIVATE_KEY` and `JWT_PUBLIC_KEY` to the new pair.
3. Remove `JWT_PREVIOUS_PUBLIC_KEY` after 15 minutes, once tokens signed with the old key have expired.

Users stay signed in throughout.

## Release pipeline

Every push to `main` runs `.github/workflows/pipeline.yaml`: build, tests, then a version tag computed from the commits added since the previous tag.

| Commit | Version | Released |
|---|---|---|
| `type!:`, `break:` or a `BREAKING CHANGE:` footer | `x` + 1 | backend and desktop application |
| `feat:` | `y` + 1 | backend and desktop application |
| `fix:`, `refactor:`, `style:`, `perf:` | `z` + 1 | tag only |
| anything else (`chore:`, `docs:`, `test:`...) | unchanged | nothing |

A release builds the backend image, pushes it to `ghcr.io/sylvain-barrepersyn/roleparty-backend`, and restarts it on the server with `[environment]/production/docker-compose.yaml`. Once the backend is up, [Conveyor](https://conveyor.hydraulic.dev) packages the desktop application for Windows, macOS and Linux and publishes it as the latest GitHub release. Installed applications update themselves from it when they start, and the download page is `https://sylvain-barrepersyn.github.io/Roleparty/download.html`.

### Set up the server once

In the directory the pipeline deploys to:

1. Copy `[environment]/production/example.env` as `.env` and fill it in. `MONITOR_USERS` is the output of `htpasswd -nB <user>`.
2. Put the files generated in [Generate the secrets](#1-generate-the-secrets) in `secrets/`: `jwt-private.pem`, `jwt-public.pem`, `account-rpc-token.txt` and `media-rpc-token.txt`.
3. Allow a dedicated SSH key to sign in, for a user in the `docker` group.

### Set up the repository once

| Secret | Description |
|---|---|
| `DEPLOY_HOST`, `DEPLOY_USER` | SSH host and user of the server |
| `DEPLOY_PORT` | SSH port, `22` if not set |
| `DEPLOY_PATH` | Absolute path of the deployment directory on the server |
| `DEPLOY_SSH_KEY` | Private key allowed to sign in |
| `DEPLOY_KNOWN_HOSTS` | Output of `ssh-keyscan -p <port> <host>` |
| `TS_OAUTH_CLIENT_ID`, `TS_OAUTH_SECRET` | Optional. When set, the pipeline joins your Tailscale network as `tag:ci` before SSH, so the SSH port doesn't need to be open to the internet |
| `SIGNING_KEY` | Conveyor root key: run `conveyor keys generate` once and copy the `app.signing-key` value it writes |

Turn on GitHub Pages for the `gh-pages` branch after the first release.

### Package the desktop application locally

```sh
eval "$(.github/scripts/prepare-conveyor-inputs.sh | grep '^SKIKO_VERSION=' | sed 's/^/export /')"
conveyor make site
```
