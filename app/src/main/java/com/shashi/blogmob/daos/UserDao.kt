package com.shashi.blogmob.daos

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.shashi.blogmob.Constants
import com.shashi.blogmob.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class UserDao {

    val db = FirebaseFirestore.getInstance()
    val userCollection = db.collection(Constants.DB_USER)

    fun addUser(user: User) {

        if (user.imageUrl == "")
            user.imageUrl =
                "https://firebasestorage.googleapis.com/v0/b/blogmob-840f4.appspot.com/o/placeholder.png?alt=media&token=fc0a61fc-fdb6-4b58-8522-aaa2f186ff34"

        user?.let {

            GlobalScope.launch(Dispatchers.IO) {
                userCollection.document(user.uid).set(it)
            }

        }
    }

    // get user info
    fun getUserById(uid: String): Task<DocumentSnapshot> {
        return userCollection.document(uid).get()
    }

}