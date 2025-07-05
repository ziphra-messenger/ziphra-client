# privacity-client

### ✅ `README.md` (ENGLISH VERSION)

```markdown
# 📱 Privacity Client

**Privacity** is a secure messaging app for Android focused on **privacy**, **confidentiality**, and **end-to-end encryption**. This is the **Android client**, built in **Java**, using **WebSocket** for real-time communication and designed to protect user data against tracking, leaks, and unauthorized access.

---

## 🔐 Features

- 🔒 End-to-end encryption using trusted cryptographic libraries (Bouncy Castle)
- 📡 Real-time chat via WebSocket
- 🧨 Self-destructing messages (configurable)
- 🔑 Strong password policy enforcement (Passay)
- 🌍 Multi-language support (via nv-i18n)
- 📦 Modular architecture with reusable libraries
- 🎯 Emoji support, QR integration, notifications, audio & video
- 🧪 Environment switching: `DEVELOPER`, `QA`, `RELEASE`

---

## 🧩 Project Structure

```

privacity-client/
├── app/

│   ├── java/com/privacity/         # Android client source code

│   ├── res/                        # UI resources

│   └── build.gradle                # Client build config

├── lib/                            # Shared libraries (local module)

└── version.properties              # Auto-managed versioning info

````

---

## ⚙️ Tech Stack

- Java 8
- WebSocket
- RxJava 2 / Retrofit 2
- ExoPlayer (for media playback)
- Dagger 2 (dependency injection)
- ZXing (QR scanner)
- SLF4J, Apache HttpClient, Commons IO
- Spring Web (limited)
- Passay (password validation)
- Bouncy Castle (crypto)
- SendGrid (email alerts)

---

## 🚀 Build Instructions

### Requirements

- Android Studio Flamingo (or newer)
- JDK 8
- Android SDK 30 + Build Tools 30.0.3
- Gradle 7.x+

### Clone & Build

```bash
git clone https://github.com/kevormagic/privacity-client.git
cd privacity-client
./gradlew assembleDeveloper
````

---

## 🧮 Versioning

The file `version.properties` is automatically updated during release builds:

* `VERSION_NUMBER`: major version
* `VERSION_PATCH`: minor version
* `VERSION_BUILD`: incremental build ID (with timestamp)

The final APK is named like:

```
AppName-<version>.apk
```

...and copied to `../apk/` automatically after each release.

---

## 🌐 Environments

| Environment | Debuggable | Placeholder |
| ----------- | ---------- | ----------- |
| `developer` | ✅          | `DEVELOPER` |
| `qa`        | ✅          | `QA`        |
| `release`   | ❌          | `RELEASE`   |

---

## 📤 Distribution

The `assembleRelease` Gradle task produces a signed release build and exports it automatically. You can find the APK under:

```
../apk/AppName-<version>.apk
```

---

## 🤝 Contributing

Contributions are welcome!

1. Fork the repository
2. Create a branch: `feature/my-feature`
3. Commit your changes
4. Open a Pull Request

---

## 🛡️ Security Notice

If you discover a security issue or vulnerability, please **do not create a public issue**. Instead, report it directly to:

📧 [security@privacity.app](mailto:security@privacity.app)

---

## 📃 License

This project is **proprietary and closed-source**. Some components are licensed under open-source licenses (Apache 2.0, MIT), but the main application is not freely redistributable or modifiable.

---

## 📞 Contact

* Email: [support@privacity.app](mailto:support@privacity.app)
* Website: [https://privacity.app](https://privacity.app)

---

> “Privacy is not a luxury, it’s a right.” — *Privacity*



- `.gitignore` optimizado para Android Studio
- `LICENSE` (aunque sea personalizado tipo "All rights reserved")
- Workflow para builds automáticos con GitHub Actions

¿O querés una estructura para documentación técnica (por ejemplo con `docs/`)?
```
