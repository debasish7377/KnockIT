package com.example.knockitUser.Model

class ProductSpecificationModel(
    var id: String,
    var brand: String,
    var value: String,
    var timeStamp: Long,
) {
    constructor() : this("","","",1)
}