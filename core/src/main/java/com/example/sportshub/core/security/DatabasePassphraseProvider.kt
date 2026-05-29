package com.example.sportshub.core.security

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.core.content.edit
import java.security.KeyStore
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

object DatabasePassphraseProvider {

    private val SQLITE_HEADER = "SQLite format 3\u0000".toByteArray(Charsets.US_ASCII)
    private const val ANDROID_KEYSTORE = "AndroidKeyStore"
    private const val KEY_ALIAS = "sporthub_database_key"
    private const val PREFS_NAME = "sporthub_secure_database"
    private const val PASSPHRASE_KEY = "encrypted_passphrase"
    private const val PASSPHRASE_SIZE = 32
    private const val GCM_TAG_SIZE = 128

    fun getPassphrase(context: Context): ByteArray {
        val appContext = context.applicationContext
        val prefs = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val storedPassphrase = prefs.getString(PASSPHRASE_KEY, null)

        if (storedPassphrase != null) {
            return decrypt(storedPassphrase)
        }

        val passphrase = ByteArray(PASSPHRASE_SIZE)
        SecureRandom().nextBytes(passphrase)
        prefs.edit {
            putString(PASSPHRASE_KEY, encrypt(passphrase))
        }

        return passphrase
    }

    fun deleteLegacyPlaintextDatabase(context: Context, databaseName: String) {
        val databaseFile = context.applicationContext.getDatabasePath(databaseName)
        if (!databaseFile.exists() || databaseFile.length() < SQLITE_HEADER.size) return

        val header = ByteArray(SQLITE_HEADER.size)
        databaseFile.inputStream().use { inputStream ->
            inputStream.read(header)
        }

        if (header.contentEquals(SQLITE_HEADER)) {
            context.applicationContext.deleteDatabase(databaseName)
        }
    }

    private fun encrypt(passphrase: ByteArray): String {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, getOrCreateSecretKey())
        val encryptedPassphrase = cipher.doFinal(passphrase)
        val payload = cipher.iv + encryptedPassphrase
        return Base64.encodeToString(payload, Base64.NO_WRAP)
    }

    private fun decrypt(encryptedPassphrase: String): ByteArray {
        val payload = Base64.decode(encryptedPassphrase, Base64.NO_WRAP)
        val iv = payload.copyOfRange(0, 12)
        val cipherText = payload.copyOfRange(12, payload.size)

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, getOrCreateSecretKey(), GCMParameterSpec(GCM_TAG_SIZE, iv))
        return cipher.doFinal(cipherText)
    }

    private fun getOrCreateSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
        keyStore.getKey(KEY_ALIAS, null)?.let { return it as SecretKey }

        val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
        val keySpec = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setRandomizedEncryptionRequired(true)
            .setUnlockedDeviceRequired(true)
            .build()

        keyGenerator.init(keySpec)
        return keyGenerator.generateKey()
    }
}
