package com.example.juscatamarared.Fragmentos

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
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
import com.example.juscatamarared.Clases.EntornoDeDatos
import com.example.juscatamarared.Clases.ImageDialog
import com.example.juscatamarared.Clases.PublicacionesUserClass
import com.example.juscatamarared.Clases.UsuarioClass
import com.example.juscatamarared.Fragmentos.Apps.AppActivity_perfil_companero
import com.example.juscatamarared.Fragmentos.Perfil.PerfilActivity_CambiarFotoPerfil
import com.example.juscatamarared.R
import com.example.juscatamarared.Servicios.ActualizarPerfilServices
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.json.JSONArray
import org.json.JSONException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Fragment_MiPerfil.newInstance] factory method to
 * create an instance of this fragment.
 */
class Fragment_MiPerfil : Fragment(){



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


    val User = UsuarioClass.UsuarioClassManager.getUsuarioClass()
    var listView: ListView?=null
    var MyAdapter: Publicacion_User_Adapter?=null
    var dialog: Dialog?  = null
    private lateinit var Mensaje:TextView


    private lateinit var FotoPerfil: ImageView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment__mi_perfil, container, false)

        EntornoDeDatos.VariablesGlobales.setMostraOcultarDelete("Mostrar")

        val Nombre = rootView.findViewById<TextView>(R.id.MiPerfil_Nombre)
        val Cargo = rootView.findViewById<TextView>(R.id.MiPerfil_cargo)
        val Area = rootView.findViewById<TextView>(R.id.MiPerfil_area)

        FotoPerfil = rootView.findViewById(R.id.MiPerfil_imgPerfil)
        val CambiarFotoPerfil = rootView.findViewById<ImageView>(R.id.MiPerfil_cambiarFoto)

        val Profesion = rootView.findViewById<EditText>(R.id.MiPerfil_profesion)
        val CorreoElectronico= rootView.findViewById<EditText>(R.id.MiPerfil_correo)
        val Fec_nacimiento= rootView.findViewById<EditText>(R.id.MiPerfil_fec_nacimiento)
        val Whatsapp= rootView.findViewById<EditText>(R.id.MiPerfil_whatsapp)
        val Direccion = rootView.findViewById<EditText>(R.id.MiPerfil_direccion)
        val Facebook = rootView.findViewById<EditText>(R.id.MiPerfil_facebook)
        val Instagram = rootView.findViewById<EditText>(R.id.MiPerfil_instagram)
        val Biografia = rootView.findViewById<EditText>(R.id.MiPerfil_Biografia)


        EntornoDeDatos.VariablesGlobales.MostraOcultarDelete == "Mostrar"


        Nombre.text = "${User?.usuario_nombre} ${User?.usuario_apellido}"
        Cargo.text="${User?.cargo_descripcion}"
        Area.text="${User?.area_descripcion}"




        //aca veo lo de la foto de perfil
        //Toast.makeText(requireContext(),"Sin cambios" +User?.usuario_foto_perfil,Toast.LENGTH_LONG).show()


        Glide.with(requireContext())
            .load(User?.usuario_foto_perfil.toString())
            .centerCrop()
            .placeholder(R.drawable.ic_person)
            .error(R.drawable.ic_person)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .into(FotoPerfil)

        //cargarImagen(User?.usuario_foto_perfil.toString())


        FotoPerfil.setOnClickListener {
            val imageDialog = ImageDialog(requireContext(), User?.usuario_foto_perfil.toString())
            imageDialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            imageDialog.show()
        }


        Profesion.text = Editable.Factory.getInstance().newEditable("${User?.usuario_profesion}")
        CorreoElectronico.text = Editable.Factory.getInstance().newEditable("${User?.usuario_email}")
        Fec_nacimiento.text = Editable.Factory.getInstance().newEditable("${User?.usuario_fecha_nacimiento}")
        Whatsapp.text = Editable.Factory.getInstance().newEditable("${User?.usuario_telefono}")
        Direccion.text = Editable.Factory.getInstance().newEditable("${User?.usuario_casa_numero}, ${User?.usuario_calle}, ${User?.usuario_depto}, ${User?.usuario_provincia}")
        Facebook.text = Editable.Factory.getInstance().newEditable("${User?.usuario_facebook}")
        Instagram.text = Editable.Factory.getInstance().newEditable("${User?.usuario_instagram}")
        Biografia.text = Editable.Factory.getInstance().newEditable("${User?.usuario_biografia}")


        Mensaje = rootView.findViewById(R.id.MiPerfil_mensaje)
        Mensaje.text="Mis Publicaciones"



        listView =  rootView.findViewById(R.id.MiPerfil_listViewNoticias)
        //MyPdfAdapter = ReciboSueldo_Adapter(requireContext(),this)
        listView?.setAdapter(MyAdapter)

        MisPublicaciones("PublicacionesDeUsuario", User?.usuario_id.toString())


        // Deshabilitar la edición inicialmente
        Profesion.isEnabled = false
        Profesion.isEnabled = false
        CorreoElectronico.isEnabled = false
        Fec_nacimiento.isEnabled = false
        Whatsapp.isEnabled = false
        Direccion.isEnabled = false
        Facebook.isEnabled = false
        Instagram.isEnabled = false
        Biografia.isEnabled=false

        val currentDate = Calendar.getInstance()


        CambiarFotoPerfil.setOnClickListener {
            val intent = Intent(requireContext(), PerfilActivity_CambiarFotoPerfil::class.java)
            startActivity(intent)
            //requireActivity().finish()
        }

        var edicionHabilitada = false
        val btnScrollToTop = rootView.findViewById<FloatingActionButton>(R.id.btnScroll_configuracion)
        btnScrollToTop.setOnClickListener {

            if (edicionHabilitada) {
                // Cambiar a icono original y deshabilitar la edición
                btnScrollToTop.setImageResource(R.drawable.ic_services_24)
                Profesion.isEnabled = false
                CorreoElectronico.isEnabled = false
                Fec_nacimiento.isEnabled = false
                Whatsapp.isEnabled = false
                Direccion.isEnabled = false
                Facebook.isEnabled = false
                Instagram.isEnabled = false
                Biografia.isEnabled=false

                EditarDatos("EditarDatosPerfil",Profesion?.text.toString(),CorreoElectronico.text.toString(),
                    Fec_nacimiento.text.toString(),Whatsapp.text.toString(),Facebook.text.toString(),Instagram.text.toString(),Biografia.text.toString(),User?.usuario_clave.toString())

            }else{
                // Cambiar a un nuevo icono y habilitar la edición
                btnScrollToTop.setImageResource(R.drawable.save_as)
                Profesion.isEnabled = true
                CorreoElectronico.isEnabled = true
                Fec_nacimiento.isEnabled = true
                Whatsapp.isEnabled =true
                Direccion.isEnabled = false
                Facebook.isEnabled = true
                Instagram.isEnabled =true
                Biografia.isEnabled= true

                // Enfocar en el EditText
                Profesion.requestFocus()

                // Colocar el cursor al final del texto
                Profesion.setSelection(Profesion.text.length)

                // Mostrar el teclado
                val imm = rootView.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(Profesion, InputMethodManager.SHOW_IMPLICIT)


                Fec_nacimiento.setOnClickListener {
                    val datePickerDialog = DatePickerDialog(
                        rootView.context,
                        DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                            // Actualizar el texto del EditText con la fecha seleccionada
                            val selectedDate = Calendar.getInstance()
                            selectedDate.set(year, month, dayOfMonth)

                            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                            Fec_nacimiento.setText(dateFormat.format(selectedDate.time))
                        },

                        currentDate.get(Calendar.YEAR),
                        currentDate.get(Calendar.MONTH),
                        currentDate.get(Calendar.DAY_OF_MONTH)
                    )
                    //Mostrar el DatePickerDialog
                    datePickerDialog.show()
                }

            }

            // Invertir el estado de la edición
            edicionHabilitada = !edicionHabilitada
        }




        return rootView
    }



    private fun EditarDatos (Accion:String,Profesion:String, Correo:String, FecNacimiento:String,
    Whatsapp:String,Facebook:String,Instagram:String,Biografia:String,
                             Clave:String){
        val User = UsuarioClass.UsuarioClassManager.getUsuarioClass()
        dialog = Dialog(requireContext())
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
                    if (response == "Edicion Correcta") {
                        Toast.makeText(requireContext(),"Actulizado Correctamante", Toast.LENGTH_SHORT).show();
                          //aca ejecuto un servicio para actualizar los datos

                        val intentNoti = Intent(requireContext(), ActualizarPerfilServices::class.java)
                        requireContext().startService(intentNoti)
                    }
                } catch (e: JSONException) {
                    Toast.makeText(activity,"No se pudo editar",Toast.LENGTH_SHORT).show();
                    e.printStackTrace()
                }
                dialog?.dismiss()
            },
            Response.ErrorListener {
                dialog?.dismiss()
                //listView?.removeHeaderView(loadingHeaderView)
                Toast.makeText(requireContext(), "REVISE CONEXIÓN A INTERNET", Toast.LENGTH_LONG).show();
            }
        ) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String>? {
                val params: MutableMap<String, String> = HashMap()
                params["Accion"] = Accion
                params["usuario_id"] = User?.usuario_id.toString()
                params["FecNacimiento"] = FecNacimiento
                params["Correo"] = Correo
                params["Biografia"] = Biografia
                params["Whatsapp"] = Whatsapp
                params["Profesion"] = Profesion
                params["Clave"] = Clave
                params["Facebook"] = Facebook
                params["Instagram"] = Instagram

                return params
            }
        }
        val requestQueue = Volley.newRequestQueue(requireContext())
        requestQueue.add(request)
    }





    private fun MisPublicaciones(Accion:String, UserId:String){
        EntornoDeDatos.VariablesGlobales.ListadoPublicacionesUser.clear()
        MyAdapter?.updateList(emptyList())

        dialog = Dialog(requireContext())
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
                        val miAdaptador = Publicacion_User_Adapter(requireContext(),listado,
                            AppActivity_perfil_companero(),this)

                        listView?.adapter = miAdaptador

                        val tempListView: ListView? = listView

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
                Toast.makeText(requireContext(), "REVISE CONEXIÓN A INTERNET", Toast.LENGTH_LONG).show();
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
        val requestQueue = Volley.newRequestQueue(requireContext())
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

    fun cargarImagen(url: String) {
        Glide.with(requireContext())
            .load(url)
            .centerCrop()
            .placeholder(R.drawable.ic_person)
            .error(R.drawable.ic_person)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .into(FotoPerfil)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Fragment_MiPerfil.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            com.example.juscatamarared.Fragmentos.Fragment_MiPerfil().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }


    }


}