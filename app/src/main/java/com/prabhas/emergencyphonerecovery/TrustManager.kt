package com.prabhas.emergencyphonerecovery

import android.content.Context
import java.security.MessageDigest
import java.util.UUID

object TrustManager {

    private const val PREF_NAME = "trust_prefs"
    private const val KEY_TRUST_ID = "trust_id"

    fun getLocalTrust(context: Context): String? {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_TRUST_ID, null)
    }

    fun createTrust(context: Context): String {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        val existingTrust = prefs.getString(KEY_TRUST_ID, null)
        if (existingTrust != null) {
            return existingTrust
        }

        val newTrust = UUID.randomUUID().toString()
        prefs.edit().putString(KEY_TRUST_ID, newTrust).apply()

        return newTrust
    }

    fun restoreTrust(context: Context, trustIdFromServer: String) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_TRUST_ID, trustIdFromServer).apply()
    }

    fun getTrustId(context: Context): String {
        return getLocalTrust(context) ?: createTrust(context)
    }

    // ✅ ADD THIS FUNCTION (fixes hash error)
    fun hash(input: String): String {
        val bytes = MessageDigest
            .getInstance("SHA-256")
            .digest(input.toByteArray())

        return bytes.joinToString("") {
            "%02x".format(it)
        }
    }

    // ✅ ADD THIS FUNCTION (fixes clearLocalTrust error)
    fun clearLocalTrust(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().remove(KEY_TRUST_ID).apply()
    }
}
