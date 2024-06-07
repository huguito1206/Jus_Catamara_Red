package com.example.juscatamarared.Fragmentos

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.juscatamarared.Fragmentos.Apps.AppActivity_Companeros
import com.example.juscatamarared.Fragmentos.Apps.AppActivity_ListadoTramites
import com.example.juscatamarared.Fragmentos.Apps.AppActivity_TramiteFormulario
import com.example.juscatamarared.Fragmentos.Apps.AppActivity_WebJus
import com.example.juscatamarared.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Fragment_Apps.newInstance] factory method to
 * create an instance of this fragment.
 */
class Fragment_Apps : Fragment() {
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



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment__apps, container, false)

        val Icon_WebJus = rootView.findViewById<ImageView>(R.id.Icon_WebJus)
        val tv_WebJus= rootView.findViewById<TextView>(R.id.tv_WebJus)

        val icon_Calendario = rootView.findViewById<ImageView>(R.id.icon_Calendario)
        val tv_Calendario= rootView.findViewById<TextView>(R.id.tv_Calendario)

        val icon_Encuesta = rootView.findViewById<ImageView>(R.id.icon_Encuesta)
        val tv_Encuesta= rootView.findViewById<TextView>(R.id.tv_Encuesta)

        val icon_Tramites  = rootView.findViewById<ImageView>(R.id.icon_Tramites)
        val tv_Tramites = rootView.findViewById<TextView>(R.id.tv_Tramites)

        val icon_Compas  = rootView.findViewById<ImageView>(R.id.icon_Companeros)
        val tv_Compas = rootView.findViewById<TextView>(R.id.tv_Companeros)

        Icon_WebJus.setOnClickListener { irWebJus("mostrar_webJus") }
        tv_WebJus.setOnClickListener { irWebJus("mostrar_webJus") }

        icon_Calendario.setOnClickListener { irWebJus("mostrar_CalendarioWeb") }
        tv_Calendario.setOnClickListener { irWebJus("mostrar_CalendarioWeb") }

        icon_Encuesta.setOnClickListener { irWebJus("mostrar_Encuestas") }
        tv_Encuesta.setOnClickListener { irWebJus("mostrar_Encuestas") }

        icon_Tramites.setOnClickListener { DialogoOpcional()}
        tv_Tramites.setOnClickListener { DialogoOpcional()}

        icon_Compas.setOnClickListener {
            val intent = Intent(requireContext(), AppActivity_Companeros::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }
        tv_Compas.setOnClickListener {
            val intent = Intent(requireContext(), AppActivity_Companeros::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
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
         * @return A new instance of fragment Fragment_Apps.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Fragment_Apps().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


    fun irWebJus(Url:String){
        val intent = Intent(requireContext(), AppActivity_WebJus::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        intent.putExtra(Url, true)
        startActivity(intent)
    }


    fun DialogoOpcional() {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = LayoutInflater.from(requireContext())
        val view = inflater.inflate(R.layout.dialogo_opciones_tramites, null)
        builder.setView(view)
        val dialog = builder.create()
        dialog.window?.setGravity(Gravity.CENTER)
        dialog.show()

        val NuevaSolicitud = view.findViewById<TextView>(R.id.txv_NuevaSolicitud)
        val ListadoSolicitud = view.findViewById<TextView>(R.id.txv_MisSolicitudes)

        NuevaSolicitud.setOnClickListener {
            val Intent = Intent(context, AppActivity_TramiteFormulario::class.java)
                startActivity(Intent)
                dialog.dismiss()
            }

        ListadoSolicitud.setOnClickListener {
            val Intent = Intent(context, AppActivity_ListadoTramites::class.java)
            startActivity(Intent)
            dialog.dismiss()
        }

        Handler().postDelayed({
            // Acciones a realizar después de 22000 ms (22 segundos), si es necesario
        }, 22000)
    }

}