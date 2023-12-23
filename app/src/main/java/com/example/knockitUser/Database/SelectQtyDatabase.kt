package com.example.knockitUser.Database

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.knockitUser.Adapter.ProductsAdapter
import com.example.knockitUser.Adapter.SelectQtyAdapter
import com.example.knockitUser.Model.ProductModel
import com.example.knockitUser.Model.SelectQtyModel
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot

class SelectQtyDatabase {

    companion object{
        fun loadQuantity(context: Context, quantityRecyclerView: RecyclerView, productId: String) {
            var qtyModel: ArrayList<SelectQtyModel> = ArrayList<SelectQtyModel>()
            var qtyAdapter = SelectQtyAdapter(context!!, qtyModel)
            val layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
            quantityRecyclerView.layoutManager = layoutManager
            quantityRecyclerView.adapter = qtyAdapter

            FirebaseFirestore.getInstance()
                .collection("PRODUCTS")
                .document(productId)
                .collection("productSize")
                .orderBy("timeStamp", Query.Direction.ASCENDING)
                .addSnapshotListener { querySnapshot: QuerySnapshot?, e: FirebaseFirestoreException? ->
                    querySnapshot?.let {
                        qtyModel.clear()
                        for (document in it) {
                            val model = document.toObject(SelectQtyModel::class.java)
                            qtyModel.add(model)
                        }
                        qtyAdapter.notifyDataSetChanged()
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