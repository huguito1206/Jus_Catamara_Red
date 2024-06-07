package com.example.juscatamarared.Fragmentos.Apps

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.juscatamarared.Adaptadores.Publicacion_User_Adapter
import com.example.juscatamarared.Clases.CompanerosClass
import com.example.juscatamarared.Clases.EntornoDeDatos
import com.example.juscatamarared.Clases.ImageDialog
import com.example.juscatamarared.Clases.PublicacionesUserClass
import com.example.juscatamarared.Fragmentos.Fragment_MiPerfil
import com.example.juscatamarared.R
import de.hdodenhof.circleimageview.CircleImageView
import org.json.JSONArray
import org.json.JSONException

class AppActivity_perfil_companero : AppCompatActivity() {

    var listView: ListView?=null
    var MyAdapter: Publicacion_User_Adapter?=null
    var dialog: Dialog?  = null

    private lateinit var loadingHeaderView: View

    private lateinit var Mensaje:TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.app_activity_perfil_companero)

        val window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = this.resources.getColor(R.color.ColorProgress)



        val nombreUser:TextView = findViewById(R.id.NombreUser)
        val FotoPerfil: CircleImageView = findViewById(R.id.cimg_perfil)
        val Cargo:TextView = findViewById(R.id.tv_Cargo)
        val Area:TextView = findViewById(R.id.tv_Area)
        val Profesion:TextView = findViewById(R.id.tv_Profesion)
        val Whatsapp:TextView = findViewById(R.id.tv_Whatsapp)
        val CorreoElectronico:TextView = findViewById(R.id.tv_email)
        val FechaNacimiento:TextView = findViewById(R.id.tv_FNacimiento)
        val Direccion:TextView = findViewById(R.id.tv_Direccion)
        val Facebook:TextView = findViewById(R.id.tv_Facebook)
        val Instagram:TextView = findViewById(R.id.tv_Instagram)
        val Biografia:TextView = findViewById(R.id.tv_Biografia)

        Mensaje = findViewById(R.id.Mensaje)
        Mensaje.text="PUBLICACIONES"


        //recibo datos del usuario seleccionado
        val Seleccionado = intent.getParcelableExtra<CompanerosClass>("CompaneroSeleccionado")





        nombreUser.text="${Seleccionado?.usuario_nombre} ${Seleccionado?.usuario_apellido}"

        Glide.with(this)
            .load(Seleccionado?.usuario_foto_perfil.toString())
            .centerCrop()
            .placeholder(R.drawable.ic_person)
            .error(R.drawable.ic_person)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .into(FotoPerfil)

        FotoPerfil.setOnClickListener {
            val imageDialog = ImageDialog(this, Seleccionado?.usuario_foto_perfil.toString())
            imageDialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            imageDialog.show()
        }



        Cargo.text="${Seleccionado?.cargo_descripcion}"
        Area.text="${Seleccionado?.area_descripcion}"
        Profesion.text="${Seleccionado?.usuario_profesion}"
        Whatsapp.text="${Seleccionado?.usuario_telefono}"
        CorreoElectronico.text="${Seleccionado?.usuario_email}"
        FechaNacimiento.text="${Seleccionado?.usuario_fecha_nacimiento}"
        Direccion.text="${Seleccionado?.usuario_casa_numero}, ${Seleccionado?.usuario_calle}, ${Seleccionado?.usuario_depto}, ${Seleccionado?.usuario_provincia}"
        Facebook.text="${Seleccionado?.usuario_facebook}"
        Instagram.text="${Seleccionado?.usuario_instagram}"
        Biografia.text="${Seleccionado?.usuario_biografia}"



        listView = findViewById(R.id.listViewNoticias)
        //MyPdfAdapter = ReciboSueldo_Adapter(requireContext(),this)
        listView?.setAdapter(MyAdapter)


        Whatsapp.setOnClickListener {
            val url_whatsapp = "https://api.whatsapp.com/send?phone=54${Seleccionado?.usuario_telefono.toString()}"
            val uri = Uri.parse(url_whatsapp)
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }

        Facebook.setOnClickListener {
            val facebookPageUrl =
                "https://www.facebook.com/${Seleccionado?.usuario_facebook.toString()}"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(facebookPageUrl))
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            }
        }


            Instagram.setOnClickListener {
                val instagramProfileUrl =
                    "https://www.instagram.com/${Seleccionado?.usuario_instagram.toString()}"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(instagramProfileUrl))
                if (intent.resolveActivity(packageManager) != null) {
                    startActivity(intent)
                }
            }


