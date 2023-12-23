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
import com.example.knockitUser.Activity.ProductDetailsActivity
import com.example.knockitUser.Model.BannerModel
import com.example.knockitUser.Model.CategoryModel
import com.example.knockitUser.Model.CouponsModel
import com.example.knockitUser.Model.ProductModel
import com.example.knockitUser.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import java.text.SimpleDateFormat
import java.util.Date

class CouponsAdapter(var context: Context, var model: List<CouponsModel>) :
    RecyclerView.Adapter<CouponsAdapter.viewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        var view: View =
            LayoutInflater.from(context).inflate(R.layout.item_coupon_redemption, parent, false)
        return viewHolder(view)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {

        holder.title.text = model[position].subject
        val sdf = SimpleDateFormat("dd/MM/yy hh:mm a")
        val netDate = Date(model[position].validTime)
        val date = sdf.format(netDate)
        holder.validDate.text = "Valid - " + date

        FirebaseFirestore.getInstance()
            .collection("USERS")
            .document(FirebaseAuth.getInstance().uid.toString())
            .collection("COUPON_APPLIED")
            .document(model[position].id.toString())
            .addSnapshotListener { querySnapshot: DocumentSnapshot?, e: FirebaseFirestoreException? ->
                querySnapshot?.let {
                    val userModel =
                        it.toObject(CouponsModel::class.java)
                    if (model[position].id.equals(userModel?.id)) {
                        holder.usedCoupon.text = "Already Used"
                    }

                }
            }

        holder.itemView.setOnClickListener {
            if (!holder.usedCoupon.text.toString().equals("Already Used")) {
                if (model[position].title.equals("Discount")) {
                    var discountedPrice =
                        ((100 - model[position].price) * ProductDetailsActivity.couponOriginalPrice.text.toString()
                            .toInt()) / 100
                    ProductDetailsActivity.couponDiscountedPrice.text =
                        "Rs. " + discountedPrice.toString()
                } else {
                    var flatOffPrice = ProductDetailsActivity.couponOriginalPrice.text.toString()
                        .toInt() - model[position].price
                    ProductDetailsActivity.couponDiscountedPrice.text =
                        "Rs. " + flatOffPrice.toString()
                }
            } else {
                Toast.makeText(context, "You can't use this coupon", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount(): Int {
        return model.size
    }

    class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var title: TextView = itemView.findViewById(R.id.couponTitle)
        var validDate: TextView = itemView.findViewById(R.id.validDate)
        var usedCoupon: TextView = itemView.findViewById(R.id.used_coupon)
        var couponBg: ConstraintLayout = itemView.findViewById(R.id.coupon_bg)
    }
}