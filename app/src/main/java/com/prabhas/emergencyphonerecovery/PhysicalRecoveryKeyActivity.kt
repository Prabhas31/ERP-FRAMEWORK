package com.prabhas.emergencyphonerecovery

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import java.util.UUID

class PhysicalRecoveryKeyActivity : AppCompatActivity() {

    private val userId = "owner_001"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_physical_recovery_key)

        val tvKey = findViewById<TextView>(R.id.tvPhysicalKey)

        val keyRef = FirebaseDatabase.getInstance()
            .getReference("users")
            .child(userId)
            .child("recovery")
            .child("physical_recovery_key")

        keyRef.get().addOnSuccessListener { snap ->

            val existingKey = snap.getValue(String::class.java)

            if (existingKey != null) {
                tvKey.text = existingKey
            } else {
                val newKey = generateKey()
                keyRef.setValue(newKey)
                tvKey.text = newKey

                Toast.makeText(
                    this,
                    "Save this key safely. It is shown only once.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun generateKey(): String {
        return UUID.randomUUID()
            .toString()
            .uppercase()
            .replace("-", "")
            .chunked(4)
            .take(4)
            .joinToString("-")
    }
}
