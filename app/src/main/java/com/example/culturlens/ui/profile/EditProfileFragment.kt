package com.example.culturlens.ui.profile

import android.app.AlertDialog
import android.content.ContentValues
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.culturlens.R
import com.example.culturlens.api.ApiClient
import com.example.culturlens.api.ApiService
import com.example.culturlens.model.UserModel
import com.example.culturlens.pref.UserPreference
import com.example.culturlens.response.UserRequest
import com.example.culturlens.response.UserResponse
import com.example.culturlens.ui.dataStore
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.imageview.ShapeableImageView
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditProfileFragment : Fragment() {

    private var imageUri: Uri? = null
    private lateinit var ivEditProfile: ShapeableImageView
    private lateinit var etUsername: EditText
    private lateinit var etName: EditText
    private lateinit var etPhone: EditText
    private lateinit var etEmail: EditText

    private lateinit var openGalleryLauncher: ActivityResultLauncher<String>
    private lateinit var openCameraLauncher: ActivityResultLauncher<Uri>

    private val apiService: ApiService by lazy {
        ApiClient.instance
    }

    private val userPreference: UserPreference by lazy {
        UserPreference.getInstance(requireContext().dataStore)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        openGalleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                ivEditProfile.setImageURI(uri)
            }
        }

        openCameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success: Boolean ->
            if (success) {
                imageUri?.let {
                    ivEditProfile.setImageURI(it)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = inflater.inflate(R.layout.fragment_edit_profile, container, false)

        ivEditProfile = binding.findViewById(R.id.ivEditProfile)
        etUsername = binding.findViewById(R.id.etUsername)
        etName = binding.findViewById(R.id.etName)
        etPhone = binding.findViewById(R.id.etPhone)
        etEmail = binding.findViewById(R.id.etEmail)

        val btnBack: ImageButton = binding.findViewById(R.id.btnBack)
        val btnCheck: ImageButton = binding.findViewById(R.id.btnCheck)

        btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        btnCheck.setOnClickListener {
            updateProfile()
        }


        val fabEditProfile: FloatingActionButton = binding.findViewById(R.id.fabEditProfile)

        fabEditProfile.setOnClickListener {
            showChangeProfilePhotoDialog()
        }

        loadUserProfile()

        return binding
    }

    private fun loadUserProfile() {
        lifecycleScope.launch {
            val user = userPreference.getSession().first() // Ambil data user dari UserPreference
            etUsername.setText(user.username)
            etName.setText(user.name)
            etPhone.setText(user.phone)
            etEmail.setText(user.email)
            Log.d("EditProfileFragment", "Loaded phone: ${user.phone}")
        }
    }

    private fun updateProfile() {
        val updatedUser = UserRequest(
            name = etName.text.toString(),
            username = etUsername.text.toString(),
            email = etEmail.text.toString(),
            phone = etPhone.text.toString(),
            profilePhotoUrl = imageUri?.toString()
        )

        lifecycleScope.launch {
            val userId = userPreference.getSession().first().userId

            apiService.updateUser(userId, updatedUser).enqueue(object : Callback<UserResponse> {
                override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                    if (response.isSuccessful) {
                        val updatedUserModel = UserModel(
                            userId = response.body()?.id ?: 0,
                            email = updatedUser.email,
                            name = updatedUser.name,
                            username = updatedUser.username,
                            isLogin = true,
                            token = ""
                        )
                        lifecycleScope.launch {
                            userPreference.saveSession(updatedUserModel)
                        }

                        findNavController().navigate(R.id.action_editProfileFragment_to_profileFragment)
                    } else {
                        Log.e("EditProfileFragment", "Failed to update profile: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                    Log.e("EditProfileFragment", "Failed to update profile: ${t.message}")
                }
            })
        }
    }

    private fun showChangeProfilePhotoDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_change_photo, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        val btnChooseFromLibrary: Button = dialogView.findViewById(R.id.btnChooseFromLibrary)
        val btnTakeNewPhoto: Button = dialogView.findViewById(R.id.btnTakeNewPhoto)

        btnChooseFromLibrary.setOnClickListener {
            openGallery()
            dialog.dismiss()
        }

        btnTakeNewPhoto.setOnClickListener {
            takeNewPhoto()
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun openGallery() {
        openGalleryLauncher.launch("image/*")
    }

    private fun takeNewPhoto() {
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.TITLE, "New Profile Picture")
        }
        imageUri = requireContext().contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        imageUri?.let {
            openCameraLauncher.launch(it)
        }
    }
}