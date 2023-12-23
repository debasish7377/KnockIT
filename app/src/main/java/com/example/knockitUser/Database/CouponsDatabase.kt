package com.example.knockitUser.Database

import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.knockitUser.Adapter.ApplyCouponAdapter
import com.example.knockitUser.Adapter.CouponsAdapter
import com.example.knockitUser.Adapter.MyCartAdapter
import com.example.knockitUser.Adapter.ProductsAdapter
import com.example.knockitUser.Adapter.SelectQtyAdapter
import com.example.knockitUser.Model.CouponsModel
import com.example.knockitUser.Model.MyCartModel
import com.example.knockitUser.Model.ProductModel
import com.example.knockitUser.Model.SelectQtyModel
import com.example.knockitUser.Model.UserModel
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot

class CouponsDatabase {

    companion object {
        fun loadCoupons(context: Context, couponsRecyclerView: RecyclerView, productPrice: Int) {
            var couponsModel: ArrayList<CouponsModel> = ArrayList<CouponsModel>()
            var couponsAdapter = CouponsAdapter(context!!, couponsModel)
            val layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
            couponsRecyclerView.layoutManager = layoutManager
            couponsRecyclerView.adapter = couponsAdapter
            var coupons: ArrayList<CouponsModel> = ArrayList<CouponsModel>()
            var userCouponApply: ArrayList<CouponsModel> = ArrayList<CouponsModel>()

            FirebaseFirestore.getInstance()
                .collection("Coupons")
                .orderBy("timeStamp", Query.Direction.ASCENDING)
                .addSnapshotListener { querySnapshot: QuerySnapshot?, e: FirebaseFirestoreException? ->
                    querySnapshot?.let {
                        coupons.clear()
                        for (document in it) {
                            val model = document.toObject(CouponsModel::class.java)
                            coupons.add(model)

                            couponsModel.clear()
                            for (p in coupons) {
                                if (p.validTime >= System.currentTimeMillis()) {
                                    if (p.productAbovePrice <= productPrice) {

                                        couponsModel.add(p)

//                                            FirebaseFirestore.getInstance()
//                                                .collection("USERS")
//                                                .document(FirebaseAuth.getInstance().uid.toString())
//                                                .collection("COUPON_APPLIED")
//                                                .document(i.id.toString())
//                                                .addSnapshotListener { querySnapshot: DocumentSnapshot?, e: FirebaseFirestoreException? ->
//                                                    querySnapshot?.let {
//                                                        val userModel =
//                                                            it.toObject(CouponsModel::class.java)
//
//                                                        if (!i.id.equals(userModel?.id)) {
//
//                                                        }
//
//                                                    }
//                                                }

                                    }

                                }
                            }
                            couponsAdapter.notifyDataSetChanged()

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

        fun loadCouponsByCart(
            context: Context,
            couponsRecyclerView: RecyclerView,
            productPrice: Int
        ) {
            var couponsModel: ArrayList<CouponsModel> = ArrayList<CouponsModel>()
            var couponsAdapter = ApplyCouponAdapter(context!!, couponsModel)
            val layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
            couponsRecyclerView.layoutManager = layoutManager
            couponsRecyclerView.adapter = couponsAdapter
            var coupons: ArrayList<CouponsModel> = ArrayList<CouponsModel>()

            FirebaseFirestore.getInstance()
                .collection("Coupons")
                .orderBy("timeStamp", Query.Direction.ASCENDING)
                .addSnapshotListener { querySnapshot: QuerySnapshot?, e: FirebaseFirestoreException? ->
                    querySnapshot?.let {
                        coupons.clear()
                        for (document in it) {
                            val model = document.toObject(CouponsModel::class.java)
                            coupons.add(model)

                            couponsModel.clear()
                            for (p in coupons) {
                                if (p.validTime >= System.currentTimeMillis()) {
                                    if (p.productAbovePrice <= productPrice) {
                                        FirebaseFirestore.getInstance()
                                            .collection("USERS")
                                            .document(FirebaseAuth.getInstance().uid.toString())
                                            .collection("COUPON_APPLIED")
                                            .addSnapshotListener { querySnapshot: QuerySnapshot?, e: FirebaseFirestoreException? ->
                                                querySnapshot?.let {
                                                    for (document in it) {
                                                        val model =
                                                            document.toObject(CouponsModel::class.java)

                                                        if (!p.id.equals(model.id)) {
                                                            couponsModel.add(p)
                                                        }
                                                    }
                                                }
                                            }

                                    }
                                }
                            }
                            couponsAdapter.notifyDataSetChanged()
                        }

                    }
                }
        }
    }
}