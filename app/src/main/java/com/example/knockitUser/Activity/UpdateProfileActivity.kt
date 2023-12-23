package com.example.knockitUser.Activity

import android.Manifest
import android.app.PendingIntent.getActivity
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.knockitUser.Model.UserModel
import com.example.knockitUser.R
import com.example.knockitUser.databinding.ActivityUpdateProfileBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import java.io.IOException
import java.security.AccessController.getContext
import java.util.Locale


class UpdateProfileActivity : AppCompatActivity() {

    lateinit var binding: ActivityUpdateProfileBinding
    lateinit var filePath: Uri
    var updatePhoto: Boolean = false
    val ACCESS_FINE_LOCATION = 1
    val REQUEST_PERMISSION_SETTING = 12
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateProfileBinding.inflate(layoutInflater)
        val view: View = binding.getRoot()
        setContentView(view)

        FirebaseFirestore.getInstance()
            .collection("USERS")
            .document(FirebaseAuth.getInstance().uid.toString())
            .addSnapshotListener { querySnapshot: DocumentSnapshot?, e: FirebaseFirestoreException? ->
                querySnapshot?.let {
                    val userModel = it.toObject(UserModel::class.java)

                    try {
                        if (userModel?.profile.equals("")) {
                            Glide.with(this).load(R.drawable.avatara)
                                .into(binding.profileImage)
                        } else {
                            Glide.with(this).load(userModel?.profile.toString())
                                .placeholder(R.drawable.avatara)
                                .into(binding.profileImage)
                        }
                    }catch (e: Exception){
                        e.printStackTrace()
                    }
                    binding.name.text = Editable.Factory.getInstance().newEditable(userModel?.name.toString())
                    binding.email.text = userModel?.email
                    binding.phone.text = userModel?.number

                }
            }

        binding.profileImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(
                Intent.createChooser(intent, "Select Picture"),
                1
            )
        }

        binding.updateBtn.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            binding.updateBtn.visibility = View.GONE
            if (!binding.name.text.equals("")) {
                if (updatePhoto) {

                    var reference: StorageReference =
                        FirebaseStorage.getInstance().getReference()
                            .child("profiles").child(
                                FirebaseAuth.getInstance().getUid()
                                    .toString()
                            );
                    reference.putFile(filePath).addOnCompleteListener {
                        reference.downloadUrl.addOnSuccessListener { profileImage ->
                            val userData: MutableMap<String, Any?> =
                                HashMap()
                            userData["name"] =
                                binding.name.text.toString()
                            userData["profile"] = profileImage

                            FirebaseFirestore.getInstance()
                                .collection("USERS")
                                .document(FirebaseAuth.getInstance().uid.toString())
                                .update(userData)
                                .addOnCompleteListener {
                                    binding.progressBar.visibility =
                                        View.GONE
                                    binding.updateBtn.visibility = View.VISIBLE
                                    Toast.makeText(
                                        this,
                                        "Profile Updated Successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        }
                    }
                } else {

                    val userData: MutableMap<String, Any?> =
                        HashMap()
                    userData["name"] =
                        binding.name.text.toString()
                    FirebaseFirestore.getInstance()
                        .collection("USERS")
                        .document(FirebaseAuth.getInstance().uid.toString())
                        .update(userData)
                        .addOnCompleteListener {
                            binding.progressBar.visibility =
                                View.GONE
                            binding.updateBtn.visibility = View.VISIBLE
                            Toast.makeText(
                                this,
                                "Profile Updated Successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                }
            }else{
                Toast.makeText(this, "Enter Name", Toast.LENGTH_SHORT).show()
                binding.progressBar.visibility = View.GONE
                binding.updateBtn.visibility = View.VISIBLE
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            //var bitmapImage: Bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data?.data!!)
            filePath = data?.data!!
            updatePhoto = true
//
//            var byteArrOutputStream = ByteArrayOutputStream()
//            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 50 , byteArrOutputStream)
//            var bytesArray: ByteArray = byteArrOutputStream.toByteArray()
//            compressedImage = BitmapFactory.decodeByteArray(bytesArray, 0 , bytesArray.size)
//
//            filePath = MediaStore.Images.Media.insertImage(this.contentResolver, compressedImage,"erg","reg").toUri()
            Glide.with(this).load(filePath).into(binding.profileImage)
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == ACCESS_FINE_LOCATION) {
            for (i in permissions.indices) {
                val per = permissions[i]
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    val showretional = shouldShowRequestPermissionRationale(per!!)
                    if (!showretional) {
                        val builder = AlertDialog.Builder(this)
                        builder.setTitle("App Permission")
                            .setMessage(
                                """
                            For showing product, You nust allow to access location on your device
                            
                            Now follow the below steps
                            
                            Open setting for the bellow button
                            Click on permission
                            Allow access to for location
                            """.trimIndent()
                            )
                            .setPositiveButton(
                                "Open settings"
                            ) { dialogInterface, i ->
                                val intent =
                                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                val uri = Uri.fromParts(
                                    "package",
                                    packageName, null
                                )
                                intent.data = uri
                                startActivityForResult(
                                    intent,
                                    REQUEST_PERMISSION_SETTING
                                )
                            }.create().show()
                    } else {
                        ActivityCompat.requestPermissions(
                            this@UpdateProfileActivity, arrayOf<String>(
                                Manifest.permission.READ_EXTERNAL_STORAGE
                            ), ACCESS_FINE_LOCATION
                        )
                    }
                }
            }
        }
    }

}