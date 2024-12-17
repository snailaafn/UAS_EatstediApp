package com.example.eatstediapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.view.menu.ListMenuItemView
import androidx.recyclerview.widget.RecyclerView
import com.example.eatstediapp.database.Cart
import com.example.eatstediapp.model.Menus

class MenuAdapter (
    private val menuList: List<Menus>,
    private var cartMenu: Set<String>,
    private val onCartClicked:(String, Boolean) -> Unit,
    private val onEditClick: (String) -> Unit) :
    RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

        private val cartedItems = cartMenu.toMutableSet()

    class MenuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val tvMenuName: TextView = itemView.findViewById(R.id.txt_product_name)
        val cartIcon: ImageView = itemView.findViewById(R.id.icon_cart)
        val iconEdit: ImageView = itemView.findViewById(R.id.icon_edit)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_menu, parent, false)
        return MenuViewHolder(view)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val menu = menuList[position]
        val menus = menu.name
        holder.tvMenuName.text = menus

        val isCarted = cartedItems.contains(menus)
        holder.cartIcon.setImageResource(
            if (isCarted){
                R.drawable.shopping_bag
            } else {
                R.drawable.empty_bag
            }
        )

        holder.cartIcon.setOnClickListener{
            val newCartState =!isCarted
            if (newCartState) {
                cartedItems.add(menus)
            } else {
                cartedItems.remove(menus)
            }
            onCartClicked(menus, newCartState)
            notifyItemChanged(position)
        }
        holder.iconEdit.setOnClickListener {
            onEditClick(menu.name) // Callback untuk edit
        }
    }

    override fun getItemCount(): Int = menuList.size

    fun updateCartMenus(cartMenus: Set<String>){
        cartMenu = cartMenus
        notifyDataSetChanged()
    }

}

