package com.shashi.blogmob.login

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.shashi.blogmob.HomeActivity
import com.shashi.blogmob.R

class SignInActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private lateinit var signinBtn: Button
    private lateinit var signupTV: TextView
    private lateinit var emailET: EditText
    private lateinit var passwordET: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Hide title bar
        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {

        }
        setContentView(R.layout.activity_signin)

        // Initialize Firebase Auth
        auth = Firebase.auth

        initalizeUI()
    }

    private fun initalizeUI() {
        signinBtn = findViewById(R.id.btn_signin_signinlayout)
        signupTV = findViewById(R.id.tv_signup_signinlayout)
        emailET = findViewById(R.id.et_email_signin)
        passwordET = findViewById(R.id.et_password_signin)

        signinBtn.setOnClickListener {

            val email = emailET.text.toString().trim()
            val password = passwordET.text.toString().trim()

            if (verifyEmailPassword(email, password))
                signin(email, password)
        }

        signupTV.setOnClickListener {
            openSignupActivity()
        }

    }

    private fun openSignupActivity() {
        startActivity(Intent(this, SignUpActivity::class.java))
        finish()
    }

    private fun verifyEmailPassword(email: String, password: String): Boolean {

        if (email == "")
            emailET.error = "Cannot be empty"
        else if (password == "")
            passwordET.error = "Cannot be empty"
        else if (password.length < 8)
            passwordET.error = "Choose a password with 8 characters minimum"
        else
            return true
        return false

    }

    private fun signin(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()

                } else {
                    // If sign in fails, display a message to the user.
                    showToast("Sign in unsuccessful, make sure credentials are valid")
                }
            }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}