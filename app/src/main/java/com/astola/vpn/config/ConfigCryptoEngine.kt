package com.astola.vpn.config

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

object ConfigCryptoEngine {

    private val MAGIC_BYTES = "ASTL".toByteArray(Charsets.UTF_8) // 4 bytes header
    private const val VERSION: Byte = 1
    private const val DEFAULT_SECRET_PASS = "AstolaVPNSecretKey2026#Secure"
    private const val GCM_TAG_LENGTH = 128
    private const val IV_LENGTH = 12

    /**
     * Encrypts an AstolaConfigModel JSON payload into a binary .astola file payload using AES-256-GCM.
     */
    fun encryptConfig(jsonString: String, password: String = DEFAULT_SECRET_PASS): ByteArray {
        val random = SecureRandom()
        val iv = ByteArray(IV_LENGTH)
        random.nextBytes(iv)

        val salt = ByteArray(16)
        random.nextBytes(salt)

        val secretKey = deriveKey(password, salt)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val gcmSpec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmSpec)

        val cipherText = cipher.doFinal(jsonString.toByteArray(Charsets.UTF_8))

        val baos = ByteArrayOutputStream()
        val dos = DataOutputStream(baos)
        dos.write(MAGIC_BYTES)
        dos.writeByte(VERSION.toInt())
        dos.write(salt)
        dos.write(iv)
        dos.writeInt(cipherText.size)
        dos.write(cipherText)
        dos.flush()

        return baos.toByteArray()
    }

    /**
     * Decrypts a binary .astola file payload back into the JSON config string.
     */
    fun decryptConfig(data: ByteArray, password: String = DEFAULT_SECRET_PASS): String {
        val bais = ByteArrayInputStream(data)
        val dis = DataInputStream(bais)

        val magic = ByteArray(4)
        dis.readFully(magic)
        check(magic.contentEquals(MAGIC_BYTES)) { "Invalid .astola file format (magic header mismatch)" }

        val version = dis.readByte()
        check(version == VERSION) { "Unsupported .astola file version: $version" }

        val salt = ByteArray(16)
        dis.readFully(salt)

        val iv = ByteArray(IV_LENGTH)
        dis.readFully(iv)

        val cipherLength = dis.readInt()
        val cipherText = ByteArray(cipherLength)
        dis.readFully(cipherText)

        val secretKey = deriveKey(password, salt)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val gcmSpec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmSpec)

        val plainTextBytes = cipher.doFinal(cipherText)
        return String(plainTextBytes, Charsets.UTF_8)
    }

    private fun deriveKey(password: String, salt: ByteArray): SecretKeySpec {
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val spec = PBEKeySpec(password.toCharArray(), salt, 10000, 256)
        val secretKey = factory.generateSecret(spec)
        return SecretKeySpec(secretKey.encoded, "AES")
    }
}
