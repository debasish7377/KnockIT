package com.example.knockitUser.Model

class AddressModel(
    var id: String,
    var address : String,
    var pincode: String,
    var city: String,
    var number: String,
    var timeStamp: Long,
    var name: String
) {
    constructor() : this("","","","","",0, "")
}