package com.example.juscatamarared.Servicios

import android.content.Intent
import android.os.Bundle
import com.example.juscatamarared.Fragment_Inicial
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val notificationTitle = remoteMessage.notification?.title
        val notificationBody = remoteMessage.notification?.body



        val intent = Intent(applicationContext, Fragment_Inicial::class.java).apply {
            putExtra("accion", "mostrar_noticias")
            putExtra("mostrar_noticias", true)
            putExtra("titulo_notificacion", notificationTitle)
            putExtra("cuerpo_notificacion", notificationBody)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        applicationContext.startActivity(intent)


    }
}