package com.example.juscatamarared.Adaptadores

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.juscatamarared.Clases.EntornoDeDatos
import com.example.juscatamarared.Clases.TramitesClass
import com.example.juscatamarared.Fragmentos.Apps.AppActivity_ListadoTramites
import com.example.juscatamarared.R

class Tramites_Adapter(
    private val context: Context,
    private var listado: List<TramitesClass>,
    private val listener: AppActivity_ListadoTramites
) : BaseAdapter() {

    fun updateList(newList: List<TramitesClass>) {
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
        return listado[position].formulario_id
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val vista = inflater.inflate(R.layout.items_lista_tramites, null)

        val Descripcion: TextView = vista.findViewById(R.id.tv_DescripcionLicencia_items)
        val NombreApellido: TextView = vista.findViewById(R.id.tv_NombreDelEMpleadoLicencia_items)
        val FechaDelTramite: TextView = vista.findViewById(R.id.tv_FechaLicencia_items)
        val EstadoTramite: TextView = vista.findViewById(R.id.tv_EstadoLicencia_items)

        Descripcion.text = "${listado[position].formulario_licencias}"
        NombreApellido.text = "Explicación: ${listado[position].formulario_explicacion}"
        FechaDelTramite.text = "Fecha solicitud: ${listado[position].formulario_fecha_hora_registro}"
        EstadoTramite.text = "Estado: ${listado[position].formulario_estado}"
        return vista
    }
}
