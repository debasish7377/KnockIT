package com.example.knockitUser.Fragments

import android.annotation.SuppressLint
import android.content.Context.MODE_APPEND
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.knockitUser.Activity.MyCartActivity
import com.example.knockitUser.Activity.SearchActivity
import com.example.knockitUser.Database.CategoryDatabase
import com.example.knockitUser.Model.UserModel
import com.example.knockitUser.R
import com.example.knockitUser.databinding.FragmentCategoryBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException

class CategoryFragment : Fragment() {

    companion object{
        lateinit var categoryMainTitle: TextView
    }
    lateinit var categoryRecyclerView: RecyclerView
    lateinit var subCategoryRecyclerView: RecyclerView
    lateinit var categoryImage: ImageView
    lateinit var cartImage: ImageView
    lateinit var productNotAvailable: TextView
    lateinit var qtySizeText: TextView
    lateinit var qtyBg: LinearLayout
    lateinit var searchView: LinearLayout
    @SuppressLint("UseRequireInsteadOfGet", "MissingInflatedId", "WrongConstant")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_category, container, false)
        categoryMainTitle = view.findViewById(R.id.categoryMainTitle)!!
        categoryRecyclerView = view.findViewById(R.id.category_recyclerView)!!
        subCategoryRecyclerView = view.findViewById(R.id.sub_categoryRecyclerView)!!
        categoryImage = view.findViewById(R.id.category_image)!!
        productNotAvailable = view.findViewById(R.id.products_not_available_text)!!
        qtyBg = view.findViewById(R.id.qty_bg)!!
        qtySizeText = view.findViewById(R.id.qty_size)!!
        cartImage = view.findViewById(R.id.cart_image)!!
        searchView = view.findViewById(R.id.search_view)!!


        var sh: SharedPreferences = activity?.getSharedPreferences("MySharedPref", MODE_APPEND)!!
        var categoryTitle: String= sh.getString("categoryTitle", "").toString();
        var categoryImages: String= sh.getString("categoryImage", "").toString();

        categoryMainTitle.text = categoryTitle
        Glide.with(this).load(categoryImages).into(categoryImage)

        CategoryDatabase.loadCategoryMini(context!!,categoryRecyclerView)
        CategoryDatabase.loadSubCategory(context!!,subCategoryRecyclerView, categoryTitle, productNotAvailable)

        FirebaseFirestore.getInstance()
            .collection("USERS")
            .document(FirebaseAuth.getInstance().uid.toString())
            .addSnapshotListener { querySnapshot: DocumentSnapshot?, e: FirebaseFirestoreException? ->
                querySnapshot?.let {
                    val userModel = it.toObject(UserModel::class.java)

                    if (userModel?.cartSize.equals("0")){
                        qtyBg.visibility = View.GONE
                    }else {
                        qtyBg.visibility = View.VISIBLE
                        qtySizeText.text = userModel?.cartSize
                    }

                }
            }

        cartImage.setOnClickListener {
            startActivity(Intent(context, MyCartActivity::class.java))
        }

        searchView.setOnClickListener {
            startActivity(Intent(context, SearchActivity::class.java))
        }
        return view
    }
}