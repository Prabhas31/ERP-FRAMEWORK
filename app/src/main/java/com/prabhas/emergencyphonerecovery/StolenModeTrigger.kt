package com.prabhas.emergencyphonerecovery

import android.content.Context

object StolenModeTrigger {

    fun triggerStolenMode(context: Context) {
        if (!StolenModeManager.isStolen(context)) {
            StolenModeManager.activate(context)
        }
    }

    fun stopStolenMode(context: Context) {
        if (StolenModeManager.isStolen(context)) {
            StolenModeManager.deactivate(context)
        }
    }
}
