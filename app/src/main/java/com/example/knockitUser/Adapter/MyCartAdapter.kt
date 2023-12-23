package com.example.knockitUser.Adapter

import android.R.attr.name
import android.app.AlertDialog
import android.app.Dialog
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
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.example.knockitUser.Activity.MainActivity
import com.example.knockitUser.Activity.MyCartActivity
import com.example.knockitUser.Activity.ProductDetailsActivity
import com.example.knockitUser.Database.CouponsDatabase
import com.example.knockitUser.Database.ProductDatabase
import com.example.knockitUser.Fragments.CategoryFragment
import com.example.knockitUser.Fragments.HomeFragment
import com.example.knockitUser.Fragments.MyOderFragment
import com.example.knockitUser.Fragments.ProfileFragment
import com.example.knockitUser.Fragments.WishlistFragment
import com.example.knockitUser.Model.CategoryModel
import com.example.knockitUser.Model.CouponsModel
import com.example.knockitUser.Model.MyCartModel
import com.example.knockitUser.Model.SelectQtyModel
import com.example.knockitUser.Model.SelectedQtyModel
import com.example.knockitUser.Model.UserModel
import com.example.knockitUser.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot


class MyCartAdapter(var context: Context, var model: ArrayList<MyCartModel>) :
    RecyclerView.Adapter<MyCartAdapter.viewHolder>() {
    lateinit var loadingDialog: Dialog

    companion object {
        lateinit var couponsDialog: Dialog
        lateinit var couponRecyclerView: RecyclerView
        lateinit var couponOriginalPrice: TextView
        lateinit var couponDiscountedPrice: TextView
        lateinit var cartId: TextView
        lateinit var couponId: TextView
        lateinit var couponApplyBtn: TextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        var view: View = LayoutInflater.from(context).inflate(R.layout.item_my_cart, parent, false)
        return viewHolder(view)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {

        ////////////////Coupons dialog
        couponsDialog = Dialog(context)
        couponsDialog.setContentView(R.layout.dialog_layout_coupon_redemption)
        couponsDialog.setCancelable(true)
        couponsDialog.window?.setBackgroundDrawable(context.getDrawable(R.drawable.login_btn_bg))
        couponsDialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        couponRecyclerView = couponsDialog.findViewById(R.id.coupons_recyclerView)
        couponOriginalPrice = couponsDialog.findViewById(R.id.originalPrice)
        couponDiscountedPrice = couponsDialog.findViewById(R.id.discountedPrice)
        cartId = couponsDialog.findViewById(R.id.cartId)
        couponId = couponsDialog.findViewById(R.id.couponId)
        couponApplyBtn = couponsDialog.findViewById(R.id.applyBtn)
        ////////////////Coupons dialog

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
        holder.productPrice.text = "₹" + model[position].productPrice
        holder.productCuttedPrice.text = "₹" + model[position].productCuttedPrice
        holder.qty_text.text = model[position].qty
        holder.qtyNo.text = model[position].qtyNo.toString()
        var youSaved: String =
            (model[position].productCuttedPrice.toInt() - model[position].productPrice.toInt()).toString()
        holder.productSavedPrice.text = "₹" + youSaved + " Saved"
        holder.qtyNo.text = model[position].qtyNo.toString()
        holder.applyCoupon.text = model[position].coupon.toString()

        FirebaseFirestore.getInstance()
            .collection("USERS")
            .document(FirebaseAuth.getInstance().uid.toString())
            .addSnapshotListener { querySnapshot: DocumentSnapshot?, e: FirebaseFirestoreException? ->
                querySnapshot?.let {
                    val userModel = it.toObject(UserModel::class.java)

                    if (userModel?.cartSize.equals("0")) {
                        MyCartActivity.totalPrice.text = "0"
                        MyCartActivity.totalSavedPrice.text = "0"
                        MyCartActivity.checkOutBtn.visibility = View.GONE
                    } else {
                        MyCartActivity.checkOutBtn.visibility = View.VISIBLE
                        var sumProductPrice = 0
                        var sumProductCuttedPrice = 0
                        for (i in model) {
                            sumProductPrice = sumProductPrice + (i.productPrice * i.qtyNo)
                            sumProductCuttedPrice =
                                sumProductCuttedPrice + (i.productCuttedPrice * i.qtyNo)

                        }
                        MyCartActivity.totalPrice.text = sumProductPrice.toString()
                        MyCartActivity.totalSavedPrice.text =
                            (sumProductCuttedPrice - sumProductPrice).toString()
                    }

                }
            }


        ////////////////loading dialog
        loadingDialog = Dialog(context)
        loadingDialog.setContentView(R.layout.dialog_loading)
        loadingDialog.setCancelable(false)
        loadingDialog.window?.setBackgroundDrawable(context.getDrawable(R.drawable.login_btn_bg))
        loadingDialog.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        ////////////////loading dialog

        holder.addQty.setOnClickListener {
            holder.lessQty.visibility = View.INVISIBLE
            holder.addQty.visibility = View.INVISIBLE
            loadingDialog.show()
            FirebaseFirestore.getInstance()
                .collection("PRODUCTS")
                .document(model[position].productId)
                .collection("productSize")
                .document(model[position].qtyId)
                .get()
                .addOnSuccessListener(OnSuccessListener<DocumentSnapshot> { documentSnapshot ->
                    val selectQtyModel: SelectQtyModel? =
                        documentSnapshot.toObject(SelectQtyModel::class.java)

                    if (selectQtyModel?.availableQty == 0) {
                        holder.lessQty.visibility = View.VISIBLE
                        holder.addQty.visibility = View.VISIBLE
                        loadingDialog.dismiss()
                        Toast.makeText(
                            context,
                            "max Quantity " + holder.qtyNo.text,
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        val update: MutableMap<String, Any?> = HashMap()
                        update["availableQty"] =
                            selectQtyModel?.availableQty.toString().toLong() - 1
                        FirebaseFirestore.getInstance()
                            .collection("PRODUCTS")
                            .document(model[position].productId)
                            .collection("productSize")
                            .document(model[position].qtyId)
                            .update(update)
                            .addOnCompleteListener {
                                val qtyUpdate: MutableMap<String, Any?> = HashMap()
                                qtyUpdate["qtyNo"] = holder.qtyNo.text.toString().toLong() + 1
                                FirebaseFirestore.getInstance()
                                    .collection("USERS")
                                    .document(FirebaseAuth.getInstance().uid.toString())
                                    .collection("MY_CART")
                                    .document(model[position].productId)
                                    .update(qtyUpdate)
                                    .addOnCompleteListener {
                                        holder.lessQty.visibility = View.VISIBLE
                                        holder.addQty.visibility = View.VISIBLE
                                    }
                                loadingDialog.dismiss()

                            }
                    }

                })
        }

        holder.lessQty.setOnClickListener {
            holder.lessQty.visibility = View.INVISIBLE
            holder.addQty.visibility = View.INVISIBLE
            loadingDialog.show()
            FirebaseFirestore.getInstance()
                .collection("PRODUCTS")
                .document(model[position].productId)
                .collection("productSize")
                .document(model[position].qtyId)
                .get()
                .addOnSuccessListener(OnSuccessListener<DocumentSnapshot> { documentSnapshot ->
                    val selectQtyModel: SelectQtyModel? =
                        documentSnapshot.toObject(SelectQtyModel::class.java)

                    if (holder.qtyNo.text.toString().toInt() <= 1) {
                        holder.lessQty.visibility = View.VISIBLE
                        holder.addQty.visibility = View.VISIBLE
                        loadingDialog.dismiss()
                        Toast.makeText(
                            context,
                            "min Quantity " + holder.qtyNo.text,
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        val update: MutableMap<String, Any?> = HashMap()
                        update["availableQty"] =
                            selectQtyModel?.availableQty.toString().toLong() + 1
                        FirebaseFirestore.getInstance()
                            .collection("PRODUCTS")
                            .document(model[position].productId)
                            .collection("productSize")
                            .document(model[position].qtyId)
                            .update(update)
                            .addOnCompleteListener {
                                val qtyUpdate: MutableMap<String, Any?> = HashMap()
                                qtyUpdate["qtyNo"] = holder.qtyNo.text.toString().toLong() - 1
                                FirebaseFirestore.getInstance()
                                    .collection("USERS")
                                    .document(FirebaseAuth.getInstance().uid.toString())
                                    .collection("MY_CART")
                                    .document(model[position].productId)
                                    .update(qtyUpdate)
                                    .addOnCompleteListener {
                                        holder.lessQty.visibility = View.VISIBLE
                                        holder.addQty.visibility = View.VISIBLE
                                    }
                                loadingDialog.dismiss()
                            }
                    }

                })
        }

        holder.deleteBtn.setOnClickListener {

            loadingDialog.show()
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Delete")
            builder.setMessage("Are you sure to delete this item ?")

            builder.setPositiveButton("Yes") { dialog, which ->

                FirebaseFirestore.getInstance()
                    .collection("PRODUCTS")
                    .document(model[position].productId)
                    .collection("productSize")
                    .document(model[position].qtyId)
                    .get()
                    .addOnSuccessListener(OnSuccessListener<DocumentSnapshot> { documentSnapshot ->
                        val selectQtyModel: SelectQtyModel? =
                            documentSnapshot.toObject(SelectQtyModel::class.java)

                        val update: MutableMap<String, Any?> = HashMap()
                        update["availableQty"] = selectQtyModel?.availableQty.toString()
                            .toLong() + holder.qtyNo.text.toString().toLong()
                        FirebaseFirestore.getInstance()
                            .collection("PRODUCTS")
                            .document(model[position].productId)
                            .collection("productSize")
                            .document(model[position].qtyId)
                            .update(update)
                            .addOnCompleteListener {
                                loadingDialog.dismiss()
                                FirebaseFirestore.getInstance()
                                    .collection("USERS")
                                    .document(FirebaseAuth.getInstance().uid.toString())
                                    .collection("MY_CART")
                                    .document(model[position].productId)
                                    .delete()
                                    .addOnCompleteListener {
                                        if (it.isSuccessful) {
                                            FirebaseFirestore.getInstance().collection("USERS")
                                                .document(FirebaseAuth.getInstance().uid.toString())
                                                .get()
                                                .addOnSuccessListener(OnSuccessListener<DocumentSnapshot> { documentSnapshot ->
                                                    val model: UserModel? =
                                                        documentSnapshot.toObject(UserModel::class.java)

                                                    val userData: MutableMap<String, Any?> =
                                                        HashMap()
                                                    userData["cartSize"] =
                                                        (model?.cartSize?.toInt()!! - 1).toString()
                                                    FirebaseFirestore.getInstance()
                                                        .collection("USERS")
                                                        .document(FirebaseAuth.getInstance().uid.toString())
                                                        .update(userData)
                                                        .addOnCompleteListener {
                                                            if (it.isSuccessful) {
                                                                notifyDataSetChanged()
                                                                Toast.makeText(
                                                                    context,
                                                                    "Successfully item deleted",
                                                                    Toast.LENGTH_SHORT
                                                                ).show()
                                                                loadingDialog.dismiss()
                                                            }
                                                        }

                                                })
                                        }

                                    }
                            }

                    })

            }

            builder.setNegativeButton("No") { dialog, which ->
                loadingDialog.show()
            }

            builder.show()

        }

            holder.applyCoupon.setOnClickListener {
                if (holder.applyCoupon.text.equals("Apply your coupon here")) {
                    couponOriginalPrice.text = model[position].productPrice.toString()
                    cartId.text = model[position].productId.toString()

                    var couponsModel: ArrayList<CouponsModel> = ArrayList<CouponsModel>()
                    var couponsAdapter = ApplyCouponAdapter(context!!, couponsModel)
                    val layoutManager =
                        StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
                    couponRecyclerView.layoutManager = layoutManager
                    couponRecyclerView.adapter = couponsAdapter
                    var coupons: ArrayList<CouponsModel> = ArrayList<CouponsModel>()

                    FirebaseFirestore.getInstance()
                        .collection("Coupons")
                        .orderBy("timeStamp", Query.Direction.ASCENDING)
                        .addSnapshotListener { querySnapshot: QuerySnapshot?, e: FirebaseFirestoreException? ->
                            querySnapshot?.let {
                                coupons.clear()
                                for (document in it) {
                                    val model = document.toObject(CouponsModel::class.java)
                                    coupons.add(model)

                                    couponsModel.clear()
                                    for (p in coupons) {
                                        if (p.validTime >= System.currentTimeMillis()) {
                                            if (p.productAbovePrice <= couponOriginalPrice.text.toString()
                                                    .toInt()
                                            ) {

                                                couponsModel.add(p)
                                                couponsDialog.show()

                                            }

                                        }
                                    }
                                    couponsAdapter.notifyDataSetChanged()

                                }

                            }
                        }
                }else {
                    Toast.makeText(context, "Coupon Already Applied", Toast.LENGTH_SHORT).show()
                }

            }

        couponApplyBtn.setOnClickListener {
            couponsDialog.dismiss()
            val userData: MutableMap<String, Any?> = HashMap()
            userData["id"] = couponId.text.toString()

            FirebaseFirestore.getInstance()
                .collection("USERS")
                .document(FirebaseAuth.getInstance().uid.toString())
                .collection("COUPON_APPLIED")
                .document(couponId.text.toString())
                .set(userData)
                .addOnCompleteListener {

                    Toast.makeText(
                        context,
                        "Coupon Applied Successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            FirebaseFirestore.getInstance()
                .collection("USERS")
                .document(FirebaseAuth.getInstance().uid.toString())
                .collection("MY_CART")
                .document(cartId.text.toString())
                .get()
                .addOnSuccessListener(OnSuccessListener<DocumentSnapshot> { documentSnapshot ->
                    val cartModel: MyCartModel? = documentSnapshot.toObject(MyCartModel::class.java)

                    val userData: MutableMap<String, Any?> = HashMap()
                    userData["coupon"] = "Coupon Applied"
                    userData["couponId"] = couponId.text.toString()
                    userData["productPrice"] = couponDiscountedPrice.text.toString().toInt()

                    FirebaseFirestore.getInstance()
                        .collection("USERS")
                        .document(FirebaseAuth.getInstance().uid.toString())
                        .collection("MY_CART")
                        .document(cartId.text.toString())
                        .update(userData)
                        .addOnCompleteListener {

                        }

                })

        }
    }

    override fun getItemCount(): Int {
        return model.size
    }

    class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var productPrice: TextView = itemView.findViewById<TextView?>(R.id.productPrice)
        var productCuttedPrice: TextView = itemView.findViewById<TextView?>(R.id.productCuttedPrice)
        var productTitle: TextView = itemView.findViewById<TextView?>(R.id.product_title)
        var productImage: ImageView = itemView.findViewById<ImageView?>(R.id.productImage)
        var productSavedPrice: TextView = itemView.findViewById<TextView?>(R.id.savedPrice)
        var qtyNo: TextView = itemView.findViewById<TextView?>(R.id.qty_no)
        var addQty: TextView = itemView.findViewById<TextView?>(R.id.add_qty)
        var lessQty: TextView = itemView.findViewById<TextView?>(R.id.less_qty)
        var qty_text: TextView = itemView.findViewById<TextView?>(R.id.product_qty)
        var applyCoupon: TextView = itemView.findViewById<TextView?>(R.id.apply_coupon)
        var deleteBtn: LinearLayout = itemView.findViewById<LinearLayout?>(R.id.delete_item)
    }
}
