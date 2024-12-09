package com.example.culturlens.ui.forum

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.lifecycle.lifecycleScope
import com.example.culturlens.MainActivity
import com.example.culturlens.R
import com.example.culturlens.api.ApiClient
import com.example.culturlens.pref.UserPreference
import com.example.culturlens.pref.UserRepository
import com.example.culturlens.response.GenericResponse
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class PostForumActivity : AppCompatActivity() {

    private lateinit var btnAddImage: TextView
    private lateinit var btnCamera: Button
    private lateinit var ivPreview: ImageView
    private lateinit var etTitle: EditText
    private lateinit var tvDescription: EditText
    private lateinit var btnPost: Button

    private lateinit var photoFile: File
    private var selectedPhotoUri: Uri? = null

    private lateinit var userRepository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_post_forum)

        val dataStore = PreferenceDataStoreFactory.create {
            applicationContext.preferencesDataStoreFile("user_prefs")
        }
        userRepository = UserRepository.getInstance(UserPreference.getInstance(dataStore))

        // Bind views
        btnAddImage = findViewById(R.id.btnAddImage)
        btnCamera = findViewById(R.id.btnCamera)
        ivPreview = findViewById(R.id.ivPreview)
        tvDescription = findViewById(R.id.etInputText)
        btnPost = findViewById(R.id.btnPost)

        btnAddImage.setOnClickListener {
            openGallery()
        }

        btnCamera.setOnClickListener {
            openCamera()
        }

        // Handle "Post" button
        btnPost.setOnClickListener {
            val title = etTitle.text.toString().trim()
            val description = tvDescription.text.toString().trim()

            // Validate inputs
            if (title.isEmpty()) {
                Toast.makeText(this, "Judul tidak boleh kosong!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (description.isEmpty()) {
                Toast.makeText(this, "Deskripsi tidak boleh kosong!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (selectedPhotoUri == null) {
                Toast.makeText(this, "Tambahkan gambar untuk forum!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Post to API
            postToForumApi(title, description, selectedPhotoUri!!)
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_IMAGE_PICK)
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        photoFile = createImageFile()
        val photoUri: Uri = FileProvider.getUriForFile(
            this,
            "com.example.culturlens.fileprovider",
            photoFile
        )
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
    }

    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_PICK -> {
                    val imageUri: Uri? = data?.data
                    if (imageUri != null) {
                        displayImage(imageUri)
                    }
                }
                REQUEST_IMAGE_CAPTURE -> {
                    val photoUri = Uri.fromFile(photoFile)
                    displayImage(photoUri)
                }
            }
        }
    }

    private fun displayImage(imageUri: Uri) {
        ivPreview.setImageURI(imageUri)
        ivPreview.visibility = ImageView.VISIBLE
        selectedPhotoUri = imageUri
    }

    private fun postToForumApi(title: String, description: String, photoUri: Uri) {
        lifecycleScope.launch {
            userRepository.getSession().collect { user ->
                val token = "Bearer ${user.token}"

                // Convert URI to File
                val file = FileUtils.getFileFromUri(this@PostForumActivity, photoUri)
                val requestBodyPhoto = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val photoMultipart = MultipartBody.Part.createFormData("image", file.name, requestBodyPhoto)

                // Create RequestBody for title, description, and username
                val titleBody = title.toRequestBody("text/plain".toMediaTypeOrNull())
                val descriptionBody = description.toRequestBody("text/plain".toMediaTypeOrNull())
                val usernameBody = user.username.toRequestBody("text/plain".toMediaTypeOrNull())

                // Call API
                ApiClient.instance.createPost(token, titleBody, descriptionBody, usernameBody, photoMultipart)
                    .enqueue(object : Callback<GenericResponse> {
                        override fun onResponse(call: Call<GenericResponse>, response: Response<GenericResponse>) {
                            if (response.isSuccessful) {
                                Toast.makeText(this@PostForumActivity, "Forum berhasil ditambahkan!", Toast.LENGTH_SHORT).show()

                                // Redirect to ForumFragment
                                val intent = Intent(this@PostForumActivity, MainActivity::class.java)
                                intent.putExtra("fragment", "ForumFragment")
                                startActivity(intent)
                                finish()
                            } else {
                                Toast.makeText(this@PostForumActivity, "Gagal menambahkan forum!", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                            Toast.makeText(this@PostForumActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                        }
                    })
            }
        }
    }

    companion object {
        private const val REQUEST_IMAGE_PICK = 1
        private const val REQUEST_IMAGE_CAPTURE = 2
    }
}




