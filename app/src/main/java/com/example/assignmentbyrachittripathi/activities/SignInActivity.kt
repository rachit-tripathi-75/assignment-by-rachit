package com.example.assignmentbyrachittripathi.activities

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.assignmentbyrachittripathi.R
import com.example.assignmentbyrachittripathi.classes.LoginRequest
import com.example.assignmentbyrachittripathi.classes.RetrofitClient
import com.example.assignmentbyrachittripathi.databinding.ActivitySignInBinding
import com.example.assignmentbyrachittripathi.interfaces.ApiService
import kotlinx.coroutines.launch
import retrofit2.Retrofit

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private val BASE_URL = "https://staging.micodetest.com/api/"
    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        val sharedPreferences = getSharedPreferences("userSessionPrefs", MODE_PRIVATE)
        if (sharedPreferences.getBoolean("isLoggedIn", true)) {
            startActivity(Intent(this@SignInActivity, MainActivity::class.java))
            finish()
        }
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignInBinding.inflate(layoutInflater)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.progressBar.visibility = View.INVISIBLE

        binding.tvPasswordToggle.setOnClickListener {
            if (isPasswordVisible) {
                binding.etPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                binding.tvPasswordToggle.text = "Show Password"
                isPasswordVisible = false
            } else {
                binding.etPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                binding.tvPasswordToggle.text = "Hide Password"
                isPasswordVisible = true
            }
        }

        binding.btnSignIn.setOnClickListener {
            if (isValidSignIn()) {
                lifecycleScope.launch {
                    signIn(binding.etUsername.text.toString(), binding.etPassword.text.toString())
                }
            }
        }

    }

    private suspend fun signIn(username: String, password: String) {
        binding.progressBar.visibility = View.VISIBLE
        binding.btnSignIn.visibility = View.INVISIBLE
        val apiService: ApiService by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .build()
                .create(ApiService::class.java)
        }

        val request = LoginRequest(username, password)

        try {
            val response = RetrofitClient.apiService.login(request)

            if (response.isSuccessful) {
                val loginResponse = response.body()
                if (loginResponse != null) {
                    binding.progressBar.visibility = View.INVISIBLE
                    binding.btnSignIn.visibility = View.VISIBLE
                    if (loginResponse.status == "success") {
                        val sharedPreferences1 = getSharedPreferences("userSessionPrefs", MODE_PRIVATE)
                        val editor1 = sharedPreferences1.edit()
                        editor1.putBoolean("isLoggedIn", true)
                        editor1.apply()
                        val userData = loginResponse.data
                        val intent = Intent(this@SignInActivity, MainActivity::class.java)
                        val sharedPreferences2 = getSharedPreferences("userDetailsPrefs", MODE_PRIVATE)
                        val editor2 = sharedPreferences2.edit()
                        editor2.putInt("id", userData?.id!!)
                        editor2.putString("username", userData.username)
                        editor2.putString("email", userData.email)
                        editor2.putString("role", userData.role)
                        editor2.apply()
                        startActivity(intent)
                        finish()


                        Log.d("responseTAG", "response: ${loginResponse.message}")
                        if (userData != null) {
                            Log.d("responseTAG", "UserId: ${userData.id}")
                            Log.d("responseTAG", "Username: ${userData.username}")
                        }

                    } else {
                        Toast.makeText(this@SignInActivity, "Incorrect details", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            } else {
                Log.d("errorTAG", "HTTP Error: ${response.code()}")
                val errorBody = response.errorBody()?.string()
                Log.d("errorTAG: ", "error: $errorBody")
            }


        } catch (e: Exception) {
            Log.w("retrofitErrorTAG", "error: ${e.message}")
            Toast.makeText(
                this@SignInActivity,
                "Something went wrong! Please try again later",
                Toast.LENGTH_SHORT
            ).show()
        }


    }

    private fun isValidSignIn(): Boolean {
        if (binding.etUsername.text.toString().isEmpty()) {
            binding.etUsername.setError("Please enter your username")
            return false
        } else if (binding.etPassword.text.toString().isEmpty()) {
            binding.etUsername.setError("Please enter your password")
            return false
        }
        return true
    }
}