package com.example.juscatamarared.Fragmentos

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.juscatamarared.Adaptadores.Noticias_Adaprter
import com.example.juscatamarared.Clases.EntornoDeDatos
import com.example.juscatamarared.Clases.NoticiasClass
import com.example.juscatamarared.Clases.SlowLinearSmoothScroller
import com.example.juscatamarared.Clases.UsuarioClass
import com.example.juscatamarared.Fragment_Inicial
import com.example.juscatamarared.Interface.OnMeGustaClickListener
import com.example.juscatamarared.Login
import com.example.juscatamarared.R
import com.example.juscatamarared.Servicios.NoticiasService
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.json.JSONArray
import org.json.JSONException

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Fragment_Inicial.newInstance] factory method to
 * create an instance of this fragment.
 */
class Fragment_Noticias : Fragment(), OnMeGustaClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var datosCargados = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }


    var progressDialog: Dialog? = null
    var listView_ListadoPublicaciones: RecyclerView? = null
    var MyNoticiaAdapter: Noticias_Adaprter? = null

    var refreshLayout:SwipeRefreshLayout?=null



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_noticias, container, false)

        Noticias("ListadoTodasNoticias")

        listView_ListadoPublicaciones = rootView.findViewById(R.id.listView_ListadoPublicaciones)
        val linearLayoutManager = LinearLayoutManager(context)
        listView_ListadoPublicaciones?.layoutManager = linearLayoutManager

        //ACA MANDO LOS DATOS DE LA CLASE DEL SERVICIO AL ADAPTADOR
        val miAdaptador = Noticias_Adaprter(requireContext(),EntornoDeDatos.VariablesGlobales.listado_noticia,this)
        listView_ListadoPublicaciones?.adapter = miAdaptador


        //controlo la velocidad de la posicion del recycler
        val layoutManager = LinearLayoutManager(context)
        listView_ListadoPublicaciones?.layoutManager = layoutManager

        refreshLayout = rootView.findViewById(R.id.refreshSwipe)
        refreshLayout?.setOnRefreshListener {
            Noticias("ListadoTodasNoticias")
        }


        val btnScrollToTop = rootView.findViewById<FloatingActionButton>(R.id.btnScrollToTop)
        btnScrollToTop.setOnClickListener {
            //listView_ListadoPublicaciones?.scrollToPosition(0)
            val smoothScroller = SlowLinearSmoothScroller(requireContext())
            smoothScroller.targetPosition = 0
            layoutManager.startSmoothScroll(smoothScroller)
        }


        val icono_ChatBot:ImageView = rootView.findViewById(R.id.ChatJusta_Menu)

        icono_ChatBot.setOnClickListener(object: View.OnClickListener{
                override fun onClick(v: View?) {
                    val url_whatsapp = "https://api.whatsapp.com/send?phone=543834773600"
                    val uri = Uri.parse(url_whatsapp)
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    startActivity(intent)
                }
            })

        val opcionesCerrarSesion:ImageView = rootView.findViewById(R.id.opcionesCerrarSesion)
        opcionesCerrarSesion.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                DialogoOpcionalCerrarSession()
            }
        })

        return rootView
    }



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Fragment_Inicial.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Fragment_Noticias().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


    fun DialogoOpcionalCerrarSession() {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = LayoutInflater.from(requireContext())
        val view = inflater.inflate(R.layout.dialogo_menu_sesion, null)
        builder.setView(view)
        val dialog = builder.create()
        dialog.window?.setGravity(Gravity.TOP)
        dialog.show()

        val cerrar = view.findViewById<TextView>(R.id.txv_cerrar)
        cerrar.setOnClickListener {
            // Aceptar
            val alertDialogBuilder = AlertDialog.Builder(requireContext())
            alertDialogBuilder.setMessage("¿Estás seguro de cerrar sesión?")
                .setCancelable(false)
                .setPositiveButton("Sí") { _, _ ->
                    dialog.dismiss()

                    //limpio la clave para evitar errores
                    val User = UsuarioClass.UsuarioClassManager.getUsuarioClass()
                    User?.usuario_dni = ""
                    User?.usuario_clave = ""

                    // Si la respuesta es afirmativa, agrega aquí la lógica para cerrar la sesión
                    val intent = Intent(requireContext(), Login::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    val intent1 = Intent(requireContext(), NoticiasService::class.java)
                    requireContext().stopService(intent1)

                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.cancel()
                }
            alertDialogBuilder.create().show()
        }

        Handler().postDelayed({
            // Acciones a realizar después de 22000 ms (22 segundos), si es necesario
        }, 22000)
    }





    private fun Noticias(Accion:String){

        val User = UsuarioClass.UsuarioClassManager.getUsuarioClass()

        var request: StringRequest = object : StringRequest(
            Method.POST, EntornoDeDatos().URL,
            Response.Listener { response ->
                refreshLayout?.setRefreshing(false)
                //Toast.makeText(activity, ""+response, Toast.LENGTH_SHORT).show();
                try {
                    val jsonArray = JSONArray(response)
                    if (jsonArray.length() > 0) {

                            //val gson = Gson()
                        val listado = parsearJSONArray(jsonArray)
                        val miAdaptador = Noticias_Adaprter(requireContext(),listado,this)
                        listView_ListadoPublicaciones?.adapter = miAdaptador

                    //Toast.makeText(activity,"${listado.size}", Toast.LENGTH_SHORT).show();
                    //EntornoDeDatos.VariablesGlobales.listado_noticia.add(listado)


                    }else{
                        //Toast.makeText(activity,"NO HAY NOTICIAS POR EL MOMENTO", Toast.LENGTH_SHORT).show();
                    }
                } catch (e: JSONException) {
                    //Toast.makeText(getApplicationContext(),"VERIFIQUE SUS DATOS",Toast.LENGTH_SHORT).show();
                    e.printStackTrace()
                }
                refreshLayout?.setRefreshing(false)
            },
            Response.ErrorListener {
                refreshLayout?.setRefreshing(false)
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



    override fun onMeGustaClick(Accion: String, noticia_id: Int) {
        val User = UsuarioClass.UsuarioClassManager.getUsuarioClass()

        var request: StringRequest = object : StringRequest(
            Method.POST, EntornoDeDatos().URL,
            Response.Listener { response ->
                refreshLayout?.setRefreshing(false)
                //Toast.makeText(activity, ""+response, Toast.LENGTH_SHORT).show();
                try {
                    if (response == "MeGustaExitoso") {
                        //Toast.makeText(activity,"Genial entro perfecto", Toast.LENGTH_SHORT).show();
                    }
                } catch (e: JSONException) {
                    //Toast.makeText(activity,"algo esta mal",Toast.LENGTH_SHORT).show();
                    e.printStackTrace()
                }
                refreshLayout?.setRefreshing(false)
            },
            Response.ErrorListener {
                refreshLayout?.setRefreshing(false)
                Toast.makeText(activity, "REVISE CONEXIÓN A INTERNET", Toast.LENGTH_LONG).show();
            }
        ) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String>? {
                val params: MutableMap<String, String> = HashMap()
                params["Accion"] = Accion
                params["usuario_id"] = User?.usuario_id.toString()
                params["noticia_id"] = noticia_id.toString()
                return params
            }
        }
        val requestQueue = Volley.newRequestQueue(activity)
        requestQueue.add(request)
    }


}


