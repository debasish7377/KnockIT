package com.example.knockitUser.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.knockitUser.Activity.OrderDetailsActivity
import com.example.knockitUser.Model.MyCartModel
import com.example.knockitUser.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore


class MyOrderAdapter(var context: Context, var model: ArrayList<MyCartModel>) :
    RecyclerView.Adapter<MyOrderAdapter.viewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        var view: View = LayoutInflater.from(context).inflate(R.layout.item_my_oder, parent, false)
        return viewHolder(view)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {

        FirebaseFirestore.getInstance()
            .collection("PRODUCTS")
            .document(model[position].productId)
            .get()
            .addOnCompleteListener(OnCompleteListener<DocumentSnapshot> { task ->
                if (task.isSuccessful) {
                    Glide.with(context).load(task.result.getString("productImage").toString())
                        .into(holder.productImage)
                }
            })
        holder.productTitle.text = model[position].productTitle
        holder.productPrice.text = model[position].productPrice.toString()
        holder.productCuttedPrice.text = model[position].productCuttedPrice.toString()
        holder.qty_text.text = model[position].qty
        var youSaved: String =
            (model[position].productCuttedPrice.toInt() - model[position].productPrice.toInt()).toString()
        holder.discountedPrice.text = "₹" + youSaved + " Saved"

        holder.itemView.setOnClickListener {
            var intent = Intent(context, OrderDetailsActivity::class.java)
            intent.putExtra("productTitle", model[position].productTitle)
            intent.putExtra("productPrice", model[position].productPrice.toString())
            intent.putExtra("productCuttedPrice", model[position].productCuttedPrice.toString())
            intent.putExtra("productId", model[position].productId)
            intent.putExtra("productQty", model[position].qty)
            intent.putExtra("productQtyNo", model[position].qtyNo.toString())


            intent.putExtra("address", model[position].address)
            intent.putExtra("city", model[position].city)
            intent.putExtra("pincode", model[position].pincode)
            intent.putExtra("uid", model[position].uid)

            intent.putExtra("paymentId", model[position].payment)
            intent.putExtra("delivery", model[position].delivery)
            intent.putExtra("orderConfirmedDate", model[position].orderConfirmedDate)
            intent.putExtra("shippedDate", model[position].shippedDate)
            intent.putExtra("outForDeliveryDate", model[position].outForDeliveryDate)
            intent.putExtra("deliveredDate", model[position].deliveredDate)
            intent.putExtra("ratingProduct", model[position].ratingProduct)
            intent.putExtra("orderId", model[position].id)
            intent.putExtra("deliveryPrice", model[position].deliveryPrice)
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return model.size
    }

    class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var productPrice: TextView = itemView.findViewById<TextView?>(R.id.mini_product_price)
        var productCuttedPrice: TextView = itemView.findViewById<TextView?>(R.id.mini_product_cutted_price)
        var productTitle: TextView = itemView.findViewById<TextView?>(R.id.mini_product_title)
        var productImage: ImageView = itemView.findViewById<ImageView?>(R.id.mini_product_image)
        var discountedPrice: TextView = itemView.findViewById<TextView?>(R.id.discount_text)
        var qty_text: TextView = itemView.findViewById<TextView?>(R.id.qty_text)

    }
}
