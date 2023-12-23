package com.example.knockitUser.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.view.animation.ScaleAnimation
import androidx.appcompat.app.AppCompatActivity
import com.example.knockitUser.R
import com.example.knockitUser.databinding.ActivitySplashscreenBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging


class SplashscreenActivity : AppCompatActivity() {
    lateinit var firebaseAuth: FirebaseAuth

    lateinit var binding: ActivitySplashscreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashscreenBinding.inflate(layoutInflater)
        val view: View = binding.getRoot()
        setContentView(view)

        val anim = RotateAnimation(0f, 350f, 15f, 15f)
        anim.interpolator = LinearInterpolator()
        anim.repeatCount = Animation.INFINITE
        anim.duration = 700

        var animSlide = AnimationUtils.loadAnimation(getApplicationContext(),
            R.anim.slide);

        var zoom = ScaleAnimation(0f, 1f, 1f, 1f)
        zoom.setDuration(1000)

// Start the animation like this
        binding.imageView3.startAnimation(zoom)

        firebaseAuth = FirebaseAuth.getInstance()
        val time: Thread = object : Thread() {
            override fun run() {
                try {
                    sleep(2000)
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    if (firebaseAuth.currentUser == null) {
                        startActivity(Intent(this@SplashscreenActivity, RegisterActivity::class.java))
                    } else {

                        FirebaseMessaging.getInstance().token.addOnCompleteListener(
                            OnCompleteListener { task ->
                                if (!task.isSuccessful) {
                                    Log.w("FCM", "Fetching FCM registration token failed", task.exception)
                                    return@OnCompleteListener
                                }

                                val token = task.result
                                val sharedPreferences =
                                    getSharedPreferences("MySharedPref", MODE_PRIVATE)

                                val myEdit = sharedPreferences.edit()

                                myEdit.putString("token", token)

                                myEdit.commit()
                                //Toast.makeText(this@SplashscreenActivity, token.toString(), Toast.LENGTH_SHORT).show()

                                FirebaseMessaging.getInstance().subscribeToTopic("your_topic_name")
                                FirebaseMessaging.getInstance().setAutoInitEnabled(true)
                                FirebaseFirestore.getInstance().collection("USERS")
                                    .document(firebaseAuth.currentUser?.uid.toString())
                                    .update(
                                        "Last seen", FieldValue.serverTimestamp(),
                                        "token",token
                                    )
                                    .addOnCompleteListener { }
                                startActivity(Intent(this@SplashscreenActivity, PermissionActivity::class.java))
                                finish()
                            })
                    }
                }
            }
        }
        time.start()
    }

    override fun onStart() {
        super.onStart()
        val userData: MutableMap<String, Any?> = HashMap()
        userData["productListSize"] = 0.toString()
        FirebaseFirestore.getInstance().collection("USERS")
            .document(FirebaseAuth.getInstance().uid.toString())
            .update(userData)
    }
}