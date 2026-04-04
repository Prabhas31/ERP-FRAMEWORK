package com.prabhas.emergencyphonerecovery

import java.security.SecureRandom

object PhysicalKeyUtil {

    private const val CHAR_POOL = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
    // Removed confusing characters: I, O, 0, 1

    fun generatePhysicalRecoveryKey(): String {
        val random = SecureRandom()
        val key = StringBuilder()

        repeat(4) { block ->
            if (block > 0) key.append("-")

            repeat(4) {
                val index = random.nextInt(CHAR_POOL.length)
                key.append(CHAR_POOL[index])
            }
        }
        return key.toString()
    }
}
// git test