package com.example.eatstediapp

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.eatstediapp.databinding.ActivitySecondBinding

class SecondActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySecondBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySecondBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Tangkap data productName
        val productName = intent.getStringExtra("productName")
        binding.editProductName.setText(productName)

        // Simpan perubahan
        binding.btnSave.setOnClickListener {
            val newProductName = binding.editProductName.text.toString()
            if (newProductName.isNotEmpty()) {
                val resultIntent = Intent()
                resultIntent.putExtra("newProductName", newProductName)
                resultIntent.putExtra("oldProductName", productName)
                setResult(RESULT_OK, resultIntent)
                finish()
            } else {
                Toast.makeText(this, "Nama produk tidak boleh kosong", Toast.LENGTH_SHORT).show()
            }
        }
    }
}