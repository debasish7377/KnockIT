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
import com.example.knockitUser.Activity.ProductDetailsActivity
import com.example.knockitUser.Database.ProductDatabase
import com.example.knockitUser.Model.CategoryModel
import com.example.knockitUser.Model.ProductImagesModel
import com.example.knockitUser.Model.ProductSelectSizeModel
import com.example.knockitUser.Model.SelectQtyModel
import com.example.knockitUser.R
import com.google.firebase.firestore.FirebaseFirestore

class ProductSelectSizeAdapter(var context: Context, var model: ArrayList<SelectQtyModel>) : RecyclerView.Adapter<ProductSelectSizeAdapter.viewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        var view: View = LayoutInflater.from(context).inflate(R.layout.item_product_select_size,parent,false)
        return viewHolder(view)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {

        holder.productSelectSize.text = model[position].qty

        holder.itemView.setOnClickListener {
            holder.productSelectSize.setTextColor(context.getColorStateList(R.color.primary))
            ProductDetailsActivity.priceText.text = model[position].price.toString()
            ProductDetailsActivity.cuttedPriceText.text = model[position].cuttedPrice.toString()
            ProductDetailsActivity.qtyText.text = model[position].qty.toString()
            ProductDetailsActivity.qtyId.text = model[position].id.toString()
            ProductDetailsActivity.qtyAvl.text = model[position].availableQty.toString()

            if (model[position].availableQty == 0){
                ProductDetailsActivity.outOfStockBtn.visibility = View.VISIBLE
                ProductDetailsActivity.outOfStockBtn.text = "Out Of Stock"
                ProductDetailsActivity.outOfStockBtn.setTextColor(context.getColorStateList(R.color.gray))
                ProductDetailsActivity.addToCardBtn.visibility = View.GONE
            }else{
                ProductDetailsActivity.outOfStockBtn.visibility = View.GONE
                ProductDetailsActivity.addToCardBtn.visibility = View.VISIBLE
            }
        }

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

        var productSelectSize: TextView = itemView.findViewById<TextView?>(R.id.select_size)

    }
}