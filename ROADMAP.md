# 🛡️ Astola VPN — Project Roadmap

> **A custom tunnel VPN application** for Android, inspired by Apna Tunnel & Dark Tunnel.
> Designed for protocol obfuscation, firewall evasion, and zero-rating exploitation
> using SSH, V2Ray/Xray, and payload injection over HTTP/TLS/WebSocket.

---

## 📌 Project Vision

Build a production-grade Android VPN application that:

1. Captures all device traffic via Android's `VpnService`
2. Encapsulates it inside obfuscated HTTP/TLS/WebSocket streams
3. Routes it through remote proxy servers (SSH, V2Ray, Xray, Shadowsocks)
4. Tricks ISP Deep Packet Inspection (DPI) using crafted payloads & SNI spoofing
5. Provides a user-friendly experience with cloud-managed server configs

---

## 🏗️ High-Level Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                      ANDROID DEVICE                         │
│                                                             │
│  ┌──────────┐    ┌──────────────┐    ┌──────────────────┐   │
│  │  Apps     │───▶│  VpnService  │───▶│  Local Proxy     │   │
│  │ (Browser, │    │  (TUN iface) │    │  127.0.0.1:8989  │   │
│  │  Games)   │    └──────────────┘    └────────┬─────────┘   │
│  └──────────┘                                  │             │
│                                                │             │
│  ┌─────────────────────────────────────────────▼───────────┐ │
│  │              TUNNEL ENGINE LAYER                        │ │
│  │                                                         │ │
│  │  ┌─────────────┐ ┌──────────┐ ┌───────────────────────┐│ │
│  │  │ Payload     │ │ SNI      │ │ Protocol Engine       ││ │
│  │  │ Injector    │ │ Spoofer  │ │ (SSH/V2Ray/Xray/SS)   ││ │
│  │  └──────┬──────┘ └────┬─────┘ └───────────┬───────────┘│ │
│  │         └─────────────┼───────────────────┘            │ │
│  └───────────────────────┼────────────────────────────────┘ │
│                          │                                   │
└──────────────────────────┼───────────────────────────────────┘
                           │ Obfuscated Traffic
                           ▼
               ┌───────────────────────┐
               │   ISP / FIREWALL      │
               │   (DPI Equipment)     │
               │   Sees: free-site.com │
               └───────────┬───────────┘
                           │
                           ▼
               ┌───────────────────────┐
               │   REMOTE VPN SERVER   │
               │                       │
               │  • Decapsulation      │
               │  • Decrypt SSH/V2Ray  │
               │  • Forward to Internet│
               └───────────┬───────────┘
                           │
                           ▼
                    🌍 Open Internet
