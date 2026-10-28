package com.nebulatech.lumi.security

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.security.SecureRandom

/**
 * Manages the generation and secure retrieval of the 256-bit database passphrase
 * using Android Keystore and EncryptedSharedPreferences.
 */
class DatabaseKeyManager(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val securePrefs by lazy {
        try {
            createSecurePrefs(context)
        } catch (e: Exception) {
            // Handle edge-case where keystore keys were invalidated or corrupted
            context.deleteSharedPreferences(PREFS_NAME)
            createSecurePrefs(context)
        }
    }

    private fun createSecurePrefs(context: Context) = EncryptedSharedPreferences.create(
        context,
        PREFS_NAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    /**
     * Retrieves the existing passphrase as a UTF-8 byte array, or generates a new
     * cryptographically secure 256-bit (64-character hex) passphrase on first launch.
     */
    fun getOrCreatePassphrase(): ByteArray {
        val existingPassphrase = securePrefs.getString(KEY_PASSPHRASE, null)
        if (!existingPassphrase.isNullOrEmpty()) {
            return existingPassphrase.toByteArray(Charsets.UTF_8)
        }

        // Generate 32 cryptographically secure random bytes (256 bits)
        val randomBytes = ByteArray(32)
        SecureRandom().nextBytes(randomBytes)
        val newPassphrase = randomBytes.joinToString("") { "%02x".format(it) }

        securePrefs.edit()
            .putString(KEY_PASSPHRASE, newPassphrase)
            .apply()

        return newPassphrase.toByteArray(Charsets.UTF_8)
    }

    /**
     * Retrieves the passphrase as a hex string for SQLCipher PRAGMA/ATTACH queries.
     */
    fun getPassphraseString(): String {
        return String(getOrCreatePassphrase(), Charsets.UTF_8)
    }

    companion object {
        const val PREFS_NAME = "lumi_secure_db_prefs"
        private const val KEY_PASSPHRASE = "lumi_db_passphrase"
    }
}
