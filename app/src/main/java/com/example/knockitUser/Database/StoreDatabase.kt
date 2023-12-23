package com.example.knockitUser.Database

import android.content.Context
import android.text.Editable
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.example.knockitUser.Adapter.CouponsAdapter
import com.example.knockitUser.Adapter.ProductsAdapter
import com.example.knockitUser.Adapter.StoreAdapter
import com.example.knockitUser.Model.CouponsModel
import com.example.knockitUser.Model.ProductModel
import com.example.knockitUser.Model.StoreModel
import com.example.knockitUser.Model.UserModel
import com.example.knockitUser.R
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot

class StoreDatabase {

    companion object {
        fun loadStore(context: Context, storeRecyclerView: RecyclerView, category: String) {
            var storeModel: ArrayList<StoreModel> = ArrayList<StoreModel>()
            var storeAdapter = StoreAdapter(context!!, storeModel)
            val layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
            storeRecyclerView.layoutManager = layoutManager
            storeRecyclerView.adapter = storeAdapter
            var stores: ArrayList<StoreModel> = ArrayList<StoreModel>()

            FirebaseFirestore.getInstance()
                .collection("USERS")
                .document(FirebaseAuth.getInstance().uid.toString())
                .addSnapshotListener { querySnapshot: DocumentSnapshot?, e: FirebaseFirestoreException? ->
                    querySnapshot?.let {
                        val userModel = it.toObject(UserModel::class.java)

                        FirebaseFirestore.getInstance()
                            .collection("BRANCHES")
                            .orderBy("timeStamp", Query.Direction.ASCENDING)
                            .addSnapshotListener { querySnapshot: QuerySnapshot?, e: FirebaseFirestoreException? ->
                                querySnapshot?.let {
                                    stores.clear()
                                    for (document in it) {
                                        val model = document.toObject(StoreModel::class.java)
                                        stores.add(model)

                                        storeModel.clear()
                                        for (p in stores) {
                                            if (p.city.equals(userModel?.city)) {
                                                if (category.equals(p.storeCategory)){
                                                    storeModel.add(p)
                                                }else if (category.equals("All")){
                                                    storeModel.add(p)
                                                }



                                            }
                                        }
                                        storeAdapter.notifyDataSetChanged()

                                    }

                                }
                            }
                    }
                }

//            FirebaseFirestore.getInstance()
//                .collection("PRODUCTS")
//                .document(productId)
//                .collection("productSize")
//                .orderBy("timeStamp", Query.Direction.DESCENDING)
//                .get().addOnSuccessListener(OnSuccessListener<QuerySnapshot> { queryDocumentSnapshots ->
//                    for (snapshot in queryDocumentSnapshots) {
//                        val model: SelectQtyModel = snapshot.toObject(SelectQtyModel::class.java)
//                        qtyModel.add(model)
//                    }
//                    qtyAdapter.notifyDataSetChanged()
//                })
        }

        fun loadProduct(
            context: Context,
            productRecyclerView: RecyclerView,
            storeId: String
        ) {
            var productModel: ArrayList<ProductModel> = ArrayList<ProductModel>()
            var productAdapter = ProductsAdapter(context!!, productModel)
            val layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
            productRecyclerView.layoutManager = layoutManager
            productRecyclerView.adapter = productAdapter
            val products = ArrayList<ProductModel>()

            FirebaseFirestore.getInstance()
                .collection("PRODUCTS")
                .orderBy("timeStamp", Query.Direction.DESCENDING)
                .addSnapshotListener { querySnapshot: QuerySnapshot?, e: FirebaseFirestoreException? ->
                    querySnapshot?.let {
                        products.clear()
                        for (document in it) {
                            val model = document.toObject(ProductModel::class.java)
                            products.add(model)

                            productModel.clear()
                            for (p in products) {
                                if (p.storeId.equals(storeId)) {
                                    productModel.add(p)
                                }

                            }
                            productAdapter.notifyDataSetChanged()
                        }
                    }
                }
        }

    }
}