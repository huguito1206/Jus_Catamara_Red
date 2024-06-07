package com.example.juscatamarared

import android.app.Dialog
import android.app.PendingIntent.OnFinished
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.hardware.biometrics.BiometricManager
import android.os.Bundle
import android.os.Handler
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.Fragment
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.juscatamarared.Clases.EntornoDeDatos
import com.example.juscatamarared.Clases.MainThreadExecutor
import com.example.juscatamarared.Clases.SesionBiometrica
import com.example.juscatamarared.Clases.UsuarioClass
import com.example.juscatamarared.Fragmentos.Apps.AppActivity_ListadoTramites
import com.example.juscatamarared.Fragmentos.Apps.AppActivity_TramiteFormulario
import com.example.juscatamarared.Servicios.NoticiasService
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.json.JSONException
import java.util.HashMap


class Fragment_Inicial : AppCompatActivity() {


    val noticiasFragment = com.example.juscatamarared.Fragmentos.Fragment_Noticias()
    val ReciboSueldoFragment = com.example.juscatamarared.Fragmentos.FragmentRecibosSueldo()
    val AppsFragment = com.example.juscatamarared.Fragmentos.Fragment_Apps()
    val MiPerfilFragment = com.example.juscatamarared.Fragmentos.Fragment_MiPerfil()

    var dialog: Dialog?  = null

