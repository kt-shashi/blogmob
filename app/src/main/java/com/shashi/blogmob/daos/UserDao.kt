package com.shashi.blogmob.daos

import com.google.firebase.firestore.FirebaseFirestore
import com.shashi.blogmob.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class UserDao {

    val db = FirebaseFirestore.getInstance()
    val userCollection = db.collection("users")

    fun addUser(user: User) {
        user?.let {

            GlobalScope.launch(Dispatchers.IO) {
                userCollection.add(user)
            }

        }
    }

}