package com.shashi.blogmob

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import com.shashi.blogmob.daos.PostDao
import com.shashi.blogmob.login.SignInActivity
import com.shashi.blogmob.models.Post

class HomeActivity : AppCompatActivity(), IPostAdapter {

    private lateinit var postDao: PostDao
    private lateinit var adapter: PostAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var createPost: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        initializeUI()

    }

    private fun initializeUI() {

        postDao = PostDao()
        val postCollection = postDao.postCollection
        val query = postCollection.orderBy("createdAt", Query.Direction.DESCENDING)
        val recyclerViewOptions =
            FirestoreRecyclerOptions.Builder<Post>().setQuery(query, Post::class.java).build()

        adapter = PostAdapter(recyclerViewOptions, this)

        recyclerView = findViewById(R.id.rv_post)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // create post
        createPost = findViewById(R.id.btn_create_post)
        createPost.setOnClickListener {
            val intent = Intent(this, PostActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        var auth = Firebase.auth
        val currentUser = auth.currentUser
        if (currentUser == null) {
            startActivity(Intent(this, SignInActivity::class.java))
        }
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    override fun onResume() {
        super.onResume()

        adapter.notifyDataSetChanged()
    }

    // Handle post like
    override fun onLikeClicked(postId: String) {
        postDao.updateLikes(postId)
    }

    // Handle logout
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.home_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val menuId = item.itemId
        if (menuId == R.id.item_logout)
            createDialog()
        return super.onOptionsItemSelected(item)
    }

    private fun createDialog() {

        var builder = AlertDialog.Builder(this)
        builder.setTitle("Logout confirmation")
        builder.setTitle("Are you sure you want to logout")
        builder.setCancelable(false)

        builder.setPositiveButton("Yes") { dialog, which ->
            logout()
        }

        builder.setNegativeButton("No") { dialog, which ->
            builder.create().dismiss()
        }

        builder.show()

    }

    private fun logout() {
        FirebaseAuth.getInstance().signOut();
        startActivity(Intent(this, SignInActivity::class.java))
        finish()
    }

}