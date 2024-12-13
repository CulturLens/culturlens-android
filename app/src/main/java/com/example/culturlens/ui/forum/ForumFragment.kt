package com.example.culturlens.ui.forum


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.culturlens.adapter.ForumAdapter
import com.example.culturlens.api.ApiClient
import com.example.culturlens.databinding.FragmentForumBinding
import com.example.culturlens.model.ForumItem
import com.example.culturlens.pref.UserPreference
import com.example.culturlens.pref.UserRepository
import com.example.culturlens.response.ForumsResponse
import com.example.culturlens.response.GenericResponse
import com.example.culturlens.response.LikeRequest
import com.example.culturlens.ui.dataStore
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForumFragment : Fragment() {

    private var _binding: FragmentForumBinding? = null
    private val binding get() = _binding!!

    private lateinit var forumAdapter: ForumAdapter
    private val forumList = mutableListOf<ForumItem>()
    private val filteredForumList = mutableListOf<ForumItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentForumBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSearchBar()
        fetchForums()
    }

    private fun setupRecyclerView() {
        forumAdapter = ForumAdapter(
            forumList = filteredForumList,
            onItemClick = { forumItem ->
                val intent = Intent(requireContext(), DetailForumActivity::class.java)
                intent.putExtra("forum_id", forumItem.id)
                startActivity(intent)
            },
            onLikeClick = { forumItem, isLiked ->
                likePost(forumItem.id.toInt(), isLiked)
            }
        )

        binding.recyclerViewForum.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = forumAdapter
            Log.d("ForumFragment", "RecyclerView setup complete, items count: ${forumAdapter.itemCount}")
        }
    }

    private fun setupSearchBar() {
        binding.etSearchForum.addTextChangedListener { text ->
            val query = text.toString().trim()
            filterForums(query)
        }
    }

    private fun filterForums(query: String) {
        filteredForumList.clear()
        if (query.isEmpty()) {
            filteredForumList.addAll(forumList)
        } else {
            filteredForumList.addAll(
                forumList.filter {
                    it.title.contains(query, ignoreCase = true) ||
                            it.description.contains(query, ignoreCase = true)
                }
            )
        }
        forumAdapter.notifyDataSetChanged()
    }

    private fun fetchForums() {
        binding.progressBar.visibility = View.VISIBLE

        val apiService = ApiClient.instance
        apiService.getForums().enqueue(object : Callback<ForumsResponse> {
            override fun onResponse(call: Call<ForumsResponse>, response: Response<ForumsResponse>) {
                binding.progressBar.visibility = View.GONE

                Log.d("ForumFragment", "Response Code: ${response.code()}")

                if (response.isSuccessful) {
                    val forumsResponse = response.body()

                    if (forumsResponse != null) {
                        Log.d("ForumFragment", "Forums response is not null")
                        Log.d("ForumFragment", "Forums size: ${forumsResponse.forums?.size}")
                    } else {
                        Log.e("ForumFragment", "Forums response is null")
                    }

                    forumsResponse?.forums?.let { forums ->
                        Log.d("ForumFragment", "Forums size before adding to list: ${forums.size}")

                        forumList.clear()
                        forumList.addAll(forums)

                        filteredForumList.clear()
                        filteredForumList.addAll(forumList)

                        Log.d("ForumFragment", "ForumList size after adding: ${forumList.size}")

                        forumAdapter.notifyDataSetChanged()
                    }

                } else {
                    Log.e("ForumFragment", "Failed to load forums. Code: ${response.code()} Message: ${response.message()}")
                    Toast.makeText(requireContext(), "Failed to load forums", Toast.LENGTH_SHORT).show()
                }
            }




            override fun onFailure(call: Call<ForumsResponse>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Network Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun likePost(postId: Int, isLiked: Boolean) {
        val userId = getCurrentUserId()
        Log.d("LikePost", "User ID: $userId, Post ID: $postId, Is Liked: $isLiked")

        val apiService = ApiClient.instance
        val likeRequest = LikeRequest(user_id = userId, post_id = postId)
        apiService.likePost(likeRequest).enqueue(object : Callback<GenericResponse> {
            override fun onResponse(call: Call<GenericResponse>, response: Response<GenericResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Post liked successfully!", Toast.LENGTH_SHORT).show()
                } else {
                    Log.e("LikePost", "Failed to like post. Code: ${response.code()} Message: ${response.message()}")
                    try {
                        val errorBody = response.errorBody()?.string()
                        Log.e("LikePost", "Error Body: $errorBody")
                    } catch (e: Exception) {
                        Log.e("LikePost", "Error parsing error body: ${e.message}")
                    }
                    Toast.makeText(requireContext(), "Failed to like post", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                Log.e("LikePost", "Network Error: ${t.message}")
                Toast.makeText(requireContext(), "Network Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun getCurrentUserId(): Int {
        val userRepository = UserRepository.getInstance(UserPreference.getInstance(requireContext().dataStore))

        var userId = -1
        lifecycleScope.launch {
            userRepository.getSession().collect { userModel ->
                userId = userModel.userId
                Log.d("User ID", "Current User ID: $userId")
            }
        }

        return userId
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}



