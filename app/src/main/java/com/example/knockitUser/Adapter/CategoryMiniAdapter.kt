package com.example.knockitUser.Adapter

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.knockitUser.Activity.MainActivity
import com.example.knockitUser.Activity.ProductActivity
import com.example.knockitUser.Fragments.CategoryFragment
import com.example.knockitUser.Model.CategoryModel
import com.example.knockitUser.R

class CategoryMiniAdapter(var context: Context, var model: List<CategoryModel>) : RecyclerView.Adapter<CategoryMiniAdapter.viewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        var view: View = LayoutInflater.from(context).inflate(R.layout.item_category_mini,parent,false)
        return viewHolder(view)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {

        holder.categoryTitle.text = model[position].categoryTitle
        Glide.with(context).load(model[position].categoryImage).into(holder.categoryImage)
        holder.categoryImageBg.backgroundTintList = ColorStateList.valueOf(Color.parseColor(model[position].categoryBackground))
        holder.itemView.setOnClickListener {
            var sharedPreferences: SharedPreferences =
                (context as FragmentActivity).getSharedPreferences("MySharedPref",
                    Context.MODE_PRIVATE
                )
            val myEdit = sharedPreferences.edit()
            myEdit.putString("categoryTitle", model[position].categoryTitle)
            myEdit.putString("categoryImage", model[position].categoryImage)
            myEdit.commit()
            setFragment(CategoryFragment())
        }
        }

    override fun getItemCount(): Int {
        return model.size
    }

    class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var categoryTitle: TextView = itemView.findViewById<TextView?>(R.id.categoryTitle)
        var categoryImage: ImageView = itemView.findViewById<ImageView?>(R.id.categoryImage)
        var categoryImageBg: LinearLayout  = itemView.findViewById<LinearLayout?>(R.id.circle_image_bg)
    }

    fun setFragment(fragment: Fragment?) {
        val fragmentTransaction: FragmentTransaction =
            (context as FragmentActivity).supportFragmentManager?.beginTransaction()!!
        fragmentTransaction.setCustomAnimations(R.anim.slide_from_right, R.anim.slideout_from_left)
        if (fragment != null) {
            fragmentTransaction.replace(MainActivity.frameLayout.getId(), fragment)
        }
        fragmentTransaction.commit()
    }
}