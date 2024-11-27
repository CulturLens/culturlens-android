package com.example.culturlens.ui.forum

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.culturlens.R

class DetailForumActivity : AppCompatActivity() {

    private lateinit var forumViewModel: ForumViewModel
    private var forumId: String = ""
    private lateinit var likeIcon: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_forum)

        likeIcon = findViewById(R.id.ivLike)

        forumViewModel = ViewModelProvider(this)[ForumViewModel::class.java]

        forumId = intent.getStringExtra("forum_id") ?: ""

        forumViewModel.forumLikeStatus.observe(this) { likeMap ->
            val isLiked = likeMap[forumId] ?: false
            updateLikeIcon(isLiked)
        }

        likeIcon.setOnClickListener {
            forumViewModel.toggleLikeStatus(forumId)
        }
    }

    private fun updateLikeIcon(isLiked: Boolean) {
        likeIcon.setImageResource(
            if (isLiked) R.drawable.ic_like_fill else R.drawable.ic_like
        )
    }
}

