package com.example.eatstediapp

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.eatstediapp.databinding.ActivityMainBinding
import com.example.eatstediapp.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("user", MODE_PRIVATE)

        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        if (isLoggedIn) {
            navigateToMainActivity()
        }

        binding.btnRegister.setOnClickListener {
            val usernameInput = binding.fieldUsername.text.toString()
            val emailInput = binding.fieldEmail.text.toString()
            val passwordInput = binding.fieldPassword.text.toString()

            // Validasi input kosong
            if (usernameInput.isEmpty()) {
                Toast.makeText(this, "Username tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (emailInput.isEmpty()) {
                Toast.makeText(this, "Email tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (passwordInput.isEmpty()) {
                Toast.makeText(this, "Password tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val editor = sharedPreferences.edit()
            editor.putString("username", usernameInput)
            editor.putString("email", emailInput)
            editor.putString("password", passwordInput)
            editor.putBoolean("isLoggedIn", false) // Pastikan user harus login setelah registrasi
            editor.apply()

            Toast.makeText(this, "Registrasi berhasil! Silakan login.", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}