package com.example.knockitUser.Database

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.knockitUser.Adapter.MyCartAdapter
import com.example.knockitUser.Adapter.ProductsAdapter
import com.example.knockitUser.Adapter.SelectQtyAdapter
import com.example.knockitUser.Model.MyCartModel
import com.example.knockitUser.Model.ProductModel
import com.example.knockitUser.Model.SelectQtyModel
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot

class MyCartDatabase {

    companion object{
        fun loadMyCart(context: Context, myCartRecyclerView: RecyclerView) {
            var cartModel: ArrayList<MyCartModel> = ArrayList<MyCartModel>()
            var CartAdapter = MyCartAdapter(context!!, cartModel)
            val layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
            myCartRecyclerView.layoutManager = layoutManager
            myCartRecyclerView.adapter = CartAdapter

            FirebaseFirestore.getInstance()
                .collection("USERS")
                .document(FirebaseAuth.getInstance().uid.toString())
                .collection("MY_CART")
                .orderBy("timeStamp", Query.Direction.ASCENDING)
                .addSnapshotListener { querySnapshot: QuerySnapshot?, e: FirebaseFirestoreException? ->
                    querySnapshot?.let {
                        cartModel.clear()
                        for (document in it) {
                            val model = document.toObject(MyCartModel::class.java)
                            cartModel.add(model)
                        }
                        CartAdapter.notifyDataSetChanged()
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
    }
}