package com.example.knockitUser.Database

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.knockitUser.Adapter.AddressAdapter
import com.example.knockitUser.Model.AddressModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot

class AddressDatabase {

    companion object {
        fun loadAddress(context: Context, addressRecyclerView: RecyclerView) {
            var addressModel: ArrayList<AddressModel> = ArrayList<AddressModel>()
            var addressAdapter = AddressAdapter(context!!, addressModel)
            val layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
            addressRecyclerView.layoutManager = layoutManager
            addressRecyclerView.adapter = addressAdapter

            FirebaseFirestore.getInstance()
                .collection("USERS")
                .document(FirebaseAuth.getInstance().uid.toString())
                .collection("ADDRESS")
                .orderBy("timeStamp", Query.Direction.DESCENDING)
                .addSnapshotListener { querySnapshot: QuerySnapshot?, e: FirebaseFirestoreException? ->
                    querySnapshot?.let {
                        addressModel.clear()
                        for (document in it) {
                            val model = document.toObject(AddressModel::class.java)
                            addressModel.add(model)

                        }
                        addressAdapter.notifyDataSetChanged()
                    }
                }
        }
    }
}