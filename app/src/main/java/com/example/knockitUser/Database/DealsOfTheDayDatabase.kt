package com.example.knockitUser.Database

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.knockitUser.Adapter.DealsOfTheDayAdapter
import com.example.knockitUser.Model.DealsOfTheDayModel
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot

class DealsOfTheDayDatabase {

    var auth: FirebaseAuth = FirebaseAuth.getInstance()
    var firebasefirestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    companion object {
        fun loadDealsOfTheDay(context: Context, dealsOfTheDayRecyclerView: RecyclerView) {
            var dealsOfTheDayModel: ArrayList<DealsOfTheDayModel> = ArrayList<DealsOfTheDayModel>()
            val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            dealsOfTheDayRecyclerView.layoutManager = layoutManager

            var dealsOfTheDayAdapter = DealsOfTheDayAdapter(context!!, dealsOfTheDayModel)
            dealsOfTheDayRecyclerView.adapter = dealsOfTheDayAdapter

            FirebaseFirestore.getInstance()
                .collection("HOME")
                .document("HomeData")
                .collection("Deals_of_the_day")
                .orderBy("timeStamp", Query.Direction.DESCENDING)
                .get().addOnSuccessListener(OnSuccessListener<QuerySnapshot> { queryDocumentSnapshots ->
                    for (snapshot in queryDocumentSnapshots) {
                        val model: DealsOfTheDayModel = snapshot.toObject(DealsOfTheDayModel::class.java)
                        dealsOfTheDayModel.add(model)
                    }
                    dealsOfTheDayAdapter.notifyDataSetChanged()
                })
        }
    }
}


//        ////////Load Category
//        categoryModel.add(
//            CategoryModel(
//                "",
//                "Shirt",
//                "https://pngimg.com/uploads/dress_shirt/dress_shirt_PNG8083.png",
//                "#FDF3FF",
//                ""
//            )
//        )
//        categoryModel.add(
//            CategoryModel(
//                "",
//                "Laptop",
//                "https://www.freepnglogos.com/uploads/laptop-png/laptop-transparent-png-pictures-icons-and-png-40.png",
//                "#EFFFF6",
//                ""
//            )
//        )
//        categoryModel.add(
//            CategoryModel(
//                "",
//                "iphone",
//                "https://emibaba.com/wp-content/uploads/2022/12/iphone-14-pro-black-12.png",
//                "#E6FFF9",
//                ""
//            )
//        )
//        categoryModel.add(
//            CategoryModel(
//                "",
//                "Book",
//                "https://digestbooks.com/wp-content/uploads/2022/02/Rich-Dad-Poor-Dad.png",
//                "#EFF4FF",
//                ""
//            )
//        )
//        categoryModel.add(
//            CategoryModel(
//                "",
//                "Jeans",
//                "https://www.pngall.com/wp-content/uploads/5/Ripped-Men-Jeans-PNG-Image.png",
//                "#FFF1F5",
//                ""
//            )
//        )
//        ////////Load Category