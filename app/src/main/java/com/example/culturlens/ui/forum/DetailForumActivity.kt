package com.example.culturlens.ui.forum

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.culturlens.R
import com.example.culturlens.api.ApiClient
import com.example.culturlens.model.ForumItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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

        fetchForumDetails(forumId)

        forumViewModel.forumLikeStatus.observe(this) { likeMap ->
            val isLiked = likeMap[forumId] ?: false
            updateLikeIcon(isLiked)
        }

        likeIcon.setOnClickListener {
            forumViewModel.toggleLikeStatus(forumId)
        }
    }

    private fun fetchForumDetails(forumId: String) {
        val apiService = ApiClient.instance
        apiService.getForumDetail(forumId).enqueue(object : Callback<ForumItem> {
            override fun onResponse(call: Call<ForumItem>, response: Response<ForumItem>) {
                if (response.isSuccessful) {
                    val forumItem = response.body()
                    if (forumItem != null) {
                        populateForumDetails(forumItem)
                    }
                } else {
                    Toast.makeText(this@DetailForumActivity, "Failed to load forum details", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ForumItem>, t: Throwable) {
                Toast.makeText(this@DetailForumActivity, "Network Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun populateForumDetails(forumItem: ForumItem) {
        findViewById<TextView>(R.id.tvUsername).text = forumItem.username
        findViewById<TextView>(R.id.tvPostContent).text = forumItem.description

        val fullImageUrl = "https://fitur-api-124653119153.asia-southeast2.run.app/${forumItem.image}"
        findViewById<ImageView>(R.id.ivPostImage).apply {
            Glide.with(this@DetailForumActivity)
                .load(fullImageUrl)
                .placeholder(R.drawable.ic_place_holder)
                .error(R.drawable.ic_error)
                .into(this)
        }
    }

    private fun updateLikeIcon(isLiked: Boolean) {
        likeIcon.setImageResource(
            if (isLiked) R.drawable.ic_like_fill else R.drawable.ic_like
        )
    }
}



