package com.tripod.fire.notification

data class PushNotification(
    val data:NotificationData,
    val to:String?=""
)
