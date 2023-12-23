package com.example.knockitUser.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.knockitUser.Database.CategoryDatabase
import com.example.knockitUser.Database.StoreDatabase
import com.example.knockitUser.R
import com.example.knockitUser.databinding.ActivityStoresBinding
import com.example.knockitUser.databinding.ActivityUpdateProfileBinding

class StoresActivity : AppCompatActivity() {

    lateinit var binding: ActivityStoresBinding
    companion object {
        lateinit var storeRecyclerView: RecyclerView
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoresBinding.inflate(layoutInflater)
        val view: View = binding.getRoot()
        setContentView(view)
        storeRecyclerView = findViewById(R.id.storeRecyclerView)

        StoreDatabase.loadStore(this, binding.storeRecyclerView, "All")
        CategoryDatabase.loadCategoryByStore(this, binding.categoryRecyclerView)
    }
}