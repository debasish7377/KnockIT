package com.example.knockitUser.Model

class BannerModel(
    var id: String,
    var bannerImage : String,
    var bannerBackground: String,
    var productId: String,
    var timeStamp: String,
    var subCategory: String,
    var discount: Long
) {
    constructor() : this("","","","","","",1)
}