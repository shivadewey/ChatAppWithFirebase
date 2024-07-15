package com.tripod.fire.notification.api

import com.tripod.fire.notification.PushNotification
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiInterface {

    @Headers("Content-Type:application/json",
        "Authorization:key=AAAAlwq4W1s:APA91bEAFJGT2ECC4jNOU_ioO3H90IDp5BEvOqLJwgkBfQGY2RUj_WJ8llKfpZMZ4CJemyFW_OIMNRhr4Fgyn6mOHieSo2fV2EESKbWQRgX_ECgeZYkIpn1IIzqZ4cE-z7IF35WE_Qim")

    @POST("fcm/send")
    fun sendNotification(@Body notification:PushNotification): Call<PushNotification>
}