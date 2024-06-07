package com.example.juscatamarared.Adaptadores

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.database.DataSetObserver
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.ListAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.juscatamarared.Clases.EntornoDeDatos
import com.example.juscatamarared.Clases.ImageDialog
import com.example.juscatamarared.Clases.NoticiasClass
import com.example.juscatamarared.Clases.UsuarioClass
import com.example.juscatamarared.Fragment_Inicial
import com.example.juscatamarared.Fragmentos.Fragment_Noticias
import com.example.juscatamarared.Interface.OnMeGustaClickListener
import com.example.juscatamarared.R
import de.hdodenhof.circleimageview.CircleImageView
import org.json.JSONException
import java.util.HashMap


class Noticias_Adaprter(private val contexto: Context, listado: List<NoticiasClass>, private val listener: OnMeGustaClickListener) : RecyclerView.Adapter<Noticias_Adaprter.ViewHolder>(),
    ListAdapter {

    var adapter: Noticias_Adaprter = this



    override fun getItemCount(): Int {
        // return dataList.size
       return EntornoDeDatos.VariablesGlobales.listado_noticia.size
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val perfil: CircleImageView = itemView.findViewById(R.id.perfilListPublicaciones)
        val nombre: TextView = itemView.findViewById(R.id.tv_NombreUsuarioListPublicaciones)
        val fecha: TextView = itemView.findViewById(R.id.tv_FechaListPublicaciones)
        val descripcion: TextView = itemView.findViewById(R.id.Tv_Descripcion)
        val imagenPublicacion: ImageView = itemView.findViewById(R.id.Imagen_noticia)
        val btnLike: ImageView = itemView.findViewById(R.id.manito)
        var tvLikeCount:TextView = itemView.findViewById(R.id.tvLikeCount)

        var EliminarNoticia: ImageView = itemView.findViewById(R.id.Delete_noticia)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.items_noticias_adapter, parent, false)
        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        val item = EntornoDeDatos.VariablesGlobales.listado_noticia[position]

        holder.EliminarNoticia.setVisibility(View.GONE);


        holder.nombre.text = "${item.nombre_apellido}"
        holder.fecha.text ="${item.noticia_fecha}"
        holder.descripcion.text= "${item.descripcion}"


        holder.tvLikeCount.text = "${item.total_megusta}"
        var likesCount = item.total_megusta.toInt()


        Glide.with(holder.itemView)
            .load(item.usuario_foto_perfil.toString())
            .centerCrop()
            .placeholder(R.drawable.ic_person)
            .into(holder.perfil)
        /*
        Glide.with(holder.itemView)
            .load("${item.usuario_foto_perfil.toString()}")
            .centerCrop()

            .placeholder(R.drawable.ic_person)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .into(holder.perfil)*/


        //Log.d("MiAdaptador", "URL de la imagen: ${item.imagen}")

        if (item.imagen == "null") {
            //holder.imagenPublicacion.visibility = View.GONE
            holder.imagenPublicacion.layoutParams.height = 0
            holder.imagenPublicacion.layoutParams.width = 0

        }else{

            Glide.with(holder.itemView)
                .load("${item.imagen.toString()}")
                .thumbnail(0.25f) // Carga una versión de baja resolución mientras se descarga la versión de alta resolución
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.imagenPublicacion)

            /*
            Glide.with(holder.itemView)
                .load("${item.imagen.toString()}")
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(holder.imagenPublicacion)*/
        }

        if (item.usuario_dio_megusta == "1") {
            item.reaccionMeGusta = true
            holder.btnLike.setBackgroundResource(R.drawable.ic_me_gusta_gris)

        }else{
            item.reaccionMeGusta = false
        }


        holder.btnLike.setOnClickListener {
            if (!item.reaccionMeGusta) {
                // Realizar la acción que desees cuando se presiona el botón "Me gusta"
                // Por ejemplo, cambiar el color del botón o realizar una acción en respuesta al "Me gusta"

                holder.btnLike.setBackgroundResource(R.drawable.ic_me_gusta_gris)

                // Luego, actualizar el estado del botón en el modelo de datos
                item.reaccionMeGusta = true
                //notifyDataSetChanged() // Notificar al adaptador para actualizar la vista


                likesCount ++
                holder.tvLikeCount.text  = likesCount.toString()
                holder.tvLikeCount.visibility = View.VISIBLE // Mostrar el contador
                // Agregar la animación de escala al botón (cambia el valor de escala según tus preferencias)
                holder.btnLike.animate().scaleX(1.2f).scaleY(1.2f).setDuration(200).withEndAction {
                    holder.btnLike.scaleX = 1.0f
                    holder.btnLike.scaleY = 1.0f
                }.start()

                listener.onMeGustaClick("MeGusta",item.noticia_id)
            }

        }


        holder.imagenPublicacion.setOnClickListener {
            val imageDialog = ImageDialog(contexto,item.imagen.toString())
            imageDialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            imageDialog.show()
        }



        holder.itemView.setOnClickListener {
            /*NoticiasClass.NoticiasClassManager.getNoticiasClass()= item
            holder.itemView.context.startActivity(
                Intent(holder.itemView.context, InformacionUsuarios::class.java)
                    .putExtra("position", position)
            )
        }

    }*/



        }

    }




    override fun registerDataSetObserver(observer: DataSetObserver?) {
        TODO("Not yet implemented")
    }

    override fun unregisterDataSetObserver(observer: DataSetObserver?) {
        TODO("Not yet implemented")
    }

    override fun getCount(): Int {
        TODO("Not yet implemented")
    }

    override fun getItem(position: Int): Any {
        TODO("Not yet implemented")
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        TODO("Not yet implemented")
    }

    override fun getViewTypeCount(): Int {
        TODO("Not yet implemented")
    }

    override fun isEmpty(): Boolean {
        TODO("Not yet implemented")
    }

    override fun areAllItemsEnabled(): Boolean {
        TODO("Not yet implemented")
    }

    override fun isEnabled(position: Int): Boolean {
        TODO("Not yet implemented")
    }

}





    /*

    (private val context: Context) : BaseAdapter() {

    override fun getCount(): Int {
        return EntornoDeDatos.VariablesGlobales.listado_noticia.size
    }

    override fun getItem(position: Int): Any {
        return EntornoDeDatos.VariablesGlobales.listado_noticia[position]
    }

    override fun getItemId(position: Int): Long {
        return EntornoDeDatos.VariablesGlobales.listado_noticia[position].noticia_id.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val vista: View
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        vista = inflater.inflate(R.layout.items_noticias_adapter, null)

        val perfil: CircleImageView = vista.findViewById(R.id.perfilListPublicaciones)

        // Perfil
        val url_imagen = EntornoDeDatos.VariablesGlobales.listado_noticia[position].usuario_foto_perfil
        Glide.with(context)
            .load(url_imagen)
            .centerCrop()
            .placeholder(R.drawable.perfil)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .into(perfil)

        val nombre: TextView = vista.findViewById(R.id.tv_NombreUsuarioListPublicaciones)
        nombre.text = EntornoDeDatos.VariablesGlobales.listado_noticia[position].nombre_apellido

        val fecha: TextView = vista.findViewById(R.id.tv_FechaListPublicaciones)
        fecha.text = EntornoDeDatos.VariablesGlobales.listado_noticia[position].noticia_fecha

        val descripcion: TextView = vista.findViewById(R.id.Tv_Descripcion)
        descripcion.text = EntornoDeDatos.VariablesGlobales.listado_noticia[position].descripcion

        val imagenPublicacion: ImageView = vista.findViewById(R.id.Imagen_noticia)

        if (EntornoDeDatos.VariablesGlobales.listado_noticia[position].imagen == null) {
            imagenPublicacion.visibility = View.GONE
        } else {
            val url_imagen_dos = EntornoDeDatos.VariablesGlobales.listado_noticia[position].imagen
            Glide.with(context)
                .load(url_imagen_dos)
                .fitCenter()
                .error(R.drawable.ic_error)
                .into(imagenPublicacion)
        }

        val meGusta: LinearLayout = vista.findViewById(R.id.meGusta)
        meGusta.setOnClickListener {
            val cantMeGusta: TextView = vista.findViewById(R.id.cantMeGusta)
            //AnadirMeGusta(EntornoDeDatos.listado_noticia[position].noticia_id.toString(), "like")
            cantMeGusta.text = "✔️"
        }

        return vista
    }*/
