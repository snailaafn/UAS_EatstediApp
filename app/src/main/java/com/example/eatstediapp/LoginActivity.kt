package com.example.eatstediapp

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.eatstediapp.databinding.ActivityLoginBinding
import com.example.eatstediapp.databinding.ActivitySecondBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("user", MODE_PRIVATE)

        binding.btnLogin.setOnClickListener{
            val usernameInput = binding.fieldUsername.text.toString()
            val passwordInput = binding.fieldPassword.text.toString()

            // Cek apakah input kosong
            if (usernameInput.isEmpty()) {
                Toast.makeText(this, "Username harus diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (passwordInput.isEmpty()) {
                Toast.makeText(this, "Password harus diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            //validasi login
            val savedUsername = sharedPreferences.getString("username", "")
            val savedPassword = sharedPreferences.getString("password", "")
            val savedEmail = sharedPreferences.getString("email", "")


            if (usernameInput == savedUsername && passwordInput == savedPassword){
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("email", savedEmail)

                startActivity(intent)
                finish()

            } else {
                Toast.makeText(this, "Invalid", Toast.LENGTH_SHORT).show()
            }
        }
    }
}