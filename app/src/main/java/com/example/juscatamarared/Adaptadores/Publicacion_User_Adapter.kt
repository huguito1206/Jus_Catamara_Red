package com.example.juscatamarared.Adaptadores

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.juscatamarared.Clases.EntornoDeDatos
import com.example.juscatamarared.Clases.ImageDialog
import com.example.juscatamarared.Clases.PublicacionesUserClass
import com.example.juscatamarared.Fragment_Inicial
import com.example.juscatamarared.Fragmentos.Apps.AppActivity_perfil_companero
import com.example.juscatamarared.Fragmentos.Fragment_MiPerfil
import com.example.juscatamarared.R
import de.hdodenhof.circleimageview.CircleImageView
import org.json.JSONException
import java.util.HashMap

class Publicacion_User_Adapter(
    private val context: Context,
    private var listado: List<PublicacionesUserClass>,
    private val listener: AppActivity_perfil_companero,
    private val listener2: Fragment_MiPerfil
) : BaseAdapter() {

    fun updateList(newList: List<PublicacionesUserClass>) {
        listado = newList
        (context as? Activity)?.runOnUiThread {
            notifyDataSetChanged()
        }
    }

    override fun getCount(): Int {
        return listado.size
    }

    override fun getItem(position: Int): Any {
        return listado[position]
    }

    override fun getItemId(position: Int): Long {
        return listado[position].noticia_id
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val itemView = inflater.inflate(R.layout.items_noticias_adapter, null)

        val perfil: CircleImageView = itemView.findViewById(R.id.perfilListPublicaciones)
        val nombre: TextView = itemView.findViewById(R.id.tv_NombreUsuarioListPublicaciones)
        val fecha: TextView = itemView.findViewById(R.id.tv_FechaListPublicaciones)
        val descripcion: TextView = itemView.findViewById(R.id.Tv_Descripcion)
        val imagenPublicacion: ImageView = itemView.findViewById(R.id.Imagen_noticia)
        val btnLike: ImageView = itemView.findViewById(R.id.manito)
        var tvLikeCount:TextView = itemView.findViewById(R.id.tvLikeCount)

        val EliminarNoticia: ImageView = itemView.findViewById(R.id.Delete_noticia)


            if (EntornoDeDatos.VariablesGlobales.MostraOcultarDelete=="Ocultar"){
                EliminarNoticia.visibility = View.GONE
            }else{
                EliminarNoticia.visibility = View.VISIBLE
                EliminarNoticia.setOnClickListener {
                    DeleteNoticia("DeleteNoticia", listado[position].noticia_id.toString())
                }
            }




        nombre.text = "${listado[position].nombre_apellido}"
        fecha.text = "${listado[position].noticia_fecha}"
        descripcion.text = "${listado[position].descripcion}"
        tvLikeCount.text = "${listado[position].total_megusta}"

        Glide.with(itemView)
            .load("${listado[position].usuario_foto_perfil.toString()}")
            .centerCrop()
            .placeholder(R.drawable.ic_person)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .into(perfil)



        if (listado[position].imagen == "null") {
            //holder.imagenPublicacion.visibility = View.GONE
            imagenPublicacion.layoutParams.height = 0
            imagenPublicacion.layoutParams.width = 0

        }else{

            Glide.with(itemView)
                .load("${listado[position].imagen.toString()}")
                .thumbnail(0.25f) // Carga una versión de baja resolución mientras se descarga la versión de alta resolución
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imagenPublicacion)
            /*
            Glide.with(itemView)
                .load("${listado[position].imagen.toString()}")
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(imagenPublicacion)*/
        }


        imagenPublicacion.setOnClickListener {
            val imageDialog = ImageDialog(context,listado[position].imagen.toString())
            imageDialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            imageDialog.show()
        }


        listado[position].reaccionMeGusta = true
        btnLike.setBackgroundResource(R.drawable.ic_me_gusta_gris)


        return itemView
    }


    fun DeleteNoticia(Accion: String,IdNoticia:String) {
        var dialog: Dialog?
        dialog = Dialog(context)
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.setContentView(R.layout.progress_dialog)
        dialog!!.setCanceledOnTouchOutside(false)
        dialog!!.show()

        var request: StringRequest = object : StringRequest(
            Method.POST, EntornoDeDatos().URL,
            Response.Listener { response ->
                //dialog?.dismiss()
                //Toast.makeText(this@Activity_NuevaNoticia, ""+response, Toast.LENGTH_SHORT).show();
                try {
                    if (response == "Eliminado") {

                        val handler = Handler()
                        val delayMillis: Long = 4000
                        handler.postDelayed({
                            dialog?.dismiss()
                            val intent = Intent(context, Fragment_Inicial::class.java)
                            intent.putExtra("accion", "mostrar_MiPerfil")

                            // Asegúrate de que el contexto es una instancia de Activity antes de llamar a startActivity
                            if (context is Activity) {
                                context.startActivity(intent)
                            } else {
                                // Si el contexto no es una instancia de Activity, es posible que debas hacer algo diferente
                                // dependiendo del flujo de tu aplicación
                            }

                            Toast.makeText(context,"Eliminado Correctamante", Toast.LENGTH_SHORT).show();
                        }, delayMillis)

                    }else{
                        Toast.makeText(context,"No se pudo eliminar", Toast.LENGTH_SHORT).show();
                    }
                } catch (e: JSONException) {
                    //Toast.makeText(activity,"algo esta mal",Toast.LENGTH_SHORT).show();
                    e.printStackTrace()
                }
                //dialog?.dismiss()
            },
            Response.ErrorListener {
                dialog?.dismiss()
                Toast.makeText(context, "REVISE CONEXIÓN A INTERNET", Toast.LENGTH_LONG).show();
            }
        ) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String>? {
                val params: MutableMap<String, String> = HashMap()
                params["Accion"] = Accion
                params["noticia_id"] = IdNoticia
                return params
            }
        }
        val requestQueue = Volley.newRequestQueue(context)
        requestQueue.add(request)
    }


}
