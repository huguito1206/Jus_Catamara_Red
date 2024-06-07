package com.example.juscatamarared

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.hardware.biometrics.BiometricManager
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.juscatamarared.Clases.EntornoDeDatos
import com.example.juscatamarared.Clases.SesionBiometrica
import com.example.juscatamarared.Clases.UsuarioClass
import com.example.juscatamarared.Servicios.NoticiasService
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONException


class SplashStart : AppCompatActivity() {

    var dialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_start)

        val window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = this.resources.getColor(R.color.ColorProgress)

        //inicio los servicios noticias
        val intent = Intent(this, NoticiasService::class.java)
        //startService(intent)


        dialog = Dialog(this@SplashStart)
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.setContentView(R.layout.progress)
        dialog!!.setCanceledOnTouchOutside(false)
        dialog!!.show()

        FirebaseMessaging.getInstance().setAutoInitEnabled(true)
        FirebaseMessaging.getInstance().getToken()
            .addOnCompleteListener(OnCompleteListener<String?> { task ->
                if (!task.isSuccessful) {
                    return@OnCompleteListener
                }
                var token:String = task.result.toString()
                //GlobalVariables.setMiVariableGlobal(token)

                EntornoDeDatos.VariablesGlobales.setVariableToken(token)
                //Toast.makeText(this,"${token}", Toast.LENGTH_SHORT).show();
            })

        object : CountDownTimer(4500, 1500) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                val intent = Intent(this@SplashStart, Login::class.java)
                startActivity(intent)
                finish()
                dialog!!.dismiss()
            }
        }.start()


    }





}