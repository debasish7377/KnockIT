package com.example.knockitUser.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.knockitUser.Database.NotificationDatabase
import com.example.knockitUser.R
import com.example.knockitUser.databinding.ActivityNotificationBinding
import com.example.knockitUser.databinding.ActivityUpdateProfileBinding

class NotificationActivity : AppCompatActivity() {

    lateinit var binding: ActivityNotificationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        val view: View = binding.getRoot()
        setContentView(view)

        NotificationDatabase.loadNotification(this, binding.notificationRecyclerView)
    }
}