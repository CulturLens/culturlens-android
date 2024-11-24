package com.example.culturlens.ui.profile

import android.app.AlertDialog
import android.content.ContentValues
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.culturlens.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.imageview.ShapeableImageView

class EditProfileFragment : Fragment() {

    private var imageUri: Uri? = null
    private lateinit var ivEditProfile: ShapeableImageView

    private lateinit var openGalleryLauncher: ActivityResultLauncher<String>
    private lateinit var openCameraLauncher: ActivityResultLauncher<Uri>

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

        val fabEditProfile: FloatingActionButton = binding.findViewById(R.id.fabEditProfile)

        fabEditProfile.setOnClickListener {
            showChangeProfilePhotoDialog()
        }

        return binding
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
