package com.example.culturlens.ui.login.signin

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.culturlens.MainActivity
import com.example.culturlens.api.ApiClient
import com.example.culturlens.databinding.ActivitySigninBinding
import com.example.culturlens.model.UserModel
import com.example.culturlens.pref.UserPreference
import com.example.culturlens.pref.UserRepository
import com.example.culturlens.response.LoginRequest
import com.example.culturlens.response.LoginResponse
import com.example.culturlens.ui.dataStore
import com.example.culturlens.ui.login.register.RegisterActivity
import com.example.culturlens.ui.profile.ViewModelFactory
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SigninActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySigninBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySigninBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
    }

    private fun setupView() {
        hideStatusBar()
        supportActionBar?.hide()

        binding.RegisterLinkTextView.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupAction() {
        binding.signinButton.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()

            if (validateInputs(email, password)) {
                performLogin(email, password)
            }
        }
    }

    private fun validateInputs(email: String, password: String): Boolean {
        return when {
            email.isEmpty() || password.isEmpty() -> {
                showError("Email dan password harus diisi.")
                false
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                showError("Email tidak valid.")
                false
            }
            else -> true
        }
    }

    private fun performLogin(email: String, password: String) {
        val request = LoginRequest(email, password)
        ApiClient.instance.login(request).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse != null) {
                        val userModel = UserModel(
                            userId = loginResponse.userId ?: 0,
                            email = email,
                            name = loginResponse.name ?: "",
                            username = loginResponse.username ?: "",
                            isLogin = true,
                            token = loginResponse.token ?: ""
                        )

                        lifecycleScope.launch {
                            val userRepository = UserRepository.getInstance(
                                UserPreference.getInstance(applicationContext.dataStore)
                            )
                            userRepository.saveSession(userModel)
                            navigateToMain()
                        }
                    } else {
                        showError("Login gagal: Data tidak valid")
                    }
                } else {
                    showError("Login gagal: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                showError("Error: ${t.localizedMessage ?: "Terjadi kesalahan saat menghubungi server"}")
            }
        })
    }


    private fun navigateToMain() {
        AlertDialog.Builder(this).apply {
            setTitle("Yeah!")
            setMessage("Anda berhasil login. Ayo mulai gunakan aplikasi.")
            setPositiveButton("Lanjut") { _, _ ->
                val intent = Intent(this@SigninActivity, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
            create()
            show()
        }
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


