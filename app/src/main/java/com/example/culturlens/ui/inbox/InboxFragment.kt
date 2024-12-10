package com.example.culturlens.ui.inbox

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.culturlens.adapter.NotificationAdapter
import com.example.culturlens.api.ApiClient
import com.example.culturlens.model.NotificationItem
import com.example.culturlens.pref.UserRepository
import com.example.culturlens.databinding.FragmentInboxBinding
import com.example.culturlens.pref.UserPreference
import com.example.culturlens.ui.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class InboxFragment : Fragment() {

    private var _binding: FragmentInboxBinding? = null
    private val binding get() = _binding!!

    private val notificationAdapter = NotificationAdapter(emptyList())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentInboxBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.rvNew.layoutManager = LinearLayoutManager(requireContext())
        binding.rvNew.adapter = notificationAdapter

        getNotifications()

        return root
    }

    private fun getNotifications() {
        lifecycleScope.launch {
            val userRepository = UserRepository.getInstance(
                UserPreference.getInstance(requireContext().dataStore)
            )
            val userSession = userRepository.getSession().first()

            val token = "Bearer ${userSession.token}"

            if (userSession.token.isEmpty()) {
                Toast.makeText(context, "Token tidak ditemukan. Silakan login kembali.", Toast.LENGTH_SHORT).show()
                return@launch
            }

            fetchNotifications(token)
        }
    }

    private fun fetchNotifications(token: String) {
        val call = ApiClient.instance.getNotifications(token)

        call.enqueue(object : Callback<List<NotificationItem>> { // Gunakan List<NotificationItem>
            override fun onResponse(call: Call<List<NotificationItem>>, response: Response<List<NotificationItem>>) {
                if (response.isSuccessful) {
                    val notifications = response.body() ?: emptyList()
                    notificationAdapter.updateData(notifications)
                } else {
                    Log.e("API_ERROR", "Error: ${response.code()} - ${response.message()}")
                    Toast.makeText(context, "Gagal mengambil notifikasi", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<NotificationItem>>, t: Throwable) {
                Log.e("API_FAILURE", "Failure: ${t.message}")
                Toast.makeText(context, "Terjadi kesalahan: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
