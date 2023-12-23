package com.example.knockitUser.Activity

import android.Manifest
import android.R.attr.country
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.knockitUser.Fragments.CategoryFragment
import com.example.knockitUser.Fragments.HomeFragment
import com.example.knockitUser.Fragments.MyOderFragment
import com.example.knockitUser.Fragments.ProfileFragment
import com.example.knockitUser.Fragments.WishlistFragment
import com.example.knockitUser.Model.UserModel
import com.example.knockitUser.R
import com.example.knockitUser.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import org.checkerframework.checker.nullness.qual.NonNull
import java.io.IOException
import java.util.Locale


class MainActivity : AppCompatActivity() {
    val HOME_FRAGMENT = 0
    val CATEGORY_FRAGMENT = 1
    val MY_ORDER_FRAGMENT = 2
    val WISHLIST_FRAGMENT = 3
    val PROFILE_FRAGMENT = 4
    var CurrentFragment = -1

    var auth: FirebaseAuth? = null
    var context: Context? = null
    var loadingDialog: Dialog? = null
    lateinit var binding: ActivityMainBinding
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var address: String

    companion object {
        var CurrentFragment1 = -1

        lateinit var frameLayout: FrameLayout
        lateinit var bottomNavigationView: BottomNavigationView
    }

    val ACCESS_FINE_LOCATION = 1

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view: View = binding.getRoot()
        setContentView(view)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                0
            )
        }

        frameLayout = findViewById(R.id.frameLayout)
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        var sh: SharedPreferences = getSharedPreferences("Address", MODE_PRIVATE)!!
        address= sh.getString("address", "").toString();

        setFragment(HomeFragment(), HOME_FRAGMENT)

        ////////////////loading dialog

        ////////////////loading dialog
        loadingDialog = Dialog(this)
        loadingDialog?.setContentView(R.layout.dialog_loading)
        loadingDialog?.setCancelable(false)
        loadingDialog?.window?.setBackgroundDrawable(getDrawable(R.drawable.login_btn_bg))
        loadingDialog?.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        ////////////////loading dialog

        FirebaseFirestore.getInstance().collection("USERS")
            .document(FirebaseAuth.getInstance().uid.toString())
            .get()
            .addOnSuccessListener(OnSuccessListener<DocumentSnapshot> { documentSnapshot ->
                val model: UserModel? = documentSnapshot.toObject(UserModel::class.java)

                if (model?.city.equals("")) {
                    finish()
                }

                if (!model?.address.equals(address)) {
                    Toast.makeText(this, model?.address.toString(), Toast.LENGTH_SHORT).show()
                    //////// Location Update
                    if (ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return@OnSuccessListener
                    }
                    fusedLocationProviderClient.lastLocation
                        .addOnSuccessListener { location ->
                            if (location != null) {
                                try {
                                    val geocoder =
                                        Geocoder(this@MainActivity, Locale.getDefault())
                                    val addresses = geocoder.getFromLocation(
                                        location.latitude,
                                        location.longitude,
                                        1
                                    )

                                        //////// Update Address
                                        val userData: MutableMap<String, Any?> = HashMap()
                                        userData["city"] = addresses!![0].locality
                                        userData["country"] = addresses!![0].countryName
                                        userData["state"] = addresses!![0].adminArea
                                        userData["pincode"] = addresses!![0].postalCode
                                        userData["address"] = addresses!![0].getAddressLine(0)
                                        userData["latitude"] = addresses!![0].latitude
                                        userData["longitude"] = addresses!![0].longitude
                                        userData["timeStamp"] = System.currentTimeMillis()

                                        var sharedPreferences: SharedPreferences =
                                            getSharedPreferences("Address", MODE_PRIVATE)
                                        val myEdit = sharedPreferences.edit()
                                        myEdit.putString(
                                            "address",
                                            addresses!![0].getAddressLine(0)
                                        )
                                        myEdit?.commit()

                                        FirebaseFirestore.getInstance()
                                            .collection("USERS")
                                            .document(FirebaseAuth.getInstance().uid.toString())
                                            .update(userData)
                                            .addOnCompleteListener() { task ->
                                                if (task.isSuccessful) {

                                                }
                                        //////// Update Address
                                    }

                                } catch (e: IOException) {
                                    e.printStackTrace()
                                }
                            }
                        }
                    //////// Location Update
                }

            })

        binding.bottomNavigationView!!.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    setCheckedChancel()
                    invalidateOptionsMenu()
                    setFragment(HomeFragment(), HOME_FRAGMENT)
                    window.setStatusBarColor(
                        ContextCompat.getColor(
                            this@MainActivity,
                            R.color.white
                        )
                    );
                    getWindow().getDecorView()
                        .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
                }

                R.id.category -> {
                    setCheckedChancel()
                    invalidateOptionsMenu()
                    setFragment(CategoryFragment(), CATEGORY_FRAGMENT)
                    item.isChecked = true
                    window.setStatusBarColor(
                        ContextCompat.getColor(
                            this@MainActivity,
                            R.color.white
                        )
                    );
                    getWindow().getDecorView()
                        .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
                }

                R.id.my_oder -> {
                    setCheckedChancel()
                    invalidateOptionsMenu()
                    setFragment(MyOderFragment(), MY_ORDER_FRAGMENT)
                    item.isChecked = true
                    window.setStatusBarColor(
                        ContextCompat.getColor(
                            this@MainActivity,
                            R.color.primary
                        )
                    );
                    getWindow().getDecorView()
                        .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR)
                }

                R.id.wishlist -> {
                    setCheckedChancel()
                    invalidateOptionsMenu()
                    setFragment(WishlistFragment(), WISHLIST_FRAGMENT)
                    item.isChecked = true
                    window.setStatusBarColor(
                        ContextCompat.getColor(
                            this@MainActivity,
                            R.color.white
                        )
                    );
                    getWindow().getDecorView()
                        .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
                }

                R.id.profile -> {
                    setCheckedChancel()
                    invalidateOptionsMenu()
                    setFragment(ProfileFragment(), PROFILE_FRAGMENT)
                    item.isChecked = true
                    window.setStatusBarColor(
                        ContextCompat.getColor(
                            this@MainActivity,
                            R.color.white
                        )
                    );
                    getWindow().getDecorView()
                        .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
                }
            }
            true
        }

        if (isconnected()) {
            binding.frameLayout.setVisibility(View.VISIBLE)
            binding.bottomNavigationView.setVisibility(View.VISIBLE)
            binding.demoLayout.setVisibility(View.GONE)
            setFragment(HomeFragment(), HOME_FRAGMENT)
        } else {
            binding.frameLayout.setVisibility(View.GONE)
            binding.bottomNavigationView.setVisibility(View.GONE)
            binding.demoLayout.setVisibility(View.VISIBLE)

            val alertDialogBuilder = AlertDialog.Builder(this)
            // Setting Alert Dialog Title
            alertDialogBuilder.setTitle("NO INTERNET CONNECTION")
            // Icon Of Alert Dialog
            alertDialogBuilder.setIcon(R.drawable.wifi_off)
            // Setting Alert Dialog Message
            alertDialogBuilder.setMessage("Please check your network connection")
            alertDialogBuilder.setCancelable(false)
            alertDialogBuilder.setPositiveButton(
                "OK"
            ) { dialog, which -> }
            val alertDialog: AlertDialog = alertDialogBuilder.create()
            alertDialog.show()
        }
    }

    private fun setFragment(fragment: Fragment, fragmentNo: Int) {
        if (fragmentNo != CurrentFragment) {
            CurrentFragment = fragmentNo
            val fragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.setCustomAnimations(
                R.anim.slide_from_right,
                R.anim.slideout_from_left
            )
            fragmentTransaction.replace(binding.frameLayout!!.id, fragment)
            fragmentTransaction.commit()
        }
    }

    private fun setCheckedChancel() {
        binding.bottomNavigationView.getMenu().getItem(0).setChecked(false)
        binding.bottomNavigationView.getMenu().getItem(1).setChecked(false)
        binding.bottomNavigationView.getMenu().getItem(2).setChecked(false)
        binding.bottomNavigationView.getMenu().getItem(3).setChecked(false)
        binding.bottomNavigationView.getMenu().getItem(4).setChecked(false)
    }


    private fun isconnected(): Boolean {
        val connectivityManager =
            applicationContext.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo!!
            .isConnectedOrConnecting
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, MainActivity::class.java))
    }
}
