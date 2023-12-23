package com.example.knockitUser.Adapter

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.knockitUser.Activity.ProductActivity
import com.example.knockitUser.Database.ProductDatabase
import com.example.knockitUser.Model.CategoryModel
import com.example.knockitUser.Model.ProductImagesModel
import com.example.knockitUser.R
import com.google.firebase.firestore.FirebaseFirestore

class ProductImagesAdapter(var context: Context, var model: ArrayList<ProductImagesModel>) : RecyclerView.Adapter<ProductImagesAdapter.viewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        var view: View = LayoutInflater.from(context).inflate(R.layout.item_product_images,parent,false)
        return viewHolder(view)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {

        Glide.with(context).load(model[position].image).into(holder.productImage)

//        for (i in model){
//            val userData: MutableMap<Any, String?> = HashMap()
//            userData["title"] = i.categoryTitle
//            FirebaseFirestore.getInstance().collection("Hii")
//                .document(i.categoryTitle)
//                .set(userData).addOnCompleteListener {
//                    Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
//                }
//        }

        }

    override fun getItemCount(): Int {
        return model.size
    }

    class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var productImage: ImageView = itemView.findViewById<ImageView?>(R.id.product_images)

    }
}