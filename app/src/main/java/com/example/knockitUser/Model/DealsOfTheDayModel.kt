package com.example.knockitUser.Model

class DealsOfTheDayModel(
    var id: String,
    var productId: String,
    var timeStamp: Long
) {
    constructor() : this("","",1)
}