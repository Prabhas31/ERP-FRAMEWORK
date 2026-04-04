package com.prabhas.emergencyphonerecovery

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase

class ManageTrustedEmailsActivity : AppCompatActivity() {

    private val userId = "owner_001"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trusted_email_setup)

        val et1 = findViewById<EditText>(R.id.etEmail1)
        val et2 = findViewById<EditText>(R.id.etEmail2)
        val et3 = findViewById<EditText>(R.id.etEmail3)
        val btnSave = findViewById<Button>(R.id.btnSaveEmails)

        val userRef = FirebaseDatabase.getInstance()
            .getReference("users")
            .child(userId)

        // 🔹 Load existing emails
        userRef.child("trusted_emails")
            .get()
            .addOnSuccessListener { snap ->
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

            // ✅ Mandatory email
            userRef.child("trusted_emails/email_1").setValue(e1)

            // ✅ Optional email 2
            if (e2.isNotEmpty()) {
                userRef.child("trusted_emails/email_2").setValue(e2)
            } else {
                userRef.child("trusted_emails/email_2").removeValue()
            }

            // ✅ Optional email 3
            if (e3.isNotEmpty()) {
                userRef.child("trusted_emails/email_3").setValue(e3)
            } else {
                userRef.child("trusted_emails/email_3").removeValue()
            }

            // ✅ FIX: mark setup done at the CORRECT path
            userRef.child("trusted_emails")
                .child("trusted_emails_set")
                .setValue(true)
                .addOnSuccessListener {
                    toast("Trusted emails updated")
                    finish()
                }
        }

        // 🔙 Back → Dashboard
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
