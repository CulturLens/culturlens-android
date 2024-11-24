package com.example.culturlens.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.culturlens.R

class PostForumActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_forum)

    }
}
