package com.example.knockitUser.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.ScaleAnimation
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import com.example.knockitUser.Activity.MainActivity
import com.example.knockitUser.Activity.PermissionActivity
import com.example.knockitUser.Activity.RegisterActivity
import com.example.knockitUser.R
import com.example.knockitUser.databinding.FragmentLoginBinding
import com.example.knockitUser.databinding.FragmentSignupBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging

class SignupFragment : Fragment() {

    lateinit var auth: FirebaseAuth
    lateinit var firebaseFirestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding: FragmentSignupBinding =
            FragmentSignupBinding.inflate(inflater, container, false)

        var zoom = ScaleAnimation(0f, 1f, 1f, 1f)
        zoom.setDuration(1000)
        binding.imageView2.startAnimation(zoom)

        auth = FirebaseAuth.getInstance()
        firebaseFirestore = FirebaseFirestore.getInstance()

        binding.haveAccount.setOnClickListener {
            setFragment(LoginFragment())
        }

        binding.btnSignup.setOnClickListener(View.OnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            if (!binding.edEmail.text.isEmpty()) {
                if (!binding.edName.text.toString().isEmpty()) {
                    if (!binding.edNumber.text.toString()
                            .isEmpty() && binding.edNumber.length() == 10
                    ) {
                        if (!binding.edPassword.text.toString()
                                .isEmpty() && binding.edPassword.length() >= 8
                        ) {
                            if (!binding.edConfirmPassword.text.toString()
                                    .isEmpty() && binding.edPassword.text.toString()
                                    .equals(binding.edPassword.text.toString())
                            ) {

                                auth.createUserWithEmailAndPassword(
                                    binding.edEmail.text.toString(),
                                    binding.edPassword.text.toString()
                                )
                                    .addOnCompleteListener() { task ->

                                        if (task.isSuccessful) {
                                            val userData: MutableMap<Any, Any?> =
                                                HashMap()
                                            userData["name"] =
                                                binding.edName.text.toString()
                                            userData["email"] =
                                                binding.edEmail.text.toString()
                                            userData["number"] =
                                                binding.edNumber.text.toString()
                                            userData["uid"] = auth.uid
                                            userData["profile"] = ""
                                            userData["wishlistSize"] = "0"
                                            userData["cartSize"] = "0"
                                            userData["notificationSize"] = "0"
                                            userData["productListSize"] = "0"
                                            userData["city"] = ""
                                            userData["country"] = ""
                                            userData["state"] = ""
                                            userData["pincode"] = ""
                                            userData["address"] = ""
                                            userData["latitude"] = 0
                                            userData["longitude"] = 0
                                            userData["timeStamp"] =
                                                System.currentTimeMillis()
                                            userData["token"] = ""

                                            firebaseFirestore.collection("USERS")
                                                .document(auth.uid.toString())
                                                .set(userData)
                                                .addOnCompleteListener() { task ->
                                                    if (task.isSuccessful) {
                                                        binding.progressBar.visibility =
                                                            View.GONE
                                                        startActivity(
                                                            Intent(
                                                                context,
                                                                PermissionActivity::class.java
                                                            )
                                                        )
                                                        requireActivity().finish()
                                                        Toast.makeText(
                                                            context,
                                                            "Successfully your profile completed",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    } else {
                                                        var error: String? =
                                                            task.exception?.message
                                                        Toast.makeText(
                                                            context,
                                                            error,
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    }
                                                }
                                        } else {
                                            var error: String? = task.exception?.message
                                            Toast.makeText(
                                                context,
                                                error,
                                                Toast.LENGTH_SHORT
                                            )
                                                .show()
                                        }

                                    }

                            } else {
                                binding.edConfirmPassword.error = "Password does not match"
                                binding.progressBar.visibility = View.GONE
                                binding.edConfirmPassword.setText("")
                            }
                        } else {
                            binding.edPassword.error = "Enter Password"
                            binding.progressBar.visibility = View.GONE
                            binding.edPassword.setText("")
                        }
                    } else {
                        binding.edNumber.error = "Enter Phone Number"
                        binding.progressBar.visibility = View.GONE
                        binding.edNumber.setText("")
                    }
                } else {
                    binding.edName.error = "Enter Name"
                    binding.progressBar.visibility = View.GONE
                    binding.edName.setText("")
                }
            } else {
                binding.edEmail.error = "Enter Email"
                binding.progressBar.visibility = View.GONE
                binding.edEmail.setText("")
            }
        })

        return binding.root
    }

    fun setFragment(fragment: Fragment?) {
        val fragmentTransaction: FragmentTransaction =
            activity?.supportFragmentManager?.beginTransaction()!!
        fragmentTransaction.setCustomAnimations(R.anim.slide_from_right, R.anim.slideout_from_left)
        if (fragment != null) {
            fragmentTransaction.replace(RegisterActivity.frameLayout.getId(), fragment)
        }
        fragmentTransaction.commit()
    }
}