/*
        // Inflar el HeaderView personalizado con el diálogo de espera
        loadingHeaderView = layoutInflater.inflate(R.layout.progress_dialog, listView, false)
        // Agregar el HeaderView al ListView
        listView?.addHeaderView(loadingHeaderView)*/

        MisPublicaciones("PublicacionesDeUsuario", Seleccionado?.usuario_id.toString())



    }

    private fun setListViewHeightBasedOnChildren(listView: ListView) {
        val listAdapter = listView.adapter ?: return
        var totalHeight = 0
        for (i in 0 until listAdapter.count) {
            val listItem = listAdapter.getView(i, null, listView)
            listItem.measure(0, 0)
            totalHeight += listItem.measuredHeight
        }

        val params = listView.layoutParams
        params.height = totalHeight + listView.dividerHeight * (listAdapter.count - 1)
        listView.layoutParams = params
        listView.requestLayout()
    }





    private fun MisPublicaciones(Accion:String, UserId:String){
        EntornoDeDatos.VariablesGlobales.ListadoPublicacionesUser.clear()
        MyAdapter?.updateList(emptyList())

        dialog = Dialog(this@AppActivity_perfil_companero)
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.setContentView(R.layout.progress_dialog)
        dialog!!.setCanceledOnTouchOutside(false)
        dialog!!.show()


        var request: StringRequest = object : StringRequest(
            Method.POST, EntornoDeDatos().URL,
            Response.Listener { response ->
                dialog?.dismiss()
                //listView?.removeHeaderView(loadingHeaderView)
                //Toast.makeText(this, ""+response, Toast.LENGTH_SHORT).show();
                try {
                    val jsonArray = JSONArray(response)
                    if (jsonArray.length() > 0) {

                        //val gson = Gson()
                        val listado = parsearJSONArray(jsonArray)
                        val miAdaptador = Publicacion_User_Adapter(this,listado,this,
                            Fragment_MiPerfil()
                        )
                        listView?.adapter = miAdaptador

                        val tempListView: ListView? = listView as ListView?

                        if (tempListView != null) {
                            setListViewHeightBasedOnChildren(tempListView)
                        }

                        //Toast.makeText(activity,"${listado.size}", Toast.LENGTH_SHORT).show();
                        //EntornoDeDatos.VariablesGlobales.listado_noticia.add(listado)

                    }else{
                        //listView?.removeHeaderView(loadingHeaderView)
                        Mensaje.text="NO HAY PUBLICACIONES"
                    }
                } catch (e: JSONException) {
                    //listView?.removeHeaderView(loadingHeaderView)
                    //Toast.makeText(getApplicationContext(),"VERIFIQUE SUS DATOS",Toast.LENGTH_SHORT).show();
                    e.printStackTrace()
                }
                dialog?.dismiss()
            },
            Response.ErrorListener {
                dialog?.dismiss()
                //listView?.removeHeaderView(loadingHeaderView)
                Toast.makeText(this, "REVISE CONEXIÓN A INTERNET", Toast.LENGTH_LONG).show();
            }
        ) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String>? {
                val params: MutableMap<String, String> = HashMap()
                params["Accion"] = Accion
                params["usuario_id"] = UserId
                return params
            }
        }
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(request)
    }

    private fun parsearJSONArray(jsonArray: JSONArray): List<PublicacionesUserClass> {
        EntornoDeDatos.VariablesGlobales.ListadoPublicacionesUser.clear()
        val lista = mutableListOf<PublicacionesUserClass>()

        try {
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val noticia_id = jsonObject.getLong("noticia_id")
                var imagen = jsonObject.getString("imagen")
                val descripcion = jsonObject.getString("descripcion")
                val usuario_id = jsonObject.getInt("usuario_id")
                val noticia_fecha = jsonObject.getString("noticia_fecha")
                val noticia_hora = jsonObject.getString("noticia_hora")
                val  nombre_apellido= jsonObject.getString("nombre_apellido")
                val  usuario_foto_perfil= jsonObject.getString("usuario_foto_perfil")
                val  usuario_dio_megusta= jsonObject.getString("usuario_dio_megusta")
                val  total_megusta= jsonObject.getString("total_megusta")

                val noticias = PublicacionesUserClass(noticia_id, imagen, descripcion, usuario_id, noticia_fecha, noticia_hora, nombre_apellido, usuario_foto_perfil,usuario_dio_megusta,total_megusta)
                lista.add(noticias)
                EntornoDeDatos.VariablesGlobales.ListadoPublicacionesUser.add(noticias)

            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return lista
    }





}


