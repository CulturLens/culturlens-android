package com.example.culturlens.ui.forum

import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.culturlens.R
import com.example.culturlens.adapter.CommentAdapter
import com.example.culturlens.api.ApiClient
import com.example.culturlens.model.CommentItem
import com.example.culturlens.model.ForumItem
import com.example.culturlens.response.CommentRequest
import com.example.culturlens.response.GenericResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DetailForumActivity : AppCompatActivity() {

    private lateinit var forumViewModel: ForumViewModel
    private lateinit var likeIcon: ImageView
    private lateinit var commentAdapter: CommentAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var sendButton: ImageButton
    private lateinit var commentEditText: EditText
    private var forumId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_forum)

        likeIcon = findViewById(R.id.ivLike)
        recyclerView = findViewById(R.id.recyclerComments)
        sendButton = findViewById(R.id.buttonSendComment)
        commentEditText = findViewById(R.id.editComment)

        forumViewModel = ViewModelProvider(this)[ForumViewModel::class.java]
        forumId = intent.getStringExtra("forum_id") ?: ""


        commentAdapter = CommentAdapter(mutableListOf())
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = commentAdapter


        fetchForumDetails(forumId)


        fetchComments()

        forumViewModel.forumLikeStatus.observe(this) { likeMap ->
            val isLiked = likeMap[forumId] ?: false
            updateLikeIcon(isLiked)
        }

        likeIcon.setOnClickListener {
            forumViewModel.toggleLikeStatus(forumId)
        }

        sendButton.setOnClickListener {
            val commentText = commentEditText.text.toString().trim()
            if (commentText.isNotBlank()) {
                postComment(commentText)
                commentEditText.text.clear()
            } else {
                Toast.makeText(this, "Comment cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchForumDetails(forumId: String) {
        val apiService = ApiClient.instance
        apiService.getForumDetail(forumId).enqueue(object : Callback<ForumItem> {
            override fun onResponse(call: Call<ForumItem>, response: Response<ForumItem>) {
                if (response.isSuccessful) {
                    val forumItem = response.body()
                    forumItem?.let {
                        populateForumDetails(it)
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

    private fun fetchComments() {
        val apiService = ApiClient.instance
        apiService.getComments(forumId).enqueue(object : Callback<List<CommentItem>> {
            override fun onResponse(call: Call<List<CommentItem>>, response: Response<List<CommentItem>>) {
                if (response.isSuccessful) {
                    val commentList = response.body() ?: emptyList()
                    commentAdapter = CommentAdapter(commentList.toMutableList())
                    recyclerView.adapter = commentAdapter
                } else {
                    Toast.makeText(this@DetailForumActivity, "Failed to load comments", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<CommentItem>>, t: Throwable) {
                Toast.makeText(this@DetailForumActivity, "Network Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun postComment(commentText: String) {
        val apiService = ApiClient.instance
        val commentRequest = CommentRequest(commentText)

        apiService.postComment(forumId, commentRequest).enqueue(object : Callback<GenericResponse> {
            override fun onResponse(call: Call<GenericResponse>, response: Response<GenericResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@DetailForumActivity, "Comment posted!", Toast.LENGTH_SHORT).show()

                    val newComment = CommentItem(
                        id = System.currentTimeMillis().toString(),
                        username = "CurrentUser",
                        content = commentText,
                        createdAt = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).format(Date())
                    )
                    commentAdapter.addComment(newComment)
                    recyclerView.scrollToPosition(commentAdapter.itemCount - 1)
                } else {
                    Toast.makeText(this@DetailForumActivity, "Failed to post comment", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                Toast.makeText(this@DetailForumActivity, "Network Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
