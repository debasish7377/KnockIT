package com.example.knockitUser.Model

class SelectedQtyModel(
    var productImage: String,
    var productTitle: String,
    var productPrice: Int,
    var productCuttedPrice: Int,
    var qty: String,
    var availableQty: Int,
    var id: String,
    var productId: String
) {

    constructor() : this(
        "",
        "",
        1,
        1,
        "",
        1,
        "",
        "",
    )
}