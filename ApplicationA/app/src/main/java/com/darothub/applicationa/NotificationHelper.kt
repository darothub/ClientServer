package com.darothub.applicationa

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.darothub.applicationa.utils.Constants

class NotificationHelper(val context: Context) {

    fun getNotificationManager() =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    @RequiresApi(Build.VERSION_CODES.O)
    private  fun createNotificationChannel() {
        val nChannel= NotificationChannel(Constants.notificationId, "CHANNEL_NAME", NotificationManager.IMPORTANCE_LOW)
        getNotificationManager().createNotificationChannel(nChannel)
    }

    fun createNotification(message:String) =
        NotificationCompat.Builder(context, Constants.notificationId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("New Message")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .build()

    @RequiresApi(Build.VERSION_CODES.O)
    fun sendNotification(notification: Notification){
        createNotificationChannel()
        getNotificationManager().notify(100, notification)
    }
}