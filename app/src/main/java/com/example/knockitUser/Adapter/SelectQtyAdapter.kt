package com.example.knockitUser.Adapter

import android.R.attr.name
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.knockitUser.Activity.MainActivity
import com.example.knockitUser.Database.ProductDatabase
import com.example.knockitUser.Fragments.CategoryFragment
import com.example.knockitUser.Fragments.HomeFragment
import com.example.knockitUser.Fragments.MyOderFragment
import com.example.knockitUser.Fragments.ProfileFragment
import com.example.knockitUser.Fragments.WishlistFragment
import com.example.knockitUser.Model.CategoryModel
import com.example.knockitUser.Model.SelectQtyModel
import com.example.knockitUser.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class SelectQtyAdapter(var context: Context, var model: List<SelectQtyModel>) :
    RecyclerView.Adapter<SelectQtyAdapter.viewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        var view: View = LayoutInflater.from(context).inflate(R.layout.item_select_qty, parent, false)
        return viewHolder(view)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {

        holder.price.text = model[position].price
        holder.cuttedPrice.text = model[position].cuttedPrice
        holder.qty_text.text = model[position].qty
        if (model[position].availableQty == 0){
            holder.avlQtyText.text = model[position].availableQty.toString()
        }else{
            holder.avlQtyText.text = model[position].availableQty.toString()+" left"
        }

        holder.itemView.setOnClickListener {

            val update: MutableMap<String, Any> = HashMap()
            update["productPrice"] = model[position].price.toLong()
            update["productCuttedPrice"] = model[position].cuttedPrice.toLong()
            update["availableQty"] = model[position].availableQty.toInt()
            update["qty"] = model[position].qty
            update["productId"] = model[position].productId
            update["id"] = model[position].id

            FirebaseFirestore.getInstance()
                .collection("SELECTED_PRODUCT")
                .document(FirebaseAuth.getInstance().uid.toString())
                .set(update)
                .addOnCompleteListener {
                    ProductsAdapter.selectQtyDialog?.dismiss()
                }

        }

    }

    override fun getItemCount(): Int {
        return model.size
    }

    class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var price: TextView = itemView.findViewById<TextView?>(R.id.price_text)
        var cuttedPrice: TextView = itemView.findViewById<TextView?>(R.id.cutted_price_text)
        var qty_text: TextView = itemView.findViewById<TextView?>(R.id.qty_text)
        var avlQtyText: TextView = itemView.findViewById<TextView?>(R.id.avl_qty_text)
    }
}