```

---

## 🧰 Technology Stack

| Layer | Technology | Purpose |
|---|---|---|
| **Language** | Kotlin + Java | Primary Android development |
| **Native** | C/C++ (NDK) | Performance-critical tunnel engines |
| **VPN Interface** | Android `VpnService` API | System-level traffic capture via TUN |
| **SSH Engine** | JSch / Trilead SSH2 / libssh2 | SSH tunnel establishment |
| **V2Ray/Xray Core** | v2ray-core / Xray-core (Go libs via gomobile) | V2Ray/VLESS/VMess/Trojan protocols |
| **Shadowsocks** | shadowsocks-android (libss-local) | Shadowsocks SOCKS5 proxy |
| **DNS Tunneling** | SlowDNS (iodine-based) | DNS-over-port-53 tunneling |
| **TLS/SSL** | BouncyCastle / Conscrypt | TLS wrapping for SSH-over-SSL |
| **WebSocket** | OkHttp / nv-websocket-client | WebSocket transport layer |
| **Networking** | OkHttp, Netty | HTTP proxy, connection pooling |
| **UI Framework** | Jetpack Compose + Material 3 | Modern declarative UI |
| **Architecture** | MVVM + Clean Architecture | Separation of concerns |
| **DI** | Hilt (Dagger) | Dependency injection |
| **Database** | Room | Local config/server storage |
| **Backend** | Firebase / Supabase | Cloud config, server lists, analytics |
| **Config Format** | Custom encrypted `.astola` files | Shareable locked config files |
| **Build** | Gradle (KTS) | Build system |
| **Min SDK** | API 21 (Android 5.0) | Wide device coverage |

---

## 📅 Development Phases

---

### 🔷 Phase 0 — Project Foundation & Research (Week 1–2)

> Set up the development environment, study open-source references, and establish the project skeleton.

| # | Task | Details | Status |
|---|---|---|---|
| 0.1 | **Environment Setup** | Android Studio, NDK, Gradle KTS, Git repo | `[x]` |
| 0.2 | **Study Open-Source References** | Study: SagerNet, v2rayNG, HTTP Injector clones, SSH tunnel apps on GitHub | `[x]` |
| 0.3 | **Project Skeleton** | Create multi-module Gradle project with Clean Architecture | `[x]` |
| 0.4 | **CI/CD Pipeline** | GitHub Actions for build, lint, APK signing | `[x]` |
| 0.5 | **Design System** | Material 3 theme, color palette, typography, component library | `[x]` |

**Module Structure:**
```
astola-vpn/
├── app/                          # Main application module
├── core/
│   ├── common/                   # Shared utilities, extensions
│   ├── model/                    # Domain models
│   ├── database/                 # Room database, DAOs
│   ├── network/                  # OkHttp, API clients
│   └── ui/                       # Shared Compose components
├── feature/
│   ├── home/                     # Dashboard / connection screen
│   ├── servers/                  # Server list management
│   ├── settings/                 # App settings
│   ├── logs/                     # Connection logs viewer
│   └── payload-editor/           # Payload crafting UI
├── tunnel/
│   ├── vpn-service/              # VpnService implementation
│   ├── ssh-engine/               # SSH tunnel engine
│   ├── v2ray-engine/             # V2Ray/Xray engine wrapper
│   ├── shadowsocks-engine/       # Shadowsocks engine
│   ├── slowdns-engine/           # DNS tunneling engine
│   ├── proxy/                    # Local SOCKS5/HTTP proxy
│   ├── payload/                  # Payload injector & parser
│   └── transport/                # HTTP, TLS, WebSocket transports
└── config/
    ├── parser/                   # .astola config file parser
    └── crypto/                   # Config encryption/decryption
