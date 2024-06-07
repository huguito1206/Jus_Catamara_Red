package com.example.juscatamarared.Adaptadores

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.juscatamarared.Clases.EntornoDeDatos
import com.example.juscatamarared.Clases.RecibosSueldoClass
import com.example.juscatamarared.Fragmentos.FragmentRecibosSueldo
import com.example.juscatamarared.R

class ReciboSueldo_Adapter(private val context: Context,
                           listado: List<RecibosSueldoClass>,
                           listener: FragmentRecibosSueldo
):BaseAdapter() {

    override fun getCount(): Int {
        return EntornoDeDatos.VariablesGlobales.listado_recibo.size
    }

    override fun getItem(position: Int): Any {
        return EntornoDeDatos.VariablesGlobales.listado_recibo[position]
    }

    override fun getItemId(position: Int): Long {
        return EntornoDeDatos.VariablesGlobales.listado_recibo[position].recibo_id

    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val vista = inflater.inflate(R.layout.items_lista_pdf, null)

        val DescripcionPdf:TextView = vista.findViewById<TextView>(R.id.tv_DescripcionPdf_item)

        DescripcionPdf.text = EntornoDeDatos.VariablesGlobales.listado_recibo[position].descripcion_pdf

        return vista
    }

}