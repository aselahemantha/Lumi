package com.nebulatech.lumi.data.local.database

import android.content.Context
import android.util.Log
import net.zetetic.database.sqlcipher.SQLiteDatabase
import java.io.File
import java.io.FileInputStream

/**
 * Handles migration of existing unencrypted SQLite databases to SQLCipher encrypted format.
 */
object DatabaseMigrator {

    private const val TAG = "DatabaseMigrator"
    private val SQLITE_HEADER = "SQLite format 3\u0000".toByteArray(Charsets.US_ASCII)

    init {
        try {
            System.loadLibrary("sqlcipher")
        } catch (e: UnsatisfiedLinkError) {
            Log.e(TAG, "Failed to load sqlcipher native library", e)
        }
    }

    /**
     * Checks if the given database exists and is unencrypted.
     * An unencrypted SQLite database always begins with the 16-byte magic header "SQLite format 3\0".
     */
    fun isPlaintextDatabase(dbFile: File): Boolean {
        if (!dbFile.exists() || dbFile.length() < 16) return false
        val header = ByteArray(16)
        try {
            FileInputStream(dbFile).use { it.read(header) }
            return header.contentEquals(SQLITE_HEADER)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to inspect database header", e)
            return false
        }
    }

    /**
     * Migrates an existing unencrypted database file to SQLCipher format if needed.
     */
    fun migrateIfNeeded(context: Context, databaseName: String, passphraseHex: String) {
        val dbFile = context.getDatabasePath(databaseName)
        if (!isPlaintextDatabase(dbFile)) {
            Log.d(TAG, "Database is already encrypted or does not exist yet. No migration needed.")
            return
        }

        Log.i(TAG, "Unencrypted SQLite database detected at ${dbFile.absolutePath}. Starting encryption migration...")

        val parentDir = dbFile.parentFile ?: return
        val tempEncryptedFile = File(parentDir, "${databaseName}_migrated")
        if (tempEncryptedFile.exists()) {
            tempEncryptedFile.delete()
        }

        var unencryptedDb: SQLiteDatabase? = null
        try {
            // Open the plaintext database (no password) in SQLCipher
            unencryptedDb = SQLiteDatabase.openOrCreateDatabase(dbFile.absolutePath, null)

            // Attach the new database with encryption key and export data
            val attachSql = "ATTACH DATABASE '${tempEncryptedFile.absolutePath}' AS encrypted KEY '$passphraseHex';"
            unencryptedDb.rawExecSQL(attachSql)
            unencryptedDb.rawExecSQL("SELECT sqlcipher_export('encrypted');")
            unencryptedDb.rawExecSQL("DETACH DATABASE encrypted;")
            unencryptedDb.close()
            unencryptedDb = null

            // Delete old unencrypted journal/WAL files if present
            File(parentDir, "$databaseName-wal").delete()
            File(parentDir, "$databaseName-shm").delete()
            File(parentDir, "$databaseName-journal").delete()

            // Replace original unencrypted database file with the newly encrypted database
            if (dbFile.delete()) {
                val renamed = tempEncryptedFile.renameTo(dbFile)
                if (renamed) {
                    Log.i(TAG, "Database encryption migration completed successfully.")
                } else {
                    Log.e(TAG, "Failed to rename migrated database to $databaseName")
                }
            } else {
                Log.e(TAG, "Failed to delete original unencrypted database file during migration")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error during database encryption migration", e)
            if (tempEncryptedFile.exists()) {
                tempEncryptedFile.delete()
            }
            throw e
        } finally {
            unencryptedDb?.close()
        }
    }
}