```

**Deliverable:** Compilable empty project with navigation skeleton and CI pipeline.

---

### 🔷 Phase 1 — VPN Service & Traffic Capture (Week 3–4)

> Implement Android VpnService to capture all device traffic and route it through a local proxy.

| # | Task | Details | Status |
|---|---|---|---|
| 1.1 | **VpnService Implementation** | Request VPN permission, create TUN interface, configure routes | `[x]` |
| 1.2 | **TUN Packet Reader** | Read raw IP packets from the TUN file descriptor | `[x]` |
| 1.3 | **TCP/UDP Packet Parser** | Parse IP headers, extract TCP/UDP segments | `[x]` |
| 1.4 | **Local SOCKS5 Proxy** | Implement local SOCKS5 server on `127.0.0.1` | `[x]` |
| 1.5 | **Traffic Routing** | Route TUN packets → local proxy using tun2socks (badvpn or go-tun2socks) | `[x]` |
| 1.6 | **DNS Handling** | Intercept DNS queries, forward to custom/DoH resolver | `[x]` |
| 1.7 | **Split Tunneling** | Per-app VPN bypass (allow/disallow list) | `[x]` |
| 1.8 | **VPN Notification** | Persistent notification with connection status, speed, disconnect button | `[x]` |
| 1.9 | **Battery Optimization** | Handle Doze mode, keep-alive, foreground service | `[x]` |

**Key Technical Decisions:**

- **tun2socks:** Use `go-tun2socks` (from xjasonlyu/tun2socks) compiled via gomobile — converts TUN packets to SOCKS5 proxy connections. This is the same approach used by v2rayNG and SagerNet.
- **DNS:** Default to `1.1.1.1` (Cloudflare DoH) with user-configurable DNS. Support DNS-over-HTTPS to prevent DNS leaks.

**Deliverable:** App captures all device traffic and routes it through a local SOCKS5 proxy (no encryption yet — passthrough mode for testing).

---

### 🔷 Phase 2 — SSH Tunnel Engine (Week 5–7)

> Build the SSH tunneling engine — the backbone protocol for payload-based VPNs.

| # | Task | Details | Status |
|---|---|---|---|
| 2.1 | **SSH Client Integration** | Integrate JSch or Trilead SSH-2 library | `[x]` |
| 2.2 | **Direct SSH Connection** | Connect to SSH server → create dynamic SOCKS5 port forward | `[x]` |
| 2.3 | **SSH over HTTP Proxy (Payload Injection)** | Route SSH through HTTP CONNECT proxy with custom headers | `[x]` |
| 2.4 | **Payload Parser & Macro Engine** | Parse payload strings with macros: `[host_port]`, `[crlf]`, `[cr]`, `[lf]`, `[protocol]`, `[netData]`, `[raw]`, `[split]`, `[delay_split]` | `[x]` |
| 2.5 | **SSH over SSL/TLS** | Wrap SSH inside a TLS connection (stunnel-like) | `[x]` |
| 2.6 | **SSH over WebSocket** | Tunnel SSH inside WebSocket frames (ws:// and wss://) | `[x]` |
| 2.7 | **SSH over CDN (Cloudflare)** | Route WebSocket SSH through Cloudflare CDN for extra obfuscation | `[x]` |
| 2.8 | **Connection Keepalive** | SSH keepalive packets, auto-reconnect on drop | `[x]` |
| 2.9 | **Multi-hop SSH** | Chain SSH connections through multiple servers | `[x]` |

**Payload Macro Reference:**

| Macro | Expansion | Purpose |
|---|---|---|
| `[host_port]` | `server_ip:port` | Actual VPN server address |
| `[host]` | `server_ip` | Server IP only |
| `[port]` | `port` | Server port only |
| `[crlf]` | `\r\n` | HTTP line terminator |
| `[cr]` | `\r` | Carriage return only |
| `[lf]` | `\n` | Line feed only |
| `[protocol]` | `HTTP/1.0` or `HTTP/1.1` | HTTP protocol version |
| `[netData]` | Server's response data | Wait for and inject server response |
| `[raw]` | Raw TCP data | Send raw bytes |
| `[split]` | — | Split request into 2 TCP packets |
| `[delay_split]` | — | Split with configurable delay (ms) |

**Payload Processing Pipeline:**
```
User Payload String
        │
        ▼
┌──────────────────┐
│  Macro Expansion  │  Replace [host_port], [crlf], etc.
└────────┬─────────┘
         │
         ▼
┌──────────────────┐
│  Split Detection  │  Check for [split] / [delay_split]
└────────┬─────────┘
         │
    ┌────┴────┐
    ▼         ▼
 Packet 1   Packet 2    (if split)
    │         │
    ▼         ▼ (after delay)
┌──────────────────┐
│  TCP Socket Send  │  Send to ISP / proxy
└────────┬─────────┘
         │
         ▼
┌──────────────────┐
│  Read Response    │  If [netData], wait for proxy/ISP response
└────────┬─────────┘
         │
         ▼
   SSH Handshake Begins
