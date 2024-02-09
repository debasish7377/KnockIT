package com.example.knockitUser.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
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

        setSupportActionBar(binding.toolbar9);

        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar()?.setDisplayHomeAsUpEnabled(true);
            getSupportActionBar()?.setDisplayShowHomeEnabled(true);
        }

        NotificationDatabase.loadNotification(this, binding.notificationRecyclerView)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // handle arrow click here
        if (item.itemId == android.R.id.home) {
            finish() // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item)
    }
}