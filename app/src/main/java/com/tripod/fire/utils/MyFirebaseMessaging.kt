package com.tripod.fire.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.tripod.fire.R
import com.tripod.fire.fragments.ChatFragment
import kotlin.random.Random

class MyFirebaseMessaging:FirebaseMessagingService() {
    private val channelID="fireapp"
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val intent=Intent(this,ChatFragment::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val  manager=getSystemService(Context.NOTIFICATION_SERVICE)
        createNotificationChannel(manager as NotificationManager)

        val intent1=PendingIntent.getActivities(this,0, arrayOf(intent),
            PendingIntent.FLAG_IMMUTABLE)

        val largeIcon=BitmapFactory.decodeResource(applicationContext.resources,R.drawable.ic_profile)

        val notification=NotificationCompat.Builder(this,channelID)
            .setContentTitle(remoteMessage.data["title"])
            .setContentText(remoteMessage.data["message"])
            .setSmallIcon(R.drawable.ic_dots)
            .setLargeIcon(largeIcon)
            .setAutoCancel(true)
            .setContentIntent(intent1)
            .build()

        manager.notify(Random.nextInt(), notification)
    }


    private fun createNotificationChannel(manager:NotificationManager){
        val channel=NotificationChannel(channelID,"fireappchat",NotificationManager.IMPORTANCE_HIGH)
        channel.description="New Message"
        channel.enableLights(true)

        manager.createNotificationChannel(channel)
    }

}