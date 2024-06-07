package com.example.juscatamarared

import android.Manifest
import android.app.Dialog
import android.app.DownloadManager
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.net.Uri
import android.os.*
import android.view.Gravity
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.juscatamarared.Clases.EntornoDeDatos
import com.example.juscatamarared.Clases.MainThreadExecutor
import com.example.juscatamarared.Clases.RecuperarcionClaveClass
import com.example.juscatamarared.Clases.SesionBiometrica
import com.example.juscatamarared.Clases.UsuarioClass
import com.example.juscatamarared.Servicios.NoticiasService
import com.google.android.gms.location.*
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONException
import java.util.concurrent.Executor


class Login : AppCompatActivity() {

    private var isPermisos = false
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: com.google.android.gms.location.LocationCallback
    private val CODIGO_PERMISO_SEGUNDO_PLANO = 100


    var dialog: Dialog?  = null
    var  iniciar_sesion:Button? = null

    var edt_dni_login:EditText? = null
    var edt_clave_login:EditText?= null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        val window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = this.resources.getColor(R.color.ColorProgress)

        verificarPermisos()

        val User = UsuarioClass.UsuarioClassManager.getUsuarioClass()
        //Toast.makeText(this@Login, User?.usuario_clave.toString(), Toast.LENGTH_SHORT).show()
       

