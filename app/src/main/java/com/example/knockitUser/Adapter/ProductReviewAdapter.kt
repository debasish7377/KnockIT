package com.example.knockitUser.Adapter

import android.app.Dialog
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
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.knockitUser.Activity.StoreDetailsActivity
import com.example.knockitUser.Model.BannerModel
import com.example.knockitUser.Model.CategoryModel
import com.example.knockitUser.Model.NotificationModel
import com.example.knockitUser.Model.ProductModel
import com.example.knockitUser.Model.ProductReviewModel
import com.example.knockitUser.Model.StoreModel
import com.example.knockitUser.Model.UserModel
import com.example.knockitUser.R
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat
import java.util.Date

class ProductReviewAdapter(var context: Context, var model: List<ProductReviewModel>) :
    RecyclerView.Adapter<ProductReviewAdapter.viewHolder>() {

    lateinit var notificationDialog: Dialog
    lateinit var notificationDesc: TextView
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        var view: View =
            LayoutInflater.from(context).inflate(R.layout.item_product_review, parent, false)
        return viewHolder(view)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {

        holder.reviewText.text = model[position].review
        val sdf = SimpleDateFormat("dd/MM/yy hh:mm a")
        val netDate = Date(model[position].timeStamp)
        val date = sdf.format(netDate)
        holder.date.text = "dt - " + date

        FirebaseFirestore.getInstance()
            .collection("USERS")
            .document(model[position].userId)
            .addSnapshotListener { querySnapshot: DocumentSnapshot?, e: FirebaseFirestoreException? ->
                querySnapshot?.let {
                    val userModel = it.toObject(UserModel::class.java)

                    holder.userName.text = userModel?.name
                    try {
                        if (userModel?.profile.equals("")) {
                            Glide.with(context!!).load(R.drawable.avatara)
                                .into(holder.userImage)
                        } else {
                            Glide.with(context!!).load(userModel?.profile.toString())
                                .placeholder(R.drawable.avatara)
                                .into(holder.userImage)
                        }
                    }catch (e: Exception){
                        e.printStackTrace()
                    }

                }
            }

    }

    override fun getItemCount(): Int {
        return model.size
    }

    class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var userName: TextView = itemView.findViewById<TextView?>(R.id.userName)
        var date: TextView = itemView.findViewById<TextView?>(R.id.date)
        var reviewText: TextView = itemView.findViewById<TextView?>(R.id.reviewText)
        var userImage: CircleImageView = itemView.findViewById<CircleImageView?>(R.id.userImage)

    }
}