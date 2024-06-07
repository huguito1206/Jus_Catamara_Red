package com.example.juscatamarared.Fragmentos

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.AdapterView
import android.widget.ListView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.fragment.app.FragmentManager
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.juscatamarared.Activity_ReciboSueldo
import com.example.juscatamarared.Adaptadores.ReciboSueldo_Adapter
import com.example.juscatamarared.Clases.EntornoDeDatos
import com.example.juscatamarared.Clases.RecibosSueldoClass
import com.example.juscatamarared.Clases.UsuarioClass
import com.example.juscatamarared.Fragment_Inicial
import com.example.juscatamarared.R
import org.json.JSONArray
import org.json.JSONException

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentRecibosSueldo.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentRecibosSueldo : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }


    }


    var listView_ListadoRecibos:ListView?=null
    var MyPdfAdapter:ReciboSueldo_Adapter?=null

    var dialog: Dialog?  = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_recibos_sueldo, container, false)


        TraerListadoRecibos("ListadoRecibos")

        listView_ListadoRecibos = rootView.findViewById(R.id.listView_ListadoRecibos)
        //MyPdfAdapter = ReciboSueldo_Adapter(requireContext(),this)

        listView_ListadoRecibos?.setAdapter(MyPdfAdapter)




        listView_ListadoRecibos?.setOnItemClickListener { parent, view, position, id ->
            // Aquí manejas el evento de clic en el elemento de la lista
            val reciboSeleccionado = EntornoDeDatos.VariablesGlobales.listado_recibo[position]

            // Crea un intent para iniciar la segunda actividad
            val intent = Intent(requireContext(), Activity_ReciboSueldo::class.java)

            // Pasa el objeto de RecibosSueldoClass a la segunda actividad
            intent.putExtra("reciboSeleccionado", reciboSeleccionado)

            // Inicia la segunda actividad
            startActivity(intent)
        }


        return rootView
    }






    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentRecibosSueldo.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FragmentRecibosSueldo().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }




    fun TraerListadoRecibos(Accion:String){
        dialog = Dialog(requireContext())
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.setContentView(R.layout.progress_dialog)
        dialog!!.setCanceledOnTouchOutside(false)
        dialog!!.show()
        val User = UsuarioClass.UsuarioClassManager.getUsuarioClass()

        var request: StringRequest = object : StringRequest(
            Method.POST, EntornoDeDatos().URL,
            Response.Listener { response ->
                dialog?.dismiss()
                //Toast.makeText(activity, ""+response, Toast.LENGTH_SHORT).show();
                try {
                    val jsonArray = JSONArray(response)
                    if (jsonArray.length() > 0) {

                        //val gson = Gson()
                        val listado = parsearJSONArray(jsonArray)

                        val adapter = ReciboSueldo_Adapter(requireContext(), listado, this)
                        listView_ListadoRecibos?.adapter = adapter

                        //Toast.makeText(activity,"${listado.size}", Toast.LENGTH_SHORT).show();
                        //EntornoDeDatos.VariablesGlobales.listado_noticia.add(listado)


                    }else{
                        //Toast.makeText(activity,"NO HAY NOTICIAS POR EL MOMENTO", Toast.LENGTH_SHORT).show();
                    }
                } catch (e: JSONException) {
                    //Toast.makeText(getApplicationContext(),"VERIFIQUE SUS DATOS",Toast.LENGTH_SHORT).show();
                    e.printStackTrace()
                }
                dialog?.dismiss()
            },
            Response.ErrorListener {
                dialog?.dismiss()
                Toast.makeText(activity, "REVISE CONEXIÓN A INTERNET", Toast.LENGTH_LONG).show();
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
        val requestQueue = Volley.newRequestQueue(activity)
        requestQueue.add(request)
    }

    private fun parsearJSONArray(jsonArray: JSONArray): List<RecibosSueldoClass> {
        EntornoDeDatos.VariablesGlobales.listado_recibo.clear()
        val lista = mutableListOf<RecibosSueldoClass>()

        try {
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val recibo_id = jsonObject.getLong("recibo_id")
                var pdf = jsonObject.getString("pdf")
                val descripcion_pdf = jsonObject.getString("descripcion_pdf")
                val usuario_id = jsonObject.getInt("usuario_id")

                val RecibosSueldo = RecibosSueldoClass(recibo_id,pdf,descripcion_pdf,usuario_id)
                lista.add(RecibosSueldo)
                EntornoDeDatos.VariablesGlobales.listado_recibo.add(RecibosSueldo)

            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return lista
    }



}

