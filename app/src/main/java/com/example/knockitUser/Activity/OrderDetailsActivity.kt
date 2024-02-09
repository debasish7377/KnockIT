package com.example.knockitUser.Activity

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.bumptech.glide.Glide
import com.example.knockitUser.Model.ProductModel
import com.example.knockitUser.Model.UserModel
import com.example.knockitUser.R
import com.example.knockitUser.databinding.ActivityOderDetailsBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.UUID

class OrderDetailsActivity : AppCompatActivity() {

    lateinit var binding: ActivityOderDetailsBinding
    lateinit var loadingDialog: Dialog
    lateinit var reviewDialog: Dialog
    lateinit var reviewText: EditText
    lateinit var okBtn: AppCompatButton

    lateinit var productId: String
    lateinit var orderId: String
    lateinit var uid: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOderDetailsBinding.inflate(layoutInflater)
        val view: View = binding.getRoot()
        setContentView(view)

        setSupportActionBar(binding.toolbar5);

        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar()?.setDisplayHomeAsUpEnabled(true);
            getSupportActionBar()?.setDisplayShowHomeEnabled(true);
        }

        var productTitle = intent.getStringExtra("productTitle")
        var productPrice = intent.getStringExtra("productPrice")
        var productCuttedPrice = intent.getStringExtra("productCuttedPrice")
        productId = intent.getStringExtra("productId").toString()
        var productQty = intent.getStringExtra("productQty")
        var productQtyNo = intent.getStringExtra("productQtyNo")

        var address = intent.getStringExtra("address")
        var city = intent.getStringExtra("city")
        var pincode = intent.getStringExtra("pincode")
        uid = intent.getStringExtra("uid").toString()

        var paymentId = intent.getStringExtra("paymentId")
        var delivery = intent.getStringExtra("delivery")
        var orderConfirmedDate = intent.getStringExtra("orderConfirmedDate")
        var shippedDate = intent.getStringExtra("shippedDate")
        var outForDeliveryDate = intent.getStringExtra("outForDeliveryDate")
        var deliveredDate = intent.getStringExtra("deliveredDate")
        var ratingProduct = intent.getStringExtra("ratingProduct")
        var deliveryPrice = intent.getStringExtra("deliveryPrice")
        orderId = intent.getStringExtra("orderId").toString()

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

        ////////////////review dialog
        reviewDialog = Dialog(this)
        reviewDialog.setContentView(R.layout.dialog_oder_review)
        reviewDialog.setCancelable(false)
        reviewDialog.window?.setBackgroundDrawable(getDrawable(R.drawable.btn_buy_now))
        reviewDialog.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        reviewText = reviewDialog.findViewById(R.id.reviewText)
        okBtn = reviewDialog.findViewById(R.id.okBtn)
        ////////////////review dialog

        FirebaseFirestore.getInstance()
            .collection("PRODUCTS")
            .document(productId.toString())
            .get()
            .addOnCompleteListener(OnCompleteListener<DocumentSnapshot> { task ->
                if (task.isSuccessful) {
                    Glide.with(this).load(task.result.getString("productImage").toString())
                        .into(binding.layoutOderDetails.productImage)
                }
            })
        binding.layoutOderDetails.productTitle.text = productTitle.toString()
        binding.layoutOderDetails.productPrice.text = "₹" + productPrice.toString()
        binding.layoutOderDetails.productId.text = "Product Id - " + productId.toString()
        binding.layoutShippingDetails.address.text = "Address - " + address.toString()

        binding.layoutPriceDetails.price.text = "₹" + productPrice.toString()
        binding.layoutPriceDetails.cuttedPrice.text = "₹" + productCuttedPrice.toString()
        binding.layoutPriceDetails.deliveryPrice.text = deliveryPrice.toString()

        binding.layoutPriceDetails.totalPrice.text = "₹" + productPrice.toString()


        if (delivery.equals("Canceled") && !orderConfirmedDate.equals("")) {
            val sdf = SimpleDateFormat("dd/MM/yy hh:mm a")
            val netDate = Date(orderConfirmedDate.toString().toLong())
            val date = sdf.format(netDate)
            binding.layoutOderDetails.confirmedDate.text = "Date - " + date
            binding.layoutOderDetails.confirmedIndicator.setBackgroundTintList(
                getResources().getColorStateList(
                    R.color.red
                )
            );
        } else if (!orderConfirmedDate.equals("")) {
            val sdf = SimpleDateFormat("dd/MM/yy hh:mm a")
            val netDate = Date(orderConfirmedDate.toString().toLong())
            val date = sdf.format(netDate)
            binding.layoutOderDetails.confirmedDate.text = "Date - " + date
            binding.layoutOderDetails.confirmedIndicator.setBackgroundTintList(
                getResources().getColorStateList(
                    R.color.primary
                )
            );
        }

        if (delivery.equals("Canceled") && !shippedDate.equals("")) {
            val sdf = SimpleDateFormat("dd/MM/yy hh:mm a")
            val netDate = Date(shippedDate.toString().toLong())
            val date = sdf.format(netDate)
            binding.layoutOderDetails.shippedDate.text = "Date - " + date
            binding.layoutOderDetails.confirmedProgressBar.setBackgroundTintList(
                getResources().getColorStateList(
                    R.color.red
                )
            );
            binding.layoutOderDetails.shippedIndicator.setBackgroundTintList(
                getResources().getColorStateList(
                    R.color.red
                )
            );
        } else if (!shippedDate.equals("")) {
            val sdf = SimpleDateFormat("dd/MM/yy hh:mm a")
            val netDate = Date(shippedDate.toString().toLong())
            val date = sdf.format(netDate)
            binding.layoutOderDetails.shippedDate.text = "Date - " + date
            binding.layoutOderDetails.confirmedProgressBar.setBackgroundTintList(
                getResources().getColorStateList(
                    R.color.primary
                )
            );
            binding.layoutOderDetails.shippedIndicator.setBackgroundTintList(
                getResources().getColorStateList(
                    R.color.primary
                )
            );
        }

        if (delivery.equals("Canceled") && !outForDeliveryDate.equals("")) {
            val sdf = SimpleDateFormat("dd/MM/yy hh:mm a")
            val netDate = Date(outForDeliveryDate.toString().toLong())
            val date = sdf.format(netDate)
            binding.layoutOderDetails.outForDeliveryDate.text = "Date - " + date
            binding.layoutOderDetails.shippedProgressBar.setBackgroundTintList(
                getResources().getColorStateList(
                    R.color.red
                )
            );
            binding.layoutOderDetails.outForDeliveryIndicator.setBackgroundTintList(
                getResources().getColorStateList(
                    R.color.red
                )
            );
        } else if (!outForDeliveryDate.equals("")) {
            val sdf = SimpleDateFormat("dd/MM/yy hh:mm a")
            val netDate = Date(outForDeliveryDate.toString().toLong())
            val date = sdf.format(netDate)
            binding.layoutOderDetails.outForDeliveryDate.text = "Date - " + date
            binding.layoutOderDetails.shippedProgressBar.setBackgroundTintList(
                getResources().getColorStateList(
                    R.color.primary
                )
            );
            binding.layoutOderDetails.outForDeliveryIndicator.setBackgroundTintList(
                getResources().getColorStateList(
                    R.color.primary
                )
            );
        }

        if (delivery.equals("Canceled") && !deliveredDate.equals("")) {
            val sdf = SimpleDateFormat("dd/MM/yy hh:mm a")
            val netDate = Date(deliveredDate.toString().toLong())
            val date = sdf.format(netDate)
            binding.layoutOderDetails.deliveredDate.text = "Date - " + date
            binding.layoutOderDetails.outForDeliveryProgressBar.setBackgroundTintList(
                getResources().getColorStateList(
                    R.color.red
                )
            );
            binding.layoutOderDetails.deliveredIndicator.setBackgroundTintList(
                getResources().getColorStateList(
                    R.color.red
                )
            );

        } else if (!deliveredDate.equals("")) {
            val sdf = SimpleDateFormat("dd/MM/yy hh:mm a")
            val netDate = Date(deliveredDate.toString().toLong())
            val date = sdf.format(netDate)
            binding.layoutOderDetails.deliveredDate.text = "Date - " + date
            binding.layoutOderDetails.outForDeliveryProgressBar.setBackgroundTintList(
                getResources().getColorStateList(
                    R.color.primary
                )
            );
            binding.layoutOderDetails.deliveredIndicator.setBackgroundTintList(
                getResources().getColorStateList(
                    R.color.primary
                )
            );

            if (ratingProduct.equals("")) {
                binding.layoutOderDetails.ratingBg.visibility = View.VISIBLE
            }
        }

        if (delivery.equals("Canceled")) {
            binding.layoutOderDetails.confirmedIndicator.setBackgroundTintList(
                getResources().getColorStateList(
                    R.color.red
                )
            );
        }


        FirebaseFirestore.getInstance()
            .collection("USERS")
            .document(FirebaseAuth.getInstance().uid.toString())
            .addSnapshotListener { querySnapshot: DocumentSnapshot?, e: FirebaseFirestoreException? ->
                querySnapshot?.let {
                    val userModel = it.toObject(UserModel::class.java)

                    binding.layoutShippingDetails.edName.text = "Name : " + userModel?.name
                    binding.layoutShippingDetails.edEmail.text = "Email : " + userModel?.email
                    binding.layoutShippingDetails.phone.text = userModel?.number.toString()

                }
            }
        productRatings()
        oderReview()

    }

    private fun oderReview() {
        okBtn.setOnClickListener {
            if (!reviewText.text.toString().equals("")) {

                loadingDialog.show()
                reviewDialog.dismiss()
                val randomString = UUID.randomUUID().toString().substring(0, 20)
                val userData: MutableMap<String, Any?> = HashMap()
                userData["id"] = randomString.toString()
                userData["review"] = reviewText.text.toString()
                userData["userId"] = uid.toString()
                userData["timeStamp"] = System.currentTimeMillis()
                FirebaseFirestore.getInstance()
                    .collection("PRODUCTS")
                    .document(productId.toString())
                    .collection("ProductReview")
                    .document(randomString.toString())
                    .set(userData)
                    .addOnCompleteListener {
                        loadingDialog.dismiss()
                        Toast.makeText(this, "Thank you for your review", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Enter your review", Toast.LENGTH_SHORT).show()
            }
        }

    }

    fun productRatings() {
        binding.layoutOderDetails.rating1.setOnClickListener {
            reviewDialog.show()
            binding.layoutOderDetails.ratingBg.visibility = View.GONE
            FirebaseFirestore.getInstance()
                .collection("PRODUCTS")
                .document(productId.toString())
                .get()
                .addOnSuccessListener(OnSuccessListener<DocumentSnapshot> { documentSnapshot ->
                    val model: ProductModel? =
                        documentSnapshot.toObject(ProductModel::class.java)

                    var ratingSum = ((model?.rating_1.toString()
                        .toLong() + 1) * 1) + (model?.rating_2.toString()
                        .toLong() * 2) + (model?.rating_3.toString()
                        .toLong() * 3) + (model?.rating_4.toString()
                        .toLong() * 4) + (model?.rating_5.toString().toLong() * 5)
                    var productRating: String =
                        (ratingSum.toFloat() / (model?.productTotalRating.toString()
                            .toFloat() + 1)).toFloat().toString()

                    val userData: MutableMap<String, Any?> = HashMap()
                    userData["productTotalRating"] =
                        (model?.productTotalRating.toString().toLong() + 1).toString()
                    userData["productRating"] = productRating.substring(0, 3)
                    userData["rating_1"] = (model?.rating_1.toString().toLong() + 1).toString()

                    FirebaseFirestore.getInstance()
                        .collection("PRODUCTS")
                        .document(productId.toString())
                        .update(userData)
                        .addOnCompleteListener {
                            Toast.makeText(this, "Rating successful", Toast.LENGTH_SHORT).show()
                        }

                    val userData1: MutableMap<String, Any?> = HashMap()
                    userData1["ratingProduct"] = 1.toString()
                    FirebaseFirestore.getInstance()
                        .collection("ORDER")
                        .document(orderId.toString())
                        .update(userData1)
                        .addOnCompleteListener {

                        }

                })

        }
        binding.layoutOderDetails.rating2.setOnClickListener {
            reviewDialog.show()
            binding.layoutOderDetails.ratingBg.visibility = View.GONE
            FirebaseFirestore.getInstance()
                .collection("PRODUCTS")
                .document(productId.toString())
                .get()
                .addOnSuccessListener(OnSuccessListener<DocumentSnapshot> { documentSnapshot ->
                    val model: ProductModel? =
                        documentSnapshot.toObject(ProductModel::class.java)

                    var ratingSum = ((model?.rating_2.toString()
                        .toLong() + 1) * 2) + (model?.rating_1.toString()
                        .toLong() * 1) + (model?.rating_3.toString()
                        .toLong() * 3) + (model?.rating_4.toString()
                        .toLong() * 4) + (model?.rating_5.toString().toLong() * 5)
                    var productRating: String =
                        (ratingSum.toFloat() / (model?.productTotalRating.toString()
                            .toFloat() + 1)).toFloat().toString()

                    val userData: MutableMap<String, Any?> = HashMap()
                    userData["productTotalRating"] =
                        (model?.productTotalRating.toString().toLong() + 1).toString()
                    userData["productRating"] = productRating.substring(0, 3)
                    userData["rating_2"] = (model?.rating_2.toString().toLong() + 1).toString()

                    FirebaseFirestore.getInstance()
                        .collection("PRODUCTS")
                        .document(productId.toString())
                        .update(userData)
                        .addOnCompleteListener {
                            Toast.makeText(this, "Rating successful", Toast.LENGTH_SHORT).show()
                        }

                    val userData1: MutableMap<String, Any?> = HashMap()
                    userData1["ratingProduct"] = 2.toString()
                    FirebaseFirestore.getInstance()
                        .collection("ORDER")
                        .document(orderId.toString())
                        .update(userData1)
                        .addOnCompleteListener {

                        }

                })
        }
        binding.layoutOderDetails.rating3.setOnClickListener {
            reviewDialog.show()
            binding.layoutOderDetails.ratingBg.visibility = View.GONE
            FirebaseFirestore.getInstance()
                .collection("PRODUCTS")
                .document(productId.toString())
                .get()
                .addOnSuccessListener(OnSuccessListener<DocumentSnapshot> { documentSnapshot ->
                    val model: ProductModel? =
                        documentSnapshot.toObject(ProductModel::class.java)

                    var ratingSum = ((model?.rating_3.toString()
                        .toLong() + 1) * 3) + (model?.rating_2.toString()
                        .toLong() * 2) + (model?.rating_1.toString()
                        .toLong() * 1) + (model?.rating_4.toString()
                        .toLong() * 4) + (model?.rating_5.toString().toLong() * 5)
                    var productRating: String =
                        (ratingSum.toFloat() / (model?.productTotalRating.toString()
                            .toFloat() + 1)).toFloat().toString()

                    val userData: MutableMap<String, Any?> = HashMap()
                    userData["productTotalRating"] =
                        (model?.productTotalRating.toString().toLong() + 1).toString()
                    userData["productRating"] = productRating.substring(0, 3)
                    userData["rating_3"] = (model?.rating_3.toString().toLong() + 1).toString()

                    FirebaseFirestore.getInstance()
                        .collection("PRODUCTS")
                        .document(productId.toString())
                        .update(userData)
                        .addOnCompleteListener {
                            Toast.makeText(this, "Rating successful", Toast.LENGTH_SHORT).show()
                        }

                    val userData1: MutableMap<String, Any?> = HashMap()
                    userData1["ratingProduct"] = 3.toString()
                    FirebaseFirestore.getInstance()
                        .collection("ORDER")
                        .document(orderId.toString())
                        .update(userData1)
                        .addOnCompleteListener {

                        }

                })
        }
        binding.layoutOderDetails.rating4.setOnClickListener {
            reviewDialog.show()
            binding.layoutOderDetails.ratingBg.visibility = View.GONE
            FirebaseFirestore.getInstance()
                .collection("PRODUCTS")
                .document(productId.toString())
                .get()
                .addOnSuccessListener(OnSuccessListener<DocumentSnapshot> { documentSnapshot ->
                    val model: ProductModel? =
                        documentSnapshot.toObject(ProductModel::class.java)

                    var ratingSum = ((model?.rating_4.toString()
                        .toLong() + 1) * 4) + (model?.rating_2.toString()
                        .toLong() * 2) + (model?.rating_3.toString()
                        .toLong() * 3) + (model?.rating_1.toString()
                        .toLong() * 1) + (model?.rating_5.toString().toLong() * 5)
                    var productRating: String =
                        (ratingSum.toFloat() / (model?.productTotalRating.toString()
                            .toFloat() + 1)).toFloat().toString()

                    val userData: MutableMap<String, Any?> = HashMap()
                    userData["productTotalRating"] =
                        (model?.productTotalRating.toString().toLong() + 1).toString()
                    userData["productRating"] = productRating.substring(0, 3)
                    userData["rating_4"] = (model?.rating_4.toString().toLong() + 1).toString()

                    FirebaseFirestore.getInstance()
                        .collection("PRODUCTS")
                        .document(productId.toString())
                        .update(userData)
                        .addOnCompleteListener {
                            Toast.makeText(this, "Rating successful", Toast.LENGTH_SHORT).show()
                        }

                    val userData1: MutableMap<String, Any?> = HashMap()
                    userData1["ratingProduct"] = 4.toString()
                    FirebaseFirestore.getInstance()
                        .collection("ORDER")
                        .document(orderId.toString())
                        .update(userData1)
                        .addOnCompleteListener {

                        }

                })
        }
        binding.layoutOderDetails.rating5.setOnClickListener {
            reviewDialog.show()
            binding.layoutOderDetails.ratingBg.visibility = View.GONE
            FirebaseFirestore.getInstance()
                .collection("PRODUCTS")
                .document(productId.toString())
                .get()
                .addOnSuccessListener(OnSuccessListener<DocumentSnapshot> { documentSnapshot ->
                    val model: ProductModel? =
                        documentSnapshot.toObject(ProductModel::class.java)

                    var ratingSum = ((model?.rating_5.toString()
                        .toLong() + 1) * 5) + (model?.rating_2.toString()
                        .toLong() * 2) + (model?.rating_3.toString()
                        .toLong() * 3) + (model?.rating_4.toString()
                        .toLong() * 4) + (model?.rating_1.toString().toLong() * 1)
                    var productRating: String =
                        (ratingSum.toFloat() / (model?.productTotalRating.toString()
                            .toFloat() + 1)).toFloat().toString()

                    val userData: MutableMap<String, Any?> = HashMap()
                    userData["productTotalRating"] =
                        (model?.productTotalRating.toString().toLong() + 1).toString()
                    userData["productRating"] = productRating.substring(0, 3)
                    userData["rating_5"] = (model?.rating_5.toString().toLong() + 1).toString()

                    FirebaseFirestore.getInstance()
                        .collection("PRODUCTS")
                        .document(productId.toString())
                        .update(userData)
                        .addOnCompleteListener {
                            Toast.makeText(this, "Rating successful", Toast.LENGTH_SHORT).show()
                        }

                    val userData1: MutableMap<String, Any?> = HashMap()
                    userData1["ratingProduct"] = 5.toString()
                    FirebaseFirestore.getInstance()
                        .collection("ORDER")
                        .document(orderId.toString())
                        .update(userData1)
                        .addOnCompleteListener {

                        }

                })
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // handle arrow click here
        if (item.itemId == android.R.id.home) {
            finish() // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item)
    }
}