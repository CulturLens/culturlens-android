package com.example.culturlens.ui.inbox

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.culturlens.R
import com.example.culturlens.adapter.NotificationAdapter
import com.example.culturlens.databinding.FragmentInboxBinding
import com.example.culturlens.model.NotificationItem

class InboxFragment : Fragment() {

    private var _binding: FragmentInboxBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInboxBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val newNotifications = listOf(
            NotificationItem(R.drawable.ic_profile, "Fanny liked your post", "30 minutes ago"),
            NotificationItem(R.drawable.ic_profile, "Sintya commented on your post", "1 hour ago")
        )

        val thisWeekNotifications = listOf(
            NotificationItem(R.drawable.ic_profile, "Amsal commented on your post", "3 days ago"),
            NotificationItem(R.drawable.ic_profile, "Adi Memes liked your post", "5 days ago")
        )

        val olderNotifications = listOf(
            NotificationItem(R.drawable.ic_profile, "Nanda commented on your post", "1 week ago"),
            NotificationItem(R.drawable.ic_profile, "Zaskia liked your post", "2 weeks ago")
        )

        // Set adapter untuk rvNew
        binding.rvNew.layoutManager = LinearLayoutManager(requireContext())
        binding.rvNew.adapter = NotificationAdapter(newNotifications)

        // Set adapter untuk rvThisWeek
        binding.rvThisWeek.layoutManager = LinearLayoutManager(requireContext())
        binding.rvThisWeek.adapter = NotificationAdapter(thisWeekNotifications)

        // Set adapter untuk rvOlder
        binding.rvOlder.layoutManager = LinearLayoutManager(requireContext())
        binding.rvOlder.adapter = NotificationAdapter(olderNotifications)

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
