package com.example.knockitUser.Database

import android.content.Context
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.knockitUser.Adapter.BannerAdapter
import com.example.knockitUser.Adapter.ProductsAdapter
import com.example.knockitUser.Model.BannerModel
import com.example.knockitUser.Model.ProductModel
import com.example.knockitUser.Model.UserModel
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import java.util.Timer
import java.util.TimerTask

class BannerDatabase {

    var auth: FirebaseAuth = FirebaseAuth.getInstance()
    var firebasefirestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    companion object {
        fun loadBanner(context: Context, bannerRecyclerView: RecyclerView) {
            var bannerModel: ArrayList<BannerModel> = ArrayList<BannerModel>()
            val bannerLayout = LinearLayoutManager(context)
            bannerLayout.orientation = RecyclerView.HORIZONTAL
            bannerRecyclerView.layoutManager = bannerLayout

            var bannerAdapter = BannerAdapter(context!!, bannerModel)
            bannerRecyclerView.adapter = bannerAdapter
            val linearSnapHelper = LinearSnapHelper()
            linearSnapHelper.attachToRecyclerView(bannerRecyclerView)

            val timer = Timer()
            timer.schedule(object : TimerTask() {
                override fun run() {
                    if (bannerLayout.findLastCompletelyVisibleItemPosition() < bannerAdapter.itemCount - 1) {
                        bannerLayout.smoothScrollToPosition(
                            bannerRecyclerView,
                            RecyclerView.State(),
                            bannerLayout.findLastCompletelyVisibleItemPosition() + 1
                        )
                    } else if (bannerLayout.findLastCompletelyVisibleItemPosition() == bannerAdapter.itemCount - 1) {
                        bannerLayout.smoothScrollToPosition(
                            bannerRecyclerView,
                            RecyclerView.State(),
                            0
                        )
                    }
                }
            }, 0, 4000)

            FirebaseFirestore.getInstance()
                .collection("HOME")
                .document("HomeData")
                .collection("Banner")
                .orderBy("timeStamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(OnSuccessListener<QuerySnapshot> { queryDocumentSnapshots ->
                    for (snapshot in queryDocumentSnapshots) {
                        val bannerModel1: BannerModel = snapshot.toObject(BannerModel::class.java)
                        bannerModel.add(bannerModel1)
                    }
                    bannerAdapter.notifyDataSetChanged()
                })
        }

        fun loadProductsByBanner(
            context: Context,
            productRecyclerView: RecyclerView,
            subcategory: String,
            discount: Long
        ) {
            var productModel: ArrayList<ProductModel> = ArrayList<ProductModel>()
            var productAdapter = ProductsAdapter(context!!, productModel)
            val layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
            productRecyclerView.layoutManager = layoutManager
            productRecyclerView.adapter = productAdapter
            val products = ArrayList<ProductModel>()

            FirebaseFirestore.getInstance()
                .collection("PRODUCTS")
                .orderBy("timeStamp", Query.Direction.ASCENDING)
                .addSnapshotListener { querySnapshot: QuerySnapshot?, e: FirebaseFirestoreException? ->
                    querySnapshot?.let {

                        products.clear()
                        for (document in it) {
                            val model = document.toObject(ProductModel::class.java)
                            products.add(model)

                            ////// Location wise product Display
                            FirebaseFirestore.getInstance().collection("USERS")
                                .document(FirebaseAuth.getInstance().uid.toString())
                                .get()
                                .addOnSuccessListener(OnSuccessListener<DocumentSnapshot> { documentSnapshot ->
                                    val model: UserModel? =
                                        documentSnapshot.toObject(UserModel::class.java)

                                    productModel.clear()
                                    for (p in products) {
                                        if (p.productVerification.equals("Public")) {
                                            if (p.productSubCategory.equals(subcategory)) {
                                                var discount1 =
                                                    100 - ((p.productPrice.toFloat() / p.productCuttedPrice.toFloat()) * 100)
                                                if (discount1 >= discount) {
                                                    if (p.city_1.equals(model?.city.toString())) {
                                                        productModel.add(p)
                                                    } else if (p.city_2.equals(model?.city.toString())) {
                                                        productModel.add(p)
                                                    } else if (p.city_3.equals(model?.city.toString())) {
                                                        productModel.add(p)
                                                    } else if (p.city_4.equals(model?.city.toString())) {
                                                        productModel.add(p)
                                                    } else if (p.city_5.equals(model?.city.toString())) {
                                                        productModel.add(p)
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    productAdapter.notifyDataSetChanged()
                                })
                            ////// Location wise product Display
                        }

                    }

                }
        }
    }
}


//        ////////Load Banner
//        bannerModel.add(BannerModel("","https://www.erajaya.com/files/uploads/newseventattachment/uri/2021/Jan/15/6001886d76043/available-web-banner-iphone-12-dekstop-1091x_.jpg","#F9F6F1",""))
//        bannerModel.add(BannerModel("","https://s3.amazonaws.com/thumbnails.venngage.com/template/10ff3ede-ce85-40ce-b202-db58c803ebbf.png","#1A1A1A",""))
//        bannerModel.add(BannerModel("","https://qualzz.com/wp-content/uploads/2019/12/Discount-Strategies.png","#A1ECDD",""))
//        bannerModel.add(BannerModel("","https://www.denimvistara.com/blog/wp-content/uploads/2018/05/Banner6-750x410.jpg","#E7EFF2",""))
//        bannerModel.add(BannerModel("","https://img.freepik.com/free-vector/flat-design-realistic-landing-page-template_23-2150102697.jpg","#F0ECFD",""))
//        ////////Load Banner