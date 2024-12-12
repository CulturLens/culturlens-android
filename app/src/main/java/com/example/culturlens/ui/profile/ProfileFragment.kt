package com.example.culturlens.ui.profile

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.culturlens.R
import com.example.culturlens.SettingPreferences
import com.example.culturlens.SettingPreferencesViewModel
import com.example.culturlens.SettingsViewModelFactory
import com.example.culturlens.api.ApiClient
import com.example.culturlens.api.ApiService
import com.example.culturlens.pref.UserPreference
import com.example.culturlens.response.UserResponse
import com.example.culturlens.ui.login.WelcomeActivity
import com.example.culturlens.ui.dataStore
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.switchmaterial.SwitchMaterial
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale

class ProfileFragment : Fragment() {

    private lateinit var tvName: TextView
    private lateinit var tvUsername: TextView
    private lateinit var ivProfilePicture: ShapeableImageView
    private lateinit var btnChangeLanguage: ImageButton

    private val userPreference: UserPreference by lazy {
        UserPreference.getInstance(requireContext().dataStore)
    }

    private val viewModel: SettingPreferencesViewModel by viewModels {
        SettingsViewModelFactory(SettingPreferences(requireContext()))
    }

    private val apiService: ApiService by lazy {
        ApiClient.instance
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        tvName = view.findViewById(R.id.tvName)
        tvUsername = view.findViewById(R.id.tvUsername)
        ivProfilePicture = view.findViewById(R.id.ivProfilePicture)
        btnChangeLanguage = view.findViewById(R.id.btnChangeLanguage)

        loadUserProfile()

        btnChangeLanguage.setOnClickListener {
            showLanguageDialog()
        }

        val switchTheme = view.findViewById<SwitchMaterial>(R.id.switch_theme)

        viewModel.getThemeSetting().observe(viewLifecycleOwner) { isDarkModeActive ->
            switchTheme.isChecked = isDarkModeActive
        }

        switchTheme.setOnCheckedChangeListener { _, isChecked ->
            viewModel.saveThemeSetting(isChecked)
        }

        val btnLogout = view.findViewById<ImageButton>(R.id.btnLogout)
        btnLogout.setOnClickListener {
            showLogoutConfirmationDialog()
        }

        val btnEditProfile = view.findViewById<Button>(R.id.btnEditProfile)
        btnEditProfile.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
        }

        return view
    }

    private fun showLanguageDialog() {
        val languages = arrayOf("Indonesia", "English")
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Pilih Bahasa")
        builder.setItems(languages) { _, which ->
            when (which) {
                0 -> setLocale("id")
                1 -> setLocale("en")
            }
        }
        builder.show()
    }

    private fun setLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = Configuration()
        config.setLocale(locale)

        val context: Context = requireContext().createConfigurationContext(config)

        resources.updateConfiguration(config, resources.displayMetrics)

        val sharedPreferences = requireActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("LANGUAGE", languageCode).apply()

        requireActivity().recreate()
    }

    private fun loadUserProfile() {
        lifecycleScope.launch {
            userPreference.getSession().collect { user ->
                tvName.text = user.name
                tvUsername.text = "@${user.username}"

                val userId = user.userId

                apiService.getUser(userId).enqueue(object : Callback<UserResponse> {
                    override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                        if (response.isSuccessful) {
                            val userResponse = response.body()
                            userResponse?.let {
                                val profileImageUrl = it.profilePhotoUrl
                                if (!profileImageUrl.isNullOrEmpty()) {
                                    Glide.with(requireContext())
                                        .load(profileImageUrl)
                                        .circleCrop()
                                        .into(ivProfilePicture)
                                } else {
                                    Glide.with(requireContext())
                                        .load(R.drawable.profile_picture)
                                        .circleCrop()
                                        .into(ivProfilePicture)
                                }
                            }
                        } else {
                            Log.e("ProfileFragment", "Error fetching user profile: ${response.message()}")
                        }
                    }

                    override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                        Log.e("ProfileFragment", "Failed to fetch user profile: ${t.message}")
                    }
                })
            }
        }
    }


    private fun showLogoutConfirmationDialog() {
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Logout")
            .setMessage("Are you sure you want to log out?")
            .setPositiveButton("Yes, log me out") { _, _ ->
                logout()
            }
            .setNegativeButton("No, keep me signed in", null)
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(
                ContextCompat.getColor(requireContext(), R.color.celadon_blue)
            )
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(
                ContextCompat.getColor(requireContext(), R.color.celadon_blue)
            )
        }

        dialog.show()
    }

    private fun logout() {
        Log.d("ProfileFragment", "Logging out")
        lifecycleScope.launch {
            try {
                userPreference.logout()
                Log.d("ProfileFragment", "Logout successful")
                navigateToLogin()
            } catch (e: Exception) {
                Log.e("ProfileFragment", "Logout failed: ${e.message}")
            }
        }
    }

    private fun navigateToLogin() {
        Log.d("ProfileFragment", "Navigating to login")
        try {
            val intent = Intent(requireContext(), WelcomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        } catch (e: Exception) {
            Log.e("ProfileFragment", "Navigation failed: ${e.message}")
        }
    }

}