package com.example.knockitUser.Fragments

import android.R.attr.name
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.knockitUser.Activity.RegisterActivity
import com.example.knockitUser.R
import com.example.knockitUser.databinding.FragmentPhoneLoginBinding


class PhoneLoginFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding: FragmentPhoneLoginBinding = FragmentPhoneLoginBinding.inflate(inflater, container, false)

        binding.getOtpBtn.setOnClickListener {
            if (!binding.edNumber.text.isEmpty()  && binding.edNumber.length() == 10) {
                val sharedPreferences: SharedPreferences? = context?.getSharedPreferences("MySharedPref", MODE_PRIVATE)
                val myEdit = sharedPreferences?.edit()
                myEdit?.putString("number", "+91"+binding.edNumber.text.toString())
                myEdit?.commit()
                setFragment(OtpFragment())
            }else{
                binding.edNumber.error = "Enter Number"
            }
        }

        return binding.root
    }

    fun setFragment(fragment: Fragment?) {
        val fragmentTransaction: FragmentTransaction = activity?.supportFragmentManager?.beginTransaction()!!
        fragmentTransaction.setCustomAnimations(R.anim.slide_from_right, R.anim.slideout_from_left)
        if (fragment != null) {
            fragmentTransaction.replace(RegisterActivity.frameLayout.getId(), fragment)
        }
        fragmentTransaction.commit()
    }
}