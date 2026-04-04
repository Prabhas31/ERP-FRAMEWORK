package com.prabhas.emergencyphonerecovery

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ChangeAppLockPasswordActivity : AppCompatActivity() {

    private lateinit var oldPasswordInput: EditText
    private lateinit var newPasswordInput: EditText
    private lateinit var confirmPasswordInput: EditText
    private lateinit var changeButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_change_applock_password)

        oldPasswordInput =
            findViewById(R.id.oldPasswordInput)

        newPasswordInput =
            findViewById(R.id.newPasswordInput)

        confirmPasswordInput =
            findViewById(R.id.confirmPasswordInput)

        changeButton =
            findViewById(R.id.changePasswordButton)

        changeButton.setOnClickListener {

            val oldPass =
                oldPasswordInput.text.toString()

            val newPass =
                newPasswordInput.text.toString()

            val confirmPass =
                confirmPasswordInput.text.toString()

            if (oldPass.isEmpty() ||
                newPass.isEmpty() ||
                confirmPass.isEmpty()
            ) {

                Toast.makeText(this,
                    "Fill all fields",
                    Toast.LENGTH_SHORT).show()

                return@setOnClickListener
            }

            val storedHash =
                SecurePrefs.getAppLockHash(this)

            val oldHash =
                HashUtils.sha256(oldPass)

            if (oldHash != storedHash) {

                Toast.makeText(this,
                    "Old password incorrect",
                    Toast.LENGTH_SHORT).show()

                return@setOnClickListener
            }

            if (newPass != confirmPass) {

                Toast.makeText(this,
                    "Passwords do not match",
                    Toast.LENGTH_SHORT).show()

                return@setOnClickListener
            }

            val newHash =
                HashUtils.sha256(newPass)

            SecurePrefs.saveAppLockHash(
                this,
                newHash
            )

            Toast.makeText(this,
                "Password changed successfully",
                Toast.LENGTH_LONG).show()

            finish()
        }
    }
}
