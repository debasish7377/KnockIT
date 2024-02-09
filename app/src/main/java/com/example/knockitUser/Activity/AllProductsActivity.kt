package com.example.knockitUser.Activity

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.knockitUser.Database.ProductDatabase
import com.example.knockitUser.Model.ProductModel
import com.example.knockitUser.R
import com.example.knockitUser.databinding.ActivityAllProductsBinding
import com.example.knockitUser.databinding.ActivityProductBinding
import java.util.Collections

class AllProductsActivity : AppCompatActivity() {
    lateinit var binding: ActivityAllProductsBinding

    var orderByDialog: Dialog? = null
    lateinit var productModel: ArrayList<ProductModel>
    lateinit var servicesNotAvailableDialog: Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllProductsBinding.inflate(layoutInflater)
        val view: View = binding.getRoot()
        setContentView(view)
        productModel = ArrayList()
        var subCategoryTitle: String = intent?.getStringExtra("subCategoryName").toString()

        ////////////////Services Not Available Dialog
        servicesNotAvailableDialog = Dialog(this)
        servicesNotAvailableDialog.setContentView(R.layout.dialog_services_not_available)
        servicesNotAvailableDialog.setCancelable(false)
        servicesNotAvailableDialog.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        ////////////////Services Not Available Dialog

        ////////////////loading dialog
        orderByDialog = Dialog(this)
        orderByDialog?.setContentView(R.layout.dialog_oder_by)
        orderByDialog?.setCancelable(true)
        orderByDialog?.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        var oderByPriceLowToHigh: TextView = orderByDialog?.findViewById(R.id.oder_by_price_low_high)!!
        var oderByPriceHighToLow: TextView = orderByDialog?.findViewById(R.id.oder_by_price_high_low)!!
        var oderByRattingHighToLow: TextView = orderByDialog?.findViewById(R.id.oder_by_rating_high_low)!!
        var oderByRattingLowToHigh: TextView = orderByDialog?.findViewById(R.id.oder_by_rating_low_high)!!
        ////////////////loading dialog

        binding.backArrow.setOnClickListener {
            finish()
        }

        ProductDatabase.loadProduct(
            this,
            binding.productRecyclerView,
            servicesNotAvailableDialog
        )

        binding.oderByImage.setOnClickListener {
            orderByDialog?.show()
        }

        oderByPriceLowToHigh.setOnClickListener {
            ProductDatabase.loadProductWithOderByPriceLowToHigh(
                this,
                binding.productRecyclerView
            )
            orderByDialog?.dismiss()
        }
        oderByPriceHighToLow.setOnClickListener {
            ProductDatabase.loadProductWithOderByPriceHighToLow(
                this,
                binding.productRecyclerView
            )
            orderByDialog?.dismiss()
        }

        oderByRattingHighToLow.setOnClickListener {
            ProductDatabase.loadProductWithOderByRattingHighToLow(
                this,
                binding.productRecyclerView
            )
            orderByDialog?.dismiss()
        }
        oderByRattingLowToHigh.setOnClickListener {
            ProductDatabase.loadProductWithOderByRattingLowToHigh(
                this,
                binding.productRecyclerView
            )
            orderByDialog?.dismiss()
        }

        binding.searchView.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }

    }
}