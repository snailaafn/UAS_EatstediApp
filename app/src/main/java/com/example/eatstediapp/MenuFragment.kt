package com.example.eatstediapp

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.view.menu.MenuAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eatstediapp.database.Cart
import com.example.eatstediapp.database.CartDao
import com.example.eatstediapp.database.CartRoomDatabase
import com.example.eatstediapp.databinding.FragmentMenuBinding
import com.example.eatstediapp.model.Menus
import com.example.eatstediapp.network.ApiClient
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Suppress("DEPRECATION")
class MenuFragment : Fragment() {
    private lateinit var binding: FragmentMenuBinding
    private lateinit var mCartDao: CartDao
    private lateinit var executorService: ExecutorService
    private var menuList: MutableList<Menus> = mutableListOf()


//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        arguments?.let {
//            param1 = it.getString(ARG_PARAM1)
//            param2 = it.getString(ARG_PARAM2)
//        }
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        executorService = Executors.newSingleThreadExecutor()
        val db = CartRoomDatabase.getDatabase(requireContext())
        mCartDao = db?.cartDao() ?: throw NullPointerException("CartDao is null")

        binding.rvMenu.layoutManager = LinearLayoutManager(requireContext())
        // Inisialisasi adapter di awal
        val adapter = MenuAdapter(
            menuList,
            emptySet(), // Set cartMenus kosong dulu, akan di-update nanti
            onCartClicked = { menus, isCarted -> handleCart(menus, isCarted) },
            onEditClick = { productName ->
                val intent = Intent(requireContext(), SecondActivity::class.java)
                intent.putExtra("productName", productName)
                editProductLauncher.launch(intent) // Start activity for result
            }
        )
        binding.rvMenu.adapter = adapter

        val client = ApiClient.getInstance()
        val responseMenu = client.getAllMenu()

        responseMenu.enqueue(object : Callback<List<Menus>> {
            override fun onResponse(call: Call<List<Menus>>, response: Response<List<Menus>>) {
                if (response.isSuccessful && response.body() != null) {
                    menuList.clear()
                    menuList.addAll(response.body()!!)

                    mCartDao.allCarts().observe(viewLifecycleOwner) { cartList ->
                        val cartMenus = cartList.map { it.productName }.toSet()
                        // Update adapter setelah data diubah
                        adapter.updateCartMenus(cartMenus) // Tambahkan fungsi di adapter
                    }
                    adapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(requireContext(), "Gagal mengambil data menu", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(p0: Call<List<Menus>>, p1: Throwable) {
                Toast.makeText(requireContext(), "Koneksi error", Toast.LENGTH_SHORT).show()
            }
        })

    }

    private fun handleCart(menus: String, isCarted: Boolean) {
        executorService.execute {
            val isCart = mCartDao.isCart(menus)
            if (isCarted) {
                if (isCart) {
                    requireActivity().runOnUiThread {
                        Toast.makeText(
                            requireContext(),
                            "Menu sudah ada di keranjang",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    mCartDao.inserCart(Cart(productName = menus))
                }
            } else {
                mCartDao.deleteByProductName(menus)
            }
        }
    }

    private val editProductLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                val oldProductName = data?.getStringExtra("oldProductName")
                val newProductName = data?.getStringExtra("newProductName")

                if (oldProductName != null && newProductName != null) {
                    executorService.execute {
                        // Perbarui database jika data ada di keranjang
                        val cart = mCartDao.getCartByProductName(oldProductName)
                        if (cart != null) {
                            val updatedCart = cart.copy(productName = newProductName)
                            mCartDao.updateCart(updatedCart)
                        }

                        // Update nama produk di menuList
                        requireActivity().runOnUiThread {
                            val itemIndex = menuList.indexOfFirst { it.name == oldProductName }
                            if (itemIndex != -1) {
                                menuList[itemIndex].name = newProductName // Update data di list

                                requireActivity().runOnUiThread {
                                    binding.rvMenu.adapter?.notifyItemChanged(itemIndex) // Perbarui satu item
                                    Toast.makeText(requireContext(), "Produk berhasil diperbarui", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
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




    ////    companion object {
////        /**
////         * Use this factory method to create a new instance of
////         * this fragment using the provided parameters.
////         *
////         * @param param1 Parameter 1.
////         * @param param2 Parameter 2.
////         * @return A new instance of fragment MenuFragment.
////         */
////        // TODO: Rename and change types and number of parameters
////        @JvmStatic
////        fun newInstance(param1: String, param2: String) =
////            MenuFragment().apply {
////                arguments = Bundle().apply {
////                    putString(ARG_PARAM1, param1)
////                    putString(ARG_PARAM2, param2)
////                }
////            }
//    }
    companion object {
        private const val REQUEST_CODE_EDIT = 100
    }
}