package com.example.knockitUser.Activity

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.knockitUser.Database.CategoryDatabase
import com.example.knockitUser.Database.ProductDatabase
import com.example.knockitUser.Model.ProductModel
import com.example.knockitUser.R
import com.example.knockitUser.databinding.ActivityProductBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firestore.v1.FirestoreGrpc.FirestoreBlockingStub

class ProductActivity : AppCompatActivity() {

    lateinit var binding: ActivityProductBinding

    var oderByDialog: Dialog? = null
    lateinit var productModel: ArrayList<ProductModel>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductBinding.inflate(layoutInflater)
        val view: View = binding.getRoot()
        setContentView(view)
        productModel = ArrayList()
        var subCategoryTitle: String = intent?.getStringExtra("subCategoryName").toString()

        ////////////////loading dialog
        oderByDialog = Dialog(this)
        oderByDialog?.setContentView(R.layout.dialog_oder_by)
        oderByDialog?.setCancelable(true)
        oderByDialog?.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        var oderByPriceLowToHigh: TextView = oderByDialog?.findViewById(R.id.oder_by_price_low_high)!!
        var oderByPriceHighToLow: TextView = oderByDialog?.findViewById(R.id.oder_by_price_high_low)!!
        var oderByRattingHighToLow: TextView = oderByDialog?.findViewById(R.id.oder_by_rating_high_low)!!
        var oderByRattingLowToHigh: TextView = oderByDialog?.findViewById(R.id.oder_by_rating_low_high)!!
        ////////////////loading dialog


        if (subCategoryTitle != null) {
            ProductDatabase.loadProductBySubCategory(
                this,
                binding.productRecyclerView,
                subCategoryTitle,
                binding.productAvailableText
            )
        }

        binding.oderByImage.setOnClickListener {
            oderByDialog?.show()
        }

        oderByPriceLowToHigh.setOnClickListener {
            ProductDatabase.loadProductBySubCategoryWithOderBYPriceLowToHigh(
                this,
                binding.productRecyclerView,
                subCategoryTitle
            )
            oderByDialog?.dismiss()
        }
        oderByPriceHighToLow.setOnClickListener {
            ProductDatabase.loadProductBySubCategoryWithOderBYPriceHighToLow(
                this,
                binding.productRecyclerView,
                subCategoryTitle
            )
            oderByDialog?.dismiss()
        }

        oderByRattingHighToLow.setOnClickListener {
            ProductDatabase.loadProductBySubCategoryWithOderBYRattingHighToLow(
                this,
                binding.productRecyclerView,
                subCategoryTitle
            )
            oderByDialog?.dismiss()
        }
        oderByRattingLowToHigh.setOnClickListener {
            ProductDatabase.loadProductBySubCategoryWithOderBYRattingLowToHigh(
                this,
                binding.productRecyclerView,
                subCategoryTitle
            )
            oderByDialog?.dismiss()
        }

        binding.searchView.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }

    }
}
