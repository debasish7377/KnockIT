package com.example.knockitUser.Model

class CouponsModel(
    var id: String,
    var productAbovePrice: Int,
    var timeStamp: Long,
    var validTime: Long,
    var price: Int,
    var title: String,
    var subject: String,

) {
     constructor(): this(
         "",
         1,
         1,
         1,
         1,
         "",
         ""
     )
}