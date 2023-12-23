package com.example.knockitUser.Activity

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.example.knockitUser.Database.AddressDatabase
import com.example.knockitUser.Database.MyOrderDatabase
import com.example.knockitUser.Model.BranchModel
import com.example.knockitUser.Model.UserModel
import com.example.knockitUser.R
import com.example.knockitUser.databinding.ActivityPaymentBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import org.json.JSONObject
import java.util.UUID

class PaymentActivity : AppCompatActivity(), PaymentResultListener {

    lateinit var binding: ActivityPaymentBinding
    lateinit var newAddressDialog: Dialog
    lateinit var cityNameDialog: Dialog
    lateinit var paymentDialog: Dialog

    lateinit var hanamkonda: TextView
    lateinit var kazipet: TextView
    lateinit var warangal: TextView

    lateinit var cod: LinearLayout
    lateinit var pay: LinearLayout

    lateinit var address: EditText
    lateinit var city: EditText
    lateinit var pincode: EditText
    lateinit var phone: EditText
    lateinit var name: EditText
    lateinit var submitBtn: AppCompatButton

    companion object {
        lateinit var binding_name: TextView
        lateinit var binding_phone: TextView
        lateinit var binding_city: TextView
        lateinit var binding_pincode: TextView
        lateinit var binding_address: TextView
        lateinit var loadingDialog: Dialog
        lateinit var totalAmount: TextView

        var freeeDeliveryPrice: Long = 0
        var hanamkondaDeliveryPrice: Long = 0
        var kazipetDeliveryPrice: Long = 0
        var warangalDeliveryPrice: Long = 0
        lateinit var totalPrice: String
        lateinit var totalSavedPrice: String
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        val view: View = binding.getRoot()
        setContentView(view)
        binding_name = findViewById(R.id.ed_name)
        binding_phone = findViewById(R.id.phone)
        binding_city = findViewById(R.id.city)
        binding_address = findViewById(R.id.address)
        binding_pincode = findViewById(R.id.pincode)
        totalAmount = findViewById(R.id.total_amount)

        AddressDatabase.loadAddress(this, binding.addressRecyclerView)

        Checkout.preload(applicationContext)
        val co = Checkout()
        // apart from setting it in AndroidManifest.xml, keyId can also be set
        // programmatically during runtime
        co.setKeyID("rzp_test_WxUA1bNiBc0pY8")
        var totalPrice = intent.getStringExtra("totalPrice")

        ////////////////cityNameDialog dialog
        paymentDialog = Dialog(this)
        paymentDialog?.setContentView(R.layout.dialog_payment)
        paymentDialog?.setCancelable(true)
        paymentDialog?.window?.setBackgroundDrawable(getDrawable(R.drawable.qty_bg))
        paymentDialog?.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        cod = paymentDialog.findViewById(R.id.cod)
        pay = paymentDialog.findViewById(R.id.pay)
        ////////////////cityNameDialog dialog

        ////////////////cityNameDialog dialog
        cityNameDialog = Dialog(this)
        cityNameDialog?.setContentView(R.layout.city_name_dialog)
        cityNameDialog?.setCancelable(false)
        cityNameDialog?.window?.setBackgroundDrawable(getDrawable(R.drawable.qty_bg))
        cityNameDialog?.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        hanamkonda = cityNameDialog.findViewById(R.id.hanamkonda)
        kazipet = cityNameDialog.findViewById(R.id.kazipet)
        warangal = cityNameDialog.findViewById(R.id.warangal)
        ////////////////cityNameDialog dialog

        ////////////////loading dialog
        newAddressDialog = Dialog(this)
        newAddressDialog?.setContentView(R.layout.dialog_new_address)
        newAddressDialog?.setCancelable(true)
        newAddressDialog?.window?.setBackgroundDrawable(getDrawable(R.drawable.login_btn_bg))
        newAddressDialog?.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        address = newAddressDialog.findViewById(R.id.address)
        city = newAddressDialog.findViewById(R.id.city)
        pincode = newAddressDialog.findViewById(R.id.pinCode)
        phone = newAddressDialog.findViewById(R.id.phone)
        name = newAddressDialog.findViewById(R.id.name)
        submitBtn = newAddressDialog.findViewById(R.id.submit_btn)
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

        binding.addNewAddress.setOnClickListener {
            newAddressDialog.show()
        }
        binding.selectCity.setOnClickListener {
            cityNameDialog.show()
        }

        FirebaseFirestore.getInstance()
            .collection("USERS")
            .document(FirebaseAuth.getInstance().uid.toString())
            .addSnapshotListener { querySnapshot: DocumentSnapshot?, e: FirebaseFirestoreException? ->
                querySnapshot?.let {
                    val userModel = it.toObject(UserModel::class.java)

                    address.text = Editable.Factory.getInstance().newEditable(userModel?.address.toString())
                    binding.city.text = userModel?.city.toString()
                    binding.pincode.text = userModel?.pincode.toString()
                    phone.text = Editable.Factory.getInstance().newEditable(userModel?.number.toString())
                    name.text = Editable.Factory.getInstance().newEditable(userModel?.name.toString())
                    city.text = Editable.Factory.getInstance().newEditable(userModel?.city.toString())
                    pincode.text = Editable.Factory.getInstance().newEditable(userModel?.pincode.toString())

                    FirebaseFirestore.getInstance()
                        .collection("BRANCHES")
                        .document("kpj8WvaDnObjKDISACzS9m05FYx1")
                        .addSnapshotListener { querySnapshot: DocumentSnapshot?, e: FirebaseFirestoreException? ->
                            querySnapshot?.let {
                                val branchModel = it.toObject(BranchModel::class.java)

                                //Toast.makeText(this, ""+distance.toInt(), Toast.LENGTH_SHORT).show()

                                binding.address.text = userModel?.address
                                binding.edName.text = userModel?.name.toString()
                                binding.edEmail.text = userModel?.email.toString()
                                binding.phone.text = userModel?.number.toString()
                                binding.itemsText.text = userModel?.cartSize + " items"

                                FirebaseFirestore.getInstance()
                                    .collection("HOME")
                                    .document("HomeData")
                                    .collection("DeliveryPrice")
                                    .document("Delivery")
                                    .addSnapshotListener { value, error ->
                                        freeeDeliveryPrice = value?.getLong("FreeDelivery")!!
                                        hanamkondaDeliveryPrice = value?.getLong("Hanamkonda")!!
                                        kazipetDeliveryPrice = value?.getLong("Kazipet")!!
                                        warangalDeliveryPrice = value?.getLong("Warangal")!!

                                        hanamkonda.setOnClickListener {
                                            binding.cityName.text = "Hanamkonda"
                                            cityNameDialog.dismiss()

                                            if (binding.cityName.text.equals("Hanamkonda")) {

                                                if (totalPrice.toString()
                                                        .toInt() >= freeeDeliveryPrice!!
                                                ) {
                                                    binding.delivery.text = "Free Delivery"
                                                    binding.totalAmount.text =
                                                        "Total Amount ₹" + totalPrice

                                                    paymentDialog.show()

                                                    pay.setOnClickListener {
                                                        paymentDialog.dismiss()
                                                        startPayment(
                                                            totalPrice.toString().toInt(),
                                                            binding.edName.text.toString(),
                                                            "https://freesvg.org/img/FaceWoman.png"
                                                        )
                                                    }

                                                    cod.setOnClickListener {
                                                        paymentDialog.dismiss()

                                                        MyOrderDatabase.insertMyOder(
                                                            this,
                                                            "Cash On Delivery",
                                                            loadingDialog,
                                                            binding.edName.text.toString(),
                                                            binding.phone.text.toString(),
                                                            binding.address.text.toString(),
                                                            binding.city.text.toString(),
                                                            binding.pincode.text.toString(),
                                                            "Free Delivery"
                                                        )
                                                        var intent = Intent(this, OrderCompletedActivity::class.java)
                                                        intent.putExtra("paymentId", "Cash On Delivery")
                                                        intent.putExtra("amount", (totalPrice?.toInt()!!).toString())
                                                        startActivity(intent)
                                                        finish()

                                                    }

                                                } else {
                                                    binding.delivery.text =
                                                        "Delivery Price ₹" + hanamkondaDeliveryPrice.toString()
                                                    binding.totalAmount.text =
                                                        "Total Amount ₹" + (totalPrice?.toInt()!! + hanamkondaDeliveryPrice!!)

                                                    paymentDialog.show()

                                                    pay.setOnClickListener {
                                                        paymentDialog.dismiss()
                                                        startPayment(
                                                            totalPrice.toString().toInt() + hanamkondaDeliveryPrice.toInt(),
                                                            binding.edName.text.toString(),
                                                            "https://freesvg.org/img/FaceWoman.png"
                                                        )
                                                    }

                                                    cod.setOnClickListener {
                                                        paymentDialog.dismiss()

                                                        MyOrderDatabase.insertMyOder(
                                                            this,
                                                            "Cash On Delivery",
                                                            loadingDialog,
                                                            binding.edName.text.toString(),
                                                            binding.phone.text.toString(),
                                                            binding.address.text.toString(),
                                                            binding.city.text.toString(),
                                                            binding.pincode.text.toString(),
                                                            "Delivery Price ₹"+hanamkondaDeliveryPrice
                                                        )
                                                        var intent = Intent(this, OrderCompletedActivity::class.java)
                                                        intent.putExtra("paymentId", "Cash On Delivery")
                                                        intent.putExtra("amount", (totalPrice?.toInt()!! + hanamkondaDeliveryPrice.toInt()).toString())
                                                        startActivity(intent)
                                                        finish()

                                                    }

                                                }

                                            }
                                        }
                                        kazipet.setOnClickListener {
                                            binding.cityName.text = "Kazipet"
                                            cityNameDialog.dismiss()

                                            if (binding.cityName.text.equals("Kazipet")) {

                                                if (totalPrice.toString()
                                                        .toInt() >= freeeDeliveryPrice!!
                                                ) {
                                                    binding.delivery.text = "Free Delivery"
                                                    binding.totalAmount.text =
                                                        "Total Amount ₹" + totalPrice

                                                    paymentDialog.show()

                                                    cod.setOnClickListener {
                                                        paymentDialog.dismiss()

                                                        MyOrderDatabase.insertMyOder(
                                                            this,
                                                            "Cash On Delivery",
                                                            loadingDialog,
                                                            binding.edName.text.toString(),
                                                            binding.phone.text.toString(),
                                                            binding.address.text.toString(),
                                                            binding.city.text.toString(),
                                                            binding.pincode.text.toString(),
                                                            "Free Delivery"
                                                        )
                                                        var intent = Intent(this, OrderCompletedActivity::class.java)
                                                        intent.putExtra("paymentId", "Cash On Delivery")
                                                        intent.putExtra("amount", (totalPrice?.toInt()!!).toString())
                                                        startActivity(intent)
                                                        finish()
                                                    }
                                                    pay.setOnClickListener {
                                                        paymentDialog.dismiss()
                                                        startPayment(
                                                            totalPrice.toString().toInt(),
                                                            binding.edName.text.toString(),
                                                            "https://freesvg.org/img/FaceWoman.png"
                                                        )
                                                    }
                                                } else {
                                                    binding.delivery.text =
                                                        "Delivery Price ₹" + kazipetDeliveryPrice.toString()
                                                    binding.totalAmount.text =
                                                        "Total Amount ₹" + (totalPrice?.toInt()!! + kazipetDeliveryPrice!!)

                                                    paymentDialog.show()

                                                    cod.setOnClickListener {
                                                        paymentDialog.dismiss()

                                                        MyOrderDatabase.insertMyOder(
                                                            this,
                                                            "Cash On Delivery",
                                                            loadingDialog,
                                                            binding.edName.text.toString(),
                                                            binding.phone.text.toString(),
                                                            binding.address.text.toString(),
                                                            binding.city.text.toString(),
                                                            binding.pincode.text.toString(),
                                                            "Delivery Price ₹"+kazipetDeliveryPrice
                                                        )
                                                        var intent = Intent(this, OrderCompletedActivity::class.java)
                                                        intent.putExtra("paymentId", "Cash On Delivery")
                                                        intent.putExtra("amount", (totalPrice?.toInt()!! + kazipetDeliveryPrice.toInt()).toString())
                                                        startActivity(intent)
                                                        finish()
                                                    }
                                                    pay.setOnClickListener {
                                                        paymentDialog.dismiss()
                                                        startPayment(
                                                            totalPrice.toString().toInt() + kazipetDeliveryPrice.toInt(),
                                                            binding.edName.text.toString(),
                                                            "https://freesvg.org/img/FaceWoman.png"
                                                        )
                                                    }
                                                }

                                            }
                                        }
                                        warangal.setOnClickListener {
                                            binding.cityName.text = "Warangal"
                                            cityNameDialog.dismiss()

                                            if (binding.cityName.text.equals("Warangal")) {

                                                if (totalPrice.toString()
                                                        .toInt() >= freeeDeliveryPrice!!
                                                ) {
                                                    binding.delivery.text = "Free Delivery"
                                                    binding.totalAmount.text =
                                                        "Total Amount ₹" + totalPrice

                                                    paymentDialog.show()

                                                    cod.setOnClickListener {
                                                        paymentDialog.dismiss()

                                                        MyOrderDatabase.insertMyOder(
                                                            this,
                                                            "Cash On Delivery",
                                                            loadingDialog,
                                                            binding.edName.text.toString(),
                                                            binding.phone.text.toString(),
                                                            binding.address.text.toString(),
                                                            binding.city.text.toString(),
                                                            binding.pincode.text.toString(),
                                                            "Free Delivery"
                                                        )
                                                        var intent = Intent(this, OrderCompletedActivity::class.java)
                                                        intent.putExtra("paymentId", "Cash On Delivery")
                                                        intent.putExtra("amount", (totalPrice?.toInt()!!).toString())
                                                        startActivity(intent)
                                                        finish()
                                                    }
                                                    pay.setOnClickListener {
                                                        paymentDialog.dismiss()
                                                        startPayment(
                                                            totalPrice.toString().toInt(),
                                                            binding.edName.text.toString(),
                                                            "https://freesvg.org/img/FaceWoman.png"
                                                        )
                                                    }

                                                } else {
                                                    binding.delivery.text =
                                                        "Delivery Price ₹" + warangalDeliveryPrice.toString()
                                                    binding.totalAmount.text =
                                                        "Total Amount ₹" + (totalPrice?.toInt()!! + warangalDeliveryPrice!!)

                                                    paymentDialog.show()

                                                    cod.setOnClickListener {
                                                        paymentDialog.dismiss()

                                                        MyOrderDatabase.insertMyOder(
                                                            this,
                                                            "Cash On Delivery",
                                                            loadingDialog,
                                                            binding.edName.text.toString(),
                                                            binding.phone.text.toString(),
                                                            binding.address.text.toString(),
                                                            binding.city.text.toString(),
                                                            binding.pincode.text.toString(),
                                                            "Delivery Price ₹"+warangalDeliveryPrice
                                                        )
                                                        var intent = Intent(this, OrderCompletedActivity::class.java)
                                                        intent.putExtra("paymentId", "Cash On Delivery")
                                                        intent.putExtra("amount", (totalPrice?.toInt()!! + warangalDeliveryPrice.toInt()).toString())
                                                        startActivity(intent)
                                                        finish()
                                                    }
                                                    pay.setOnClickListener {
                                                        paymentDialog.dismiss()
                                                        startPayment(
                                                            totalPrice.toString().toInt() + warangalDeliveryPrice.toInt(),
                                                            binding.edName.text.toString(),
                                                            "https://freesvg.org/img/FaceWoman.png"
                                                        )
                                                    }

                                                }
                                            }
                                        }

                                    }

                                if (binding.cityName.text.equals("Select City")) {
                                    binding.payBtn.setOnClickListener {
                                        Toast.makeText(this, "Select City", Toast.LENGTH_SHORT)
                                            .show()
                                    }
                                }
                            }

                        }
                }
            }

        submitBtn.setOnClickListener {
            newAddressDialog.dismiss()
            loadingDialog.show()

            val randomString = UUID.randomUUID().toString().substring(0, 15)
            val userData: MutableMap<String, Any?> =
                HashMap()
            userData["address"] = address.text.toString()
            userData["city"] = city.text.toString()
            userData["pincode"] = pincode.text.toString()
            userData["number"] = phone.text.toString()
            userData["timeStamp"] = System.currentTimeMillis().toLong()
            userData["name"] = name.text.toString()
            userData["id"] = randomString.toString()

            FirebaseFirestore.getInstance()
                .collection("USERS")
                .document(FirebaseAuth.getInstance().uid.toString())
                .collection("ADDRESS")
                .document(randomString)
                .set(userData)
                .addOnCompleteListener {
                    Toast.makeText(this, "New Address Added", Toast.LENGTH_SHORT).show()
                    loadingDialog.dismiss()
                }
        }

    }

