package com.example.eatstediapp

import CartAdapter
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eatstediapp.database.Cart
import com.example.eatstediapp.database.CartDao
import com.example.eatstediapp.database.CartRoomDatabase
import com.example.eatstediapp.databinding.FragmentCartBinding
import com.example.eatstediapp.model.Menus
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CartFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@Suppress("DEPRECATION")
class CartFragment : Fragment() {
    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!
    private lateinit var mCartDao: CartDao
    private lateinit var executorService: ExecutorService
    private val cartItems: MutableList<Cart> = mutableListOf()
    private lateinit var cartAdapter: CartAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       executorService = Executors.newSingleThreadExecutor()

        val db = CartRoomDatabase.getDatabase(requireContext())
        mCartDao =db?.cartDao() ?: throw IllegalStateException("CartDao is null")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        setupRecyclerView()
        getAllCarts()
    }

    private fun setupRecyclerView() {
        // Inisialisasi adapter
        cartAdapter = CartAdapter(cartItems) { item ->
            deleteCartItem(item)
        }
        // Atur RecyclerView
        binding.rvCart.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = cartAdapter
        }
    }

    private fun getAllCarts() {
        mCartDao.allCarts().observe(viewLifecycleOwner) { carts ->
            Log.d("CartFragment", "Data Carts: $carts")

            cartItems.clear()
            cartItems.addAll(carts)
            cartAdapter.notifyDataSetChanged() // Perbarui RecyclerView setelah data diubah
        }
    }

    private fun deleteCartItem(item: Cart) {
        executorService.execute {
            // Menghapus item dari database
            mCartDao.deleteCart(item)

            // Menghapus item dari list di adapter
            val position = cartItems.indexOf(item)
            if (position != -1) {
                requireActivity().runOnUiThread {
                    // Menghapus item dari RecyclerView
                    cartAdapter.removeItem(position)

                    // Menampilkan Toast bahwa item telah dihapus
                    Toast.makeText(requireContext(), "${item.productName} berhasil dihapus", Toast.LENGTH_SHORT).show()
                }
            }
        }
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

private fun <E> MutableList<E>.addAll(elements: List<Cart>?) {

}
//    private fun getAllCarts() {
//        mCartDao.allCarts().observe(viewLifecycleOwner) { carts ->
//            val cartProductName = carts.map {it.productName}
//
//            val adapter: ArrayAdapter<String> = ArrayAdapter(
//                requireContext(),
//                android.R.layout.simple_list_item_1,
//                cartProductName
//            )
//            binding.lvCart.adapter = adapter
//
//        }
//    }


//    companion object {
//        /**
//         * Use this factory method to create a new instance of
//         * this fragment using the provided parameters.
//         *
//         * @param param1 Parameter 1.
//         * @param param2 Parameter 2.
//         * @return A new instance of fragment CartFragment.
//         */
//        // TODO: Rename and change types and number of parameters
//        @JvmStatic
//        fun newInstance(param1: String, param2: String) =
//            CartFragment().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
//                }
//            }
//    }
//}