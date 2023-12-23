package com.example.knockitUser.Database

import android.app.Dialog
import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.knockitUser.Adapter.MyCartAdapter
import com.example.knockitUser.Adapter.MyOrderAdapter
import com.example.knockitUser.Model.MyCartModel
import com.example.knockitUser.Model.UserModel
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import org.json.JSONObject
import java.util.UUID
import javax.xml.transform.ErrorListener
import javax.xml.transform.TransformerException

class MyOrderDatabase {

    companion object {
        fun insertMyOder(
            context: Context,
            paymentId: String,
            loadingDialog: Dialog,
            name: String,
            number: String,
            address: String,
            city: String,
            pincode: String,
            deliveryPrice: String,
        ) {
            var cartModel: ArrayList<MyCartModel> = ArrayList<MyCartModel>()
            var CartAdapter = MyCartAdapter(context!!, cartModel)
            var cartItems: ArrayList<MyCartModel> = ArrayList<MyCartModel>()

            FirebaseFirestore.getInstance()
                .collection("USERS")
                .document(FirebaseAuth.getInstance().uid.toString())
                .collection("MY_CART")
                .orderBy("timeStamp", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(OnSuccessListener<QuerySnapshot> { queryDocumentSnapshots ->
                    for (snapshot in queryDocumentSnapshots) {
                        val model = snapshot.toObject(MyCartModel::class.java)
                        cartItems.add(model)

                        val randomString = UUID.randomUUID().toString().substring(0, 15)
                        for (p in cartItems) {
                            FirebaseFirestore.getInstance()
                                .collection("ORDER")
                                .document(randomString)
                                .set(p)
                                .addOnCompleteListener {

                                    FirebaseFirestore.getInstance()
                                        .collection("USERS")
                                        .document(FirebaseAuth.getInstance().uid.toString())
                                        .collection("MY_CART")
                                        .document(p.productId)
                                        .delete()

                                    FirebaseFirestore.getInstance()
                                        .collection("ORDER")
                                        .orderBy("timeStamp", Query.Direction.ASCENDING)
                                        .get()
                                        .addOnSuccessListener(OnSuccessListener<QuerySnapshot> { queryDocumentSnapshots ->
                                            for (document in queryDocumentSnapshots) {
                                                    val model =
                                                        document.toObject(MyCartModel::class.java)
                                                    cartModel.add(model)

                                                    for (i in cartModel) {
                                                        if (i.payment.equals("")) {
                                                            if (i.uid.equals(FirebaseAuth.getInstance().uid.toString())) {

                                                                FirebaseFirestore.getInstance()
                                                                    .collection("USERS")
                                                                    .document(FirebaseAuth.getInstance().uid.toString())
                                                                    .addSnapshotListener { querySnapshot: DocumentSnapshot?, e: FirebaseFirestoreException? ->
                                                                        querySnapshot?.let {
                                                                            val userModel =
                                                                                it.toObject(
                                                                                    UserModel::class.java
                                                                                )

                                                                            val userData: MutableMap<String, Any?> = HashMap()
                                                                            userData["delivery"] = "Pending"
                                                                            userData["deliveryPrice"] = deliveryPrice
                                                                            userData["payment"] = paymentId
                                                                            userData["id"] = randomString
                                                                            userData["name"] = name
                                                                            userData["number"] = number
                                                                            userData["address"] = address
                                                                            userData["pincode"] = pincode
                                                                            userData["city"] = city
                                                                            userData["userToken"] = userModel?.token?.toFloat()
                                                                            userData["latitude"] = userModel?.latitude?.toFloat()
                                                                            userData["longitude"] = userModel?.longitude?.toFloat()
                                                                            userData["timeStamp"] = System.currentTimeMillis().toLong()

                                                                            FirebaseFirestore.getInstance()
                                                                                .collection("ORDER")
                                                                                .document(
                                                                                    randomString
                                                                                )
                                                                                .update(userData)
                                                                                .addOnCompleteListener {
                                                                                }
                                                                        }
                                                                    }

                                                            }
                                                        }
                                                    }

                                                }
                                        })

                                    sendNotification(
                                        context,
                                        "Order Amount ₹" + p.price, "Please confirm Your Order",
                                        p.storeToken.toString()
                                    )

                                }
                        }
                    }

                    val userData: MutableMap<String, Any?> = HashMap()
                    userData["cartSize"] = "0"
                    FirebaseFirestore.getInstance()
                        .collection("USERS")
                        .document(FirebaseAuth.getInstance().uid.toString())
                        .update(userData)
                        .addOnCompleteListener {

                        }
                })

//            FirebaseFirestore.getInstance()
//                .collection("PRODUCTS")
//                .document(productId)
//                .collection("productSize")
//                .orderBy("timeStamp", Query.Direction.DESCENDING)
//                .get().addOnSuccessListener(OnSuccessListener<QuerySnapshot> { queryDocumentSnapshots ->
//                    for (snapshot in queryDocumentSnapshots) {
//                        val model: SelectQtyModel = snapshot.toObject(SelectQtyModel::class.java)
//                        qtyModel.add(model)
//                    }
//                    qtyAdapter.notifyDataSetChanged()
//                })
        }

        fun loadMyOder(context: Context, myOrderRecyclerView: RecyclerView) {
            var orderModel: ArrayList<MyCartModel> = ArrayList<MyCartModel>()
            var CartAdapter = MyOrderAdapter(context!!, orderModel)
            val layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
            myOrderRecyclerView.layoutManager = layoutManager
            myOrderRecyclerView.adapter = CartAdapter
            var orderItems: ArrayList<MyCartModel> = ArrayList<MyCartModel>()

            FirebaseFirestore.getInstance()
                .collection("ORDER")
                .orderBy("timeStamp", Query.Direction.DESCENDING)
                .addSnapshotListener { querySnapshot: QuerySnapshot?, e: FirebaseFirestoreException? ->
                    querySnapshot?.let {
                        orderItems.clear()
                        for (document in it) {
                            val model = document.toObject(MyCartModel::class.java)
                            orderItems.add(model)

                            orderModel.clear()
                            for (p in orderItems){
                                if (p.uid.equals(FirebaseAuth.getInstance().uid)){
                                    orderModel.add(p)
                                }
                            }
                            CartAdapter.notifyDataSetChanged()
                        }
                    }
                }

//            FirebaseFirestore.getInstance()
//                .collection("PRODUCTS")
//                .document(productId)
//                .collection("productSize")
//                .orderBy("timeStamp", Query.Direction.DESCENDING)
//                .get().addOnSuccessListener(OnSuccessListener<QuerySnapshot> { queryDocumentSnapshots ->
//                    for (snapshot in queryDocumentSnapshots) {
//                        val model: SelectQtyModel = snapshot.toObject(SelectQtyModel::class.java)
//                        qtyModel.add(model)
//                    }
//                    qtyAdapter.notifyDataSetChanged()
//                })
        }

        fun sendNotification(context: Context,name: String?, message: String?, token: String?) {
            val key =
                "Key=AAAA1GKyPQY:APA91bHHqpGYjpQWwlHkB1SKY1HU_MbJHgll3RvthoX6C3CHDl3o86eb54u0ytDkvPtf4Zjr_acmVUKRVjtMwzND3bGg6XGQrzSxQFazinkADaAS4VJYFEOuIE0XtyhD0Cy02DjfPknL"
            var headers = HashMap<String, String>()
            headers["Content-Type"] = "application/json"
            headers["Authorization"] = key

            try {
                val queue: RequestQueue = Volley.newRequestQueue(context)
                val url = "https://fcm.googleapis.com/fcm/send"
                val data = JSONObject()
                data.put("title", name)
                data.put("body", message)
                val notificationData = JSONObject()
                notificationData.put("notification", data)
                notificationData.put("to", token)
                val request: JsonObjectRequest =
                    object : JsonObjectRequest(url, notificationData,
                        Response.Listener<JSONObject> {
                            fun onResponse(response: JSONObject?) {}
                        }, object : ErrorListener, Response.ErrorListener {
                            override fun warning(p0: TransformerException?) {
                                TODO("Not yet implemented")
                            }

                            override fun error(p0: TransformerException?) {
                                TODO("Not yet implemented")
                            }

                            override fun fatalError(p0: TransformerException?) {
                                TODO("Not yet implemented")
                            }

                            override fun onErrorResponse(error: VolleyError?) {
                                TODO("Not yet implemented")
                            }

                        }) {
                        // Override getHeaders() to set custom headers
                        @Throws(AuthFailureError::class)
                        override fun getHeaders(): Map<String, String> {
                            return headers
                        }
                    }
                queue.add(request)
            } catch (ex: java.lang.Exception) {
            }
        }

    }
}