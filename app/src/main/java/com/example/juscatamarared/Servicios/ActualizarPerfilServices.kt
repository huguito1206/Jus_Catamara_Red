package com.example.juscatamarared.Servicios

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.juscatamarared.Clases.EntornoDeDatos
import com.example.juscatamarared.Clases.UsuarioClass
import com.example.juscatamarared.Fragment_Inicial
import com.example.juscatamarared.Fragmentos.Fragment_MiPerfil
import com.example.juscatamarared.R
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONException

class ActualizarPerfilServices : Service() {

    private val TAG ="servicie"
    val myHandler = Handler(Looper.getMainLooper())


    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //aca va todo lo que hara la app cuando inicie el sercivio

        Login("Login",EntornoDeDatos.VariablesGlobales.VariableToken,EntornoDeDatos.VariablesGlobales.VariableUbicacion,
            EntornoDeDatos.VariablesGlobales.fabricanteYmodelo)

        Log.d(TAG,"start")

        return super.onStartCommand(intent, flags, startId)
    }



    override fun onDestroy() {
        //aca detiene el servicio
        Log.d(TAG,"finish")
        myHandler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }


    private fun Login(Accion:String, Token:String, LAT_LON:String, Model:String){
        val User = UsuarioClass.UsuarioClassManager.getUsuarioClass()
        var request: StringRequest = object : StringRequest(
            Method.POST, EntornoDeDatos().URL,
            Response.Listener { response ->
                //dialog?.dismiss()
                //Toast.makeText(getApplicationContext(), ""+response, Toast.LENGTH_SHORT).show();
                try {
                        val jsonArray = JSONArray(response)
                        if (jsonArray.length() > 0) {
                            //aca trae array de datos de array
                            for (i in 0 until jsonArray.length()) {
                                val gson = Gson()
                                var ObjetoJson: UsuarioClass = gson.fromJson(jsonArray[i].toString(), UsuarioClass::class.java)

                                UsuarioClass.UsuarioClassManager.setUsuarioClass(ObjetoJson)

                                //Toast.makeText(getApplicationContext(),"Servcio:${ObjetoJson?.usuario_foto_perfil.toString()}", Toast.LENGTH_SHORT).show();

                                val intent1 = Intent(this, NoticiasService::class.java)
                                stopService(intent1)

                            }
                        }

                } catch (e: JSONException) {

                    Toast.makeText(getApplicationContext(),"VERIFIQUE SUS DATOS", Toast.LENGTH_SHORT).show();
                    e.printStackTrace()

                }
            },
            Response.ErrorListener {
                Toast.makeText(this, "REVISE CONEXIÓN A INTERNET", Toast.LENGTH_LONG).show();
            }
        ) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String>? {
                val params: MutableMap<String, String> = HashMap()
                params["Accion"] = Accion
                params["Token"] = Token
                params["LAT_LON"] = LAT_LON
                params["Model"] = Model
                params["Dni"] = User?.usuario_dni.toString()
                params["Clave"] = User?.usuario_clave.toString()
                return params
            }
        }
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(request)
    }




}