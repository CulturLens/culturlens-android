package com.example.culturlens.ui.forum


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.startActivity
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.culturlens.adapter.ForumAdapter
import com.example.culturlens.databinding.FragmentForumBinding
import com.example.culturlens.model.ForumItem

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

        loadDummyData()
    }

    private fun setupRecyclerView() {
        forumAdapter = ForumAdapter(
            forumList = filteredForumList,
            onItemClick = { forumItem ->
                val intent = Intent(requireContext(), DetailForumActivity::class.java)
                intent.putExtra("forum_id", forumItem.id)
                startActivity(intent)
            },
            onLikeClick = { forumItem ->
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
        filteredForumList.clear()
        if (query.isEmpty()) {
            filteredForumList.addAll(forumList)
        } else {
            filteredForumList.addAll(
                forumList.filter { it.content.contains(query, ignoreCase = true) }
            )
        }
        forumAdapter.notifyDataSetChanged()
    }

    private fun loadDummyData() {
        forumList.addAll(
            listOf(
                ForumItem("1", "User1", "This is a forum post.", "https://via.placeholder.com/150"),
                ForumItem("2", "User2", "Another interesting post.", "https://via.placeholder.com/150"),
                ForumItem("3", "User3", "A beautiful picture of nature.", "https://via.placeholder.com/150")
            )
        )
        filteredForumList.addAll(forumList)
        forumAdapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
