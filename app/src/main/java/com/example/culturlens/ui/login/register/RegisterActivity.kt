package com.example.culturlens.ui.login.register

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.culturlens.api.ApiClient
import com.example.culturlens.databinding.ActivityRegisterBinding
import com.example.culturlens.response.LoginResponse
import com.example.culturlens.response.RegisterRequest
import com.example.culturlens.ui.login.signin.SigninActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()

        binding.loginLinkTextView.setOnClickListener {
            val intent = Intent(this@RegisterActivity, SigninActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupView() {
        hideStatusBar()
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.registerButton.setOnClickListener {
            val name = binding.nameEditText.text.toString().trim()
            val username = binding.usernameEditText.text.toString().trim()
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()

            if (validateInputs(name, username, email, password)) {
                performRegistration(name, username, email, password)
            }
        }
    }

    private fun validateInputs(name: String, username: String, email: String, password: String): Boolean {
        return when {
            name.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty() -> {
                showError("Semua kolom harus diisi.")
                false
            }
            password.length < 8 -> {
                showError("Password harus terdiri dari minimal 8 karakter.")
                false
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                showError("Format email tidak valid.")
                false
            }
            else -> true
        }
    }

    private fun performRegistration(name: String, username: String, email: String, password: String) {
        binding.progressBar.visibility = View.VISIBLE

        val request = RegisterRequest(name, username, email, password)
        ApiClient.instance.register(request).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                binding.progressBar.visibility = View.GONE

                if (response.isSuccessful) {
                    val message = response.body()?.message
                    AlertDialog.Builder(this@RegisterActivity).apply {
                        setTitle("Berhasil!")
                        setMessage("$message. Silakan login untuk mulai menggunakan aplikasi.")
                        setPositiveButton("Login") { _, _ ->
                            val intent = Intent(this@RegisterActivity, SigninActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        create()
                        show()
                    }
                } else {
                    val errorMessage = response.errorBody()?.string()
                    Log.e("RegisterError", "Error Body: $errorMessage")
                    showError("Gagal mendaftar: $errorMessage")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                showError("Error: ${t.localizedMessage ?: "Terjadi kesalahan saat menghubungi server"}")
            }
        })
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun hideStatusBar() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }
}