```

**Deliverable:** Working SSH tunnel with payload injection, split support, and SSL/WebSocket transports.

---

### 🔷 Phase 3 — V2Ray / Xray Engine (Week 8–10)

> Integrate V2Ray/Xray core for modern obfuscation protocols.

| # | Task | Details | Status |
|---|---|---|---|
| 3.1 | **V2Ray Core Integration** | Compile Xray-core via gomobile, create JNI bridge | `[x]` |
| 3.2 | **VMess Protocol** | Support VMess inbound/outbound with UUID auth | `[x]` |
| 3.3 | **VLESS Protocol** | Support VLESS (lighter than VMess) | `[x]` |
| 3.4 | **Trojan Protocol** | Support Trojan (TLS-based, looks like HTTPS) | `[x]` |
| 3.5 | **Transport: TCP** | Plain TCP transport | `[x]` |
| 3.6 | **Transport: WebSocket** | WS/WSS transport with custom path & headers | `[x]` |
| 3.7 | **Transport: gRPC** | gRPC transport for CDN-compatible tunneling | `[x]` |
| 3.8 | **Transport: HTTP/2** | H2 transport | `[x]` |
| 3.9 | **TLS Configuration** | Custom SNI, ALPN, fingerprint, allow insecure | `[x]` |
| 3.10 | **Reality** | Support REALITY (Xray's latest anti-detection) | `[x]` |
| 3.11 | **Config Generator** | Programmatically build V2Ray JSON configs | `[x]` |

**Deliverable:** V2Ray/Xray tunneling with VMess, VLESS, Trojan over WS/gRPC/TCP with TLS.

---

### 🔷 Phase 4 — Shadowsocks & SlowDNS (Week 11–12)

> Add Shadowsocks and DNS tunneling for maximum protocol coverage.

| # | Task | Details | Status |
|---|---|---|---|
| 4.1 | **Shadowsocks Integration** | Integrate shadowsocks-libev / shadowsocks-rust | `[x]` |
| 4.2 | **SS Ciphers** | Support AEAD ciphers (chacha20-ietf-poly1305, aes-256-gcm) | `[x]` |
| 4.3 | **SIP003 Plugins** | Support obfs plugins (simple-obfs, v2ray-plugin) | `[x]` |
| 4.4 | **SlowDNS Engine** | Implement DNS tunneling (encode TCP data inside DNS TXT/CNAME queries) | `[x]` |
| 4.5 | **SlowDNS + SSH** | Combine: DNS tunnel as transport layer → SSH on top | `[x]` |
| 4.6 | **UDP Relay** | UDP association for Shadowsocks (gaming, VoIP) | `[x]` |

**SlowDNS Architecture:**
```
Device App Traffic
        │
        ▼
┌──────────────────┐
│  SSH Connection   │
│  (encrypted)      │
└────────┬─────────┘
         │
         ▼
┌──────────────────┐
│  DNS Encoder      │  Encode SSH bytes as DNS queries
│                   │  query: <base32-data>.ns.yourdomain.com
└────────┬─────────┘
         │ Port 53 (DNS)
         ▼
┌──────────────────┐
│  ISP Firewall     │  Sees: Normal DNS traffic → PASS ✅
└───────────┬───────────┘
         │
         ▼
