package com.example.knockitUser.Fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.knockitUser.Activity.AllProductsActivity
import com.example.knockitUser.Activity.DealsOfTheDayActivity
import com.example.knockitUser.Activity.MyCartActivity
import com.example.knockitUser.Activity.NotificationActivity
import com.example.knockitUser.Activity.PermissionActivity
import com.example.knockitUser.Activity.ProductActivity
import com.example.knockitUser.Activity.RegisterActivity
import com.example.knockitUser.Activity.SearchActivity
import com.example.knockitUser.Activity.StoresActivity
import com.example.knockitUser.Adapter.BannerAdapter
import com.example.knockitUser.Adapter.CategoryMiniAdapter
import com.example.knockitUser.Adapter.DealsOfTheDayAdapter
import com.example.knockitUser.Adapter.ProductsAdapter
import com.example.knockitUser.Database.BannerDatabase
import com.example.knockitUser.Database.CategoryDatabase
import com.example.knockitUser.Database.DealsOfTheDayDatabase
import com.example.knockitUser.Database.ProductDatabase
import com.example.knockitUser.Model.BannerModel
import com.example.knockitUser.Model.CategoryModel
import com.example.knockitUser.Model.ProductModel
import com.example.knockitUser.Model.UserModel
import com.example.knockitUser.R
import com.example.knockitUser.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException

class HomeFragment : Fragment() {

    lateinit var categoryAdapter: CategoryMiniAdapter
    lateinit var categoryModel: ArrayList<CategoryModel>

    lateinit var dealsOfTheDayAdapter: DealsOfTheDayAdapter
    lateinit var dealsOfTheDayModel: ArrayList<ProductModel>

    lateinit var bannerAdapter: BannerAdapter
    lateinit var bannerModel: ArrayList<BannerModel>

    lateinit var productAdapter: ProductsAdapter
    lateinit var productModel: ArrayList<ProductModel>
    lateinit var servicesNotAvailableDialog: Dialog

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding: FragmentHomeBinding = FragmentHomeBinding.inflate(inflater, container, false)
        categoryModel = ArrayList()
        dealsOfTheDayModel = ArrayList()
        bannerModel = ArrayList<BannerModel>()
        productModel = ArrayList()

        ////////////////Services Not Available Dialog
        servicesNotAvailableDialog = Dialog(context!!)
        servicesNotAvailableDialog.setContentView(R.layout.dialog_services_not_available)
        servicesNotAvailableDialog.setCancelable(false)
        servicesNotAvailableDialog.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        var dialogOkBtn: AppCompatButton = servicesNotAvailableDialog.findViewById(R.id.dialog_ok_btn)
        ////////////////Services Not Available Dialog

        //loadCategory(binding.categoryLayout.categoryRecyclerView)
        //loadDealsOfTheDay(binding.dealsOfTheDayLayout.dealsOfTheDayRecyclerView)
        //loadBanner(binding.bannerLayout.bannerRecyclerView)
        //loadProductForYou(binding.productLayout.productRecyclerView)
        binding.dealsOfTheDayLayout.dealsOfTheDayRecyclerView.stopNestedScroll()
        binding.dealsOfTheDayLayout.dealsOfTheDayRecyclerView.setNestedScrollingEnabled(false);
        BannerDatabase.loadBanner(context!!,binding.bannerLayout.bannerRecyclerView)
        CategoryDatabase.loadCategory(context!!,binding.categoryLayout.categoryRecyclerView)
        DealsOfTheDayDatabase.loadDealsOfTheDay(context!!,binding.dealsOfTheDayLayout.dealsOfTheDayRecyclerView)
        ProductDatabase.loadProduct(context!!,binding.productLayout.productRecyclerView, servicesNotAvailableDialog)

        binding.productLayout.productViewAllBtn.setOnClickListener {
            startActivity(Intent(context, AllProductsActivity::class.java))
        }

        dialogOkBtn.setOnClickListener {
            requireActivity().finish()
        }

        binding.myCartBtn.setOnClickListener {
            startActivity(Intent(context, MyCartActivity::class.java))
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
            startActivity(Intent(context, SearchActivity::class.java))
        }

