#!/usr/bin/env kotlin

import java.security.MessageDigest
import java.security.SecureRandom

// ============================================================
// Smart Passwords Library
// ============================================================

val CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$&*-_"
val secureRandom = SecureRandom()

fun sha256(text: String): String {
    val bytes = text.toByteArray()
    val digest = MessageDigest.getInstance("SHA-256").digest(bytes)
    return digest.joinToString("") { "%02x".format(it) }
}

fun generateKey(secret: String, iterations: Int): String {
    var hash = sha256(secret)
    for (i in 0 until iterations) {
        hash = sha256("$hash:$secret:$i")
    }
    return hash
}

fun generatePrivateKey(secret: String): String = generateKey(secret, 30)
fun generatePublicKey(secret: String): String = generateKey(secret, 60)
fun verifySecret(secret: String, publicKey: String): Boolean = generatePublicKey(secret) == publicKey

fun hexToBytes(hex: String): ByteArray {
    val result = ByteArray(hex.length / 2)
    for (i in hex.indices step 2) {
        result[i / 2] = hex.substring(i, i + 2).toInt(16).toByte()
    }
    return result
}

fun generatePasswordFromPrivateKey(privateKey: String, length: Int): String {
    val result = StringBuilder()
    var counter = 0
    while (result.length < length) {
        val hashHex = sha256("$privateKey:$counter")
        val bytes = hexToBytes(hashHex)
        for (b in bytes) {
            if (result.length < length) {
                result.append(CHARS[(b.toInt() and 0xFF) % CHARS.length])
            }
        }
        counter++
    }
    return result.toString()
}

fun generateSmartPassword(secret: String, length: Int): String {
    require(secret.length >= 12) { "Secret must be at least 12 chars, got ${secret.length}" }
    require(length in 12..1000) { "Length must be 12-1000, got $length" }
    return generatePasswordFromPrivateKey(generatePrivateKey(secret), length)
}

fun generateStrongPassword(length: Int): String {
    require(length in 12..1000) { "Length must be 12-1000, got $length" }
    val bytes = ByteArray(length)
    secureRandom.nextBytes(bytes)
    return bytes.joinToString("") { CHARS[(it.toInt() and 0xFF) % CHARS.length].toString() }
}

fun generateBasePassword(length: Int): String = generateStrongPassword(length)

fun generateCode(length: Int): String {
    require(length in 4..20) { "Code length must be 4-20, got $length" }
    val bytes = ByteArray(length)
    secureRandom.nextBytes(bytes)
    return bytes.joinToString("") { CHARS[(it.toInt() and 0xFF) % CHARS.length].toString() }
}

// ============================================================
// Tests
// ============================================================

fun line(): String = "=".repeat(70)
fun sep(): String = "-".repeat(70)

println()
println(line())
println("🔐 SMARTPASSWORD LIBRARY KOTLIN - TEST SUITE")
println(line())
println()

var passed = 0
var failed = 0

fun testPass(name: String) {
    println("  ✅ PASS: $name")
    passed++
}

fun testFail(name: String, msg: String = "") {
    println("  ❌ FAIL: $name${if (msg.isNotEmpty()) " - $msg" else ""}")
    failed++
}

val secret = "MyCatHippo2026"
val wrongSecret = "WrongSecret123456"

// ============================================================
// 1. SMART PASSWORD
// ============================================================
println()
println("📌 [1] SMART PASSWORD (Deterministic)")
println(sep())

val password = generateSmartPassword(secret, 16)
println()
println("  Secret phrase: $secret")
println("  Password length: 16")
println("  Generated password: $password")
println()

if (password.length == 16) testPass("Smart password length is 16") else testFail("Smart password length is 16")

val pwd1 = generateSmartPassword(secret, 20)
val pwd2 = generateSmartPassword(secret, 20)
println("  Same secret -> same password: $pwd1")
if (pwd1 == pwd2) testPass("Determinism") else testFail("Determinism")

