package com.prabhas.emergencyphonerecovery

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.prabhas.emergencyphonerecovery.LiveLocationService

object StolenModeManager {

    private const val PREF_NAME = "stolen_mode_prefs"
    private const val KEY_STOLEN = "is_stolen"
    private const val KEY_RECOVERING = "is_recovering"

    fun isStolen(context: Context): Boolean {
        return context
            .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_STOLEN, false)
    }

    fun startRecovery(context: Context) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_RECOVERING, true)
            .apply()
    }

    /** Called ONLY after 5 failed app-lock attempts */
    fun activate(context: Context) {

        val prefs =
            context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        // 🚫 Do not re-enter stolen mode during recovery
        if (prefs.getBoolean(KEY_RECOVERING, false)) return

        prefs.edit()
            .putBoolean(KEY_STOLEN, true)
            .putBoolean(KEY_RECOVERING, false)
            .apply()

        // 📍 Start live tracking
        ContextCompat.startForegroundService(
            context,
            Intent(context, LiveLocationService::class.java)
        )

        // 🔗 Existing session logic (unchanged)
        ShareSessionManager.onStolenModeActivated(context)
    }

    /** FINAL EXIT AFTER CORRECT RECOVERY PASSWORD */
    fun deactivate(context: Context) {

        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_STOLEN, false)
            .putBoolean(KEY_RECOVERING, false)
            .apply()

        // 🛑 Stop tracking
        context.stopService(
            Intent(context, LiveLocationService::class.java)
        )

        // 🧹 Cleanup sessions
        ShareSessionManager.onRecoveryCompleted(context)
    }
}
