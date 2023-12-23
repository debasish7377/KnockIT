package com.example.knockitUser.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import com.bumptech.glide.Glide
import com.example.knockitUser.Database.MyCartDatabase
import com.example.knockitUser.Model.UserModel
import com.example.knockitUser.R
import com.example.knockitUser.databinding.ActivityMyCartBinding
import com.example.knockitUser.databinding.ActivityProductDetailsBinding
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException

class MyCartActivity : AppCompatActivity() {

    lateinit var binding: ActivityMyCartBinding
    companion object {
        lateinit var deliveryAddress: TextView
        lateinit var totalSavedPrice: TextView
        lateinit var totalPrice: TextView
        lateinit var checkOutBtn: AppCompatButton
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyCartBinding.inflate(layoutInflater)
        val view: View = binding.getRoot()
        setContentView(view)
        deliveryAddress = findViewById(R.id.delivery_address)
        totalPrice = findViewById(R.id.total_price)
        totalSavedPrice = findViewById(R.id.totalSavedPrice)
        checkOutBtn = findViewById(R.id.check_out_btn)

        MyCartDatabase.loadMyCart(this, binding.cartRecyclerView)

        Glide.with(this).load(R.raw.empty_cart).into(binding.emptyCartImg)

        FirebaseFirestore.getInstance()
            .collection("USERS")
            .document(FirebaseAuth.getInstance().uid.toString())
            .addSnapshotListener { querySnapshot: DocumentSnapshot?, e: FirebaseFirestoreException? ->
                querySnapshot?.let {
                    val userModel = it.toObject(UserModel::class.java)

                    if (userModel?.cartSize.equals("0")){
                        binding.cartRecyclerView.visibility = View.GONE
                        binding.checkOutBtn.visibility = View.GONE
                        binding.emptyCartImg.visibility = View.VISIBLE
                    }else {
                        binding.cartRecyclerView.visibility = View.VISIBLE
                        binding.checkOutBtn.visibility = View.VISIBLE
                        binding.emptyCartImg.visibility = View.GONE
                    }

                }
            }

        FirebaseFirestore.getInstance().collection("USERS")
            .document(FirebaseAuth.getInstance().uid.toString())
            .get()
            .addOnSuccessListener(OnSuccessListener<DocumentSnapshot> { documentSnapshot ->
                val model: UserModel? = documentSnapshot.toObject(UserModel::class.java)

                deliveryAddress.text = model?.address.toString()
            })

        binding.checkOutBtn.setOnClickListener {
            var intent = Intent(this, PaymentActivity::class.java)
            intent.putExtra("totalPrice", totalPrice.text)
            startActivity(intent)
        }
    }
}