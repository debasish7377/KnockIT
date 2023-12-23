package com.example.knockitUser.Adapter

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.example.knockitUser.Activity.ProductDetailsActivity
import com.example.knockitUser.Model.MyWishlistModel
import com.example.knockitUser.Model.ProductModel
import com.example.knockitUser.Model.SelectQtyModel
import com.example.knockitUser.R
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot

class MyWishlistAdapter(var context: Context, var model: List<MyWishlistModel>) :
    RecyclerView.Adapter<MyWishlistAdapter.viewHolder>() {

    companion object {
        var selectQtyDialog: Dialog? = null
        var price: String = ""
        var show_dialog: Boolean = false
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        var view: View =
            LayoutInflater.from(context).inflate(R.layout.item_wishlist, parent, false)
        return viewHolder(view)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {

        ////// load productDetails
        FirebaseFirestore.getInstance().collection("PRODUCTS")
            .document(model[position].productId)
            .get()
            .addOnSuccessListener(OnSuccessListener<DocumentSnapshot> { documentSnapshot ->
                val productModel: ProductModel? =
                    documentSnapshot.toObject(ProductModel::class.java)

                holder.miniProductTitle.text = productModel?.productTitle!!+" | "+productModel?.qty
                holder.miniProductPrice.text = "₹"+productModel.productPrice
                Glide.with(context).load(productModel.productImage).into(holder.miniProductImage)
                holder.miniProductCuttedPrice.text = "₹"+productModel.productCuttedPrice
                holder.miniProductRatting.text  = productModel.productRating
                holder.miniProductTotalRatting.text = productModel.productTotalRating
                holder.miniProductBrand.text = productModel.productBrandName
                if (productModel.productPrice.toLong() >= 800){
                    holder.miniProductDelivery.text = "Free Delivery"
                }else{
                    holder.miniProductDelivery.text = "80₹ Rupees Delivery Price"
                }

                if (productModel.availableQty.equals("0")){
                    holder.addToCardBtn.setText("Out of stock")
                    holder.addToCardBtn.visibility = View.VISIBLE
                    holder.addToCardBtn.setBackgroundTintList(context.getResources().getColorStateList(R.color.gray));
                }else{
                    holder.addToCardBtn.visibility = View.GONE
                    holder.addToCardBtn.setText("Add")
                    holder.addToCardBtn.setBackgroundTintList(context.getResources().getColorStateList(R.color.primary));
                }

                var discount =
                    100 - ((productModel.productPrice.toFloat() / productModel.productCuttedPrice.toFloat()) * 100)
                holder.productDiscount.text = discount.toInt().toString() + "% OFF"
                Glide.with(context).load(productModel.productImage).into(holder.miniProductImage)
            })
        ////// load productDetails

        holder.itemView.setOnClickListener {
            var intent = Intent(context, ProductDetailsActivity::class.java)
            intent.putExtra("productId", model[position].productId)
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return model.size
    }

    class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        var miniProductTitle: TextView = itemView.findViewById<TextView?>(R.id.mini_product_title)
        var miniProductPrice: TextView = itemView.findViewById<TextView?>(R.id.mini_product_price)
        var miniProductRatting: TextView = itemView.findViewById<TextView?>(R.id.mini_product_ratting_text)
        var miniProductTotalRatting: TextView = itemView.findViewById<TextView?>(R.id.mini_product_total_ratting)
        var miniProductDelivery: TextView = itemView.findViewById<TextView?>(R.id.mini_product_delivery)
        var miniProductCuttedPrice: TextView = itemView.findViewById<TextView?>(R.id.mini_product_cutted_price)
        var miniProductBrand: TextView = itemView.findViewById<TextView?>(R.id.product_brand)
        var productDiscount: TextView = itemView.findViewById<TextView?>(R.id.discount_text)
        var productId: TextView = itemView.findViewById<TextView?>(R.id.productId)
        var addToCardBtn: AppCompatButton = itemView.findViewById<AppCompatButton?>(R.id.btn_add_card)
        var miniProductImage: ImageView = itemView.findViewById<ImageView?>(R.id.mini_product_image)

    }
}