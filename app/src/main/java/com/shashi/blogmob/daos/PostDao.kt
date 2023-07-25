package com.shashi.blogmob.daos

import android.net.Uri
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.shashi.blogmob.Constants
import com.shashi.blogmob.models.Post
import com.shashi.blogmob.models.User
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*

class PostDao() {

    val db = FirebaseFirestore.getInstance()
    val postCollection = db.collection(Constants.DB_POST)
    val auth = Firebase.auth

    fun addPost(title: String, desc: String) {

        val currentUserId = auth.currentUser!!.uid

        GlobalScope.launch {
            val userDao = UserDao()
            val user = userDao.getUserById(currentUserId).await().toObject(User::class.java)!!

            val currentTime = System.currentTimeMillis()
            val post = Post(title, desc, user, currentTime)

            postCollection.document().set(post)
        }
    }

    fun getPostById(postId: String): Task<DocumentSnapshot> {
        return postCollection.document(postId).get()
    }

    fun updateLikes(postId: String) {

        GlobalScope.launch {

            val currentUserId = auth.currentUser!!.uid
            val post = getPostById(postId).await().toObject(Post::class.java)!!

            val isLiked = post.likedBy.contains(currentUserId)

            if (isLiked)
                post.likedBy.remove(currentUserId)
            else
                post.likedBy.add(currentUserId)

            postCollection.document(postId).set(post)
        }

    }

}