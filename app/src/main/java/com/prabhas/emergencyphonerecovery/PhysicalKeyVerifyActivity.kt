package com.prabhas.emergencyphonerecovery

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase

class PhysicalKeyVerifyActivity : AppCompatActivity() {

    private val userId = "owner_001"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_physical_key_verify)

        val etKey = findViewById<EditText>(R.id.etPhysicalKey)
        val btnVerify = findViewById<Button>(R.id.btnVerifyKey)

        btnVerify.setOnClickListener {

            val enteredRaw = etKey.text.toString()
            if (enteredRaw.isBlank()) {
                toast("Enter the physical recovery key")
                return@setOnClickListener
            }

            // ✅ Normalize USER INPUT
            val enteredKey = enteredRaw
                .trim()
                .uppercase()
                .replace(" ", "")

            val ref = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(userId)
                .child("recovery")
                .child("physical_recovery_key")

            ref.get()
                .addOnSuccessListener { snap ->

                    val storedRaw = snap.getValue(String::class.java)

                    if (storedRaw == null) {
                        toast("Physical recovery key not found")
                        return@addOnSuccessListener
                    }

                    // ✅ Normalize STORED KEY
                    val storedKey = storedRaw
                        .trim()
                        .uppercase()
                        .replace(" ", "")

                    if (enteredKey == storedKey) {
                        // ✅ VERIFIED
                        startActivity(
                            Intent(
                                this,
                                ResetRecoveryPasswordActivity::class.java
                            )
                        )
                        finish()
                    } else {
                        toast("Invalid physical recovery key")
                    }
                }
                .addOnFailureListener {
                    toast("Verification failed")
                }
        }
    }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}
