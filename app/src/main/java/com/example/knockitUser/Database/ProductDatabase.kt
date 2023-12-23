package com.example.knockitUser.Database

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.knockitUser.Activity.PermissionActivity
import com.example.knockitUser.Activity.RegisterActivity
import com.example.knockitUser.Adapter.ProductsAdapter
import com.example.knockitUser.Model.ProductModel
import com.example.knockitUser.Model.UserModel
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import java.util.Collections

class ProductDatabase {

    var auth: FirebaseAuth = FirebaseAuth.getInstance()
    var firebasefirestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    companion object {
        fun loadProduct(
            context: Context,
            productRecyclerView: RecyclerView,
            servicesDialog: Dialog
        ) {
            var productModel: ArrayList<ProductModel> = ArrayList<ProductModel>()
            var productAdapter = ProductsAdapter(context!!, productModel)
            val layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
            productRecyclerView.layoutManager = layoutManager
            productRecyclerView.adapter = productAdapter
            val products = ArrayList<ProductModel>()
            val products2 = ArrayList<ProductModel>()
            var showProduct: Boolean = true

            FirebaseFirestore.getInstance()
                .collection("PRODUCTS")
                .orderBy("timeStamp", Query.Direction.DESCENDING)
                .addSnapshotListener { querySnapshot: QuerySnapshot?, e: FirebaseFirestoreException? ->
                    querySnapshot?.let {
                        products.clear()
                        for (document in it) {
                            val model = document.toObject(ProductModel::class.java)
                            products.add(model)
                            products2.add(model)

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

//                                        FirebaseFirestore.getInstance()
//                                            .collection("USERS")
//                                            .document(FirebaseAuth.getInstance().uid.toString())
//                                            .addSnapshotListener { querySnapshot: DocumentSnapshot?, e: FirebaseFirestoreException? ->
//                                                querySnapshot?.let {
//                                                    val userModel =
//                                                        it.toObject(UserModel::class.java)
//
//                                                    if (userModel?.productListSize.equals("0")) {
//                                                        servicesDialog.show()
//                                                    } else {
//                                                        servicesDialog.dismiss()
//                                                    }
//
//                                                }
//                                            }

                                    }
                                    productAdapter.notifyDataSetChanged()
                                })

                            FirebaseFirestore.getInstance().collection("USERS")
                                .document(FirebaseAuth.getInstance().uid.toString())
                                .get()
                                .addOnSuccessListener(OnSuccessListener<DocumentSnapshot> { documentSnapshot ->
                                    val model: UserModel? =
                                        documentSnapshot.toObject(UserModel::class.java)

                                    for (i in products2) {
                                        if (i.productVerification.equals("Public")) {
                                            if (i.city_1.equals(model?.city.toString())) {
                                                showProduct = false

                                                return@OnSuccessListener
                                            } else if (i.city_2.equals(model?.city.toString())) {
                                                showProduct = false
                                                return@OnSuccessListener
                                            } else if (i.city_3.equals(model?.city.toString())) {
                                                showProduct = false
                                                return@OnSuccessListener
                                            } else if (i.city_4.equals(model?.city.toString())) {
                                                showProduct = false
                                                return@OnSuccessListener
                                            } else if (i.city_5.equals(model?.city.toString())) {
                                                showProduct = false
                                                return@OnSuccessListener
                                            }
                                        }

                                    }
                                    if (showProduct){
                                        servicesDialog.show()
                                    }else{

                                    }
                                })
                            ////// Location wise product Display


                        }
                    }
                }
        }

        fun loadProductsBySearch(
            context: Context,
            productRecyclerView: RecyclerView,
            searchProduct: String
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
                .whereArrayContains("productSearch", searchProduct)
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

                                    productAdapter.notifyDataSetChanged()
                                })
                            ////// Location wise product Display
                        }

                    }

                }
        }

        fun loadProductWithOderByPriceLowToHigh(
            context: Context,
            productRecyclerView: RecyclerView
        ) {
            var productModel: ArrayList<ProductModel> = ArrayList<ProductModel>()
            var productAdapter = ProductsAdapter(context!!, productModel)
            val layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
            productRecyclerView.layoutManager = layoutManager
            productRecyclerView.adapter = productAdapter
            val products = ArrayList<ProductModel>()

            FirebaseFirestore.getInstance()
                .collection("PRODUCTS")
                .orderBy("productPrice", Query.Direction.ASCENDING)
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
                                    productAdapter.notifyDataSetChanged()
                                })
                            ////// Location wise product Display
                        }

                    }

                }
        }

        fun loadProductWithOderByPriceHighToLow(
            context: Context,
            productRecyclerView: RecyclerView
        ) {
            var productModel: ArrayList<ProductModel> = ArrayList<ProductModel>()
            var productAdapter = ProductsAdapter(context!!, productModel)
            val layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
            productRecyclerView.layoutManager = layoutManager
            productRecyclerView.adapter = productAdapter
            val products = ArrayList<ProductModel>()

            FirebaseFirestore.getInstance()
                .collection("PRODUCTS")
                .orderBy("productPrice", Query.Direction.DESCENDING)
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
                                    productAdapter.notifyDataSetChanged()
                                })
                            ////// Location wise product Display
                        }

                    }
                }
        }

        fun loadProductWithOderByRattingHighToLow(
            context: Context,
            productRecyclerView: RecyclerView
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
                        productModel.clear()
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
                                    Collections.sort(productModel, ProductModel.rattingHighToLow)
                                    productAdapter.notifyDataSetChanged()
                                })
                            ////// Location wise product Display
                        }

                    }
                }
        }

        fun loadProductWithOderByRattingLowToHigh(
            context: Context,
            productRecyclerView: RecyclerView
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
                        productModel.clear()
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
                                    Collections.sort(productModel, ProductModel.rattingLowTOHigh)
                                    productAdapter.notifyDataSetChanged()
                                })
                            ////// Location wise product Display
                        }

                    }
                }
        }

        fun loadProductBySubCategory(
            context: Context,
            productRecyclerView: RecyclerView,
            subCategory: String,
            productNotAvailable: TextView
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
                        productModel.clear()
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
                                        if (p.productSubCategory.equals(subCategory)) {
                                            if (p.productSubCategory.equals("")) {
                                                productRecyclerView.visibility =
                                                    View.GONE
                                                productNotAvailable.visibility =
                                                    View.VISIBLE
                                            } else {
                                                if (p.productVerification.equals("Public")) {
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
                                                productRecyclerView.visibility = View.VISIBLE
                                                productNotAvailable.visibility = View.GONE
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


        fun loadProductBySubCategoryWithOderBYPriceLowToHigh(
            context: Context,
            productRecyclerView: RecyclerView,
            subCategory: String
        ) {
            var productModel: ArrayList<ProductModel> = ArrayList<ProductModel>()
            var productAdapter = ProductsAdapter(context!!, productModel)
            val layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
            productRecyclerView.layoutManager = layoutManager
            productRecyclerView.adapter = productAdapter
            val products = ArrayList<ProductModel>()

            FirebaseFirestore.getInstance()
                .collection("PRODUCTS")
                .orderBy("productPrice", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(OnSuccessListener<QuerySnapshot> { queryDocumentSnapshots ->
                    for (snapshot in queryDocumentSnapshots) {
                        val model: ProductModel = snapshot.toObject(ProductModel::class.java)
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
                                        if (p.productSubCategory.equals(subCategory)) {
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
                                productAdapter.notifyDataSetChanged()
                            })
                        ////// Location wise product Display

                    }
                })
        }

        fun loadProductBySubCategoryWithOderBYPriceHighToLow(
            context: Context,
            productRecyclerView: RecyclerView,
            subCategory: String
        ) {
            var productModel: ArrayList<ProductModel> = ArrayList<ProductModel>()
            var productAdapter = ProductsAdapter(context!!, productModel)
            val layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
            productRecyclerView.layoutManager = layoutManager
            productRecyclerView.adapter = productAdapter
            val products = ArrayList<ProductModel>()

            FirebaseFirestore.getInstance()
                .collection("PRODUCTS")
                .orderBy("productPrice", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(OnSuccessListener<QuerySnapshot> { queryDocumentSnapshots ->
                    for (snapshot in queryDocumentSnapshots) {
                        val model: ProductModel = snapshot.toObject(ProductModel::class.java)
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
                                        if (p.productSubCategory.equals(subCategory)) {
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
                                productAdapter.notifyDataSetChanged()
                            })
                        ////// Location wise product Display
                    }
                })
        }

        fun loadProductBySubCategoryWithOderBYRattingHighToLow(
            context: Context,
            productRecyclerView: RecyclerView,
            subCategory: String
        ) {
            var productModel: ArrayList<ProductModel> = ArrayList<ProductModel>()
            var productAdapter = ProductsAdapter(context!!, productModel)
            val layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
            productRecyclerView.layoutManager = layoutManager
            productRecyclerView.adapter = productAdapter
            val products = ArrayList<ProductModel>()

            FirebaseFirestore.getInstance()
                .collection("PRODUCTS")
                .orderBy("productRating", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(OnSuccessListener<QuerySnapshot> { queryDocumentSnapshots ->
                    for (snapshot in queryDocumentSnapshots) {
                        val model: ProductModel = snapshot.toObject(ProductModel::class.java)
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
                                        if (p.productSubCategory.equals(subCategory)) {
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
                                productAdapter.notifyDataSetChanged()
                            })
                        ////// Location wise product Display
                    }
                })

        }

        fun loadProductBySubCategoryWithOderBYRattingLowToHigh(
            context: Context,
            productRecyclerView: RecyclerView,
            subCategory: String
        ) {
            var productModel: ArrayList<ProductModel> = ArrayList<ProductModel>()
            var productAdapter = ProductsAdapter(context!!, productModel)
            val layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
            productRecyclerView.layoutManager = layoutManager
            productRecyclerView.adapter = productAdapter
            val products = ArrayList<ProductModel>()

            FirebaseFirestore.getInstance()
                .collection("PRODUCTS")
                .orderBy("productRating", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(OnSuccessListener<QuerySnapshot> { queryDocumentSnapshots ->
                    for (snapshot in queryDocumentSnapshots) {
                        val model: ProductModel = snapshot.toObject(ProductModel::class.java)
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
                                        if (p.productSubCategory.equals(subCategory)) {
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
                                productAdapter.notifyDataSetChanged()
                            })
                        ////// Location wise product Display

                    }
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