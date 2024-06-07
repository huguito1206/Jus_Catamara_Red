package com.example.juscatamarared.Fragmentos.Apps

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.ListView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.juscatamarared.Activity_ReciboSueldo
import com.example.juscatamarared.Adaptadores.Companeros_Adapter
import com.example.juscatamarared.Adaptadores.ReciboSueldo_Adapter
import com.example.juscatamarared.Adaptadores.Tramites_Adapter
import com.example.juscatamarared.Clases.CompanerosClass
import com.example.juscatamarared.Clases.EntornoDeDatos
import com.example.juscatamarared.Clases.TramitesClass
import com.example.juscatamarared.Clases.UsuarioClass
import com.example.juscatamarared.R
import org.json.JSONArray
import org.json.JSONException

class AppActivity_Companeros : AppCompatActivity() {

    var listView: ListView?=null
    var MyAdapter: Companeros_Adapter?=null

    var dialog: Dialog?  = null
    var User = UsuarioClass.UsuarioClassManager.getUsuarioClass()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.app_activity_companeros)

        val window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = this.resources.getColor(R.color.ColorProgress)


        EntornoDeDatos.VariablesGlobales.setMostraOcultarDelete("Ocultar")


        TraerListado("CompanerosListado")

        listView = findViewById(R.id.listView_ListadoUsuariosCopaneros)
        //MyPdfAdapter = ReciboSueldo_Adapter(requireContext(),this)
        listView?.setAdapter(MyAdapter)




        listView?.setOnItemClickListener { parent, view, position, id ->
            // Aquí manejas el evento de clic en el elemento de la lista
            val Seleccionado = EntornoDeDatos.VariablesGlobales.listado_companeros[position]

            //Toast.makeText(this,"${Seleccionado.usuario_apellido}",Toast.LENGTH_LONG).show()

            // Crea un intent para iniciar la segunda actividad
            val intent = Intent(this, AppActivity_perfil_companero::class.java)

            // Pasa el objeto de RecibosSueldoClass a la segunda actividad
            intent.putExtra("CompaneroSeleccionado", Seleccionado)

            // Inicia la segunda actividad
            startActivity(intent)

        }

    }


    fun TraerListado(Accion:String){
        var User = UsuarioClass.UsuarioClassManager.getUsuarioClass()
        EntornoDeDatos.VariablesGlobales.listado_companeros.clear()
        val Adapter = Companeros_Adapter(this, emptyList(), this)
        Adapter.updateList(emptyList())

        dialog = Dialog(this@AppActivity_Companeros)
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.setContentView(R.layout.progress_dialog)
        dialog!!.setCanceledOnTouchOutside(false)
        dialog!!.show()

        var request: StringRequest = object : StringRequest(
            Method.POST, EntornoDeDatos().URL,
            Response.Listener { response ->
                dialog?.dismiss()
                //Toast.makeText(this@AppActivity_Companeros, ""+response, Toast.LENGTH_SHORT).show();
                try {
                    val jsonArray = JSONArray(response)
                    if (jsonArray.length() > 0) {
                        //val gson = Gson()
                        val listado = parsearJSONArray(jsonArray)
                        val adapter =Companeros_Adapter(this@AppActivity_Companeros, listado, this)
                        listView?.adapter = adapter
                        //Toast.makeText(this,"${listado.size}", Toast.LENGTH_SHORT).show();
                    }else{
                        val adapter = listView?.adapter as? Companeros_Adapter
                        adapter?.updateList(emptyList())
                        Toast.makeText(this,"NO TIENES COMPAÑEROS POR EL MOMENTO", Toast.LENGTH_SHORT).show();
                    }
                } catch (e: JSONException) {
                    //Toast.makeText(getApplicationContext(),"VERIFIQUE SUS DATOS",Toast.LENGTH_SHORT).show();
                    e.printStackTrace()
                }
                dialog?.dismiss()
            },
            Response.ErrorListener {
                dialog?.dismiss()
                Toast.makeText(this@AppActivity_Companeros, "REVISE CONEXIÓN A INTERNET", Toast.LENGTH_LONG).show();
            }
        ) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String>? {
                val params: MutableMap<String, String> = HashMap()
                params["Accion"] = Accion
                params["usuario_id"] = User?.usuario_id.toString()
                //esto para poder mostrar a futuro compañeros solo de mi area
                params["area_id"] = User?.area_id.toString()
                return params
            }
        }
        val requestQueue = Volley.newRequestQueue(this@AppActivity_Companeros)
        requestQueue.add(request)
    }

    private fun parsearJSONArray(jsonArray: JSONArray): List<CompanerosClass> {
        EntornoDeDatos.VariablesGlobales.listado_companeros.clear()
        val lista = mutableListOf<CompanerosClass>()

        try {
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)

                val usuario_id= jsonObject.getLong("usuario_id")
                val usuario_nombre= jsonObject.getString("usuario_nombre")
                val usuario_apellido= jsonObject.getString("usuario_apellido")
                val usuario_dni= jsonObject.getString("usuario_dni")
                val usuario_cuil= jsonObject.getString("usuario_cuil")
                val usuario_legajo= jsonObject.getString("usuario_legajo")
                val usuario_provincia= jsonObject.getString("usuario_provincia")
                val usuario_depto= jsonObject.getString("usuario_depto")
                val usuario_calle= jsonObject.getString("usuario_calle")
                val usuario_casa_numero= jsonObject.getString("usuario_casa_numero")
                val usuario_fecha_nacimiento= jsonObject.getString("usuario_fecha_nacimiento")
                val usuario_email= jsonObject.getString("usuario_email")
                val usuario_biografia= jsonObject.getString("usuario_biografia")
                val usuario_telefono= jsonObject.getString("usuario_telefono")
                val usuario_profesion= jsonObject.getString("usuario_profesion")
                val usuario_clave= jsonObject.getString("usuario_clave")
                val usuario_foto_perfil= jsonObject.getString("usuario_foto_perfil")
                val usuario_facebook= jsonObject.getString("usuario_facebook")
                val usuario_instagram= jsonObject.getString("usuario_instagram")
                val cargo_id= jsonObject.getInt("cargo_id")
                val cargo_descripcion= jsonObject.getString("cargo_descripcion")
                val cargo_comentario= jsonObject.getString("cargo_comentario")
                val tipo_usuario_id= jsonObject.getInt("tipo_usuario_id")
                val tipo_usuario_descripcion= jsonObject.getString("tipo_usuario_descripcion")
                val entidad_id= jsonObject.getInt("entidad_id")
                val entidad_descripcion= jsonObject.getString("entidad_descripcion")
                val area_id= jsonObject.getInt("area_id")
                val area_descripcion= jsonObject.getString("area_descripcion")
                val estado_id= jsonObject.getInt("estado_id")
                val estado_descripcion= jsonObject.getString("estado_descripcion")


                val Companeros = CompanerosClass(usuario_id, usuario_nombre, usuario_apellido, usuario_dni, usuario_cuil, usuario_legajo, usuario_provincia, usuario_depto, usuario_calle, usuario_casa_numero, usuario_fecha_nacimiento, usuario_email, usuario_biografia, usuario_telefono, usuario_profesion, usuario_clave, usuario_foto_perfil, usuario_facebook, usuario_instagram, cargo_id, cargo_descripcion, cargo_comentario, tipo_usuario_id, tipo_usuario_descripcion, entidad_id, entidad_descripcion, area_id, area_descripcion, estado_id, estado_descripcion)
                lista.add(Companeros)

                EntornoDeDatos.VariablesGlobales.listado_companeros.add(Companeros)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return lista
    }




}