        binding.store.setOnClickListener {
            startActivity(Intent(context, StoresActivity::class.java))
        }

        binding.notification.setOnClickListener {
            startActivity(Intent(context, NotificationActivity::class.java))
        }
        binding.dealsOfTheDayLayout.viewAll.setOnClickListener {
            startActivity(Intent(context, DealsOfTheDayActivity::class.java))
        }

        return binding.root
    }

    @SuppressLint("UseRequireInsteadOfGet")
    private fun loadCategory(categoryRecyclerView: RecyclerView) {
        categoryAdapter = CategoryMiniAdapter(context!!, categoryModel)
        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        categoryRecyclerView.layoutManager = layoutManager
        categoryRecyclerView.adapter = categoryAdapter
    }

    @SuppressLint("UseRequireInsteadOfGet")
    private fun loadProductForYou(productForYouRecyclerView: RecyclerView) {
        productAdapter = ProductsAdapter(context!!, productModel)
        val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        productForYouRecyclerView.layoutManager = layoutManager
        productForYouRecyclerView.adapter = productAdapter
    }

}



////////Load product for you
//        productModel.add(
//            ProductModel(
//                "https://www.freepnglogos.com/uploads/laptop-png/laptop-transparent-png-pictures-icons-and-png-40.png",
//                "Amazon Brand - Symbol Men's Regular Fit T-Shirt",
//                "₹579",
//                "₹799",
//                "₹80 Rupees Delivery Price",
//                "4.5",
//                "200",
//                ""
//            )
//        )
//        productModel.add(
//            ProductModel(
//                "https://emibaba.com/wp-content/uploads/2022/12/iphone-14-pro-black-12.png",
//                "i phone 14 pro max",
//                "₹135000",
//                "₹158000",
//                "Free Delivery",
//                "4.0",
//                "500",
//                ""
//            )
//        )
//        productModel.add(
//            ProductModel(
//                "https://www.pngall.com/wp-content/uploads/5/Ripped-Men-Jeans-PNG-Image.png",
//                "Puma branded jeans with 1yr warranty",
//                "₹1500",
//                "₹3000",
//                "₹50 Rupees Delivery Price",
//                "3.5",
//                "700",
//                ""
//            )
//        )
//        productModel.add(
//            ProductModel(
//                "https://www.freepnglogos.com/uploads/laptop-png/laptop-transparent-png-pictures-icons-and-png-40.png",
//                "Amazon Brand - Symbol Men's Regular Fit T-Shirt",
//                "₹579",
//                "₹799",
//                "₹80 Rupees Delivery Price",
//                "4.5",
//                "200",
//                ""
//            )
//        )
//        productModel.add(
//            ProductModel(
//                "https://www.freepnglogos.com/uploads/laptop-png/laptop-transparent-png-pictures-icons-and-png-40.png",
//                "Amazon Brand - Symbol Men's Regular Fit T-Shirt",
//                "₹579",
//                "₹799",
//                "₹80 Rupees Delivery Price",
//                "4.5",
//                "200",
//                ""
//            )
//        )
//        productModel.add(
//            ProductModel(
//                "https://emibaba.com/wp-content/uploads/2022/12/iphone-14-pro-black-12.png",
//                "i phone 14 pro max",
//                "₹135000",
//                "₹158000",
//                "Free Delivery",
//                "4.0",
//                "500",
//                ""
//            )
//        )
//        productModel.add(
//            ProductModel(
//                "https://www.pngall.com/wp-content/uploads/5/Ripped-Men-Jeans-PNG-Image.png",
//                "Puma branded jeans with 1yr warranty",
//                "₹1500",
//                "₹3000",
//                "₹50 Rupees Delivery Price",
//                "3.5",
//                "700",
//                ""
//            )
//        )
//        productModel.add(
//            ProductModel(
//                "https://www.freepnglogos.com/uploads/laptop-png/laptop-transparent-png-pictures-icons-and-png-40.png",
//                "Amazon Brand - Symbol Men's Regular Fit T-Shirt",
//                "₹579",
//                "₹799",
//                "₹80 Rupees Delivery Price",
//                "4.5",
//                "200",
//                ""
//            )
//        )
////////Load product for you