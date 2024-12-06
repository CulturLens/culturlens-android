package com.example.culturlens.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.culturlens.adapter.ForumAdapter
import com.example.culturlens.databinding.FragmentHomeBinding
import com.example.culturlens.model.ForumItem
import com.example.culturlens.ui.forum.PostForumActivity
import com.example.culturlens.ui.forum.DetailForumActivity
import com.example.culturlens.ui.forum.ForumViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var forumAdapter: ForumAdapter
    private val forumList = mutableListOf<ForumItem>()
    private lateinit var forumViewModel: ForumViewModel // Shared ViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        forumViewModel = ViewModelProvider(requireActivity())[ForumViewModel::class.java]

        setupRecyclerView()
        setupClickListeners()

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        forumList.clear()
        loadDummyData()

        forumViewModel.forumLikeStatus.observe(viewLifecycleOwner) { likeMap ->
            forumList.forEach { forumItem ->
                forumItem.isLiked = likeMap[forumItem.id] ?: false
            }
            forumAdapter.notifyDataSetChanged()
        }
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

    private fun loadDummyData() {
        forumList.addAll(
            listOf(
                ForumItem("1", "User1", "This is a forum post.", "https://via.placeholder.com/150"),
                ForumItem("2", "User2", "Another interesting post.", "https://via.placeholder.com/150"),
                ForumItem("3", "User3", "A beautiful picture of nature.", "https://via.placeholder.com/150")
            )
        )
        forumAdapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}



