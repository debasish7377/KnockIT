package com.example.knockitUser.Adapter

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.example.knockitUser.Activity.ProductDetailsActivity
import com.example.knockitUser.Database.ProductDatabase
import com.example.knockitUser.Database.SelectQtyDatabase
import com.example.knockitUser.Model.MyCartModel
import com.example.knockitUser.Model.ProductModel
import com.example.knockitUser.Model.SelectQtyModel
import com.example.knockitUser.Model.SelectedQtyModel
import com.example.knockitUser.Model.UserModel
import com.example.knockitUser.R
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.collection.LLRBNode.Color
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot

class ProductsAdapter(var context: Context, var model: List<ProductModel>) :
    RecyclerView.Adapter<ProductsAdapter.viewHolder>() {
    lateinit var loadingDialog: Dialog
    companion object {
        var selectQtyDialog: Dialog? = null
        var price: String = ""
        var show_dialog: Boolean = false
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        var view: View =
            LayoutInflater.from(context).inflate(R.layout.item_mini_product_view, parent, false)
        return viewHolder(view)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {

        holder.miniProductTitle.text = model[position].productTitle
        holder.miniProductPrice.text = model[position].productPrice.toString()
        holder.miniProductCuttedPrice.text = model[position].productCuttedPrice.toString()
        holder.miniProductRatting.text = model[position].productRating
        holder.miniProductTotalRatting.text = model[position].productTotalRating
        holder.miniProductBrand.text = model[position].productBrandName
        holder.productId.text = model[position].id
        holder.storeIdText.text = model[position].storeId
        holder.productImageText.text = model[position].productImage

        if (model[position].availableQty.equals("0")){
            holder.addToCardBtn.setText("Out of stock")
            holder.addToCardBtn.setBackgroundTintList(context.getResources().getColorStateList(R.color.gray));
            holder.addToCardBtn.visibility = View.VISIBLE
        }else{
            holder.addToCardBtn.setText("Add")
            holder.addToCardBtn.setBackgroundTintList(context.getResources().getColorStateList(R.color.primary));
            holder.addToCardBtn.visibility = View.VISIBLE
        }

        var discount =
            100 - ((model[position].productPrice.toFloat() / model[position].productCuttedPrice.toFloat()) * 100)
        holder.productDiscount.text = discount.toInt().toString() + "% OFF"
        Glide.with(context).load(model[position].productImage).into(holder.miniProductImage)

        holder.itemView.setOnClickListener {
            var intent = Intent(context, ProductDetailsActivity::class.java)
            intent.putExtra("productId", model[position].id)
            context.startActivity(intent)
        }
        ////////////////loading dialog
        selectQtyDialog = Dialog(context)
        selectQtyDialog?.setContentView(R.layout.dialog_select_qty)
        selectQtyDialog?.setCancelable(false)
        selectQtyDialog?.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        ////////////////loading dialog


        holder.selectQuantity.setOnClickListener {

            var quantityRecyclerView: RecyclerView =
                selectQtyDialog?.findViewById(R.id.select_qty_recyclerView)!!

            var qtyModel: ArrayList<SelectQtyModel> = ArrayList<SelectQtyModel>()
            var qtyAdapter = SelectQtyAdapter(context!!, qtyModel)
            val layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
            quantityRecyclerView.layoutManager = layoutManager
            quantityRecyclerView.adapter = qtyAdapter
            qtyModel.clear()
            FirebaseFirestore.getInstance()
                .collection("PRODUCTS")
                .document(model[position].id)
                .collection("productSize")
                .orderBy("timeStamp", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(OnSuccessListener<QuerySnapshot> { queryDocumentSnapshots ->
                    for (snapshot in queryDocumentSnapshots) {
                        val model: SelectQtyModel = snapshot.toObject(SelectQtyModel::class.java)
                        qtyModel.add(model)
                        selectQtyDialog?.show()

                        FirebaseFirestore.getInstance()
                            .collection("SELECTED_PRODUCT")
                            .document(FirebaseAuth.getInstance().uid.toString())
                            .addSnapshotListener { querySnapshot: DocumentSnapshot?, e: FirebaseFirestoreException? ->
                                querySnapshot?.let {
                                    val model = it.toObject(SelectedQtyModel::class.java)

                                    if (holder.productId.text.equals(model?.productId)) {
                                        holder.miniProductPrice.text = model?.productPrice.toString()
                                        holder.miniProductCuttedPrice.text = model?.productCuttedPrice.toString()
                                        holder.selectQtyText.text = model?.qty
                                        holder.avlQtyText.text = model?.availableQty.toString()
                                        holder.qtyId.text = model?.id.toString()
                                        holder.addToCardBtn.visibility = View.VISIBLE
                                        holder.qtyBtn.visibility = View.GONE
                                        holder.qtyNo.text = 1.toString()
                                        if (model?.availableQty == 0){
                                            holder.addToCardBtn.setText("Out of stock")
                                            holder.addToCardBtn.setBackgroundTintList(context.getResources().getColorStateList(R.color.gray));
                                            holder.addToCardBtn.isClickable = false
                                        }else{
                                            holder.addToCardBtn.setText("Add")
                                            holder.addToCardBtn.setBackgroundTintList(context.getResources().getColorStateList(R.color.primary));
                                            holder.addToCardBtn.isClickable = true

                                        }

                                        var discount =
                                            100 - ((model?.productPrice?.toFloat()!! / model?.productCuttedPrice?.toFloat()!!) * 100)
                                        holder.productDiscount.text = discount.toInt().toString() + "% OFF"
                                    }

                                }
                            }

                    }
                    qtyAdapter.notifyDataSetChanged()
                })

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

        FirebaseFirestore.getInstance().collection("USERS")
            .document(FirebaseAuth.getInstance().uid.toString())
            .collection("MY_CART")
            .document(holder.productId.text.toString())
            .get()
            .addOnSuccessListener(OnSuccessListener<DocumentSnapshot> { documentSnapshot ->
                val model: MyCartModel? = documentSnapshot.toObject(
                    MyCartModel::class.java
                )

                if (model?.productId.equals(holder.productId.text.toString())) {
//                    holder.qtyBtn.visibility = View.VISIBLE
//                    holder.addToCardBtn.visibility = View.GONE

                    holder.qtyNo.text = 1.toString()

                }

            })

        holder.addQty.setOnClickListener {
            holder.lessQty.visibility = View.INVISIBLE
            holder.addQty.visibility = View.INVISIBLE
            loadingDialog.show()
            FirebaseFirestore.getInstance()
                .collection("PRODUCTS")
                .document(model[position].id)
                .collection("productSize")
                .document(holder.qtyId.text.toString())
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
                            .document(model[position].id)
                            .collection("productSize")
                            .document(holder.qtyId.text.toString())
                            .update(update)
                            .addOnCompleteListener {
                                val qtyUpdate: MutableMap<String, Any?> = HashMap()
                                qtyUpdate["qtyNo"] = holder.qtyNo.text.toString().toLong() + 1
                                FirebaseFirestore.getInstance()
                                    .collection("USERS")
                                    .document(FirebaseAuth.getInstance().uid.toString())
                                    .collection("MY_CART")
                                    .document(model[position].id)
                                    .update(qtyUpdate)
                                    .addOnCompleteListener {
                                        holder.lessQty.visibility = View.VISIBLE
                                        holder.addQty.visibility = View.VISIBLE
                                        holder.qtyNo.text = (holder.qtyNo.text.toString().toInt() + 1).toString()
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
                .document(model[position].id)
                .collection("productSize")
                .document(holder.qtyId.text.toString())
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
                            .document(model[position].id)
                            .collection("productSize")
                            .document(holder.qtyId.text.toString())
                            .update(update)
                            .addOnCompleteListener {
                                val qtyUpdate: MutableMap<String, Any?> = HashMap()
                                qtyUpdate["qtyNo"] = holder.qtyNo.text.toString().toLong() - 1
                                FirebaseFirestore.getInstance()
                                    .collection("USERS")
                                    .document(FirebaseAuth.getInstance().uid.toString())
                                    .collection("MY_CART")
                                    .document(model[position].id)
                                    .update(qtyUpdate)
                                    .addOnCompleteListener {
                                        holder.lessQty.visibility = View.VISIBLE
                                        holder.addQty.visibility = View.VISIBLE
                                        holder.qtyNo.text = (holder.qtyNo.text.toString().toInt() - 1).toString()
                                    }
                                loadingDialog.dismiss()
                            }
                    }

                })
        }

        //////// Add to cart
        holder.addToCardBtn.setOnClickListener {
            if (holder.selectQtyText.text.equals("Select Quantity")) {
                var quantityRecyclerView: RecyclerView =
                    selectQtyDialog?.findViewById(R.id.select_qty_recyclerView)!!

                var qtyModel: ArrayList<SelectQtyModel> = ArrayList<SelectQtyModel>()
                var qtyAdapter = SelectQtyAdapter(context!!, qtyModel)
                val layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
                quantityRecyclerView.layoutManager = layoutManager
                quantityRecyclerView.adapter = qtyAdapter
                qtyModel.clear()
                FirebaseFirestore.getInstance()
                    .collection("PRODUCTS")
                    .document(model[position].id)
                    .collection("productSize")
                    .orderBy("timeStamp", Query.Direction.ASCENDING)
                    .get()
                    .addOnSuccessListener(OnSuccessListener<QuerySnapshot> { queryDocumentSnapshots ->
                        for (snapshot in queryDocumentSnapshots) {
                            val productModel: SelectQtyModel = snapshot.toObject(SelectQtyModel::class.java)
                            qtyModel.add(productModel)
                            selectQtyDialog?.show()

                            FirebaseFirestore.getInstance()
                                .collection("SELECTED_PRODUCT")
                                .document(FirebaseAuth.getInstance().uid.toString())
                                .addSnapshotListener { querySnapshot: DocumentSnapshot?, e: FirebaseFirestoreException? ->
                                    querySnapshot?.let {
                                        val selectedQtyModel = it.toObject(SelectedQtyModel::class.java)

                                        if (holder.productId.text.equals(selectedQtyModel?.productId)) {
                                            holder.miniProductPrice.text = selectedQtyModel?.productPrice.toString()
                                            holder.miniProductCuttedPrice.text = selectedQtyModel?.productCuttedPrice.toString()
                                            holder.selectQtyText.text = selectedQtyModel?.qty
                                            holder.avlQtyText.text = selectedQtyModel?.availableQty.toString()
                                            holder.qtyId.text = selectedQtyModel?.id.toString()
                                            holder.addToCardBtn.visibility = View.VISIBLE
                                            holder.qtyBtn.visibility = View.GONE
                                            holder.qtyNo.text = 1.toString()
                                            if (selectedQtyModel?.availableQty == 0){
                                                holder.addToCardBtn.setText("Out of stock")
                                                holder.addToCardBtn.setBackgroundTintList(context.getResources().getColorStateList(R.color.gray));
                                                holder.addToCardBtn.isClickable = false
                                            }else{
                                                holder.addToCardBtn.setText("Add")
                                                holder.addToCardBtn.setBackgroundTintList(context.getResources().getColorStateList(R.color.primary));
                                                holder.addToCardBtn.isClickable = true


                                                loadingDialog.show()
                                                FirebaseFirestore.getInstance().collection("USERS")
                                                    .document(FirebaseAuth.getInstance().uid.toString())
                                                    .collection("MY_CART")
                                                    .document(holder.productId.text.toString())
                                                    .get()
                                                    .addOnSuccessListener(OnSuccessListener<DocumentSnapshot> { documentSnapshot ->
                                                        val myCartModel: MyCartModel? = documentSnapshot.toObject(
                                                            MyCartModel::class.java
                                                        )

                                                        if (myCartModel?.productId.equals(holder.productId.text.toString())) {
                                                            Toast.makeText(
                                                                context,
                                                                "Product already added to the cart",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                            loadingDialog.dismiss()
                                                        } else {
                                                            FirebaseFirestore.getInstance().collection("USERS")
                                                                .document(FirebaseAuth.getInstance().uid.toString())
                                                                .get()
                                                                .addOnSuccessListener(OnSuccessListener<DocumentSnapshot> { documentSnapshot ->
                                                                    val userModel: UserModel? =
                                                                        documentSnapshot.toObject(
                                                                            UserModel::class.java
                                                                        )

                                                                    val userData: MutableMap<Any, Any?> = HashMap()
                                                                    userData["uid"] = userModel?.uid
                                                                    userData["city"] = userModel?.city
                                                                    userData["state"] = userModel?.state
                                                                    userData["pincode"] = userModel?.pincode
                                                                    userData["address"] = userModel?.address
                                                                    userData["latitude"] = userModel?.latitude
                                                                    userData["longitude"] = userModel?.longitude
                                                                    userData["name"] = userModel?.name
                                                                    userData["number"] = userModel?.number
                                                                    userData["userToken"] = userModel?.token

                                                                    userData["productId"] = holder.productId.text.toString()
                                                                    userData["storeToken"] = model[position].storeToken
                                                                    userData["storeId"] = holder.storeIdText.text.toString()
                                                                    userData["productPrice"] =
                                                                        holder.miniProductPrice.text.toString().toInt()
                                                                    userData["price"] = holder.miniProductPrice.text.toString().toInt()
                                                                    userData["productCuttedPrice"] =
                                                                        holder.miniProductCuttedPrice.text.toString().toInt()
                                                                    userData["qty"] = holder.selectQtyText.text.toString()
                                                                    userData["productTitle"] = holder.miniProductTitle.text.toString()
                                                                    userData["productImage"] = holder.productImageText.text.toString()
                                                                    userData["timeStamp"] = System.currentTimeMillis().toLong()
                                                                    userData["qtyNo"] = 1
                                                                    userData["qtyId"] = holder.qtyId.text.toString()
                                                                    userData["couponId"] = ""
                                                                    userData["coupon"] = "Apply your coupon here"
                                                                    userData["delivery"] = "Pending"
                                                                    userData["orderConfirmedDate"] = ""
                                                                    userData["shippedDate"] = ""
                                                                    userData["outForDeliveryDate"] = ""
                                                                    userData["deliveredDate"] = ""
                                                                    userData["payment"] = ""
                                                                    userData["id"] = ""
                                                                    userData["riderId"] = ""
                                                                    userData["ratingProduct"] = ""

                                                                    FirebaseFirestore.getInstance().collection("USERS")
                                                                        .document(FirebaseAuth.getInstance().uid.toString())
                                                                        .collection("MY_CART")
                                                                        .document(holder.productId.text.toString())
                                                                        .set(userData)
                                                                        .addOnCompleteListener {
                                                                            if (it.isSuccessful) {
                                                                                val update: MutableMap<String, Any?> = HashMap()
                                                                                update["availableQty"] =
                                                                                    holder.avlQtyText.text.toString().toLong() - 1
                                                                                FirebaseFirestore.getInstance()
                                                                                    .collection("PRODUCTS")
                                                                                    .document(holder.productId.text.toString())
                                                                                    .collection("productSize")
                                                                                    .document(
                                                                                        holder.qtyId.text.toString()
                                                                                    )
                                                                                    .update(update)
                                                                                    .addOnCompleteListener {
                                                                                        if (it.isSuccessful) {
                                                                                            val userData: MutableMap<String, Any?> =
                                                                                                HashMap()
                                                                                            userData["cartSize"] =
                                                                                                (userModel?.cartSize?.toInt()!! + 1).toString()
                                                                                            FirebaseFirestore.getInstance()
                                                                                                .collection("USERS")
                                                                                                .document(FirebaseAuth.getInstance().uid.toString())
                                                                                                .update(userData)
                                                                                                .addOnCompleteListener {
                                                                                                    if (it.isSuccessful) {
                                                                                                        Toast.makeText(
                                                                                                            context,
                                                                                                            "Successfully product added to the cart",
                                                                                                            Toast.LENGTH_SHORT
                                                                                                        ).show()
                                                                                                        holder.qtyBtn.visibility =
                                                                                                            View.VISIBLE
                                                                                                        holder.addToCardBtn.visibility =
                                                                                                            View.GONE
                                                                                                        loadingDialog.dismiss()
                                                                                                    }
                                                                                                }
                                                                                        }
                                                                                    }

                                                                            }
                                                                        }

                                                                })
                                                        }
                                                    })

                                            }

                                            var discount =
                                                100 - ((selectedQtyModel?.productPrice?.toFloat()!! / selectedQtyModel?.productCuttedPrice?.toFloat()!!) * 100)
                                            holder.productDiscount.text = discount.toInt().toString() + "% OFF"
                                        }

                                    }
                                }

                        }
                        qtyAdapter.notifyDataSetChanged()
                    })
            } else {

                loadingDialog.show()
                FirebaseFirestore.getInstance().collection("USERS")
                    .document(FirebaseAuth.getInstance().uid.toString())
                    .collection("MY_CART")
                    .document(holder.productId.text.toString())
                    .get()
                    .addOnSuccessListener(OnSuccessListener<DocumentSnapshot> { documentSnapshot ->
                        val myCartModel: MyCartModel? = documentSnapshot.toObject(
                            MyCartModel::class.java
                        )

                        if (myCartModel?.productId.equals(holder.productId.text.toString())) {
                            Toast.makeText(
                                context,
                                "Product already added to the cart",
                                Toast.LENGTH_SHORT
                            ).show()
                            loadingDialog.dismiss()
                        } else {
                            FirebaseFirestore.getInstance().collection("USERS")
                                .document(FirebaseAuth.getInstance().uid.toString())
                                .get()
                                .addOnSuccessListener(OnSuccessListener<DocumentSnapshot> { documentSnapshot ->
                                    val userModel: UserModel? =
                                        documentSnapshot.toObject(
                                            UserModel::class.java
                                        )

                                    val userData: MutableMap<Any, Any?> = HashMap()
                                    userData["uid"] = userModel?.uid
                                    userData["city"] = userModel?.city
                                    userData["state"] = userModel?.state
                                    userData["pincode"] = userModel?.pincode
                                    userData["address"] = userModel?.address
                                    userData["latitude"] = userModel?.latitude
                                    userData["longitude"] = userModel?.longitude
                                    userData["name"] = userModel?.name
                                    userData["number"] = userModel?.number
                                    userData["userToken"] = userModel?.token

                                    userData["productId"] = holder.productId.text.toString()
                                    userData["storeToken"] = model[position].storeToken
                                    userData["storeId"] = holder.storeIdText.text.toString()
                                    userData["productPrice"] =
                                        holder.miniProductPrice.text.toString().toInt()
                                    userData["price"] = holder.miniProductPrice.text.toString().toInt()
                                    userData["productCuttedPrice"] =
                                        holder.miniProductCuttedPrice.text.toString().toInt()
                                    userData["qty"] = holder.selectQtyText.text.toString()
                                    userData["productTitle"] = holder.miniProductTitle.text.toString()
                                    userData["productImage"] = holder.productImageText.text.toString()
                                    userData["timeStamp"] = System.currentTimeMillis().toLong()
                                    userData["qtyNo"] = 1
                                    userData["qtyId"] = holder.qtyId.text.toString()
                                    userData["couponId"] = ""
                                    userData["coupon"] = "Apply your coupon here"
                                    userData["delivery"] = "Pending"
                                    userData["orderConfirmedDate"] = ""
                                    userData["shippedDate"] = ""
                                    userData["outForDeliveryDate"] = ""
                                    userData["deliveredDate"] = ""
                                    userData["payment"] = ""
                                    userData["id"] = ""
                                    userData["riderId"] = ""
                                    userData["ratingProduct"] = ""

                                    FirebaseFirestore.getInstance().collection("USERS")
                                        .document(FirebaseAuth.getInstance().uid.toString())
                                        .collection("MY_CART")
                                        .document(holder.productId.text.toString())
                                        .set(userData)
                                        .addOnCompleteListener {
                                            if (it.isSuccessful) {
                                                val update: MutableMap<String, Any?> = HashMap()
                                                update["availableQty"] =
                                                    holder.avlQtyText.text.toString().toLong() - 1
                                                FirebaseFirestore.getInstance()
                                                    .collection("PRODUCTS")
                                                    .document(holder.productId.text.toString())
                                                    .collection("productSize")
                                                    .document(
                                                        holder.qtyId.text.toString()
                                                    )
                                                    .update(update)
                                                    .addOnCompleteListener {
                                                        if (it.isSuccessful) {
                                                            val userData: MutableMap<String, Any?> =
                                                                HashMap()
                                                            userData["cartSize"] =
                                                                (userModel?.cartSize?.toInt()!! + 1).toString()
                                                            FirebaseFirestore.getInstance()
                                                                .collection("USERS")
                                                                .document(FirebaseAuth.getInstance().uid.toString())
                                                                .update(userData)
                                                                .addOnCompleteListener {
                                                                    if (it.isSuccessful) {
                                                                        Toast.makeText(
                                                                            context,
                                                                            "Successfully product added to the cart",
                                                                            Toast.LENGTH_SHORT
                                                                        ).show()
                                                                        holder.qtyBtn.visibility =
                                                                            View.VISIBLE
                                                                        holder.addToCardBtn.visibility =
                                                                            View.GONE
                                                                        loadingDialog.dismiss()
                                                                    }
                                                                }
                                                        }
                                                    }

                                            }
                                        }

                                })
                        }
                    })
            }
        }
        //////// Add to cart

    }

    override fun getItemCount(): Int {
        return model.size
    }

    class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        var miniProductTitle: TextView = itemView.findViewById<TextView?>(R.id.mini_product_title)
        var miniProductPrice: TextView = itemView.findViewById<TextView?>(R.id.mini_product_price)
        var miniProductRatting: TextView = itemView.findViewById<TextView?>(R.id.mini_product_ratting_text)
        var miniProductTotalRatting: TextView = itemView.findViewById<TextView?>(R.id.mini_product_total_ratting)
        var miniProductCuttedPrice: TextView = itemView.findViewById<TextView?>(R.id.mini_product_cutted_price)
        var miniProductBrand: TextView = itemView.findViewById<TextView?>(R.id.product_brand)
        var productDiscount: TextView = itemView.findViewById<TextView?>(R.id.discount_text)
        var selectQtyText: TextView = itemView.findViewById<TextView?>(R.id.select_qty_text)
        var avlQtyText: TextView = itemView.findViewById<TextView?>(R.id.avl_qty_text)
        var qtyId: TextView = itemView.findViewById<TextView?>(R.id.qty_id)
        var productId: TextView = itemView.findViewById<TextView?>(R.id.productId)
        var storeIdText: TextView = itemView.findViewById<TextView?>(R.id.storeIdText)
        var addToCardBtn: AppCompatButton = itemView.findViewById<AppCompatButton?>(R.id.btn_add_card)
        var miniProductImage: ImageView = itemView.findViewById<ImageView?>(R.id.mini_product_image)
        var selectQuantity: LinearLayout = itemView.findViewById<LinearLayout?>(R.id.select_qty)
        var productImageText: TextView = itemView.findViewById<TextView?>(R.id.productImageText)
        var qtyBtn: LinearLayout = itemView.findViewById<LinearLayout?>(R.id.qty_btn)
        var qtyNo: TextView = itemView.findViewById<TextView?>(R.id.qty_no)
        var addQty: AppCompatButton = itemView.findViewById<AppCompatButton?>(R.id.add_qty)
        var lessQty: AppCompatButton = itemView.findViewById<AppCompatButton?>(R.id.less_qty)

    }
}