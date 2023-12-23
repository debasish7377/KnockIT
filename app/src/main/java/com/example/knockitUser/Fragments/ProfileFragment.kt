package com.example.knockitUser.Fragments

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.knockitUser.Activity.RegisterActivity
import com.example.knockitUser.Activity.UpdateProfileActivity
import com.example.knockitUser.Model.UserModel
import com.example.knockitUser.R
import com.example.knockitUser.databinding.FragmentProfileBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException


class ProfileFragment : Fragment() {

    private val CHECKEDITEM = "checked_item"

    var preferences: SharedPreferences? = null
    var editor: SharedPreferences.Editor? = null
    var selected: String? = null
    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding: FragmentProfileBinding = FragmentProfileBinding.inflate(inflater, container, false)

        FirebaseFirestore.getInstance()
            .collection("USERS")
            .document(FirebaseAuth.getInstance().uid.toString())
            .addSnapshotListener { querySnapshot: DocumentSnapshot?, e: FirebaseFirestoreException? ->
                querySnapshot?.let {
                    val userModel = it.toObject(UserModel::class.java)

                    try {
                        if (userModel?.profile.equals("")) {
                            Glide.with(context!!).load(R.drawable.avatara)
                                .into(binding.profileImage)
                        } else {
                            Glide.with(context!!).load(userModel?.profile.toString())
                                .placeholder(R.drawable.avatara)
                                .into(binding.profileImage)
                        }
                    }catch (e: Exception){
                        e.printStackTrace()
                    }
                    binding.fullName.text = userModel?.name
                    binding.email.text = userModel?.email

                }
            }

        binding.updateProfile.setOnClickListener {
            startActivity(Intent(context, UpdateProfileActivity::class.java))
        }
        binding.logOut.setOnClickListener{
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(getContext(), RegisterActivity::class.java))
            getActivity()?.finish()
        }
        binding.dayNight.setOnClickListener {
            showDialog1()
        }
        binding.rateUs.setOnClickListener {
            val intent1 = Intent(Intent.ACTION_VIEW)
            intent1.data = Uri.parse(
                "https://play.google.com/store/apps/details?id=com.example.thugsoffacts"
            )
            intent1.setPackage("com.android.vending")
            startActivity(intent1)
        }
        return binding.root
    }

    @SuppressLint("UseRequireInsteadOfGet")
    private fun showDialog1() {
        val thems = this.resources.getStringArray(R.array.Them)
        val builder = MaterialAlertDialogBuilder(context!!)
        builder.setTitle("Select them")
        builder.setSingleChoiceItems(R.array.Them,0,
            DialogInterface.OnClickListener { dialog, i ->
                selected = thems[i]
            })
        builder.setPositiveButton("Ok") { dialog, i ->
            if (selected == null) {
                selected = thems[0]
            }
            when (selected) {
                "System Default" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                "Light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                "Dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                else -> {}
            }

        }
        builder.setNegativeButton(
            "Cancel"
        ) { dialog, which -> dialog.dismiss() }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}