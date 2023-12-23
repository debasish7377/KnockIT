package com.example.knockitUser.Model

class ProductReviewModel(
    var id: String,
    var review : String,
    var userId: String,
    var timeStamp: Long
) {
    constructor() : this("","","",1)
}