    private fun startPayment(payment: Int, name: String, profile: String) {
        /*
        *  You need to pass the current activity to let Razorpay create CheckoutActivity
        * */
        val activity: Activity = this
        val co = Checkout()

        try {
            val options = JSONObject()
            options.put("name", name)
            options.put("description", "Demoing Charges")
            //You can omit the image option to fetch the image from the dashboard
            options.put("image", profile)
            options.put("theme.color", "#468F27");
            options.put("currency", "INR");
            //options.put("order_id", "order_DBJOWzybf0sJbb");
            options.put("amount", payment * 100)//pass amount in currency subunits

            val retryObj = JSONObject();
            retryObj.put("enabled", true);
            retryObj.put("max_count", 4);
            options.put("retry", retryObj);

            val prefill = JSONObject()
            prefill.put("email", "gaurav.kumar@example.com")
            prefill.put("contact", "9876543210")

            options.put("prefill", prefill)
            co.open(activity, options)
        } catch (e: Exception) {
            Toast.makeText(activity, "Error in payment: " + e.message, Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    override fun onPaymentSuccess(paymentId: String?) {
        FirebaseFirestore.getInstance()
            .collection("USERS")
            .document(FirebaseAuth.getInstance().uid.toString())
            .addSnapshotListener { querySnapshot: DocumentSnapshot?, e: FirebaseFirestoreException? ->
                querySnapshot?.let {
                    val userModel = it.toObject(UserModel::class.java)

                    address.text =
                        Editable.Factory.getInstance().newEditable(userModel?.address.toString())
                    binding.city.text = userModel?.city.toString()
                    binding.pincode.text = userModel?.pincode.toString()
                    phone.text =
                        Editable.Factory.getInstance().newEditable(userModel?.number.toString())
                    name.text =
                        Editable.Factory.getInstance().newEditable(userModel?.name.toString())
                    city.text =
                        Editable.Factory.getInstance().newEditable(userModel?.city.toString())
                    pincode.text =
                        Editable.Factory.getInstance().newEditable(userModel?.pincode.toString())

                    FirebaseFirestore.getInstance()
                        .collection("BRANCHES")
                        .document("kpj8WvaDnObjKDISACzS9m05FYx1")
                        .addSnapshotListener { querySnapshot: DocumentSnapshot?, e: FirebaseFirestoreException? ->
                            querySnapshot?.let {
                                val branchModel = it.toObject(BranchModel::class.java)

                                //Toast.makeText(this, ""+distance.toInt(), Toast.LENGTH_SHORT).show()

                                binding.address.text = userModel?.address
                                binding.edName.text = userModel?.name.toString()
                                binding.edEmail.text = userModel?.email.toString()
                                binding.phone.text = userModel?.number.toString()
                                binding.itemsText.text = userModel?.cartSize + " items"

                                FirebaseFirestore.getInstance()
                                    .collection("HOME")
                                    .document("HomeData")
                                    .collection("DeliveryPrice")
                                    .document("Delivery")
                                    .addSnapshotListener { value, error ->
                                        freeeDeliveryPrice = value?.getLong("FreeDelivery")!!
                                        hanamkondaDeliveryPrice = value?.getLong("Hanamkonda")!!
                                        kazipetDeliveryPrice = value?.getLong("Kazipet")!!
                                        warangalDeliveryPrice = value?.getLong("Warangal")!!

                                        if (binding.cityName.text.equals("Hanamkonda")) {

                                            if (totalPrice.toString()
                                                    .toInt() >= freeeDeliveryPrice!!
                                            ) {
                                                binding.delivery.text = "Free Delivery"
                                                binding.totalAmount.text =
                                                    "Total Amount ₹" + totalPrice

                                                MyOrderDatabase.insertMyOder(
                                                    this,
                                                    "Payment Completed",
                                                    loadingDialog,
                                                    binding.edName.text.toString(),
                                                    binding.phone.text.toString(),
                                                    binding.address.text.toString(),
                                                    binding.city.text.toString(),
                                                    binding.pincode.text.toString(),
                                                    "Free Delivery"
                                                )
                                                var intent =
                                                    Intent(this, OrderCompletedActivity::class.java)
                                                intent.putExtra("paymentId", paymentId)
                                                intent.putExtra(
                                                    "amount",
                                                    binding.totalAmount.text.toString()
                                                )
                                                startActivity(intent)
                                                finish()

                                            } else {
                                                binding.delivery.text =
                                                    "Delivery Price ₹" + hanamkondaDeliveryPrice.toString()
                                                binding.totalAmount.text =
                                                    "Total Amount ₹" + (totalPrice?.toInt()!! + hanamkondaDeliveryPrice!!)

                                                MyOrderDatabase.insertMyOder(
                                                    this,
                                                    "Payment Completed",
                                                    loadingDialog,
                                                    binding.edName.text.toString(),
                                                    binding.phone.text.toString(),
                                                    binding.address.text.toString(),
                                                    binding.city.text.toString(),
                                                    binding.pincode.text.toString(),
                                                    "Delivery Price ₹" + hanamkondaDeliveryPrice
                                                )
                                                var intent =
                                                    Intent(this, OrderCompletedActivity::class.java)
                                                intent.putExtra("paymentId", "Cash On Delivery")
                                                intent.putExtra(
                                                    "amount",
                                                    (totalPrice?.toInt()!! + hanamkondaDeliveryPrice.toInt()).toString()
                                                )
                                                startActivity(intent)
                                                finish()

                                            }

                                        } else if (binding.cityName.text.equals("Kazipet")) {

                                            if (totalPrice.toString()
                                                    .toInt() >= freeeDeliveryPrice!!
                                            ) {
                                                binding.delivery.text = "Free Delivery"
                                                binding.totalAmount.text =
                                                    "Total Amount ₹" + totalPrice

                                                MyOrderDatabase.insertMyOder(
                                                    this,
                                                    "Payment Completed",
                                                    loadingDialog,
                                                    binding.edName.text.toString(),
                                                    binding.phone.text.toString(),
                                                    binding.address.text.toString(),
                                                    binding.city.text.toString(),
                                                    binding.pincode.text.toString(),
                                                    "Free Delivery"
                                                )
                                            } else {
                                                binding.delivery.text =
                                                    "Delivery Price ₹" + kazipetDeliveryPrice.toString()
                                                binding.totalAmount.text =
                                                    "Total Amount ₹" + (totalPrice?.toInt()!! + kazipetDeliveryPrice!!)

                                                MyOrderDatabase.insertMyOder(
                                                    this,
                                                    "Payment Completed",
                                                    loadingDialog,
                                                    binding.edName.text.toString(),
                                                    binding.phone.text.toString(),
                                                    binding.address.text.toString(),
                                                    binding.city.text.toString(),
                                                    binding.pincode.text.toString(),
                                                    "Delivery Price ₹" + kazipetDeliveryPrice
                                                )
                                                var intent =
                                                    Intent(this, OrderCompletedActivity::class.java)
                                                intent.putExtra("paymentId", "Cash On Delivery")
                                                intent.putExtra(
                                                    "amount",
                                                    (totalPrice?.toInt()!! + kazipetDeliveryPrice.toInt()).toString()
                                                )
                                                startActivity(intent)
                                                finish()
                                            }

                                        } else if (binding.cityName.text.equals("Warangal")) {

                                            if (totalPrice.toString()
                                                    .toInt() >= freeeDeliveryPrice!!
                                            ) {
                                                binding.delivery.text = "Free Delivery"
                                                binding.totalAmount.text =
                                                    "Total Amount ₹" + totalPrice

                                                MyOrderDatabase.insertMyOder(
                                                    this,
                                                    "Payment Completed",
                                                    loadingDialog,
                                                    binding.edName.text.toString(),
                                                    binding.phone.text.toString(),
                                                    binding.address.text.toString(),
                                                    binding.city.text.toString(),
                                                    binding.pincode.text.toString(),
                                                    "Free Delivery"
                                                )

                                            } else {
                                                binding.delivery.text =
                                                    "Delivery Price ₹" + warangalDeliveryPrice.toString()
                                                binding.totalAmount.text =
                                                    "Total Amount ₹" + (totalPrice?.toInt()!! + warangalDeliveryPrice!!)

                                                paymentDialog.show()

                                                cod.setOnClickListener {
                                                    paymentDialog.dismiss()

                                                    MyOrderDatabase.insertMyOder(
                                                        this,
                                                        "Payment Completed",
                                                        loadingDialog,
                                                        binding.edName.text.toString(),
                                                        binding.phone.text.toString(),
                                                        binding.address.text.toString(),
                                                        binding.city.text.toString(),
                                                        binding.pincode.text.toString(),
                                                        "Delivery Price ₹" + warangalDeliveryPrice
                                                    )
                                                    var intent = Intent(
                                                        this,
                                                        OrderCompletedActivity::class.java
                                                    )
                                                    intent.putExtra("paymentId", "Cash On Delivery")
                                                    intent.putExtra(
                                                        "amount",
                                                        (totalPrice?.toInt()!! + warangalDeliveryPrice.toInt()).toString()
                                                    )
                                                    startActivity(intent)
                                                    finish()

                                                }
                                            }
                                        }

                                        if (binding.cityName.text.equals("Select City")) {
                                            binding.payBtn.setOnClickListener {
                                                Toast.makeText(
                                                    this,
                                                    "Select City",
                                                    Toast.LENGTH_SHORT
                                                )
                                                    .show()
                                            }
                                        }
                                    }

                            }
                        }
                }

            }
    }

    override fun onPaymentError(p0: Int, p1: String?) {
        Toast.makeText(this, "Payment Again! Payment failed due to " + p1, Toast.LENGTH_SHORT)
            .show()
    }
}