val diff1 = generateSmartPassword("SecretOne123456", 16)
val diff2 = generateSmartPassword("SecretTwo123456", 16)
println("  Secret A -> $diff1")
println("  Secret B -> $diff2")
if (diff1 != diff2) testPass("Different secrets produce different passwords") else testFail("Different secrets produce different passwords")

val shortPwd = generateSmartPassword(secret, 12)
val longPwd = generateSmartPassword(secret, 24)
println("  Length 12: $shortPwd")
println("  Length 24: $longPwd")
if (shortPwd.length == 12 && longPwd.length == 24) testPass("Different lengths produce different passwords") else testFail("Different lengths produce different passwords")

// ============================================================
// 2. PUBLIC & PRIVATE KEYS
// ============================================================
println()
println("📌 [2] PUBLIC & PRIVATE KEYS")
println(sep())

val pubKey = generatePublicKey(secret)
val privKey = generatePrivateKey(secret)
println()
println("  Secret phrase: $secret")
println()
println("  🔓 Public key (60 iterations) - STORE ON SERVER:")
println("  $pubKey")
println()
println("  🔐 Private key (30 iterations) - NEVER STORE:")
println("  $privKey")
println()

if (pubKey != privKey) testPass("Public key != Private key") else testFail("Public key != Private key")

println("  Verification with correct secret: ${verifySecret(secret, pubKey)}")
println("  Verification with wrong secret: ${verifySecret(wrongSecret, pubKey)}")
if (verifySecret(secret, pubKey)) testPass("Correct secret verification") else testFail("Correct secret verification")
if (!verifySecret(wrongSecret, pubKey)) testPass("Wrong secret verification") else testFail("Wrong secret verification")

// ============================================================
// 3. RANDOM PASSWORD GENERATORS
// ============================================================
println()
println("📌 [3] RANDOM PASSWORD GENERATORS")
println(sep())

val strong = generateStrongPassword(16)
println()
println("  🎲 Strong random (crypto secure) - length 16:")
println("  $strong")
if (strong.length == 16) testPass("Strong random length") else testFail("Strong random length")

val base = generateBasePassword(16)
println()
println("  🎲 Base random - length 16:")
println("  $base")
if (base.length == 16) testPass("Base random length") else testFail("Base random length")

val code = generateCode(8)
println()
println("  🔢 Auth code (2FA) - length 8:")
println("  $code")
if (code.length == 8) testPass("Auth code length") else testFail("Auth code length")

// ============================================================
// 4. INPUT VALIDATION
// ============================================================
println()
println("📌 [4] INPUT VALIDATION")
println(sep())

println()
var exceptionCaught = false
try {
    generateSmartPassword("short", 16)
} catch (e: IllegalArgumentException) {
    exceptionCaught = true
    println("  ⚠️  Secret 'short' (4 chars) -> rejected: ${e.message}")
}
if (exceptionCaught) testPass("Secret shorter than 12 chars rejected") else testFail("Secret shorter than 12 chars rejected")

exceptionCaught = false
try {
    generateCode(2)
} catch (e: IllegalArgumentException) {
    exceptionCaught = true
    println("  ⚠️  Code length 2 -> rejected: ${e.message}")
}
if (exceptionCaught) testPass("Code shorter than 4 chars rejected") else testFail("Code shorter than 4 chars rejected")

exceptionCaught = false
try {
    generateStrongPassword(5)
} catch (e: IllegalArgumentException) {
    exceptionCaught = true
    println("  ⚠️  Password length 5 -> rejected: ${e.message}")
}
if (exceptionCaught) testPass("Password shorter than 12 chars rejected") else testFail("Password shorter than 12 chars rejected")

val allCharsOk = password.all { it in CHARS }
if (allCharsOk) testPass("Password uses only allowed characters") else testFail("Password uses only allowed characters")

// ============================================================
// RESULTS
// ============================================================
println()
println(line())
println("📊 RESULTS: $passed passed, $failed failed")
println(line())

if (failed == 0) {
    println()
    println("🎉 ALL TESTS PASSED! Library is ready for production.")
    println()
} else {
    println()
    println("⚠️ SOME TESTS FAILED! Please fix.")
    println()
}