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
    private lateinit var nameET: EditText
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
        nameET = findViewById(R.id.et_name)
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

            var userName = getUserName(email)

            if (verifyEmailPassword(email, password))
                signup(userName, email, password)
        }

    }

    private fun getUserName(email: String): String {
        var name = nameET.text.toString().trim()

        if (name.isNotEmpty())
            return name

        var newUserName = ""
        for (i in 0 until email.length) {
            if (email[i] == '@') {
                break
            } else {
                newUserName += email[i]
            }
        }

        return newUserName
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

    private fun signup(userName: String, email: String, password: String) {

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val currentUser = auth.currentUser

                    val user = User(
                        currentUser?.uid ?: "",
                        userName,
                        "https://firebasestorage.googleapis.com/v0/b/blogmob-840f4.appspot.com/o/placeholder.png?alt=media&token=fc0a61fc-fdb6-4b58-8522-aaa2f186ff34"
                    )

                    val userDao = UserDao()
                    userDao.addUser(user)

                    showToast("Sign up successful, you can now sign in into your account")

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