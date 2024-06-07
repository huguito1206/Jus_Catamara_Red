package com.example.juscatamarared.Adaptadores

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.juscatamarared.Clases.CompanerosClass
import com.example.juscatamarared.Clases.EntornoDeDatos
import com.example.juscatamarared.Clases.ImageDialog
import com.example.juscatamarared.Clases.RecibosSueldoClass
import com.example.juscatamarared.Clases.TramitesClass
import com.example.juscatamarared.Fragmentos.Apps.AppActivity_Companeros
import com.example.juscatamarared.Fragmentos.Apps.AppActivity_ListadoTramites
import com.example.juscatamarared.Fragmentos.FragmentRecibosSueldo
import com.example.juscatamarared.R
import de.hdodenhof.circleimageview.CircleImageView

class Companeros_Adapter (private val context: Context,
                          private var listado: List<CompanerosClass>,
                          private val listener: AppActivity_Companeros
): BaseAdapter() {

    fun updateList(newList: List<CompanerosClass>) {
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
        return listado[position].usuario_id
    }


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val vista = inflater.inflate(R.layout.items_lista_companeros, null)

        val Nombre = vista.findViewById<TextView>(R.id.tv_UsuariosNombre_item)
        val perfil = vista.findViewById<CircleImageView>(R.id.perfilListadoUsuarios)


        val Entorno = EntornoDeDatos.VariablesGlobales.listado_companeros[position]

        Nombre.text = "${Entorno.usuario_nombre} ${Entorno.usuario_apellido}"

        Glide.with(context)
            .load(Entorno.usuario_foto_perfil)
            .centerCrop()
            .placeholder(R.drawable.ic_person)
            .error(R.drawable.ic_person)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .into(perfil)

        perfil.setOnClickListener {
            val imageDialog = ImageDialog(context, Entorno.usuario_foto_perfil)
            imageDialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            imageDialog.show()
        }

        return vista
    }

}