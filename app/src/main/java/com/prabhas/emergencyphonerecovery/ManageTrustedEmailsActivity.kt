package com.prabhas.emergencyphonerecovery

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase

class ManageTrustedEmailsActivity : AppCompatActivity() {

    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userId = SecurePrefs.getUserId(this)

        setContentView(R.layout.activity_trusted_email_setup)

        val et1 = findViewById<EditText>(R.id.etEmail1)
        val et2 = findViewById<EditText>(R.id.etEmail2)
        val et3 = findViewById<EditText>(R.id.etEmail3)
        val btnSave = findViewById<Button>(R.id.btnSaveEmails)

        val userRef = FirebaseDatabase.getInstance()
            .getReference("users")
            .child(userId)
            .child("trusted_emails")

        // 🔹 Load existing emails
        userRef.get().addOnSuccessListener { snap ->
            et1.setText(snap.child("email_1").getValue(String::class.java) ?: "")
            et2.setText(snap.child("email_2").getValue(String::class.java) ?: "")
            et3.setText(snap.child("email_3").getValue(String::class.java) ?: "")
        }

        btnSave.setOnClickListener {

            val e1 = et1.text.toString().trim()
            val e2 = et2.text.toString().trim()
            val e3 = et3.text.toString().trim()

            if (e1.isEmpty()) {
                toast("At least one email is required")
                return@setOnClickListener
            }

            println("🔥 UPDATING EMAILS FOR USER: $userId")

            val updates = mutableMapOf<String, Any>(
                "email_1" to e1,
                "trusted_emails_set" to true
            )

            // ✅ Only add if exists
            if (e2.isNotEmpty()) {
                updates["email_2"] = e2
            }

            if (e3.isNotEmpty()) {
                updates["email_3"] = e3
            }

            userRef.updateChildren(updates)
                .addOnSuccessListener {

                    // ✅ Handle removals separately
                    if (e2.isEmpty()) {
                        userRef.child("email_2").removeValue()
                    }
                    if (e3.isEmpty()) {
                        userRef.child("email_3").removeValue()
                    }

                    toast("Trusted emails updated")
                    finish()
                }
                .addOnFailureListener { e ->
                    toast("Failed: ${e.message}")
                    e.printStackTrace()
                }
        }

        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    finish()
                }
            }
        )
    }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}
// git test