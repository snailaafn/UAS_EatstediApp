package com.example.eatstediapp

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.eatstediapp.databinding.FragmentProfileBinding

@Suppress("DEPRECATION")
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize SharedPreferences
        sharedPreferences = requireContext().getSharedPreferences("user", AppCompatActivity.MODE_PRIVATE)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        // Get username and email from SharedPreferences
        val username = sharedPreferences.getString("username", "Unknown")
        val email = sharedPreferences.getString("email", "Unknown")

        // Set data to TextViews
        binding.showFullName.text = username
        binding.showEmail.text = email
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_option, menu)
    }
    // Handle klik menu item
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.option_logout -> {
                handleLogout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    private fun handleLogout() {
        // Hapus data sesi di SharedPreferences
        val sharedPref: SharedPreferences =
            requireActivity().getSharedPreferences("my_prefs", android.content.Context.MODE_PRIVATE)
        sharedPref.edit().clear().apply()

        // Arahkan ke LoginActivity
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)

        // Tutup Fragment atau Activity yang sedang berjalan
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
