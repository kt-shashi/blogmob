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
import com.shashi.blogmob.daos.UserDao
import com.shashi.blogmob.models.User

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private lateinit var signinTV: TextView
    private lateinit var signupBtn: Button
    private lateinit var nameET: EditText
    private lateinit var emailET: EditText
    private lateinit var passwordET: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        // Initialize Firebase Auth
        auth = Firebase.auth

        initalizeUI()
    }

    private fun initalizeUI() {
        signinTV = findViewById(R.id.tv_signin_signuplayout)
        signupBtn = findViewById(R.id.btn_signup_signuplayout)
        nameET = findViewById(R.id.et_name_signup)
        emailET = findViewById(R.id.et_email_signup)
        passwordET = findViewById(R.id.et_password_signup)

        signinTV.setOnClickListener {
            openSigninActivity()
        }

        signupBtn.setOnClickListener {
            val email = emailET.text.toString().trim()
            val password = passwordET.text.toString().trim()

            val userName = getUserName(email)

            if (verifyEmailPassword(email, password))
                signup(userName, email, password)
        }

    }

    private fun getUserName(email: String): String {
        val name = nameET.text.toString().trim()

        if (name.isNotEmpty())
            return name

        var newUserName = ""
        for (i in email.indices) {
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
            emailET.error = "Cannot be empty"
        else if (password == "")
            passwordET.error = "Cannot be empty"
        else if (password.length < 8)
            passwordET.error = "Choose a password with 8 characters minimum"
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

                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()

                } else {
                    // If sign in fails, display a message to the user.
                    showToast("Sign up failed, make sure email is not already in use")
                }
            }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun openSigninActivity() {
        startActivity(Intent(this, SignInActivity::class.java))
        finish()
    }

}