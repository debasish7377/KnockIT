package com.example.knockitUser.Database

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.knockitUser.Adapter.MyWishlistAdapter
import com.example.knockitUser.Adapter.ProductImagesAdapter
import com.example.knockitUser.Adapter.ProductReviewAdapter
import com.example.knockitUser.Adapter.ProductSelectSizeAdapter
import com.example.knockitUser.Adapter.ProductSpecificationAdapter
import com.example.knockitUser.Adapter.ProductsAdapter
import com.example.knockitUser.Fragments.WishlistFragment
import com.example.knockitUser.Model.MyWishlistModel
import com.example.knockitUser.Model.ProductImagesModel
import com.example.knockitUser.Model.ProductModel
import com.example.knockitUser.Model.ProductReviewModel
import com.example.knockitUser.Model.ProductSelectSizeModel
import com.example.knockitUser.Model.ProductSpecificationModel
import com.example.knockitUser.Model.SelectQtyModel
import com.example.knockitUser.Model.UserModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.auth.User
import java.util.Timer
import java.util.TimerTask


class ProductDetailsDatabase {
    companion object{
        lateinit var wishlistAdapter : MyWishlistAdapter
        fun loadImages(context: Context ,productId: String, imagesRecyclerView: RecyclerView){
            var productImagesModel: ArrayList<ProductImagesModel> = ArrayList<ProductImagesModel>()
            val bannerLayout = LinearLayoutManager(context)
            bannerLayout.orientation = RecyclerView.HORIZONTAL
            imagesRecyclerView.layoutManager = bannerLayout

            var productImagesAdapter = ProductImagesAdapter(context!!, productImagesModel)
            imagesRecyclerView.adapter = productImagesAdapter
            val linearSnapHelper = LinearSnapHelper()
            linearSnapHelper.attachToRecyclerView(imagesRecyclerView)

            val timer = Timer()
            timer.schedule(object : TimerTask() {
                override fun run() {
                    if (bannerLayout.findLastCompletelyVisibleItemPosition() < productImagesAdapter.itemCount - 1) {
                        bannerLayout.smoothScrollToPosition(
                            imagesRecyclerView,
                            RecyclerView.State(),
                            bannerLayout.findLastCompletelyVisibleItemPosition() + 1
                        )
                    } else if (bannerLayout.findLastCompletelyVisibleItemPosition() == productImagesAdapter.itemCount - 1) {
                        bannerLayout.smoothScrollToPosition(imagesRecyclerView, RecyclerView.State(), 0)
                    }
                }
            }, 0, 4000)

            FirebaseFirestore.getInstance()
                .collection("PRODUCTS")
                .document(productId)
                .collection("productImages")
                .orderBy("timeStamp", Query.Direction.ASCENDING)
                .get().addOnSuccessListener(OnSuccessListener<QuerySnapshot> { queryDocumentSnapshots ->
                    for (snapshot in queryDocumentSnapshots) {
                        val productImagesModel1: ProductImagesModel = snapshot.toObject(ProductImagesModel::class.java)
                        productImagesModel.add(productImagesModel1)
                    }
                    productImagesAdapter.notifyDataSetChanged()
                })
        }

        fun loadSelectSize(context: Context ,productId: String, productSelectRecyclerView: RecyclerView){
            var selectQtyModel: ArrayList<SelectQtyModel> = ArrayList<SelectQtyModel>()
            val bannerLayout = LinearLayoutManager(context)
            bannerLayout.orientation = RecyclerView.HORIZONTAL
            productSelectRecyclerView.layoutManager = bannerLayout

            var productSelectAdapter = ProductSelectSizeAdapter(context!!, selectQtyModel)
            productSelectRecyclerView.adapter = productSelectAdapter

            FirebaseFirestore.getInstance()
                .collection("PRODUCTS")
                .document(productId)
                .collection("productSize")
                .orderBy("timeStamp", Query.Direction.ASCENDING)
                .addSnapshotListener { querySnapshot: QuerySnapshot?, e: FirebaseFirestoreException? ->
                    querySnapshot?.let {
                        selectQtyModel.clear()
                        for (document in it) {
                            val model = document.toObject(SelectQtyModel::class.java)
                            selectQtyModel.add(model)
                        }
                        productSelectAdapter.notifyDataSetChanged()
                    }
                }

//            FirebaseFirestore.getInstance()
//                .collection("PRODUCTS")
//                .document(productId)
//                .collection("productSize")
//                .orderBy("timeStamp", Query.Direction.ASCENDING)
//                .get().addOnSuccessListener(OnSuccessListener<QuerySnapshot> { queryDocumentSnapshots ->
//                    for (snapshot in queryDocumentSnapshots) {
//                        val model: SelectQtyModel = snapshot.toObject(SelectQtyModel::class.java)
//                        selectQtyModel.add(model)
//                    }
//                    productSelectAdapter.notifyDataSetChanged()
//                })
        }

        fun loadSpecification(context: Context ,productId: String, specificationRecyclerView: RecyclerView){
            var specificationModel: ArrayList<ProductSpecificationModel> = ArrayList<ProductSpecificationModel>()
            val bannerLayout = LinearLayoutManager(context)
            bannerLayout.orientation = RecyclerView.VERTICAL
            specificationRecyclerView.layoutManager = bannerLayout

            var specificationAdapter = ProductSpecificationAdapter(context!!, specificationModel)
            specificationRecyclerView.adapter = specificationAdapter

            FirebaseFirestore.getInstance()
                .collection("PRODUCTS")
                .document(productId)
                .collection("productSpecification")
                .orderBy("timeStamp", Query.Direction.ASCENDING)
                .get().addOnSuccessListener(OnSuccessListener<QuerySnapshot> { queryDocumentSnapshots ->
                    for (snapshot in queryDocumentSnapshots) {
                        val model: ProductSpecificationModel = snapshot.toObject(ProductSpecificationModel::class.java)
                        specificationModel.add(model)
                    }
                    specificationAdapter.notifyDataSetChanged()
                })
        }

        fun loadProductReview(context: Context ,productId: String, productReviewRecyclerView: RecyclerView){
            var productReviewModel: ArrayList<ProductReviewModel> = ArrayList<ProductReviewModel>()
            val bannerLayout = LinearLayoutManager(context)
            bannerLayout.orientation = RecyclerView.VERTICAL
            productReviewRecyclerView.layoutManager = bannerLayout

            var productReviewAdapter = ProductReviewAdapter(context!!, productReviewModel)
            productReviewRecyclerView.adapter = productReviewAdapter

            FirebaseFirestore.getInstance()
                .collection("PRODUCTS")
                .document(productId)
                .collection("ProductReview")
                .orderBy("timeStamp", Query.Direction.ASCENDING)
                .get().addOnSuccessListener(OnSuccessListener<QuerySnapshot> { queryDocumentSnapshots ->
                    for (snapshot in queryDocumentSnapshots) {
                        val model: ProductReviewModel = snapshot.toObject(ProductReviewModel::class.java)
                        productReviewModel.add(model)
                    }
                    productReviewAdapter.notifyDataSetChanged()
                })
        }

        fun loadWishlist(context: Context, wishlistRecyclerView: RecyclerView){
            var wishlistModel = ArrayList<MyWishlistModel>()
            wishlistAdapter = MyWishlistAdapter(context!!, wishlistModel)
            wishlistRecyclerView.adapter = wishlistAdapter

            FirebaseFirestore.getInstance()
                .collection("USERS")
                .document(FirebaseAuth.getInstance().uid.toString())
                .collection("MY_WISHLIST")
                .orderBy("timeStamp", Query.Direction.ASCENDING)
                .get().addOnSuccessListener(OnSuccessListener<QuerySnapshot> { queryDocumentSnapshots ->
                    wishlistModel.clear()
                    for (snapshot in queryDocumentSnapshots) {
                        val model: MyWishlistModel = snapshot.toObject(MyWishlistModel::class.java)
                        wishlistModel.add(model)

                    }
                    wishlistAdapter.notifyDataSetChanged()
                })
        }

        fun loadWishlistAdded(context: Context, productId: String){
            val update: MutableMap<String, Any> = HashMap()
            update["productId"] = productId
            update["timeStamp"] = System.currentTimeMillis().toString()

            FirebaseFirestore.getInstance()
                .collection("USERS")
                .document(FirebaseAuth.getInstance().uid.toString())
                .collection("MY_WISHLIST")
                .document(productId)
                .set(update)
                .addOnCompleteListener {task ->
                    if (task.isSuccessful){
                        Toast.makeText(context, "Wishlist Successfully added", Toast.LENGTH_SHORT).show()
                    }else{
                        var error: String = task.exception.toString()
                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                    }
                }
        }

        fun loadWishlistRemoved(context: Context, productId: String){
            val update: MutableMap<String, Any> = HashMap()
            update["productId"] = productId
            update["timeStamp"] = System.currentTimeMillis()

            FirebaseFirestore.getInstance()
                .collection("USERS")
                .document(FirebaseAuth.getInstance().uid.toString())
                .collection("MY_WISHLIST")
                .document(productId)
                .delete()
                .addOnCompleteListener {task ->
                    if (task.isSuccessful){
                        Toast.makeText(context, "Wishlist Successfully Removed", Toast.LENGTH_SHORT).show()
                    }else{
                        var error: String = task.exception.toString()
                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                    }
                }
        }

    }
}

