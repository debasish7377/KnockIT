package com.example.knockitUser.Fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.knockitUser.Activity.MyCartActivity
import com.example.knockitUser.Activity.SearchActivity
import com.example.knockitUser.Adapter.MyWishlistAdapter
import com.example.knockitUser.Database.ProductDetailsDatabase
import com.example.knockitUser.Model.MyWishlistModel
import com.example.knockitUser.Model.UserModel
import com.example.knockitUser.R
import com.example.knockitUser.databinding.FragmentOtpBinding
import com.example.knockitUser.databinding.FragmentWishlistBinding
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot

class WishlistFragment : Fragment() {

    var count: Int = 0
    lateinit var wishlistModel: ArrayList<MyWishlistModel>
    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding: FragmentWishlistBinding = FragmentWishlistBinding.inflate(inflater, container, false)

        ProductDetailsDatabase.loadWishlist(context!!, binding.wishlistRecyclerView)
        FirebaseFirestore.getInstance().collection("USERS")
            .document(FirebaseAuth.getInstance().uid.toString())
            .get()
            .addOnSuccessListener(OnSuccessListener<DocumentSnapshot> { documentSnapshot ->
                val model: UserModel? = documentSnapshot.toObject(UserModel::class.java)

                if (model?.wishlistSize!!.equals("0")) {
                    binding.wishlistEmptyText.visibility = View.VISIBLE
                    binding.wishlistEmptyImg.visibility = View.VISIBLE
                    binding.wishlistRecyclerView.visibility = View.GONE
                } else {
                    binding.wishlistEmptyText.visibility = View.GONE
                    binding.wishlistEmptyImg.visibility = View.GONE
                    binding.wishlistRecyclerView.visibility = View.VISIBLE
                }
            })

        binding.swipeLayout.setOnRefreshListener {
            ProductDetailsDatabase.loadWishlist(context!!, binding.wishlistRecyclerView)
            FirebaseFirestore.getInstance().collection("USERS")
                .document(FirebaseAuth.getInstance().uid.toString())
                .get()
                .addOnSuccessListener(OnSuccessListener<DocumentSnapshot> { documentSnapshot ->
                    val model: UserModel? = documentSnapshot.toObject(UserModel::class.java)

                    if (model?.wishlistSize!!.equals("0")) {
                        binding.wishlistEmptyText.visibility = View.VISIBLE
                        binding.wishlistEmptyImg.visibility = View.VISIBLE
                        binding.wishlistRecyclerView.visibility = View.GONE
                    } else {
                        binding.wishlistEmptyText.visibility = View.GONE
                        binding.wishlistEmptyImg.visibility = View.GONE
                        binding.wishlistRecyclerView.visibility = View.VISIBLE
                    }
                })
            binding.swipeLayout.isRefreshing = false
        }

        binding.myCartBtn.setOnClickListener {
            startActivity(Intent(context, MyCartActivity::class.java))
        }

        FirebaseFirestore.getInstance()
            .collection("USERS")
            .document(FirebaseAuth.getInstance().uid.toString())
            .addSnapshotListener { querySnapshot: DocumentSnapshot?, e: FirebaseFirestoreException? ->
                querySnapshot?.let {
                    val userModel = it.toObject(UserModel::class.java)

                    if (userModel?.cartSize.equals("0")){
                        binding.qtyBg.visibility = View.GONE
                    }else {
                        binding.qtyBg.visibility = View.VISIBLE
                        binding.qtySize.text = userModel?.cartSize
                    }

                }
            }

        binding.searchView.setOnClickListener {
            startActivity(Intent(context, SearchActivity::class.java))
        }

        return binding.root
    }

    fun content() {
        count++
        refresh(5000)
    }

    fun refresh(milliseconds: Int) {

        val handler = Handler()
        handler.postDelayed(object : Runnable {
            override fun run() {
                content()
                // Repeat again after 2 seconds
                handler.postDelayed(this, milliseconds.toLong())
            }
        }, milliseconds.toLong())


    }
}