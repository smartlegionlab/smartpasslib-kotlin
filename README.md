# SmartPasswordLib Kotlin <sup>v1.0.1</sup>

**Kotlin implementation of deterministic smart password generator. Same secret + same length = same password across all platforms (Python, JS, Go, Kotlin).**

---

[![GitHub top language](https://img.shields.io/github/languages/top/smartlegionlab/smartpasslib-kotlin)](https://github.com/smartlegionlab/smartpasslib-kotlin)
[![GitHub license](https://img.shields.io/github/license/smartlegionlab/smartpasslib-kotlin)](https://github.com/smartlegionlab/smartpasslib-kotlin/blob/master/LICENSE)
[![GitHub release](https://img.shields.io/github/v/release/smartlegionlab/smartpasslib-kotlin)](https://github.com/smartlegionlab/smartpasslib-kotlin/)
[![GitHub stars](https://img.shields.io/github/stars/smartlegionlab/smartpasslib-kotlin?style=social)](https://github.com/smartlegionlab/smartpasslib-kotlin/stargazers)
[![GitHub forks](https://img.shields.io/github/forks/smartlegionlab/smartpasslib-kotlin?style=social)](https://github.com/smartlegionlab/smartpasslib-kotlin/network/members)

---

## ⚠️ Disclaimer

**By using this software, you agree to the full disclaimer terms.**

**Summary:** Software provided "AS IS" without warranty. You assume all risks.

**Full legal disclaimer:** See [DISCLAIMER.md](https://github.com/smartlegionlab/smart-password-manager/blob/master/DISCLAIMER.md)

---

## Core Principles

- **Deterministic Generation**: Same secret + same length = same password, every time
- **Zero Storage**: Passwords exist only when generated, never stored
- **Cross-Platform**: Compatible with Python, JS, Go implementations
- **JVM/Android Ready**: Works on any Kotlin platform

## Key Features

- **Smart Password Generation**: Deterministic from secret phrase
- **Public/Private Key System**: 30 iterations for private key, 60 for public key
- **Secret Verification**: Verify secret without exposing it
- **Random Password Generation**: Cryptographically secure random passwords
- **Authentication Codes**: Short codes for 2FA/MFA (4-20 chars)
- **No External Dependencies**: Pure Kotlin, uses standard crypto

## Security Model

- **Proof of Knowledge**: Public keys verify secrets without exposing them
- **Deterministic Certainty**: Mathematical certainty in password regeneration
- **Ephemeral Passwords**: Passwords exist only in memory during generation
- **Local Computation**: No data leaves your device
- **No Recovery Backdoors**: Lost secret = permanently lost passwords (by design)

---

## Research Paradigms & Publications

- **[Pointer-Based Security Paradigm](https://doi.org/10.5281/zenodo.17204738)** - Architectural Shift from Data Protection to Data Non-Existence
- **[Local Data Regeneration Paradigm](https://doi.org/10.5281/zenodo.17264327)** - Ontological Shift from Data Transmission to Synchronous State Discovery

---

## Technical Foundation

**Key derivation (same as Python/JS/Go versions):**

| Key Type    | Iterations | Purpose                            |
|-------------|------------|------------------------------------|
| Private Key | 30         | Password generation (never stored) |
| Public Key  | 60         | Verification (stored on server)    |

**Character Set:** `abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$&*-_`

## Installation

Copy `SmartPasswordLib.kt` to your project.

## Quick Usage

### Generate Smart Password
```kotlin
import com.smartlegionlab.smartpasslib.SmartPasswordLib

fun main() {
    val secret = "MyCatHippo2026"
    val length = 16
    
    val password = SmartPasswordLib.generateSmartPassword(secret, length)
    println(password) // e.g., "jrh_E5V!2#neNjnP"
}
```

### Generate Public/Private Keys
```kotlin
val secret = "MyCatHippo2026"

val publicKey = SmartPasswordLib.generatePublicKey(secret)
val privateKey = SmartPasswordLib.generatePrivateKey(secret)

println("Public Key (store on server): $publicKey")
println("Private Key (never store): $privateKey")
```

### Verify Secret Against Public Key
```kotlin
val secret = "MyCatHippo2026"
val storedPublicKey = "..." // from server

val isValid = SmartPasswordLib.verifySecret(secret, storedPublicKey)
if (isValid) {
    val password = SmartPasswordLib.generateSmartPassword(secret, 16)
}
```

### Generate Random Passwords
```kotlin
// Strong random (cryptographically secure)
val strong = SmartPasswordLib.generateStrongPassword(20)

// Base random
val base = SmartPasswordLib.generateBasePassword(16)

// Authentication code (4-20 chars)
val code = SmartPasswordLib.generateCode(8)
```

## API Reference

### Properties

| Property  | Type   | Description                       |
|-----------|--------|-----------------------------------|
| `VERSION` | String | Library version                   |
| `CHARS`   | String | Character set used for generation |

### Methods

| Method                                  | Parameters        | Returns | Description                      |
|-----------------------------------------|-------------------|---------|----------------------------------|
| `generatePrivateKey(secret)`            | secret: String    | String  | Private key (30 iterations)      |
| `generatePublicKey(secret)`             | secret: String    | String  | Public key (60 iterations)       |
| `verifySecret(secret, publicKey)`       | secret, publicKey | Boolean | Verify secret matches public key |
| `generateSmartPassword(secret, length)` | secret, length    | String  | Deterministic password           |
| `generateStrongPassword(length)`        | length            | String  | Cryptographically random         |
| `generateBasePassword(length)`          | length            | String  | Simple random password           |
| `generateCode(length)`                  | length            | String  | Short code (4-20 chars)          |

### Input Validation

| Parameter | Minimum | Maximum |
|-----------|---------|---------|
| Secret phrase | 12 chars | unlimited |
| Password length | 12 chars | 1000 chars |
| Code length | 4 chars | 20 chars |

## Security Requirements

### Secret Phrase
- **Minimum 12 characters** (enforced)
- Case-sensitive
- Use mix of: uppercase, lowercase, numbers, symbols, emoji, or Cyrillic
- Never store digitally

### Strong Secret Examples
```
✅ "MyCatHippo2026"          — mixed case + numbers
✅ "P@ssw0rd!LongSecret"     — special chars + numbers + length
✅ "КотБегемот2026НаДиете"   — Cyrillic + numbers
```

### Weak Secret Examples (avoid)
```
❌ "password"                — dictionary word, too short
❌ "1234567890"              — only digits, too short
❌ "qwerty123"               — keyboard pattern
```

## Cross-Platform Compatibility

SmartPasswordLib Kotlin produces **identical passwords** to:

| Platform   | Repository                                                                                                                |
|------------|:--------------------------------------------------------------------------------------------------------------------------|
| Python     | [smartpasslib](https://github.com/smartlegionlab/smartpasslib)                                                            |
| JavaScript | [smartpasslib-js](https://github.com/smartlegionlab/smartpasslib-js)                                                      |
| Kotlin     | [smartpasslib-kotlin](https://github.com/smartlegionlab/smartpasslib-kotlin)                                              |
| Go         | [smartpasslib-go](https://github.com/smartlegionlab/smartpasslib-go)                                                      |
| Web        | [Web Manager](https://github.com/smartlegionlab/smart-password-manager-web)                                               |
| Android    | [Android Manager](https://github.com/smartlegionlab/smart-password-manager-android)                                       |
| Desktop    | [Desktop Manager](https://github.com/smartlegionlab/smart-password-manager-desktop)                                       |
| CLI        | [CLI PassMan](https://github.com/smartlegionlab/clipassman) / [CLI PassGen](https://github.com/smartlegionlab/clipassgen) |

## Testing

### Install Kotlin


Arch Linux: `sudo pacman -S kotlin`

Run the test script:
```bash
kotlin test.kts
```

## Ecosystem

**Core Libraries:**
- **[smartpasslib](https://github.com/smartlegionlab/smartpasslib)** - Python implementation
- **[smartpasslib-js](https://github.com/smartlegionlab/smartpasslib-js)** - JavaScript implementation
- **[smartpasslib-kotlin](https://github.com/smartlegionlab/smartpasslib-kotlin)** - Kotlin implementation
- **[smartpasslib-go](https://github.com/smartlegionlab/smartpasslib-go)** - Golang implementation

**Applications:**
- **[Desktop Manager](https://github.com/smartlegionlab/smart-password-manager-desktop)** - Cross-platform desktop app
- **[CLI PassMan](https://github.com/smartlegionlab/clipassman)** - Console password manager
- **[CLI PassGen](https://github.com/smartlegionlab/clipassgen)** - Console password generator
- **[Web Manager](https://github.com/smartlegionlab/smart-password-manager-web)** - Web interface
- **[Android Manager](https://github.com/smartlegionlab/smart-password-manager-android)** - Mobile Android app

## License

**[BSD 3-Clause License](LICENSE)**

Copyright (©) 2026, [Alexander Suvorov](https://github.com/smartlegionlab)

## Author

**Alexander Suvorov** - [GitHub](https://github.com/smartlegionlab)

---

## Support

- **Issues**: [GitHub Issues](https://github.com/smartlegionlab/smartpasslib-kotlin/issues)
- **Documentation**: This README

---

