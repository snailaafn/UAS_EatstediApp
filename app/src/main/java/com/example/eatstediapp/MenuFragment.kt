package com.example.eatstediapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MenuFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MenuFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentMenuBinding
    private lateinit var mCartDao: CartDao
    private lateinit var executorService: ExecutorService

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

        executorService = Executors.newSingleThreadExecutor()
        val db = CartRoomDatabase.getDatabase(requireContext())
        mCartDao = db?.cartDao() ?: throw NullPointerException("CartDao is null")

        binding.rvMenu.layoutManager = LinearLayoutManager(requireContext())
        val client = ApiClient.getInstance()
        val responseMenu = client.getAllMenu()

        responseMenu.enqueue(object : Callback<List<Menus>> {
            override fun onResponse(call: Call<List<Menus>>, response: Response<List<Menus>>) {
                if (response.isSuccessful && response.body() != null) {
                    val menuList = response.body()!!

                    mCartDao.allCarts().observe(viewLifecycleOwner) { cartList ->
                        cartList?.let {
                        val cartMenus = cartList.map { it.productName }.toSet()

                        val adapter = MenuAdapter(menuList, cartMenus) { menus, isCarted ->
                            handleCart(menus, isCarted)
                        }
                        binding.rvMenu.adapter = adapter
                        }
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Gagal mengambil data menu",
                        Toast.LENGTH_SHORT
                    ).show()
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
}