┌──────────────────┐
│  DNS Server       │  Decode DNS → Extract SSH data
│  (your NS)        │  Forward to SSH server
└──────────────────┘
```

**Deliverable:** Shadowsocks and SlowDNS tunneling functional.

---

### 🔷 Phase 5 — SNI Spoofing & Advanced Obfuscation (Week 13–14)

> Implement SNI-based evasion for modern HTTPS-heavy networks.

| # | Task | Details | Status |
|---|---|---|---|
| 5.1 | **SNI Spoofing** | Inject zero-rated domain into TLS Client Hello SNI field | `[x]` |
| 5.2 | **Domain Fronting** | Use CDN domain fronting (Cloudflare, Akamai) | `[x]` |
| 5.3 | **TLS Fingerprint Spoofing** | Mimic browser TLS fingerprints (Chrome/Firefox JA3) | `[x]` |
| 5.4 | **HTTP/2 CONNECT** | CONNECT method over HTTP/2 for modern proxies | `[x]` |
| 5.5 | **Packet Fragmentation** | Fragment TLS Client Hello to bypass DPI | `[x]` |
| 5.6 | **Padding & Timing** | Add random padding, timing jitter to defeat traffic analysis | `[x]` |

**Deliverable:** Advanced obfuscation suite making tunnel traffic indistinguishable from normal HTTPS browsing.

---

### 🔷 Phase 6 — User Interface (Week 15–18)

> Build a polished, modern UI with Jetpack Compose.

| # | Task | Details | Status |
|---|---|---|---|
| 6.1 | **Home / Dashboard Screen** | Connection button, speed meter, timer, server info | `[x]` |
| 6.2 | **Server List Screen** | List/grid of servers with ping, load, country flags | `[x]` |
| 6.3 | **Payload Editor Screen** | Syntax-highlighted editor with macro autocomplete | `[x]` |
| 6.4 | **Settings Screen** | DNS, split tunneling, theme, about | `[x]` |
| 6.5 | **Connection Log Screen** | Real-time scrolling log viewer with filters | `[x]` |
| 6.6 | **Speed Graph** | Real-time upload/download speed chart | `[x]` |
| 6.7 | **Dark/Light Theme** | Material 3 dynamic color + manual dark mode | `[x]` |
| 6.8 | **Onboarding Flow** | First-launch tutorial / walkthrough | `[x]` |
| 6.9 | **Connection Animations** | Animated globe/shield during connecting/connected states | `[x]` |
| 6.10 | **Widget** | Home screen widget for quick connect/disconnect | `[x]` |

**UI Screens Breakdown:**

```
┌─────────────────────────────────────────────┐
│                HOME SCREEN                   │
│                                             │
│   ┌───────────────────────────────────┐     │
│   │        🌐 Astola VPN             │     │
│   │                                   │     │
│   │     ┌─────────────────────┐       │     │
│   │     │                     │       │     │
│   │     │   ⬤ CONNECT        │       │     │
│   │     │   (Animated Button) │       │     │
│   │     │                     │       │     │
│   │     └─────────────────────┘       │     │
│   │                                   │     │
│   │   ↑ 0.00 KB/s    ↓ 0.00 KB/s    │     │
│   │   Duration: 00:00:00             │     │
│   │                                   │     │
│   │   ┌─Server──────────────────┐    │     │
│   │   │ 🇵🇰 Pakistan - ISB      │    │     │
│   │   │ SSH | Port 443 | WS    │    │     │
│   │   └─────────────────────────┘    │     │
│   │                                   │     │
│   │   [Payload] [Servers] [Logs]      │     │
│   └───────────────────────────────────┘     │
│                                             │
│   ┌──┐  ┌──┐  ┌──┐  ┌──┐  ┌──┐            │
│   │🏠│  │📡│  │📝│  │📊│  │⚙️│            │
│   └──┘  └──┘  └──┘  └──┘  └──┘            │
│  Home  Servers Payload Logs Settings       │
└─────────────────────────────────────────────┘
```

**Deliverable:** Complete, polished UI with all screens, animations, and theming.

---

### 🔷 Phase 7 — Config System & File Format (Week 19–20)

> Build the encrypted config file system for sharing and cloud distribution.

| # | Task | Details | Status |
|---|---|---|---|
| 7.1 | **Config Data Model** | Define JSON schema for all connection parameters | `[x]` |
| 7.2 | **`.astola` File Format** | Custom binary format: header + AES-256-GCM encrypted JSON | `[x]` |
| 7.3 | **Config Export** | Export current settings as `.astola` file | `[x]` |
| 7.4 | **Config Import** | Import `.astola` files (file picker, deep link, share intent) | `[x]` |
| 7.5 | **Config Locking** | Option to lock configs (hide payload, prevent editing) | `[x]` |
| 7.6 | **Config URL Import** | Import from URL (paste link → auto-download & apply) | `[x]` |
| 7.7 | **QR Code** | Generate/scan QR codes containing config or config URLs | `[x]` |

**`.astola` File Structure:**
```
┌─────────────────────────────────────────┐
│  Magic Bytes: "ASTL" (4 bytes)          │
│  Version: uint8 (1 byte)               │
│  Flags: uint8 (1 byte)                 │
│    - Bit 0: Is Locked                  │
│    - Bit 1: Has Expiry                 │
│    - Bit 2-7: Reserved                 │
│  Expiry Timestamp: int64 (8 bytes)     │
│  IV/Nonce: (12 bytes)                  │
│  Encrypted Payload Length: uint32      │
│  Encrypted Payload: AES-256-GCM(JSON)  │
│  Auth Tag: (16 bytes)                  │
└─────────────────────────────────────────┘
```

**Deliverable:** Shareable, encrypted config file format with import/export.

---

### 🔷 Phase 8 — Cloud Backend & Server Management (Week 21–23)

> Build the backend for dynamic server distribution and app management.

| # | Task | Details | Status |
|---|---|---|---|
| 8.1 | **Firebase/Supabase Setup** | Project setup, auth, Firestore/database | `[ ]` |
| 8.2 | **Server Registry API** | CRUD for server entries (host, port, protocol, payload) | `[ ]` |
| 8.3 | **Cloud Server List** | App fetches server list from cloud on launch | `[ ]` |
| 8.4 | **Server Health Monitoring** | Background pings, auto-disable dead servers | `[ ]` |
| 8.5 | **Remote Config** | Push config updates (new payloads, announcements) | `[ ]` |
| 8.6 | **ISP Profile System** | Pre-built profiles per ISP (Jazz, Zong, Telenor, etc.) | `[ ]` |
| 8.7 | **Usage Analytics** | Anonymous connection success/fail rates per ISP | `[ ]` |
| 8.8 | **Push Notifications** | Notify users of new servers, payload updates | `[ ]` |
| 8.9 | **Admin Panel (Web)** | Simple web dashboard to manage servers & configs | `[ ]` |

**Deliverable:** Cloud-connected app with dynamic server lists and ISP-specific profiles.

---

### 🔷 Phase 9 — Testing, Optimization & Security (Week 24–26)

> Harden the app for production use.

| # | Task | Details | Status |
|---|---|---|---|
| 9.1 | **Unit Tests** | Test payload parser, config crypto, protocol engines | `[ ]` |
| 9.2 | **Integration Tests** | End-to-end tunnel tests against test SSH/V2Ray servers | `[ ]` |
| 9.3 | **Memory Optimization** | Profile and reduce memory usage in tunnel engines | `[ ]` |
| 9.4 | **Battery Optimization** | Minimize CPU wake-locks, optimize keepalive intervals | `[ ]` |
| 9.5 | **IP/DNS Leak Tests** | Verify no traffic leaks outside the tunnel | `[ ]` |
| 9.6 | **ProGuard/R8 Rules** | Obfuscate app code, shrink APK | `[ ]` |
| 9.7 | **Root/Magisk Detection** | Detect and warn about root-level traffic leaks | `[ ]` |
| 9.8 | **APK Signing & Versioning** | Release signing, semantic versioning | `[ ]` |
| 9.9 | **Crash Reporting** | Firebase Crashlytics integration | `[ ]` |

**Deliverable:** Stable, optimized, leak-free application.

---

### 🔷 Phase 10 — Launch & Distribution (Week 27–28)

> Prepare for public release.

| # | Task | Details | Status |
|---|---|---|---|
| 10.1 | **Play Store Listing** | Screenshots, description, feature graphic | `[ ]` |
| 10.2 | **Privacy Policy** | GDPR/Play Store compliant privacy policy | `[ ]` |
| 10.3 | **Terms of Service** | Legal terms for VPN usage | `[ ]` |
| 10.4 | **APK Distribution** | Direct APK download site (for users outside Play Store) | `[ ]` |
| 10.5 | **Telegram Bot/Channel** | Community channel for server updates & support | `[ ]` |
| 10.6 | **Documentation** | User guide, payload writing tutorial, FAQ | `[ ]` |

**Deliverable:** Published application with community support channels.

---

## 🗓️ Timeline Summary

| Phase | Description | Duration | Cumulative |
|---|---|---|---|
| **Phase 0** | Foundation & Research | 2 weeks | Week 2 |
| **Phase 1** | VPN Service & Traffic Capture | 2 weeks | Week 4 |
| **Phase 2** | SSH Tunnel Engine | 3 weeks | Week 7 |
| **Phase 3** | V2Ray / Xray Engine | 3 weeks | Week 10 |
| **Phase 4** | Shadowsocks & SlowDNS | 2 weeks | Week 12 |
| **Phase 5** | SNI Spoofing & Obfuscation | 2 weeks | Week 14 |
| **Phase 6** | User Interface | 4 weeks | Week 18 |
| **Phase 7** | Config System | 2 weeks | Week 20 |
| **Phase 8** | Cloud Backend | 3 weeks | Week 23 |
| **Phase 9** | Testing & Security | 3 weeks | Week 26 |
| **Phase 10** | Launch | 2 weeks | Week 28 |

> **Total Estimated Duration: ~28 weeks (7 months)**

> [!NOTE]
> Phases 6 (UI) can be developed in parallel with Phases 2–5 (engines) to reduce total timeline to ~20 weeks with a 2-person team.

---

## 🔑 Key Technical Risks & Mitigations

| Risk | Impact | Mitigation |
|---|---|---|
| Google Play VPN policy rejection | Can't distribute via Play Store | Direct APK distribution + alternative stores |
| ISP patches zero-rating exploit | Users lose free internet access | Cloud-pushed payload updates, multi-protocol fallback |
| V2Ray/Xray Go→Android compilation issues | Engine integration delays | Use pre-built AAR from SagerNet/v2rayNG community |
| Battery drain from persistent tunnel | Poor user reviews | Aggressive keepalive tuning, wake-lock optimization |
| DNS/IP leaks | Security vulnerability | Mandatory leak testing in CI, kill-switch implementation |
| Config file reverse engineering | Payload leaks to ISPs | Strong AES-256-GCM encryption, code obfuscation |

---

## 📚 Reference Projects (Open Source)

| Project | What to Study |
|---|---|
| [SagerNet](https://github.com/SagerNet/SagerNet) | Multi-protocol VPN architecture, V2Ray integration |
| [v2rayNG](https://github.com/2dust/v2rayNG) | V2Ray/Xray Android integration patterns |
| [shadowsocks-android](https://github.com/shadowsocks/shadowsocks-android) | Shadowsocks + VpnService + tun2socks |
| [Xray-core](https://github.com/XTLS/Xray-core) | Core V2Ray/VLESS/REALITY engine |
| [go-tun2socks](https://github.com/xjasonlyu/tun2socks) | TUN to SOCKS5 bridge |
| [JSch](http://www.jcraft.com/jsch/) | Java SSH client library |
| [iodine](https://github.com/yarrick/iodine) | DNS tunneling reference (SlowDNS basis) |

---

## 🚀 MVP Scope (Minimum Viable Product)

For the **first release**, target these features:

- [ ] Android VpnService traffic capture
- [ ] SSH tunnel with payload injection
- [ ] Basic payload macros (`[host_port]`, `[crlf]`, `[split]`)
- [ ] SSH over SSL/TLS
- [ ] SSH over WebSocket
- [ ] Home screen with connect/disconnect
- [ ] Server list (manual entry)
- [ ] Connection logs
- [ ] Basic settings (DNS, theme)
- [ ] `.astola` config import/export

**Post-MVP additions:** V2Ray, Shadowsocks, SlowDNS, cloud servers, SNI spoofing, admin panel.

---

*Last updated: August 5, 2026*
*Project: Astola VPN*
*Version: 1.0-draft*