    val sesionBiometrica = SesionBiometrica.SesionBiometricaManager.getSesionBiometrica()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_inicial)


        val window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = this.resources.getColor(R.color.ColorProgress)




        //aca verifico si es que el celular tiene lector de huellas
        val biometricManager = androidx.biometric.BiometricManager.from(this)
        when (biometricManager.canAuthenticate()) {
            androidx.biometric.BiometricManager.BIOMETRIC_SUCCESS -> {
                //println("El dispositivo tiene un lector biométrico.")

                //aca pregunto si quiere activar la huella

                //1) ya lo tiene activo?


                if (sesionBiometrica?.biometria_activada == 0 || sesionBiometrica?.biometria_activada==null){
                    DialogoDeHuella("Activar autenticación por huella digital")
                }else{
                    if (sesionBiometrica?.usuario_id != 0 && sesionBiometrica?.usuario_id != UsuarioClass.UsuarioClassManager.getUsuarioClass()?.usuario_id){
                        DialogoDeHuella("Inicio sesion en nuevo dispositivo")
                    }
                }
            }
            androidx.biometric.BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                //println("El dispositivo no tiene un lector biométrico.")
            }
            androidx.biometric.BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                //println("El lector biométrico no está disponible actualmente.")
            }
            androidx.biometric.BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                //println("No hay datos biométricos registrados en el dispositivo.")
            }
        }












        val delayMillis: Long = 4000
        val handler = Handler()
        handler.postDelayed({
            val intentNoti = Intent(this, NoticiasService::class.java)
            stopService(intentNoti)
            //Toast.makeText(this, "Se detuvo servicio noticias", Toast.LENGTH_SHORT).show()
        }, delayMillis)

        val navigation:BottomNavigationView = findViewById(R.id.bottom_navigation)
        navigation.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);








        val User = UsuarioClass.UsuarioClassManager.getUsuarioClass()

        if (User?.usuario_id == null){
            val intent = Intent(this, Login::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }else{
            //acciones para mostrar pantallas de acuerdo a accion previa
            if ("mostrar_noticias" == this.intent.getStringExtra("accion")) {
                cargarFragment(noticiasFragment)
            }
            if ("mostrar_apps" == this.intent.getStringExtra("accion")) {
                cargarFragment(AppsFragment)
            }

            if ("mostrar_MiPerfil" == this.intent.getStringExtra("accion")) {
                cargarFragment(MiPerfilFragment)
            }
        }





    }


    override fun onResume() {
        super.onResume()
        intent?.let { onNewIntent(it) }
    }



    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        if (intent != null) {
            val tituloNotificacion = intent.getStringExtra("titulo_notificacion")
            val cuerpoNotificacion = intent.getStringExtra("cuerpo_notificacion")

            if (tituloNotificacion != null && cuerpoNotificacion != null) {
                // Mostrar un diálogo con los datos de la notificación
                mostrarDialogoNotificacion(tituloNotificacion, cuerpoNotificacion)
            }
        }
    }




    private fun  DialogoDeHuella(Text:String){

        // Mostrar un diálogo de confirmación al usuario
        val builder = AlertDialog.Builder(this)
        //builder.setTitle("Activar autenticación por huella digital")
        builder.setTitle(Text)
        builder.setMessage("¿Desea activar la autenticación por huella digital para una mayor seguridad?")

        // Botón para activar la autenticación por huella digital
        builder.setPositiveButton("Sí") { dialog, _ ->
            // Aquí puedes iniciar el proceso para activar la autenticación por huella digital

            val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle("Ingresá con tu huella")
                .setNegativeButtonText("Cancelar")
                .build()

            val biometricPrompt = BiometricPrompt(this, MainThreadExecutor(), object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    if (errorCode == BiometricPrompt.ERROR_USER_CANCELED) {
                        // El usuario ha cancelado la autenticación
                        // Aquí puedes manejar la lógica correspondiente
                        //Toast.makeText(applicationContext, "entra al cancelar", Toast.LENGTH_SHORT).show()

                    } else {
                        dialog!!.dismiss()
                        // Otro tipo de error durante la autenticación
                        // Puedes mostrar un mensaje de error o realizar otras acciones apropiadas
                    }
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    // Autenticación exitosa
                    //Toast.makeText(applicationContext, "mando metodo update", Toast.LENGTH_SHORT).show()
                    UpdateTokenClaveBiometrico("ActivarBiometria",EntornoDeDatos.VariablesGlobales.VariableToken.toString())

                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    // Manejar fallos de autenticación
                    //Toast.makeText(applicationContext, "Huella incorrecta", Toast.LENGTH_SHORT).show()
                }
            })
            biometricPrompt.authenticate(promptInfo)

            dialog.dismiss()

        }

    //Botón para cancelar la acción
        builder.setNegativeButton("No") { dialog, _ ->
            //aca desabilito para que no salga el cartel a acada rato de pedir la biometria
            val sesionBiometrica = SesionBiometrica.SesionBiometricaManager.getSesionBiometrica()
            sesionBiometrica?.biometria_activada = 2

            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }


    override fun onPause() {
        super.onPause()
        dialog?.dismiss()
    }

    override fun onDestroy() {
        super.onDestroy()
        dialog?.dismiss()
    }


    private fun UpdateTokenClaveBiometrico(Accion: String,Token:String) {
        val User = UsuarioClass.UsuarioClassManager.getUsuarioClass()
        dialog = Dialog(this)
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.setContentView(R.layout.progress_dialog)
        dialog!!.setCanceledOnTouchOutside(false)
        dialog!!.show()

        var request: StringRequest = object : StringRequest(
            Method.POST, EntornoDeDatos().URL,
            Response.Listener { response ->
                dialog?.dismiss()
                //Toast.makeText(this@Activity_NuevaNoticia, ""+response, Toast.LENGTH_SHORT).show();
                try {
                    if (response == "HuellaActiva") {
                        Toast.makeText(this@Fragment_Inicial,"Acción Exitosa", Toast.LENGTH_SHORT).show();
                        sesionBiometrica?.biometria_activada == 1 //aca cambio la variable para que no me salga el cartel pidiendo de nuevo la activacion por huella
                        dialog!!.dismiss()
                    }
                } catch (e: JSONException) {
                    //Toast.makeText(activity,"algo esta mal",Toast.LENGTH_SHORT).show();
                    e.printStackTrace()
                }
                dialog?.dismiss()
            },
            Response.ErrorListener {
                dialog?.dismiss()
                Toast.makeText(this@Fragment_Inicial, "REVISE CONEXIÓN A INTERNET", Toast.LENGTH_LONG).show();
            }
        ) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String>? {
                val params: MutableMap<String, String> = HashMap()
                params["Accion"] = Accion
                params["Token"] = Token
                params["usuario_id"] = User?.usuario_id.toString()
                return params
            }
        }
        val requestQueue = Volley.newRequestQueue(this@Fragment_Inicial)
        requestQueue.add(request)
    }




    private fun mostrarDialogoNotificacion(titulo: String, cuerpo: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("$titulo")
        builder.setMessage("$cuerpo")

        // Botón positivo (OK)
        builder.setPositiveButton("Ir a Solicitudes") { dialog, _ ->
            //dialog.dismiss()
            val intent = Intent(this@Fragment_Inicial, AppActivity_ListadoTramites::class.java)
            startActivity(intent)
            finish()
            dialog.dismiss()
        }

        // Botón negativo (Cancelar)
        builder.setNegativeButton("Cancelar") { dialog, _ ->
            // Puedes manejar la lógica del botón Cancelar aquí
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }





      private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
       when (item.itemId) {

            R.id.Inicio -> {
                cargarFragment(noticiasFragment)

                return@OnNavigationItemSelectedListener true
            }

           R.id.Recibos -> {
               cargarFragment(ReciboSueldoFragment)
               return@OnNavigationItemSelectedListener true
           }

           R.id.Agregar -> {
               val intent = Intent(this@Fragment_Inicial, Activity_NuevaNoticia::class.java)
               //val intent = Intent(this@Fragment_Inicial, Nueva_Noticia_pruebas::class.java)
               startActivity(intent)
               return@OnNavigationItemSelectedListener true
           }

           R.id.Apps -> {
               cargarFragment(AppsFragment)
               return@OnNavigationItemSelectedListener true
           }

           R.id.Perfil-> {
               cargarFragment(MiPerfilFragment)
               return@OnNavigationItemSelectedListener true
           }
        }
        false
    }



    private fun cargarFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_container, fragment)
        transaction.commit()
    }





    override fun onBackPressed() {

        /*
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setMessage("¿Estás seguro de cerrar sesión?")
            .setCancelable(false)
            .setPositiveButton("Sí") { _, _ ->

                // Si la respuesta es afirmativa, agrega aquí la lógica para cerrar la sesión
                val intent = Intent(this, Login::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                val intent1 = Intent(this, NoticiasService::class.java)
                this.stopService(intent1)

            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.cancel()
            }
        alertDialogBuilder.create().show()
        */
    }


}