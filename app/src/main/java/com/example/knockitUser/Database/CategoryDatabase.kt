package com.example.knockitUser.Database

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.knockitUser.Adapter.CategoryAdapter
import com.example.knockitUser.Adapter.CategoryMiniAdapter
import com.example.knockitUser.Adapter.SelectCategoryAdapterByStores
import com.example.knockitUser.Adapter.SubCategoryAdapter
import com.example.knockitUser.Model.CategoryModel
import com.example.knockitUser.Model.SubCategoryModel
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot

class CategoryDatabase {

    var auth: FirebaseAuth = FirebaseAuth.getInstance()
    var firebasefirestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    companion object {
        fun loadCategoryMini(context: Context, categoryRecyclerView: RecyclerView) {
            var categoryModel: ArrayList<CategoryModel> = ArrayList<CategoryModel>()
            val bannerLayout = LinearLayoutManager(context)
            bannerLayout.orientation = RecyclerView.VERTICAL
            categoryRecyclerView.layoutManager = bannerLayout

            var categoryAdapter = CategoryMiniAdapter(context!!, categoryModel)
            categoryRecyclerView.adapter = categoryAdapter

            FirebaseFirestore.getInstance()
                .collection("Category")
                .orderBy("timeStamp", Query.Direction.DESCENDING)
                .get().addOnSuccessListener(OnSuccessListener<QuerySnapshot> { queryDocumentSnapshots ->
                    for (snapshot in queryDocumentSnapshots) {
                        val model: CategoryModel = snapshot.toObject(CategoryModel::class.java)
                        categoryModel.add(model)
                    }
                    categoryAdapter.notifyDataSetChanged()
                })
        }

        fun loadCategory(context: Context, categoryRecyclerView: RecyclerView) {
            var categoryModel: ArrayList<CategoryModel> = ArrayList<CategoryModel>()
            val layoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
            categoryRecyclerView.layoutManager = layoutManager

            var categoryAdapter = CategoryAdapter(context!!, categoryModel)
            categoryRecyclerView.adapter = categoryAdapter

            FirebaseFirestore.getInstance()
                .collection("Category")
                .orderBy("timeStamp", Query.Direction.DESCENDING)
                .get().addOnSuccessListener(OnSuccessListener<QuerySnapshot> { queryDocumentSnapshots ->
                    for (snapshot in queryDocumentSnapshots) {
                        val model: CategoryModel = snapshot.toObject(CategoryModel::class.java)
                        categoryModel.add(model)
                    }
                    categoryAdapter.notifyDataSetChanged()
                })
        }

        fun loadCategoryByStore(context: Context, categoryRecyclerView: RecyclerView) {
            var categoryModel: ArrayList<CategoryModel> = ArrayList<CategoryModel>()
            val bannerLayout = LinearLayoutManager(context)
            bannerLayout.orientation = RecyclerView.HORIZONTAL
            categoryRecyclerView.layoutManager = bannerLayout

            var selectCategoryAdapterByStores = SelectCategoryAdapterByStores(context!!, categoryModel)
            categoryRecyclerView.adapter = selectCategoryAdapterByStores

            FirebaseFirestore.getInstance()
                .collection("Category")
                .orderBy("timeStamp", Query.Direction.DESCENDING)
                .get().addOnSuccessListener(OnSuccessListener<QuerySnapshot> { queryDocumentSnapshots ->
                    for (snapshot in queryDocumentSnapshots) {
                        val model: CategoryModel = snapshot.toObject(CategoryModel::class.java)
                        categoryModel.add(model)
                    }
                    selectCategoryAdapterByStores.notifyDataSetChanged()
                })
        }

        fun loadSubCategory(context: Context, subCategoryRecyclerView: RecyclerView, categoryTitle: String, productAvailable: TextView) {
            var subCategoryModel: ArrayList<SubCategoryModel> = ArrayList<SubCategoryModel>()
            val layoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
            subCategoryRecyclerView.layoutManager = layoutManager

            var subCategoryAdapter = SubCategoryAdapter(context!!, subCategoryModel)
            subCategoryRecyclerView.adapter = subCategoryAdapter

            FirebaseFirestore.getInstance()
                .collection("SubCategory")
                .orderBy("timeStamp", Query.Direction.DESCENDING)
                .get().addOnSuccessListener(OnSuccessListener<QuerySnapshot> { queryDocumentSnapshots ->
                    for (snapshot in queryDocumentSnapshots) {
                        val model: SubCategoryModel = snapshot.toObject(SubCategoryModel::class.java)

                        if (model.category.equals(categoryTitle)){
                            if (model.subCategoryTitle.equals("")){
                                subCategoryRecyclerView.visibility = View.GONE
                                productAvailable.visibility = View.VISIBLE
                            }else{
                                subCategoryModel.add(model)
                                subCategoryRecyclerView.visibility = View.VISIBLE
                                productAvailable.visibility = View.GONE
                            }
                        }

                    }
                    subCategoryAdapter.notifyDataSetChanged()
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