package com.example.knockitUser.Fragments

import android.content.Intent
import android.os.Bundle
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.knockitUser.Activity.MainActivity
import com.example.knockitUser.Activity.RegisterActivity
import com.example.knockitUser.R
import com.example.knockitUser.databinding.FragmentLoginBinding
import com.example.knockitUser.databinding.FragmentPasswordResetBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.collection.LLRBNode.Color


class PasswordResetFragment : Fragment() {

    lateinit var auth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding: FragmentPasswordResetBinding = FragmentPasswordResetBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()

        binding.resetButton.setOnClickListener(View.OnClickListener {
            binding.emailText.setText("Processing.....")
            binding.emailText.setTextColor(resources.getColor(com.example.knockitUser.R.color.black))
            binding.resetButton.setTextColor(resources.getColor(com.example.knockitUser.R.color.gray))
            binding.resetButton.visibility = View.GONE
            binding.progressBar.visibility = View.VISIBLE
            auth.sendPasswordResetEmail(binding.resetEmail.getText().toString())
                .addOnCompleteListener(OnCompleteListener<Void?> { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(context, "successfully reset link sent to your registered email", Toast.LENGTH_SHORT).show()
                        binding.emailText.setText("successfully reset link sent to your registered email")
                        binding.emailText.setTextColor(resources.getColor(com.example.knockitUser.R.color.green))
                        startActivity(Intent(context, RegisterActivity::class.java))
                    } else {
                        val error = task.exception!!.message
                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                        binding.emailText.setText(error)
                        binding.emailText.setTextColor(resources.getColor(com.example.knockitUser.R.color.red))
                    }
                    binding.resetButton.setEnabled(true)
                    binding.resetButton.setTextColor(resources.getColor(com.example.knockitUser.R.color.gray))
                    binding.resetButton.visibility = View.VISIBLE
                    binding.progressBar.visibility = View.GONE
                })
        })

        return binding.root
    }
}