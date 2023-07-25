package com.shashi.blogmob.login

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.shashi.blogmob.HomeActivity
import com.shashi.blogmob.R
import com.shashi.blogmob.daos.UserDao
import com.shashi.blogmob.models.User
import java.util.*

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private lateinit var signinTV: TextView
    private lateinit var signupBtn: Button
    private lateinit var nameET: EditText
    private lateinit var emailET: EditText
    private lateinit var passwordET: EditText

    private lateinit var imageIV: ImageView
    private lateinit var imageURI: Uri
    private val IMG_REQUEDT_ID = 1
    private var imageURL = ""


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
        imageIV = findViewById(R.id.iv_photo_signup)

        signinTV.setOnClickListener {
            openSigninActivity()
        }

        signupBtn.setOnClickListener {

            signupBtn.isEnabled = false
            signinTV.isEnabled = false

            val email = emailET.text.toString().trim()
            val password = passwordET.text.toString().trim()

            val userName = getUserName(email)

            if (verifyEmailPassword(email, password))
                signup(userName, email, password)

            signupBtn.isEnabled = true
            signinTV.isEnabled = true

        }

        imageIV.setOnClickListener {
            selectImage()
        }

    }

    private fun selectImage() {

        var intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select profile image"), IMG_REQUEDT_ID)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == IMG_REQUEDT_ID && resultCode == RESULT_OK && data != null && data.data != null) {

            imageURI = data.data!!

            try {

                var bit = MediaStore.Images.Media.getBitmap(contentResolver, imageURI)
                imageIV.setImageBitmap(bit)

            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }

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
                    uploadImage(object : ImageUploadCallback {
                        override fun onImageUploadSuccess(imageURL: String) {
                            val currentUser = auth.currentUser
                            val user = User(
                                currentUser?.uid ?: "",
                                userName,
                                imageURL
                            )

                            val userDao = UserDao()
                            userDao.addUser(user)
                            startActivity(Intent(this@SignUpActivity, HomeActivity::class.java))
                            finish()
                        }

                        override fun onImageUploadFailure() {
                            showToast("Failed to upload image.")
                        }
                    })
                } else {
                    showToast("Sign up failed, make sure email is not already in use")
                }
            }
    }

    fun uploadImage(callback: ImageUploadCallback) {
        val storage = Firebase.storage("gs://blogmob-840f4.appspot.com")
        val storageRef = storage.reference

        val reference = storageRef.child("profilepic/" + UUID.randomUUID().toString())

        val uploadTask = reference.putFile(imageURI)

        val urlTask = uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            reference.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                imageURL = task.result.toString()
                callback.onImageUploadSuccess(imageURL)
            } else {
                imageURL = ""
                callback.onImageUploadFailure()
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

    interface ImageUploadCallback {
        fun onImageUploadSuccess(imageURL: String)
        fun onImageUploadFailure()
    }

}