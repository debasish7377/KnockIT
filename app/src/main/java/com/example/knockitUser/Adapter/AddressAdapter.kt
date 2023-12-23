package com.example.knockitUser.Adapter

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.knockitUser.Activity.PaymentActivity
import com.example.knockitUser.Model.AddressModel
import com.example.knockitUser.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AddressAdapter(var context: Context, var model: List<AddressModel>) :
    RecyclerView.Adapter<AddressAdapter.viewHolder>() {

    lateinit var notificationDialog: Dialog
    lateinit var notificationDesc: TextView
    lateinit var newAddressDialog: Dialog

    lateinit var address: EditText
    lateinit var city: EditText
    lateinit var pincode: EditText
    lateinit var phone: EditText
    lateinit var name: EditText
    lateinit var id: TextView
    lateinit var submitBtn: AppCompatButton
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        var view: View =
            LayoutInflater.from(context).inflate(R.layout.item_address, parent, false)
        return viewHolder(view)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {

        holder.address.text = "Address - "+model[position].address
        holder.city.text = "City - "+model[position].city
        holder.phone.text = "Phone - "+model[position].number

        ////////////////loading dialog
        newAddressDialog = Dialog(context)
        newAddressDialog?.setContentView(R.layout.dialog_new_address)
        newAddressDialog?.setCancelable(true)
        newAddressDialog?.window?.setBackgroundDrawable(context.getDrawable(R.drawable.login_btn_bg))
        newAddressDialog?.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        address = newAddressDialog.findViewById(R.id.address)
        city = newAddressDialog.findViewById(R.id.city)
        pincode = newAddressDialog.findViewById(R.id.pinCode)
        phone = newAddressDialog.findViewById(R.id.phone)
        name = newAddressDialog.findViewById(R.id.name)
        id = newAddressDialog.findViewById(R.id.id)
        submitBtn = newAddressDialog.findViewById(R.id.submit_btn)
        ////////////////loading dialog

        holder.itemView.setOnClickListener {
            holder.itemAddress.setBackgroundTintList(
                context.getResources().getColorStateList(R.color.addressClickedBg)
            );

            PaymentActivity.binding_address.text = model[position].address
            PaymentActivity.binding_phone.text = model[position].number
            PaymentActivity.binding_city.text = model[position].city
            PaymentActivity.binding_name.text = model[position].name
            PaymentActivity.binding_pincode.text = model[position].pincode
        }

        holder.editor.setOnClickListener {
            newAddressDialog.show()
            address.text = Editable.Factory.getInstance().newEditable(model[position].address)
            city.text = Editable.Factory.getInstance().newEditable(model[position].city)
            phone.text = Editable.Factory.getInstance().newEditable(model[position].number)
            pincode.text = Editable.Factory.getInstance().newEditable(model[position].pincode)
            name.text = Editable.Factory.getInstance().newEditable(model[position].name)
            id.text = model[position].id
        }

        submitBtn.setOnClickListener {
            newAddressDialog.dismiss()
            PaymentActivity.loadingDialog.show()

            val userData: MutableMap<String, Any?> = HashMap()
            userData["address"] = address.text.toString()
            userData["city"] = city.text.toString()
            userData["pincode"] = pincode.text.toString()
            userData["number"] = phone.text.toString()
            userData["name"] = name.text.toString()

            FirebaseFirestore.getInstance()
                .collection("USERS")
                .document(FirebaseAuth.getInstance().uid.toString())
                .collection("ADDRESS")
                .document(id.text.toString())
                .update(userData)
                .addOnCompleteListener {
                    Toast.makeText(context, "Address Updated", Toast.LENGTH_SHORT).show()
                    PaymentActivity.loadingDialog.dismiss()
                }
        }

        holder.itemView.setOnLongClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Order")
            builder.setMessage("Are you sure to delete this order ?")

            builder.setPositiveButton("Yes") { dialog, which ->
                PaymentActivity.loadingDialog.show()
                FirebaseFirestore.getInstance()
                    .collection("USERS")
                    .document(FirebaseAuth.getInstance().uid.toString())
                    .collection("ADDRESS")
                    .document(model[position].id)
                    .delete()
                    .addOnCompleteListener {
                        Toast.makeText(context, "Address Deleted Successfully", Toast.LENGTH_SHORT).show()
                        PaymentActivity.loadingDialog.dismiss()
                    }
            }
            builder.setNegativeButton("No") { dialog, which ->
                PaymentActivity.loadingDialog.dismiss()
                Toast.makeText(context, "Canceled", Toast.LENGTH_SHORT).show()
            }

            builder.show()

            true
        }

    }

    override fun getItemCount(): Int {
        return model.size
    }

    class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var address: TextView = itemView.findViewById<TextView?>(R.id.address)
        var city: TextView = itemView.findViewById<TextView?>(R.id.cityName)
        var phone: TextView = itemView.findViewById<TextView?>(R.id.phone)
        var itemAddress: ConstraintLayout = itemView.findViewById<ConstraintLayout?>(R.id.item_address)
        var editor: ImageView = itemView.findViewById<ImageView?>(R.id.edit)
    }
}