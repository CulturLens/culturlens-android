package com.example.culturlens.ui.login.signin

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.culturlens.MainActivity
import com.example.culturlens.api.ApiResponse
import com.example.culturlens.api.LoginRequest
import com.example.culturlens.api.RetrofitClient
import com.example.culturlens.databinding.ActivitySigninBinding
import com.example.culturlens.model.UserModel
import com.example.culturlens.ui.login.register.RegisterActivity
import com.example.culturlens.ui.profile.ViewModelFactory

class SigninActivity : AppCompatActivity() {

    private val viewModel by viewModels<SigninViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivitySigninBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySigninBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()

        binding.RegisterLinkTextView.setOnClickListener {
            val intent = Intent(this@SigninActivity, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.signinButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                val request = LoginRequest(email, password)
                RetrofitClient.instance.login(request).enqueue(object : retrofit2.Callback<ApiResponse> {
                    override fun onResponse(call: retrofit2.Call<ApiResponse>, response: retrofit2.Response<ApiResponse>) {
                        if (response.isSuccessful) {
                            val token = response.body()?.token
                            viewModel.saveSession(UserModel(email, token ?: ""))
                            AlertDialog.Builder(this@SigninActivity).apply {
                                setTitle("Yeah!")
                                setMessage("Anda berhasil login. Ayo mulai gunakan CulturLens")
                                setPositiveButton("Lanjut") { _, _ ->
                                    val intent = Intent(this@SigninActivity, MainActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                    startActivity(intent)
                                    finish()
                                }
                                create()
                                show()
                            }
                        } else {
                            showError("Login gagal. Cek email dan password.")
                        }
                    }

                    override fun onFailure(call: retrofit2.Call<ApiResponse>, t: Throwable) {
                        showError("Error: ${t.message}")
                    }
                })
            } else {
                showError("Email dan password harus diisi.")
            }
        }
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}