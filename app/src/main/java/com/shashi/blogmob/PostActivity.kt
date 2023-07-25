package com.shashi.blogmob

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.shashi.blogmob.daos.PostDao

class PostActivity : AppCompatActivity() {

    private lateinit var postBtn: Button
    private lateinit var titleET: EditText
    private lateinit var descET: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        initiatizeUI()

    }

    private fun initiatizeUI() {

        postBtn = findViewById(R.id.btn_post)
        titleET = findViewById(R.id.et_title)
        descET = findViewById(R.id.et_desc)

        postBtn.setOnClickListener {
            postBtn.isEnabled = false
            createPost()
            postBtn.isEnabled = true
        }

    }

    private fun createPost() {

        var title = titleET.text.toString().trim()
        var desc = descET.text.toString().trim()

        if (verifyContent(title, desc))
            postToFirestore(title, desc)

    }

    private fun postToFirestore(title: String, desc: String) {

        var postDao = PostDao()
        postDao.addPost(title, desc)

        finish()

    }

    private fun verifyContent(title: String, desc: String): Boolean {

        if (title == "")
            titleET.setError("Cannot be empty")
        else if (desc == "")
            descET.setError("Cannot be empty")
        else
            return true

        return false

    }

}