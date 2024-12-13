package com.example.culturlens.ui.forum

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
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
import android.Manifest
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat



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
        checkAndRequestPermissions()

        val dataStore = PreferenceDataStoreFactory.create {
            applicationContext.preferencesDataStoreFile("user_prefs")
        }
        userRepository = UserRepository.getInstance(UserPreference.getInstance(dataStore))

        btnAddImage = findViewById(R.id.btnAddImage)
        btnCamera = findViewById(R.id.btnCamera)
        ivPreview = findViewById(R.id.ivPreview)
        etTitle = findViewById(R.id.etInputText)
        tvDescription = findViewById(R.id.etInputText)
        btnPost = findViewById(R.id.btnPost)

        val tvUsername: TextView = findViewById(R.id.tvUsername)

        lifecycleScope.launch {
            userRepository.getSession().collect { user ->
                val username = user.username
                if (!username.isNullOrEmpty()) {
                    tvUsername.text = username
                } else {
                    tvUsername.text = "Username tidak ditemukan"
                }
            }
        }

        btnAddImage.setOnClickListener {
            openGallery()
        }

        btnCamera.setOnClickListener {
            openCamera()
        }

        btnPost.setOnClickListener {
            val title = etTitle.text.toString().trim()
            val description = tvDescription.text.toString().trim()

            if (title.isEmpty()) {
                Toast.makeText(this,
                    getString(R.string.judul_tidak_boleh_kosong), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (description.isEmpty()) {
                Toast.makeText(this,
                    getString(R.string.deskripsi_tidak_boleh_kosong), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (selectedPhotoUri == null) {
                Toast.makeText(this,
                    getString(R.string.tambahkan_gambar_untuk_forum), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

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
                        Log.d("PostForumActivity", "Selected image URI: $imageUri")
                        displayImage(imageUri)
                    } else {
                        Log.e("PostForumActivity", "Image URI is null")
                    }
                }
                REQUEST_IMAGE_CAPTURE -> {
                    if (::photoFile.isInitialized && photoFile.exists()) {
                        val photoUri = FileProvider.getUriForFile(
                            this,
                            "com.example.culturlens.fileprovider",
                            photoFile
                        )
                        Log.d("PostForumActivity", "Captured image URI: $photoUri")
                        displayImage(photoUri)
                    } else {
                        Toast.makeText(this,
                            getString(R.string.gagal_mengambil_gambar_dari_kamera), Toast.LENGTH_SHORT).show()
                    }
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
                val username = user.username

                if (username.isNullOrEmpty()) {
                    Toast.makeText(this@PostForumActivity,
                        getString(R.string.username_tidak_ditemukan), Toast.LENGTH_SHORT).show()
                    return@collect
                }

                try {
                    val file = FileUtils.getFileFromUri(this@PostForumActivity, photoUri)
                    if (!file.exists()) {
                        Toast.makeText(this@PostForumActivity,
                            getString(R.string.file_gambar_tidak_ditemukan), Toast.LENGTH_SHORT).show()
                        return@collect
                    }

                    val requestBodyPhoto = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    val photoMultipart = MultipartBody.Part.createFormData("image", file.name, requestBodyPhoto)
                    val titleBody = title.toRequestBody("text/plain".toMediaTypeOrNull())
                    val descriptionBody = description.toRequestBody("text/plain".toMediaTypeOrNull())
                    val usernameBody = username.toRequestBody("text/plain".toMediaTypeOrNull())

                    ApiClient.instance.createPost(token, titleBody, descriptionBody, usernameBody, photoMultipart)
                        .enqueue(object : Callback<GenericResponse> {
                            override fun onResponse(call: Call<GenericResponse>, response: Response<GenericResponse>) {
                                if (response.isSuccessful) {
                                    Toast.makeText(this@PostForumActivity,
                                        getString(R.string.forum_berhasil_ditambahkan), Toast.LENGTH_SHORT).show()
                                    val intent = Intent(this@PostForumActivity, MainActivity::class.java)
                                    intent.putExtra("fragment", "ForumFragment")
                                    startActivity(intent)
                                    finish()
                                } else {
                                    Log.e("PostForumActivity", "Error response: ${response.errorBody()?.string()}")
                                    Toast.makeText(this@PostForumActivity,
                                        getString(R.string.gagal_menambahkan_forum), Toast.LENGTH_LONG).show()
                                }
                            }

                            override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                                Log.e("PostForumActivity", "Request failed: ${t.message}")
                                Toast.makeText(this@PostForumActivity, "Error jaringan: ${t.message}", Toast.LENGTH_SHORT).show()
                            }
                        })
                } catch (e: Exception) {
                    Log.e("PostForumActivity", "Exception: ${e.message}")
                    Toast.makeText(this@PostForumActivity,
                        getString(R.string.terjadi_kesalahan, e.message), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun checkAndRequestPermissions() {
        val permissions = mutableListOf<String>()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.CAMERA)
        }

        if (permissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissions.toTypedArray(), REQUEST_PERMISSION_CODE)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                Toast.makeText(this, getString(R.string.izin_diberikan), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this,
                    getString(R.string.izin_diperlukan_untuk_melanjutkan), Toast.LENGTH_SHORT).show()
            }
        }
    }


    companion object {
        private const val REQUEST_IMAGE_PICK = 1
        private const val REQUEST_IMAGE_CAPTURE = 2
        private const val REQUEST_PERMISSION_CODE = 100
    }
}
