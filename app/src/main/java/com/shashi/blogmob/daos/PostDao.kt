package com.shashi.blogmob.daos

import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.shashi.blogmob.Constants
import com.shashi.blogmob.models.Post
import com.shashi.blogmob.models.User
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class PostDao() {

    val db = FirebaseFirestore.getInstance()
    val postCollection = db.collection(Constants.DB_POST)
    val auth = Firebase.auth

    fun addPost(title: String, desc:String) {

        val currentUserId = auth.currentUser!!.uid

        GlobalScope.launch {
            val userDao = UserDao()
            val user = userDao.getUserById(currentUserId).await().toObject(User::class.java)!!

            val currentTime = Timestamp.now()
            val post = Post(title, desc, user, currentTime)

            postCollection.document().set(post)
        }
    }

}