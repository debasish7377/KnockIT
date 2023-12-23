package com.example.knockitUser.Adapter

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.knockitUser.Activity.BannerResultActivity
import com.example.knockitUser.Activity.ProductDetailsActivity
import com.example.knockitUser.Model.BannerModel
import com.example.knockitUser.Model.CategoryModel
import com.example.knockitUser.Model.ProductModel
import com.example.knockitUser.R

class BannerAdapter(var context: Context, var model: List<BannerModel>) :
    RecyclerView.Adapter<BannerAdapter.viewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        var view: View =
            LayoutInflater.from(context).inflate(R.layout.item_banner_layout, parent, false)
        return viewHolder(view)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {

        Glide.with(context).load(model[position].bannerImage).into(holder.bannerImage)
        holder.bannerImageBg.backgroundTintList =
            ColorStateList.valueOf(Color.parseColor(model[position].bannerBackground))

        holder.itemView.setOnClickListener {
            if (!model[position].productId.equals("")) {
                var intent = Intent(context, ProductDetailsActivity::class.java)
                intent.putExtra("productId", model[position].productId)
                context.startActivity(intent)
            } else if (!model[position].subCategory.equals("")){
                var intent = Intent(context, BannerResultActivity::class.java)
                intent.putExtra("subCategory", model[position].subCategory)
                intent.putExtra("discount", model[position].discount.toString())
                context.startActivity(intent)
            }
        }

    }

    override fun getItemCount(): Int {
        return model.size
    }

    class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var bannerImage: ImageView = itemView.findViewById<ImageView?>(R.id.banner_image)
        var bannerImageBg: ConstraintLayout =
            itemView.findViewById<ConstraintLayout?>(R.id.banner_image_bg)
    }
}