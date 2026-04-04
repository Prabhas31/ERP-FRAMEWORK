package com.prabhas.emergencyphonerecovery

import android.content.Context
import android.content.SharedPreferences
import java.security.MessageDigest

object SecurePrefs {

    private const val PREF_NAME = "secure_prefs"

    // ---------- KEYS ----------
    private const val KEY_USER_ID = "user_id"
    private const val KEY_APP_LOCK_SET = "app_lock_set"
    private const val KEY_APP_LOCK_HASH = "app_lock_password_hash"
    private const val KEY_RECOVERY_SET = "recovery_password_set"
    private const val KEY_FAILED_ATTEMPTS = "failed_app_lock_attempts"

    private fun prefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    // ---------- USER ID (FINAL FIX) ----------
    fun initUserId(context: Context): String {

        val prefs = prefs(context)

        val existing = prefs.getString(KEY_USER_ID, null)
        if (existing != null) return existing

        val newUserId = "user_" + System.currentTimeMillis()

        prefs.edit().putString(KEY_USER_ID, newUserId).commit()

        return newUserId
    }

    fun getUserId(context: Context): String {
        return prefs(context).getString(KEY_USER_ID, null)
            ?: throw IllegalStateException("UserId not initialized")
    }

    // ---------- APP LOCK ----------
    fun setAppLockSet(context: Context, value: Boolean) {
        prefs(context).edit().putBoolean(KEY_APP_LOCK_SET, value).apply()
    }

    fun isAppLockSet(context: Context): Boolean {
        return prefs(context).getBoolean(KEY_APP_LOCK_SET, false)
    }

    fun setAppLockPassword(context: Context, password: String) {
        prefs(context).edit()
            .putString(KEY_APP_LOCK_HASH, sha256(password))
            .apply()
    }

    fun verifyAppLockPassword(context: Context, input: String): Boolean {
        val savedHash =
            prefs(context).getString(KEY_APP_LOCK_HASH, null) ?: return false
        return sha256(input) == savedHash
    }

    fun getAppLockHash(context: Context): String? {
        return prefs(context).getString(KEY_APP_LOCK_HASH, null)
    }

    fun saveAppLockHash(context: Context, hash: String) {
        prefs(context).edit().putString(KEY_APP_LOCK_HASH, hash).apply()
    }

    // ---------- RECOVERY ----------
    fun setRecoveryPasswordSet(context: Context, value: Boolean) {
        prefs(context).edit().putBoolean(KEY_RECOVERY_SET, value).apply()
    }

    fun isRecoveryPasswordSet(context: Context): Boolean {
        return prefs(context).getBoolean(KEY_RECOVERY_SET, false)
    }

    // ---------- FAILED ATTEMPTS ----------
    fun getFailedAttempts(context: Context): Int {
        return prefs(context).getInt(KEY_FAILED_ATTEMPTS, 0)
    }

    fun incrementFailedAttempts(context: Context) {
        prefs(context).edit()
            .putInt(KEY_FAILED_ATTEMPTS, getFailedAttempts(context) + 1)
            .apply()
    }

    fun resetFailedAttempts(context: Context) {
        prefs(context).edit().putInt(KEY_FAILED_ATTEMPTS, 0).apply()
    }

    // ---------- HASH ----------
    private fun sha256(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256")
            .digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}