package com.example.knockitUser.Model

class NotificationModel(
    var id: String,
    var title : String,
    var description: String,
    var timeStamp: Long,
    var read: String
) {
    constructor() : this("","","",1,"")
}