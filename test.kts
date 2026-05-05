#!/usr/bin/env kotlin

import java.security.MessageDigest
import java.security.SecureRandom

// ============================================================
// SmartPassLib v4.0.0 - Quick Test
// ============================================================

val CHARS = "!@#$%^&*()_+-=[]{};:,.<>?/ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyz"

fun sha256(text: String): String {
    val bytes = text.toByteArray()
    val digest = MessageDigest.getInstance("SHA-256").digest(bytes)
    return digest.joinToString("") { "%02x".format(it) }
}

fun getStepsFromSecret(secret: String, minSteps: Int, maxSteps: Int, salt: String): Int {
    val hashValue = sha256("$secret:$salt")
    val hashInt = hashValue.substring(0, 8).toLong(16)
    return minSteps + (hashInt % (maxSteps - minSteps + 1)).toInt()
}

fun generateKey(secret: String, steps: Int, salt: String): String {
    var allHash = sha256("$secret:$salt")
    for (i in 0 until steps) {
        allHash = sha256("$allHash:$i")
    }
    return allHash
}

fun generatePrivateKey(secret: String): String {
    val steps = getStepsFromSecret(secret, 15, 30, "private")
    return generateKey(secret, steps, "private")
}

fun generatePublicKey(secret: String): String {
    val steps = getStepsFromSecret(secret, 45, 60, "public")
    return generateKey(secret, steps, "public")
}

fun hexToBytes(hex: String): ByteArray {
    val result = ByteArray(hex.length / 2)
    for (i in hex.indices step 2) {
        result[i / 2] = hex.substring(i, i + 2).toInt(16).toByte()
    }
    return result
}

fun generateSmartPassword(secret: String, length: Int): String {
    require(secret.length >= 12) { "Secret must be at least 12 chars" }
    require(length in 12..100) { "Length must be 12-100" }

    val privateKey = generatePrivateKey(secret)
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

// ============================================================
// TEST
// ============================================================

println()
println("=".repeat(60))
println("SmartPassLib Kotlin v4.0.0 - Generator Test")
println("=".repeat(60))
println()

val secret1 = "MyCatHippo2026"
val lengths = listOf(12, 16, 20, 24)

println("Secret phrase 1: $secret1")
println()

for (len in lengths) {
    val password = generateSmartPassword(secret1, len)
    println("Length $len: $password")
}

println()
println("Public/Private keys for secret 1:")
val pubKey1 = generatePublicKey(secret1)
val privKey1 = generatePrivateKey(secret1)
println("Public key:  $pubKey1")
println("Private key: $privKey1")
if (pubKey1 != privKey1) {
    println("Keys are different: YES")
} else {
    println("Keys are different: NO")
}

println()
println("-".repeat(60))

val secret2 = "TestSecret2026!"
println()
println("Secret phrase 2: $secret2")
println()

val password1 = generateSmartPassword(secret2, 16)
val password2 = generateSmartPassword(secret2, 16)

println("Determinism test:")
if (password1 == password2) {
    println("Same secret + same length = SAME")
} else {
    println("Same secret + same length = DIFFERENT")
}
println("Password: $password1")

println()
println("Public/Private keys for secret 2:")
val pubKey2 = generatePublicKey(secret2)
val privKey2 = generatePrivateKey(secret2)
println("Public key:  $pubKey2")
println("Private key: $privKey2")
if (pubKey2 != privKey2) {
    println("Keys are different: YES")
} else {
    println("Keys are different: NO")
}

println()
println("=".repeat(60))
println("Test complete")
println("=".repeat(60))