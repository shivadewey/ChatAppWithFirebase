package com.tripod.fire.models

data class UserData(
    val uid:String?="",
    val name:String?="",
    val email:String?="",
    val password:String?="",
    val userImage:String?="",
    var online:Boolean=false,
    var lastSeen: Long?=0,
    val fcmToken:String?=""
)
