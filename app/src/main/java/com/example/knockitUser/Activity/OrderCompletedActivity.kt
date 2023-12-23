package com.example.knockitUser.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.knockitUser.databinding.ActivityOderCompletedBinding

class OrderCompletedActivity : AppCompatActivity() {

    lateinit var binding: ActivityOderCompletedBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOderCompletedBinding.inflate(layoutInflater)
        val view: View = binding.getRoot()
        setContentView(view)

        var paymentId = intent.getStringExtra("paymentId")
        var totalAmount = intent.getStringExtra("amount")

        binding.amountText.text = totalAmount.toString()
        binding.paymentId.text = "Payment Id - "+paymentId.toString()

        binding.okBtn.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}