package com.tripod.fire.models


data class MessageModel(
    var senderID:String?="",
    var receiverID:String?="",
    var message:String?="",
    var time:Long?=0
)