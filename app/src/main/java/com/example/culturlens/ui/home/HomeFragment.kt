package com.example.culturlens.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.culturlens.adapter.ForumAdapter
import com.example.culturlens.api.ApiClient
import com.example.culturlens.databinding.FragmentHomeBinding
import com.example.culturlens.model.ForumItem
import com.example.culturlens.pref.UserPreference
import com.example.culturlens.ui.dataStore
import com.example.culturlens.ui.forum.PostForumActivity
import com.example.culturlens.ui.forum.DetailForumActivity
import com.example.culturlens.ui.forum.ForumViewModel
import com.example.culturlens.ui.login.signin.SigninActivity
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var forumAdapter: ForumAdapter
    private val forumList = mutableListOf<ForumItem>()
    private lateinit var forumViewModel: ForumViewModel

    private val userPreference: UserPreference by lazy {
        UserPreference.getInstance(requireContext().dataStore)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        forumViewModel = ViewModelProvider(requireActivity())[ForumViewModel::class.java]

        setupRecyclerView()
        setupClickListeners()
        loadUserSession()

        fetchForums()

        forumViewModel.forumLikeStatus.observe(viewLifecycleOwner) { likeMap ->
            forumList.forEach { forumItem ->
                forumItem.isLiked = likeMap[forumItem.id] ?: false
            }
            forumAdapter.submitList(forumList) // Update list di adapter
        }
    }

    private fun loadUserSession() {
        lifecycleScope.launch {
            userPreference.getSession().collect { user ->
                if (user.isLogin) {
                    binding.tvUsername.text = "Hello, ${user.name}"
                } else {
                    binding.tvUsername.text = "Guest"
                    redirectToLogin()
                }
            }
        }
    }

    private fun redirectToLogin() {
        val intent = Intent(requireContext(), SigninActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun setupRecyclerView() {
        forumAdapter = ForumAdapter(
            forumList = forumList,
            onItemClick = { forumItem ->
                val intent = Intent(requireContext(), DetailForumActivity::class.java)
                intent.putExtra("forum_id", forumItem.id)
                startActivity(intent)
            },
            onLikeClick = { forumItem ->
                forumViewModel.toggleLikeStatus(forumItem.id)
            }
        )

        binding.recyclerViewForum.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = forumAdapter
        }
    }

    private fun setupClickListeners() {
        binding.llShareSomething.setOnClickListener {
            val intent = Intent(requireContext(), PostForumActivity::class.java)
            startActivity(intent)
        }
    }

    private fun fetchForums() {
        binding.progressBar.visibility = View.VISIBLE

        val apiService = ApiClient.instance
        apiService.getForums().enqueue(object : Callback<List<ForumItem>> {
            override fun onResponse(call: Call<List<ForumItem>>, response: Response<List<ForumItem>>) {
                binding.progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    response.body()?.let { forums ->
                        forumList.clear()
                        forumList.addAll(forums)
                        forumAdapter.submitList(forumList)
                    }
                } else {
                    Toast.makeText(requireContext(), "Failed to load forums", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<ForumItem>>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Network Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}



