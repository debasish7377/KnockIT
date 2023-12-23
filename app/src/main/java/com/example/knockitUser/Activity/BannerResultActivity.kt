package com.example.knockitUser.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.knockitUser.Database.BannerDatabase
import com.example.knockitUser.Database.DealsOfTheDayDatabase
import com.example.knockitUser.Model.UserModel
import com.example.knockitUser.R
import com.example.knockitUser.databinding.ActivityBannerResultBinding
import com.example.knockitUser.databinding.ActivityDealsOfTheDayBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException

class BannerResultActivity : AppCompatActivity() {

    lateinit var binding: ActivityBannerResultBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBannerResultBinding.inflate(layoutInflater)
        val view: View = binding.getRoot()
        setContentView(view)
        var subCategory = intent.getStringExtra("subCategory")
        var discount = intent.getStringExtra("discount")
        BannerDatabase.loadProductsByBanner(this, binding.bannerResultRecyclerView, subCategory.toString(), discount.toString().toLong())

        binding.myCartBtn.setOnClickListener {
            startActivity(Intent(this, MyCartActivity::class.java))
        }
        FirebaseFirestore.getInstance()
            .collection("USERS")
            .document(FirebaseAuth.getInstance().uid.toString())
            .addSnapshotListener { querySnapshot: DocumentSnapshot?, e: FirebaseFirestoreException? ->
                querySnapshot?.let {
                    val userModel = it.toObject(UserModel::class.java)

                    if (userModel?.cartSize.equals("0")){
                        binding.qtyBg.visibility = View.GONE
                    }else {
                        binding.qtyBg.visibility = View.VISIBLE
                        binding.qtySize.text = userModel?.cartSize
                    }

                    if (userModel?.notificationSize.equals("0")){
                        binding.notificationBg.visibility = View.GONE
                    }else {
                        binding.notificationBg.visibility = View.VISIBLE
                        binding.notificationSize.text = userModel?.notificationSize
                    }

                }
            }

        binding.searchView.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }

        binding.notification.setOnClickListener {
            startActivity(Intent(this, NotificationActivity::class.java))
        }

    }
}