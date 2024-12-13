package com.example.culturlens.ui.forum

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.culturlens.adapter.ForumAdapter
import com.example.culturlens.api.ApiClient
import com.example.culturlens.databinding.FragmentForumBinding


class ForumFragment : Fragment() {

    private var _binding: FragmentForumBinding? = null
    private val binding get() = _binding!!

    private lateinit var forumAdapter: ForumAdapter
    private lateinit var forumViewModel: ForumViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentForumBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val apiService = ApiClient.instance
        val repository = ForumRepository(apiService)
        val factory = ForumViewModelFactory(repository)
        forumViewModel = ViewModelProvider(this, factory)[ForumViewModel::class.java]

        setupRecyclerView()
        setupSearchBar()

        forumViewModel.forums.observe(viewLifecycleOwner) { forums ->
            forumAdapter.submitList(forums)
        }

        forumViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        forumViewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            message?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvPostForum.setOnClickListener {
            val intent = Intent(requireContext(), PostForumActivity::class.java)
            startActivity(intent)
        }

        forumViewModel.fetchForums()
    }

    private fun setupRecyclerView() {
        forumAdapter = ForumAdapter(
            forumList = emptyList(),
            onItemClick = { forumItem ->
                Toast.makeText(requireContext(), "Forum item: ${forumItem.title}", Toast.LENGTH_SHORT).show()
            }
        )

        binding.recyclerViewForum.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = forumAdapter
        }
    }

    private fun setupSearchBar() {
        binding.etSearchForum.addTextChangedListener { text ->
            val query = text.toString().trim()
            filterForums(query)
        }
    }

    private fun filterForums(query: String) {
        val currentList = forumViewModel.forums.value.orEmpty()
        val filteredList = if (query.isEmpty()) {
            currentList
        } else {
            currentList.filter {
                it.title.contains(query, ignoreCase = true) ||
                        it.description.contains(query, ignoreCase = true)
            }
        }
        forumAdapter.submitList(filteredList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

