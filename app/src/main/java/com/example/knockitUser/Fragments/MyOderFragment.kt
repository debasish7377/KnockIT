package com.example.knockitUser.Fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.knockitUser.Database.MyOrderDatabase
import com.example.knockitUser.databinding.FragmentMyOderBinding

class MyOderFragment : Fragment() {
    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding: FragmentMyOderBinding = FragmentMyOderBinding.inflate(inflater, container, false)

        MyOrderDatabase.loadMyOder(context!!, binding.myOderRecyclerView)

        return binding.root
    }
}