        //aca verifico si es que el celular tiene lector de huellas
        val biometricManager = BiometricManager.from(this)
        when (biometricManager.canAuthenticate()) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                //println("El dispositivo tiene un lector biométrico.")
                VerificarTokenDeLogin("ConsultarBiometriaToken",EntornoDeDatos.VariablesGlobales.VariableToken.toString())
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                //println("El dispositivo no tiene un lector biométrico.")
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                //println("El lector biométrico no está disponible actualmente.")
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                //println("No hay datos biométricos registrados en el dispositivo.")
            }
        }





        //despues borrar
       // Login("Login",EntornoDeDatos.VariablesGlobales.VariableToken,EntornoDeDatos.VariablesGlobales.VariableUbicacion,EntornoDeDatos.VariablesGlobales.fabricanteYmodelo,"1","123")
        //DialogoOpcional()
        edt_dni_login = findViewById(R.id.edt_dni_login)
        edt_clave_login = findViewById(R.id.edt_clave_login)

        iniciar_sesion = findViewById(R.id.iniciar_sesion)


        iniciar_sesion?.setOnClickListener(object :View.OnClickListener{
            override fun onClick(v: View?) {
                if (edt_dni_login?.text.toString().isEmpty()) {
                    Toast.makeText(this@Login, "Ingrese DNI", Toast.LENGTH_SHORT).show()
                } else {
                    if (edt_clave_login?.text.toString().isEmpty()) {
                        Toast.makeText(this@Login, "Ingrese CLave", Toast.LENGTH_SHORT).show()
                    }else{
                        //Toast.makeText(this@Login, "apreo botn", Toast.LENGTH_SHORT).show();
                        Login("Login",EntornoDeDatos.VariablesGlobales.VariableToken,EntornoDeDatos.VariablesGlobales.VariableUbicacion,EntornoDeDatos.VariablesGlobales.fabricanteYmodelo,edt_dni_login?.text.toString(),edt_clave_login?.text.toString())
                    }
                }
            }

        })

        val RecuperarClave = findViewById<TextView>(R.id.olvide_clave)

        RecuperarClave.setOnClickListener {
            // abre un dialogo solicitando el dni
            DialogoSolicitarDNIRecClave()
        }


    }


    private fun VerificarTokenDeLogin(Accion:String, Token:String){
        dialog = Dialog(this)
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.setContentView(R.layout.progress_dialog)
        dialog!!.setCanceledOnTouchOutside(false)
        dialog!!.show()
        var request: StringRequest = object : StringRequest(
            Method.POST, EntornoDeDatos().URL,
            Response.Listener { response ->
                //dialog?.dismiss()
                //Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                try {
                    val jsonArray = JSONArray(response)
                    if (jsonArray.length() > 0) {

                        //aca trae array de datos de array
                        for (i in 0 until jsonArray.length()) {
                            // por aca si viene un token activado para biometria
                            val gson = Gson()
                            var ObjetoJson: SesionBiometrica = gson.fromJson(jsonArray[i].toString(), SesionBiometrica::class.java)
                            SesionBiometrica.SesionBiometricaManager.setSesionBiometrica(ObjetoJson)
                            val sesionBiometrica = SesionBiometrica.SesionBiometricaManager.getSesionBiometrica()

                            if (sesionBiometrica?.biometria_activada == 1) {
                                //biometria Activa

                                //vamos a validar con huella
                                val promptInfo = BiometricPrompt.PromptInfo.Builder()
                                    .setTitle("Ingresá con tu huella")
                                    .setNegativeButtonText("Usar Clave")
                                    .build()

                                val biometricPrompt = BiometricPrompt(this, MainThreadExecutor(), object : BiometricPrompt.AuthenticationCallback() {
                                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                                        super.onAuthenticationError(errorCode, errString)
                                        if (errorCode == BiometricPrompt.ERROR_USER_CANCELED) {
                                            // El usuario ha cancelado la autenticación
                                            // Aquí puedes manejar la lógica correspondiente
                                            dialog!!.dismiss()
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
                                        Login("Login",EntornoDeDatos.VariablesGlobales.VariableToken,EntornoDeDatos.VariablesGlobales.VariableUbicacion,EntornoDeDatos.VariablesGlobales.fabricanteYmodelo,
                                            sesionBiometrica.usuario_dni.toString(),sesionBiometrica.usuario_clave.toString())
                                    }

                                    override fun onAuthenticationFailed() {
                                        super.onAuthenticationFailed()
                                        // Manejar fallos de autenticación
                                        dialog!!.dismiss()
                                        //Toast.makeText(applicationContext, "Huella incorrecta", Toast.LENGTH_SHORT).show()
                                    }
                                })

                                biometricPrompt.authenticate(promptInfo)

                            } else {
                                //Biometria No Activa
                                dialog!!.dismiss()
                            }


                        }
                    }else{
                        // por aca si NOOO viene un token activado para biometria, osea no viene nada
                        dialog!!.dismiss()
                    }

                } catch (e: JSONException) {
                }

            },
            Response.ErrorListener {
                Toast.makeText(this, "REVISE CONEXIÓN A INTERNET", Toast.LENGTH_LONG).show();
                dialog!!.dismiss()
            }
        ) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String>? {
                val params: MutableMap<String, String> = HashMap()
                params["Accion"] = Accion
                params["Token"] = Token
                return params
            }
        }
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(request)
    }





    fun DialogoSolicitarDNIRecClave() {
        val builder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.dialogo_dni_recuperar_clave, null)
        builder.setView(view)
        val dialog = builder.create()
        dialog.window?.setGravity(Gravity.CENTER)
        dialog.show()

        val Dni = view.findViewById<EditText>(R.id.Et_DniRC)
        val Siguiente = view.findViewById<TextView>(R.id.TvSiguienteRC)


       Siguiente.setOnClickListener{
           if (Dni?.text.toString().isEmpty()) {
               Toast.makeText(this@Login, "Ingrese DNI", Toast.LENGTH_SHORT).show()
           }else{
               //aca hacemos un metodo para validar que el dni exista
               ValidadDNIRecupararClave("EnviarCodigoRecuperacionClave",Dni?.text.toString())
               dialog.dismiss()
           }
        }


        Handler().postDelayed({
            // Acciones a realizar después de 22000 ms (22 segundos), si es necesario
        }, 22000)
    }



    private fun ValidadDNIRecupararClave(Accion:String, Dni:String){
        //iniciar_sesion?. isEnabled = false
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
                //Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                try {   val jsonArray = JSONArray(response)
                        if (jsonArray.length() > 0) {
                            for (i in 0 until jsonArray.length()) {
                                val gson = Gson()
                                var ObjetoJson: RecuperarcionClaveClass = gson.fromJson(jsonArray[i].toString(), RecuperarcionClaveClass::class.java)
                                RecuperarcionClaveClass.RecuperacionClaveClassManager.setRecuperarcionClaveClass(ObjetoJson)
                                val intent = Intent(this, Activity_RecuperarClave::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                                startActivity(intent)
                                //finish()
                            }
                        }else{
                            dialog?.dismiss()
                            Toast.makeText(getApplicationContext(),"NO SE ENCUENTRA EL DNI",Toast.LENGTH_SHORT).show();
                            //iniciar_sesion?. isEnabled = true
                        }

                } catch (e: JSONException) {
                    dialog?.dismiss()
                    Toast.makeText(getApplicationContext(),"SE PRODUJO UN ERROR",Toast.LENGTH_SHORT).show();
                    e.printStackTrace()
                    //iniciar_sesion?. isEnabled = true
                }

            },
            Response.ErrorListener {
                dialog?.dismiss()
                Toast.makeText(this, "REVISE CONEXIÓN A INTERNET", Toast.LENGTH_LONG).show();
                //iniciar_sesion?. isEnabled = true
            }
        ) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String>? {
                val params: MutableMap<String, String> = HashMap()
                params["Accion"] = Accion
                params["Dni"] = Dni
                return params
            }
        }
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(request)
    }






    //LOGIN
    private fun Login(Accion:String, Token:String, LAT_LON:String, Model:String, Dni:String,Clave:String){
        iniciar_sesion?. isEnabled = false
        dialog = Dialog(this)
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.setContentView(R.layout.progress_dialog)
        dialog!!.setCanceledOnTouchOutside(false)
        dialog!!.show()
        var request: StringRequest = object : StringRequest(
            Method.POST, EntornoDeDatos().URL,
            Response.Listener { response ->
                //dialog?.dismiss()
                //Toast.makeText(getApplicationContext(), ""+response, Toast.LENGTH_SHORT).show();
                try {
                    if (response == "ActualizarApp") {
                        dialog?.dismiss()
                        Toast.makeText(getApplicationContext(),"ACTUALIZAR LA APLICACIÓN",Toast.LENGTH_SHORT).show();
                    }else{
                        val jsonArray = JSONArray(response)
                        if (jsonArray.length() > 0) {

                            //aca trae array de datos de array
                            for (i in 0 until jsonArray.length()) {
                                val gson = Gson()
                                var ObjetoJson: UsuarioClass = gson.fromJson(jsonArray[i].toString(), UsuarioClass::class.java)
                                UsuarioClass.UsuarioClassManager.setUsuarioClass(ObjetoJson)

                                val intentNoti = Intent(this, NoticiasService::class.java)
                                startService(intentNoti)
                                //Toast.makeText(this , "", Toast.LENGTH_SHORT).show()

                                if (UsuarioClass.UsuarioClassManager.getUsuarioClass()?.usuario_clave == UsuarioClass.UsuarioClassManager.getUsuarioClass()?.usuario_dni){
                                    DialogoOpcional()
                                }else{
                                    val handler = Handler()

                                    // Define el tiempo de retardo en milisegundos (por ejemplo, 3000 ms = 3 segundos)
                                    val delayMillis: Long = 4000

                                    handler.postDelayed({

                                        val intent = Intent(this, Fragment_Inicial::class.java)
                                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                                        intent.putExtra("accion", "mostrar_noticias")
                                        intent.putExtra("mostrar_noticias", true)
                                        startActivity(intent)
                                        dialog?.dismiss()
                                    }, delayMillis)


                                    edt_dni_login!!.text.clear()
                                    edt_clave_login!!.text.clear()

                                }

                            }
                    }else{
                            dialog?.dismiss()
                            Toast.makeText(getApplicationContext(),"VERIFIQUE SUS DATOS",Toast.LENGTH_SHORT).show();
                            iniciar_sesion?. isEnabled = true
                        }
                    }
                } catch (e: JSONException) {
                    dialog?.dismiss()
                    Toast.makeText(getApplicationContext(),"VERIFIQUE SUS DATOS",Toast.LENGTH_SHORT).show();
                    e.printStackTrace()
                    iniciar_sesion?. isEnabled = true
                }

            },
            Response.ErrorListener {
               dialog?.dismiss()
                Toast.makeText(this, "REVISE CONEXIÓN A INTERNET", Toast.LENGTH_LONG).show();
                iniciar_sesion?. isEnabled = true
            }
        ) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String>? {
                val params: MutableMap<String, String> = HashMap()
                params["Accion"] = Accion
                params["Token"] = Token
                params["LAT_LON"] = LAT_LON
                params["Model"] = Model
                params["Dni"] = Dni
                params["Clave"] = Clave
                return params
            }
        }
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(request)
    }


