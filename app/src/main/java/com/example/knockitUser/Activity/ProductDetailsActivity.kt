package com.example.knockitUser.Activity

import android.annotation.SuppressLint
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import com.example.knockitUser.Adapter.MyWishlistAdapter
import com.example.knockitUser.Database.CouponsDatabase
import com.example.knockitUser.Database.ProductDetailsDatabase
import com.example.knockitUser.Fragments.WishlistFragment
import com.example.knockitUser.Model.MyCartModel
import com.example.knockitUser.Model.MyWishlistModel
import com.example.knockitUser.Model.UserModel
import com.example.knockitUser.R
import com.example.knockitUser.databinding.ActivityProductDetailsBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException

class ProductDetailsActivity : AppCompatActivity() {

    lateinit var binding: ActivityProductDetailsBinding
    lateinit var productId: String
    var ADD_WISHLIST: Boolean = false
    lateinit var loadingDialog: Dialog
    lateinit var couponsDialog: Dialog
    lateinit var price : String
    lateinit var cuttedPrice: String
    lateinit var productImage: String
    lateinit var storeToken: String

    companion object {
        lateinit var priceText: TextView
        lateinit var cuttedPriceText: TextView
        lateinit var qtyText: TextView
        lateinit var qtyId: TextView
        lateinit var qtyAvl: TextView
        lateinit var addToCardBtn: AppCompatButton
        lateinit var outOfStockBtn: AppCompatButton
        lateinit var couponRecyclerView: RecyclerView
        lateinit var couponOriginalPrice: TextView
        lateinit var couponDiscountedPrice: TextView
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailsBinding.inflate(layoutInflater)
        val view: View = binding.getRoot()
        setContentView(view)
        productId = intent.getStringExtra("productId").toString()
        priceText = findViewById(R.id.product_price)
        cuttedPriceText = findViewById(R.id.product_cutted_price)
        addToCardBtn = findViewById(R.id.btn_add_to_card)
        outOfStockBtn = findViewById(R.id.btn_out_of_stock)
        qtyText = findViewById(R.id.qty_text)
        qtyId = findViewById(R.id.qty_id)
        qtyAvl = findViewById(R.id.qty_avl)

        ////////////////loading dialog
        loadingDialog = Dialog(this)
        loadingDialog.setContentView(R.layout.dialog_loading)
        loadingDialog.setCancelable(false)
        loadingDialog.window?.setBackgroundDrawable(getDrawable(R.drawable.login_btn_bg))
        loadingDialog.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        ////////////////loading dialog

        ////////////////Coupons dialog
        couponsDialog = Dialog(this)
        couponsDialog.setContentView(R.layout.dialog_layout_coupon_redemption)
        couponsDialog.setCancelable(true)
        couponsDialog.window?.setBackgroundDrawable(getDrawable(R.drawable.login_btn_bg))
        couponsDialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        couponRecyclerView = couponsDialog.findViewById(R.id.coupons_recyclerView)
        couponOriginalPrice = couponsDialog.findViewById(R.id.originalPrice)
        couponDiscountedPrice = couponsDialog.findViewById(R.id.discountedPrice)
        ////////////////Coupons dialog

        ProductDetailsDatabase.loadImages(
            this,
            productId,
            binding.layoutProductDetails.productImagesRecyclerView
        )
        ProductDetailsDatabase.loadSelectSize(
            this,
            productId,
            binding.layoutProductSelectSize.selectSizeRecyclerView
        )
        ProductDetailsDatabase.loadSpecification(
            this,
            productId,
            binding.layoutProductSpecification.specificationRecyclerView
        )
        ProductDetailsDatabase.loadProductReview(
            this,
            productId,
            binding.layoutProductReview.productReview
        )

        //////   Wishlist
        FirebaseFirestore.getInstance()
            .collection("USERS")
            .document(FirebaseAuth.getInstance().uid.toString())
            .collection("MY_WISHLIST")
            .document(productId)
            .get().addOnSuccessListener(OnSuccessListener<DocumentSnapshot> { documentSnapshot ->
                val wishlistModel: MyWishlistModel? =
                    documentSnapshot.toObject(MyWishlistModel::class.java)

                try {
                    binding.layoutProductDetails.wishlistText.text = wishlistModel?.productId!!
                    if (binding.layoutProductDetails.wishlistText.text.equals(productId)) {
                        ADD_WISHLIST = true
                        binding.layoutProductDetails.btnWishlist.setImageDrawable(
                            getResources().getDrawable(R.drawable.wishlist_red)
                        )
                    } else {
                        ADD_WISHLIST = false
                        binding.layoutProductDetails.btnWishlist.setImageDrawable(
                            getResources().getDrawable(R.drawable.wishlist)
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                binding.layoutProductDetails.btnWishlist.setOnClickListener {
                    if (ADD_WISHLIST) {

                        FirebaseFirestore.getInstance().collection("USERS")
                            .document(FirebaseAuth.getInstance().uid.toString())
                            .get()
                            .addOnSuccessListener(OnSuccessListener<DocumentSnapshot> { documentSnapshot ->
                                val model: UserModel? =
                                    documentSnapshot.toObject(UserModel::class.java)

                                val userDataUpdate: MutableMap<String, Any?> = HashMap()
                                userDataUpdate["wishlistSize"] =
                                    (model?.wishlistSize!!.toLong() - 1).toString()

                                FirebaseFirestore.getInstance().collection("USERS")
                                    .document(FirebaseAuth.getInstance().uid.toString())
                                    .update(userDataUpdate)
                                    .addOnCompleteListener {
                                        ADD_WISHLIST = false
                                        binding.layoutProductDetails.btnWishlist.setImageDrawable(
                                            getResources().getDrawable(R.drawable.wishlist)
                                        )
                                        ProductDetailsDatabase.loadWishlistRemoved(this, productId)
                                    }
                            })


                    } else {

                        FirebaseFirestore.getInstance().collection("USERS")
                            .document(FirebaseAuth.getInstance().uid.toString())
                            .get()
                            .addOnSuccessListener(OnSuccessListener<DocumentSnapshot> { documentSnapshot ->
                                val model: UserModel? =
                                    documentSnapshot.toObject(UserModel::class.java)

                                val userDataUpdate: MutableMap<String, Any?> = HashMap()
                                userDataUpdate["wishlistSize"] =
                                    (model?.wishlistSize!!.toLong() + 1).toString()

                                FirebaseFirestore.getInstance().collection("USERS")
                                    .document(FirebaseAuth.getInstance().uid.toString())
                                    .update(userDataUpdate)
                                    .addOnCompleteListener {
                                        ADD_WISHLIST = true
                                        binding.layoutProductDetails.btnWishlist.setImageDrawable(
                                            getResources().getDrawable(R.drawable.wishlist_red)
                                        )
                                        ProductDetailsDatabase.loadWishlistAdded(this, productId)
                                    }
                            })

                    }
                }

            })
        //////   Wishlist

        FirebaseFirestore.getInstance()
            .collection("PRODUCTS")
            .document(productId)
            .get()
            .addOnCompleteListener(OnCompleteListener<DocumentSnapshot> { task ->
                if (task.isSuccessful) {
                    price = task.result.getLong("productPrice").toString()
                    cuttedPrice = task.result.getLong("productCuttedPrice").toString()
                    productImage = task.result.getString("productImage").toString()
                    storeToken = task.result.getString("storeToken").toString()
                    CouponsDatabase.loadCoupons(this, couponRecyclerView, price.toString().toInt())
                    couponOriginalPrice.text = price
                    binding.layoutProductDetails.productPrice.text = price
                    binding.layoutProductDetails.productTitle.text =
                        task.result.getString("productTitle").toString()
                    binding.layoutProductDetails.productCuttedPrice.text = cuttedPrice
                    binding.layoutProductDetails.productImage.text =
                        "₹" + cuttedPrice
                    binding.layoutProductDetails.productRattingText.text =
                        task.result.getString("productRating").toString()
                    binding.layoutProductDetails.storeIdText.text =
                        task.result.getString("storeId").toString()
                    binding.layoutProductDetails.productTotalRatting.text =
                        "(" + task.result.getString("productTotalRating").toString() + ")"
                    binding.layoutProductOtherDetails.productOtherDetails.text =
                        task.result.getString("productDescription").toString()
                    if (task.result.getLong("productPrice")!! >= 800) {
                        binding.layoutProductDetails.productDelivery.text = "Free Delivery"
                    } else {
                        binding.layoutProductDetails.productDelivery.text =
                            "80₹ Rupees Delivery Price"
                    }

                    binding.layoutProductRatting.rating.text =
                        task.result.getString("productRating").toString()
                    binding.layoutProductRatting.totalRatingUpper.text =
                        task.result.getString("productTotalRating").toString()
                    binding.layoutProductRatting.totalRatingLower.text =
                        task.result.getString("productTotalRating").toString()

                    binding.layoutProductRatting.rating1.text =
                        task.result.getString("rating_1").toString()
                    binding.layoutProductRatting.rating2.text =
                        task.result.getString("rating_2").toString()
                    binding.layoutProductRatting.rating3.text =
                        task.result.getString("rating_3").toString()
                    binding.layoutProductRatting.rating4.text =
                        task.result.getString("rating_4").toString()
                    binding.layoutProductRatting.rating5.text =
                        task.result.getString("rating_5").toString()

                    binding.layoutProductRatting.rating1ProgressBar.max =
                        task.result.getString("productTotalRating").toString().toInt()
                    binding.layoutProductRatting.rating2ProgressBar.max =
                        task.result.getString("productTotalRating").toString().toInt()
                    binding.layoutProductRatting.rating3ProgressBar.max =
                        task.result.getString("productTotalRating").toString().toInt()
                    binding.layoutProductRatting.rating4ProgressBar.max =
                        task.result.getString("productTotalRating").toString().toInt()
                    binding.layoutProductRatting.rating5ProgressBar.max =
                        task.result.getString("productTotalRating").toString().toInt()

                    binding.layoutProductRatting.rating1ProgressBar.progress =
                        task.result.getString("rating_1").toString().toInt()
                    binding.layoutProductRatting.rating2ProgressBar.progress =
                        task.result.getString("rating_2").toString().toInt()
                    binding.layoutProductRatting.rating3ProgressBar.progress =
                        task.result.getString("rating_3").toString().toInt()
                    binding.layoutProductRatting.rating4ProgressBar.progress =
                        task.result.getString("rating_4").toString().toInt()
                    binding.layoutProductRatting.rating5ProgressBar.progress =
                        task.result.getString("rating_5").toString().toInt()

                    ////// Location wise product Display
                    FirebaseFirestore.getInstance().collection("USERS")
                        .document(FirebaseAuth.getInstance().uid.toString())
                        .get()
                        .addOnSuccessListener(OnSuccessListener<DocumentSnapshot> { documentSnapshot ->
                            val model: UserModel? =
                                documentSnapshot.toObject(UserModel::class.java)

                            if (task.result.getString("city_1").equals(model?.city.toString())) {

                            } else if (task.result.getString("city_2")
                                    .equals(model?.city.toString())
                            ) {

                            } else if (task.result.getString("city_3")
                                    .equals(model?.city.toString())
                            ) {

                            } else if (task.result.getString("city_4")
                                    .equals(model?.city.toString())
                            ) {

                            } else if (task.result.getString("city_5")
                                    .equals(model?.city.toString())
                            ) {

                            } else {
                                binding.btnOutOfStock.visibility = View.VISIBLE
                                binding.btnAddToCard.visibility = View.GONE
                                binding.layoutProductSelectSize.selectSizeRecyclerView.visibility =
                                    View.GONE
                                binding.layoutProductDetails.btnWishlist.visibility = View.GONE
                                binding.btnOutOfStock.setTextColor(resources.getColorStateList(R.color.gray))
                                binding.btnOutOfStock.text =
                                    "This product not available to your location"
                            }

                        })
                    ////// Location wise product Display

                } else {
                    val error = task.exception!!.message
                    Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
                }
            })

        addToCardBtn.setOnClickListener {
            loadingDialog.show()
            addToCardBtn.visibility = View.GONE
            outOfStockBtn.visibility = View.VISIBLE
            FirebaseFirestore.getInstance().collection("USERS")
                .document(FirebaseAuth.getInstance().uid.toString())
                .collection("MY_CART")
                .document(productId)
                .get()
                .addOnSuccessListener(OnSuccessListener<DocumentSnapshot> { documentSnapshot ->
                    val model: MyCartModel? = documentSnapshot.toObject(MyCartModel::class.java)

                        if (model?.productId.equals(productId)) {
                            Toast.makeText(
                                this,
                                "Product already added to the cart",
                                Toast.LENGTH_SHORT
                            ).show()
                            outOfStockBtn.text = "Product already added to the cart"
                            binding.btnOutOfStock.setTextColor(resources.getColorStateList(R.color.primary))
                            loadingDialog.dismiss()
                        } else {
                            FirebaseFirestore.getInstance().collection("USERS")
                                .document(FirebaseAuth.getInstance().uid.toString())
                                .get()
                                .addOnSuccessListener(OnSuccessListener<DocumentSnapshot> { documentSnapshot ->
                                    val model: UserModel? =
                                        documentSnapshot.toObject(UserModel::class.java)

                                    val userData: MutableMap<Any, Any?> = HashMap()
                                    userData["uid"] = model?.uid
                                    userData["city"] = model?.city
                                    userData["state"] = model?.state
                                    userData["pincode"] = model?.pincode
                                    userData["address"] = model?.address
                                    userData["latitude"] = model?.latitude
                                    userData["longitude"] = model?.longitude
                                    userData["name"] = model?.name
                                    userData["number"] = model?.number

                                    userData["productId"] = productId.toString()
                                    userData["storeToken"] = storeToken.toString()
                                    userData["storeId"] = binding.layoutProductDetails.storeIdText.text.toString()
                                    userData["productPrice"] = binding.layoutProductDetails.productPrice.text.toString().toInt()
                                    userData["price"] = binding.layoutProductDetails.productPrice.text.toString().toInt()
                                    userData["productCuttedPrice"] = binding.layoutProductDetails.productCuttedPrice.text.toString().toInt()
                                    userData["productImage"] = productImage.toString()
                                    userData["qty"] = qtyText.text.toString()
                                    userData["productTitle"] =
                                        binding.layoutProductDetails.productTitle.text.toString()
                                    userData["timeStamp"] = System.currentTimeMillis().toLong()
                                    userData["qtyNo"] = 1
                                    userData["qtyId"] = qtyId.text.toString()
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
                                        .document(productId)
                                        .set(userData)
                                        .addOnCompleteListener {
                                            if (it.isSuccessful) {
                                                val update: MutableMap<String, Any?> = HashMap()
                                                update["availableQty"] =
                                                    qtyAvl.text.toString().toLong() - 1
                                                FirebaseFirestore.getInstance()
                                                    .collection("PRODUCTS")
                                                    .document(productId)
                                                    .collection("productSize")
                                                    .document(qtyId.text.toString())
                                                    .update(update)
                                                    .addOnCompleteListener {
                                                        if (it.isSuccessful) {
                                                            val userData: MutableMap<String, Any?> = HashMap()
                                                            userData["cartSize"] = (model?.cartSize?.toInt()!! + 1).toString()
                                                            FirebaseFirestore.getInstance().collection("USERS")
                                                                .document(FirebaseAuth.getInstance().uid.toString())
                                                                .update(userData)
                                                                .addOnCompleteListener {
                                                                    if (it.isSuccessful){
                                                                        Toast.makeText(
                                                                            this,
                                                                            "Successfully product added to the cart",
                                                                            Toast.LENGTH_SHORT
                                                                        ).show()
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

        binding.layoutProductDetails.couponText.setOnClickListener {
            couponsDialog.show()
        }

    }
}