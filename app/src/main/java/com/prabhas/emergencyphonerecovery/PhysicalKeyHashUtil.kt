package com.prabhas.emergencyphonerecovery

import java.security.MessageDigest

object PhysicalKeyHashUtil {

    fun hashPhysicalKey(physicalKey: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(physicalKey.toByteArray())

        return hashBytes.joinToString("") { byte ->
            "%02x".format(byte)
        }
    }
}
// git test