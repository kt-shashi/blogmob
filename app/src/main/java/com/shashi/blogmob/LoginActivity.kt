package com.shashi.blogmob

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.shashi.blogmob.daos.UserDao
import com.shashi.blogmob.models.User

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private lateinit var signinBtn: Button
    private lateinit var signupBtn: Button
    private lateinit var emailET: EditText
    private lateinit var passwordET: EditText

    private var TAG = "blogmob"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

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

            var email = emailET.text.toString().trim()
            var password = passwordET.text.toString().trim()

            if (verifyEmailPassword(email, password))
                signin(email, password)
        }

        signupBtn.setOnClickListener {
            var email = emailET.text.toString().trim()
            var password = passwordET.text.toString().trim()

            if (verifyEmailPassword(email, password))
                signup(email, password)
        }

    }

    private fun verifyEmailPassword(email: String, password: String): Boolean {

        if (email == "")
            emailET.setError("Cannot be empty")
        else if (password == "")
            passwordET.setError("Cannot be empty")
        else if (password.length < 8)
            passwordET.setError("Choose a password with 8 characters minimum")
        else
            return true
        return false

    }

    private fun signup(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val currentUser = auth.currentUser

                    val user = User(
                        currentUser?.uid ?: "",
                        email,
                        "https://ralfvanveen.com/wp-content/uploads/2021/06/Placeholder-_-Begrippenlijst.svg"
                    )

                    val userDao = UserDao()
                    userDao.addUser(user)

                    showToast("Sign up successful, you can now sign in ino your account")

                } else {
                    // If sign in fails, display a message to the user.
                    showToast("Sign up failed, make sure email is not already in use")
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
                    showToast("Sign in successful")
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