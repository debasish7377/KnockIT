package com.example.knockitUser.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.example.knockitUser.Database.StoreDatabase
import com.example.knockitUser.R
import com.example.knockitUser.databinding.ActivityStoreDetailsBinding
import com.example.knockitUser.databinding.ActivityStoresBinding

class StoreDetailsActivity : AppCompatActivity() {

    lateinit var binding: ActivityStoreDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoreDetailsBinding.inflate(layoutInflater)
        val view: View = binding.getRoot()
        setContentView(view)
        var storeId = intent.getStringExtra("storeId")
        var storeImage = intent.getStringExtra("storeImage")
        var storeProfile = intent.getStringExtra("storeProfile")
        var storeName = intent.getStringExtra("storeName")
        var storeDesc = intent.getStringExtra("storeDesc")
        var deliveryTiming = intent.getStringExtra("deliveryTiming")

        StoreDatabase.loadProduct(this, binding.productRecyclerView, storeId.toString())

        binding.storeTitle.text = storeName
        binding.storeDesc.text = storeDesc
        binding.deliveryTime.text = deliveryTiming
        Glide.with(this).load(storeImage).placeholder(R.drawable.avatara).into(binding.storeImage)
        Glide.with(this).load(storeProfile).placeholder(R.drawable.avatara).into(binding.circleImageView)

    }
}