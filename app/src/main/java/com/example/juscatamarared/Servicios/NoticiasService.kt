package com.example.juscatamarared.Servicios

import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.widget.ListView
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.juscatamarared.Adaptadores.Noticias_Adaprter
import com.example.juscatamarared.Clases.EntornoDeDatos
import com.example.juscatamarared.Clases.NoticiasClass
import com.example.juscatamarared.Clases.UsuarioClass
import com.example.juscatamarared.R
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException

class NoticiasService : Service() {

    private val TAG ="servicie"
    val myHandler = Handler(Looper.getMainLooper())


    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //aca va todo lo que hara la app cuando inicie el sercivio
        myHandler.post(object : Runnable {
            override fun run() {
                /*-----*/
                //ejecuta proceso...
                NoticiasService("ListadoTodasNoticias")
                /*-----*/
                myHandler.postDelayed(this, 5000 /*5 segundos*/)
            }
        })

        Log.d(TAG,"start")

        return super.onStartCommand(intent, flags, startId)
    }



    override fun onDestroy() {
        //aca detiene el servicio
        Log.d(TAG,"finish")
        myHandler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }





    private fun NoticiasService(Accion:String){
        val User = UsuarioClass.UsuarioClassManager.getUsuarioClass()
        //EntornoDeDatos.VariablesGlobales.listado_noticia.clear()
        var request: StringRequest = object : StringRequest(
            Method.POST, EntornoDeDatos().URL,
            Response.Listener { response ->
                //Toast.makeText(activity, ""+response, Toast.LENGTH_SHORT).show();
                try {
                    val jsonArray = JSONArray(response)
                    if (jsonArray.length() > 0) {

                        //val gson = Gson()
                        //EntornoDeDatos.VariablesGlobales.listado_noticia.clear()
                        val listado = parsearJSONArray(jsonArray)
                        //val miAdaptador = Noticias_Adaprter(this@Fragment_Noticias,listado)
                        //listView_ListadoPublicaciones?.adapter = miAdaptador
                        //Toast.makeText(this,"${listado}", Toast.LENGTH_SHORT).show();
                    }else{
                        //Toast.makeText(this,"NO HAY NOTICIAS POR EL MOMENTO", Toast.LENGTH_SHORT).show();
                    }
                } catch (e: JSONException) {
                    //Toast.makeText(getApplicationContext(),"VERIFIQUE SUS DATOS",Toast.LENGTH_SHORT).show();
                    e.printStackTrace()
                }
            },
            Response.ErrorListener {
                //Toast.makeText(this, "REVISE CONEXIÓN A INTERNET", Toast.LENGTH_LONG).show();
            }
        ) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String>? {
                val params: MutableMap<String, String> = HashMap()
                params["Accion"] = Accion
                params["usuario_id"] = User?.usuario_id.toString()
                return params
            }
        }
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(request)
    }

    private fun parsearJSONArray(jsonArray: JSONArray): List<NoticiasClass> {
        EntornoDeDatos.VariablesGlobales.listado_noticia.clear()
        val lista = mutableListOf<NoticiasClass>()

        try {
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val noticia_id = jsonObject.getInt("noticia_id")
                var imagen = jsonObject.getString("imagen")
                val descripcion = jsonObject.getString("descripcion")
                val usuario_id = jsonObject.getInt("usuario_id")
                val noticia_fecha = jsonObject.getString("noticia_fecha")
                val noticia_hora = jsonObject.getString("noticia_hora")
                val  nombre_apellido= jsonObject.getString("nombre_apellido")
                val  usuario_foto_perfil= jsonObject.getString("usuario_foto_perfil")
                val  usuario_dio_megusta= jsonObject.getString("usuario_dio_megusta")
                val  total_megusta= jsonObject.getString("total_megusta")

                val noticias = NoticiasClass(noticia_id, imagen, descripcion, usuario_id, noticia_fecha, noticia_hora, nombre_apellido, usuario_foto_perfil,usuario_dio_megusta,total_megusta)
                lista.add(noticias)
                EntornoDeDatos.VariablesGlobales.listado_noticia.add(noticias)

            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return lista
    }




}