package com.shashi.blogmob

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SigninActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private lateinit var signinBtn: Button
    private lateinit var signupBtn: Button
    private lateinit var emailET: EditText
    private lateinit var passwordET: EditText

    private var TAG = "blogmob"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        // Initialize Firebase Auth
        auth = Firebase.auth

        initalizeUI()
    }

    private fun initalizeUI() {
        signinBtn = findViewById(R.id.btn_signin)
        signupBtn = findViewById(R.id.btn_signup)
        emailET = findViewById(R.id.et_email)
        passwordET = findViewById(R.id.et_password)

        signinBtn.setOnClickListener {

            var email = emailET.text.toString()
            var password = passwordET.text.toString()

            if (email == "")
                emailET.setError("Cannot be empty")
            else if (password == "")
                passwordET.setError("Cannot be empty")
            else if (password.length < 8)
                passwordET.setError("Choose a password with 8 characters minimum")
            else
                signin(email, password)
        }

        signupBtn.setOnClickListener {

            var email = emailET.text.toString()
            var password = passwordET.text.toString()

            if (email == "")
                emailET.setError("Cannot be empty")
            else if (password == "")
                passwordET.setError("Cannot be empty")
            else if (password.length < 8)
                passwordET.setError("Choose a password with 8 characters minimum")
            else
                signup(email, password)
        }

    }

    private fun signup(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
//                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
//                    updateUI(null)
                }
            }
    }

    private fun signin(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser
//                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
//                    updateUI(null)
                }
            }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            showToast("signed in already")
        }
    }
}