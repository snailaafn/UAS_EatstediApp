import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.eatstediapp.R
import com.example.eatstediapp.database.Cart
import com.example.eatstediapp.model.Menus

class CartAdapter(
    private val cartItems: MutableList<Cart>, // Data keranjang
    private val onDeleteClick: (Cart) -> Unit // Callback untuk hapus item
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    // ViewHolder untuk setiap item
    class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtProductName: TextView = itemView.findViewById(R.id.txt_product_incart)
        val imgDelete: ImageView = itemView.findViewById(R.id.icon_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = cartItems[position]
        holder.txtProductName.text = item.productName

        // Set listener untuk tombol hapus
        holder.imgDelete.setOnClickListener {
            onDeleteClick(item)
        }
    }
    override fun getItemCount(): Int = cartItems.size

    // Menghapus item di posisi tertentu
    fun removeItem(position: Int) {
        cartItems.removeAt(position)
        notifyItemRemoved(position)
    }
}