//Dialogo para cambiar clave al iniciar por primera vez
fun DialogoOpcional() {
    val builder = AlertDialog.Builder(this)
    val inflater = LayoutInflater.from(this)
    val view = inflater.inflate(R.layout.dialogo_cambio_clave_primer_inicio, null)
    builder.setView(view)
    val dialog = builder.create()
    dialog.window?.setGravity(Gravity.CENTER)
    dialog.show()

    val editTextPassword1:EditText? = view.findViewById(R.id.edt_PrimeraClaveDialog)
    val editTextPassword2:EditText?= view.findViewById(R.id.edt_SegundaClaveDialog)
    val CambiarClave = view.findViewById<Button>(R.id.BtnCambiarClave)

    CambiarClave.setOnClickListener {
        val password1 = editTextPassword1?.text.toString()
        val password2 = editTextPassword2?.text.toString()

        // Verifica que los campos no estén vacíos
        if (password1.isNotEmpty() && password2.isNotEmpty()) {
            if (password1 == password2) {
                //Toast.makeText(this, "Las contraseñas coinciden", Toast.LENGTH_SHORT).show()
                cambiarClave("CambiarClaveAlIniciar",password2)
                //dialog.dismiss()

            } else {
                // Las contraseñas no coinciden, muestra un mensaje de error
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            }
        } else {
            // Al menos uno de los campos está vacío, muestra un mensaje de error
            Toast.makeText(this, "Por favor, completa ambos campos", Toast.LENGTH_SHORT).show()
        }

    }

    Handler().postDelayed({
        // Acciones a realizar después de 22000 ms (22 segundos), si es necesario
    }, 22000)
}


    fun cambiarClave (Accion: String,NuevaClave:String) {
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
                    if (response == "CambioExitoso") {
                        Toast.makeText(this@Login,"Inicie Sesión con su Nueva Clave", Toast.LENGTH_SHORT).show();
                        val intent = Intent(this@Login, Login::class.java)
                       // intent.putExtra("accion", "mostrar_noticias")
                        startActivity(intent)
                        finish()
                    }
                } catch (e: JSONException) {
                    Toast.makeText(this,"algo salió mal, reintente",Toast.LENGTH_SHORT).show();
                    e.printStackTrace()
                }
                dialog?.dismiss()
            },
            Response.ErrorListener {
                dialog?.dismiss()
                Toast.makeText(this@Login, "REVISE CONEXIÓN A INTERNET", Toast.LENGTH_LONG).show();
            }
        ) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String>? {
                val params: MutableMap<String, String> = java.util.HashMap()
                params["Accion"] = Accion
                params["NuevaClave"] = NuevaClave
                params["usuario_id"] = User?.usuario_id.toString()
                return params
            }
        }
        val requestQueue = Volley.newRequestQueue(this@Login)
        requestQueue.add(request)
    }



    //UBICAICON DEL DISPOSITIVO

    private fun verificarPermisos() {
        val permisos = arrayListOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            permisos.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }
        val permisosArray = permisos.toTypedArray()
        if (tienePermisos(permisosArray)) {
            isPermisos = true
            onPermisosConcedidos()
        } else {
            solicitarPermisos(permisosArray)
        }
    }


    private fun tienePermisos(permisos: Array<String>): Boolean {
        return permisos.all {
            return ContextCompat.checkSelfPermission(
                this,
                it
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun onPermisosConcedidos() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener {
                if (it != null) {
                    imprimirUbicacion(it)
                } else {
                    Toast.makeText(this, "No se puede obtener la ubicacion", Toast.LENGTH_SHORT).show()
                }
            }
            val locationRequest = com.google.android.gms.location.LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                100000000000000000
            ).apply {
                setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
                setWaitForAccurateLocation(true)
            }.build()

            locationCallback = object : com.google.android.gms.location.LocationCallback() {
                override fun onLocationResult(p0: LocationResult) {
                    super.onLocationResult(p0)

                    for (location in p0.locations) {
                        imprimirUbicacion(location)
                        //Datos("DatosInicio",EntornoDeDatos.VariablesGlobales.VariableToken,"LAT: ${location.latitude} - LON: ${location.longitude}",EntornoDeDatos.VariablesGlobales.fabricanteYmodelo)
                    }
                }
            }
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )


        } catch (_: SecurityException) {

        }
    }

    private fun solicitarPermisos(permisos: Array<String>) {
        requestPermissions(
            permisos,
            CODIGO_PERMISO_SEGUNDO_PLANO
        )
    }


    private fun imprimirUbicacion(ubicacion: Location) {
        //Log.d("GPS", "LAT: ${ubicacion.latitude} - LON: ${ubicacion.longitude}")
        //val ubicaion = Ubicacion("LAT: ${ubicacion.latitude} - LON: ${ubicacion.longitude}")
        EntornoDeDatos.VariablesGlobales.setVariableUbicacion("LAT: ${ubicacion.latitude} - LON: ${ubicacion.longitude}")


    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == CODIGO_PERMISO_SEGUNDO_PLANO) {
            val todosPermisosConcedidos = grantResults.all { it == PackageManager.PERMISSION_GRANTED }

            if (grantResults.isNotEmpty() && todosPermisosConcedidos) {
                isPermisos = true
                onPermisosConcedidos()
            }
        }
    }




}