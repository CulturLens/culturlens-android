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
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.culturlens.R
import com.example.culturlens.adapter.CommentAdapter
import com.example.culturlens.api.ApiClient
import com.example.culturlens.model.CommentItem
import com.example.culturlens.model.ForumItem
import com.example.culturlens.pref.UserPreference
import com.example.culturlens.pref.UserRepository
import com.example.culturlens.response.CommentRequest
import com.example.culturlens.response.GenericResponse
import com.example.culturlens.response.UserResponse
import com.example.culturlens.ui.dataStore
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
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
    private lateinit var userRepository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_forum)
        userRepository = UserRepository.getInstance(UserPreference.getInstance(dataStore))

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

//        likeIcon.setOnClickListener {
//            forumViewModel.toggleLikeStatus(forumId)
//        }

        sendButton.setOnClickListener {
            val commentText = commentEditText.text.toString().trim()
            if (commentText.isBlank()) {
                Toast.makeText(this, "Comment cannot be empty", Toast.LENGTH_SHORT).show()
            } else if (commentText.length > 500) {
                Toast.makeText(this, "Comment is too long", Toast.LENGTH_SHORT).show()
            } else {
                postComment(commentText)
                commentEditText.text.clear()
            }
        }

    }

    private fun fetchForumDetails(forumId: String) {
        val apiService = ApiClient.instance
        apiService.getForumDetail(forumId).enqueue(object : Callback<ForumItem> {
            override fun onResponse(call: Call<ForumItem>, response: Response<ForumItem>) {
                if (response.isSuccessful) {
                    response.body()?.let { forumItem ->
                        populateForumDetails(forumItem)
                    } ?: run {
                        Toast.makeText(this@DetailForumActivity, "Forum details not found", Toast.LENGTH_SHORT).show()
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

        Glide.with(this)
            .load(forumItem.imageUrl)
            .placeholder(R.drawable.ic_place_holder)
            .error(R.drawable.ic_error)
            .into(findViewById(R.id.ivPostImage))
    }

    private fun updateLikeIcon(isLiked: Boolean) {
        likeIcon.setImageResource(
            if (isLiked) R.drawable.ic_like_fill else R.drawable.ic_like
        )
    }

    fun fetchUsername(userId: Int, onResult: (String) -> Unit) {
        val apiService = ApiClient.instance
        apiService.getUser(userId).enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.isSuccessful) {
                    val username = response.body()?.username ?: "Unknown User"
                    onResult(username)
                } else {
                    onResult("Unknown User")
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                onResult("Unknown User")
            }
        })
    }

    private fun fetchComments() {
        val apiService = ApiClient.instance
        apiService.getComments(forumId).enqueue(object : Callback<List<CommentItem>> {
            override fun onResponse(call: Call<List<CommentItem>>, response: Response<List<CommentItem>>) {
                if (response.isSuccessful) {
                    val comments = response.body() ?: listOf()
                    commentAdapter.updateComments(comments)
                } else {
                    Toast.makeText(this@DetailForumActivity, "Failed to load comments", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<CommentItem>>, t: Throwable) {
                Toast.makeText(this@DetailForumActivity, "Network Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private suspend fun fetchUsernameSync(userId: Int): String {
        return suspendCancellableCoroutine { continuation ->
            val apiService = ApiClient.instance
            apiService.getUser(userId).enqueue(object : Callback<UserResponse> {
                override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                    if (response.isSuccessful) {
                        continuation.resume(response.body()?.username ?: "Unknown User", null)
                    } else {
                        continuation.resume("Unknown User", null)
                    }
                }

                override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                    continuation.resume("Unknown User", null)
                }
            })
        }
    }

    private fun postComment(commentText: String) {
        val apiService = ApiClient.instance
        val commentRequest = CommentRequest(commentText)

        apiService.postComment(forumId, commentRequest).enqueue(object : Callback<GenericResponse> {
            override fun onResponse(call: Call<GenericResponse>, response: Response<GenericResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@DetailForumActivity, "Comment posted!", Toast.LENGTH_SHORT).show()
                    fetchComments()
                    commentEditText.text.clear()
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
