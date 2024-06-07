package com.example.juscatamarared.Fragmentos.Apps

import android.app.Dialog
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.juscatamarared.Adaptadores.Tramites_Adapter
import com.example.juscatamarared.Clases.EntornoDeDatos
import com.example.juscatamarared.Clases.TramitesClass
import com.example.juscatamarared.Clases.UsuarioClass
import com.example.juscatamarared.R
import org.json.JSONArray
import org.json.JSONException
import java.util.*
import kotlin.collections.HashMap

class AppActivity_ListadoTramites : AppCompatActivity()  {

    var listView_ListadoTramites: ListView?=null

    //var MyAdapter: Tramites_Adapter?=null

    var dialog: Dialog?  = null

    var User = UsuarioClass.UsuarioClassManager.getUsuarioClass()

    lateinit var Seleccionado:TramitesClass

    //var LayouOpcionesTramites:LinearLayout? =null

    var RadioGroup_Tramites:RadioGroup?=null

    var CondicionBtn:String ="BtnEmpleado"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listado_tramites)


        val window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = this.resources.getColor(R.color.ColorProgress)

        TraerListadoMisTramites("MisSolicitudesDeTramite")





        listView_ListadoTramites = findViewById(R.id.listView_ListadoTramites)
        //listView_ListadoTramites?.setAdapter(MyAdapter)


        listView_ListadoTramites?.setOnItemClickListener { parent, view, position, id ->
            // Aquí manejas el evento de clic en el elemento de la lista
            Seleccionado = EntornoDeDatos.VariablesGlobales.listado_tramites[position]

            DialogoOpcional()

            //Crea un intent para iniciar la segunda actividad
            //val intent = Intent(this, Activity_ReciboSueldo::class.java)

            //Pasa el objeto de RecibosSueldoClass a la segunda actividad
            //intent.putExtra("reciboSeleccionado", Seleccionado)

            // Inicia la segunda actividad
            //startActivity(intent)
        }

        RadioGroup_Tramites = findViewById(R.id.RadioGroup_Tramites)

        if(User?.tipo_usuario_id ==3 ){
            RadioGroup_Tramites?.visibility = View.VISIBLE
        }else{
            RadioGroup_Tramites?.visibility = View.GONE
        }





        val radioButton1: RadioButton = findViewById(R.id.Radio_MisTramites)
        val radioButton2: RadioButton = findViewById(R.id.Radio_TramitesPendientes)




        var lastSelectedButtonId: Int = R.id.Radio_MisTramites // Inicializar con el ID del primer botón

        RadioGroup_Tramites?.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId != -1 && lastSelectedButtonId != checkedId) {
                lastSelectedButtonId = checkedId

                when (checkedId) {
                    R.id.Radio_MisTramites -> {
                        // Ejecutar acción específica para radioButton1
                        TraerListadoMisTramites("MisSolicitudesDeTramite")
                        CondicionBtn = "BtnEmpleado"
                        //Toast.makeText(this, "radio 1", Toast.LENGTH_SHORT).show()
                    }
                    R.id.Radio_TramitesPendientes -> {
                        // Ejecutar acción específica para radioButton2
                        CondicionBtn = "BtnJefe"
                        TraerListadoMisTramites("ConsultaTramitesRecibidos")
                        //Toast.makeText(this, "radio 2", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                lastSelectedButtonId = -1
            }
        }








    }

    fun DialogoOpcional() {
        val builder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.dialogo_resumen_tramite, null)
        builder.setView(view)
        val dialog = builder.create()
        dialog.window?.setGravity(Gravity.CENTER)
        dialog.show()

        var DiasDetalle:String?
        var Documentacion:String?
        var ValorCheckBox:String ? = ""



        if (Seleccionado.formulario_dia_desde == Seleccionado.formulario_dia_hasta){
            DiasDetalle = Seleccionado.formulario_dia_desde.toString()
        }else{
            DiasDetalle= "${Seleccionado.formulario_dia_desde} hasta ${Seleccionado.formulario_dia_hasta}"
        }


        if (Seleccionado.formulario_img_id != "null"){
            Documentacion = "Si"
        }else{Documentacion = "NO"}


        val TvLegajo = view.findViewById<TextView>(R.id.txv_legajo_tramite)
        val TvNombreApellidoEmpleado = view.findViewById<TextView>(R.id.txv_NombreApellido_tramite)
        val TvDependencia = view.findViewById<TextView>(R.id.txv_Dependencia_tramite)
        val TvDescripcionTramita = view.findViewById<TextView>(R.id.txv_Descripcion_tramite)
        val TvDetalledias= view.findViewById<TextView>(R.id.txv_DetalleDias_tramite)
        val TvExplicacion = view.findViewById<TextView>(R.id.txv_Explicacion_tramite)
        val TvDocumentacion = view.findViewById<TextView>(R.id.txv_Domentacion_tramite)

        val checkBox1 = view.findViewById<CheckBox>(R.id.checkBox_AfectaFuncionamiento_tramite)
        val checkBox2 = view.findViewById<CheckBox>(R.id.checkBox_NO_AfectaFuncionamiento_tramite)

        val BtndescargarPDF = view.findViewById<Button>(R.id.btnDescargar_tramite)

        val BtnAprobar = view.findViewById<Button>(R.id.btnAprobar_tramite)
        //val BtnRechazar = view.findViewById<Button>(R.id.btnRechazar_tramite)

        TvLegajo.text="LEGAJO: ${User?.usuario_legajo}"
        TvNombreApellidoEmpleado.text="${Seleccionado.UserOrigen}"
        TvDependencia.text="Dependencia Laboral: ${Seleccionado.AreaOrigenDes}"
        TvDescripcionTramita.text="Trámite: ${Seleccionado.formulario_licencias}"
        TvDetalledias.text="Detalle los Días: ${DiasDetalle}"
        TvExplicacion.text="Explicación: ${Seleccionado.formulario_explicacion}"
        TvDocumentacion.text="Documentación: ${Documentacion}"

        //Toast.makeText(this,"el estado es ${Seleccionado.formulario_estado}", Toast.LENGTH_SHORT).show()


        if (CondicionBtn == "BtnJefe"){
            checkBox1.visibility = View.VISIBLE
            checkBox2.visibility = View.VISIBLE
            BtnAprobar.visibility = View.VISIBLE
            BtndescargarPDF.visibility = View.GONE
        }

        if (CondicionBtn == "BtnEmpleado"){
            checkBox1.visibility = View.GONE
            checkBox2.visibility = View.GONE
            BtnAprobar.visibility = View.GONE

            if(Seleccionado.formulario_estado == "EN ESPERA DE CONFIRMACION POR JEFE DE AREA"){
                BtndescargarPDF.visibility = View.GONE
            }else{
                BtndescargarPDF.visibility = View.VISIBLE
            }

        }





        checkBox1.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                ValorCheckBox = "NO AFECTA EL NORMAL FUNCIONAMIENTO DEL AREA"
                checkBox2.isChecked = false
               // checkBox2.isEnabled = false
            } else {
                checkBox2.isEnabled = true
            }
        }

        checkBox2.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                ValorCheckBox = "SI AFECTA EL NORMAL FUNCIONAMIENTO DEL AREA"
                checkBox1.isChecked = false
                //checkBox1.isEnabled = false
            } else {
                checkBox1.isEnabled = true
            }
        }

        BtndescargarPDF.setOnClickListener{

            val nombrerecibo = "SOLICITUD N°${Seleccionado.formulario_id}_ ${Seleccionado.formulario_fecha_hora_registro}.pdf"
            val imageUrl ="${EntornoDeDatos().URL_General}/FormularioSolicitud.php?formulario_id=${Seleccionado.formulario_id}"
            //val imageUrl = "$URL_SERVIDOR$url_servidor/boleta/Ticket_PDF.php?$accionGet"

            val dm = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val request = DownloadManager.Request(Uri.parse(imageUrl))

            request.allowScanningByMediaScanner()
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, nombrerecibo)
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

            dm.enqueue(request)
        }

        BtnAprobar.setOnClickListener {


            if (checkBox1.isChecked || checkBox2.isChecked ) {
                // El CheckBox está marcado

                val intent = Intent(this, AppActivity_FirmarJefe::class.java)
                intent.putExtra("ValorCheck", ValorCheckBox.toString())
                intent.putExtra("IdFormulario", Seleccionado.formulario_id.toString())
                intent.putExtra("IdUsuarioSeleccionado", Seleccionado.usuario_id.toString())
                startActivity(intent)


            } else {
                    Toast.makeText(this,"Indique si afecta o no el funcionamiento de su area", Toast.LENGTH_SHORT).show()
            }


/*

            if (checkBox1.isChecked) {
                // El CheckBox está marcado
                AccionTramites("AprobarTramitePorJefeDeArea",ValorCheckBox.toString(),Seleccionado.formulario_id.toString(),"APROBADO POR JEFE DE AREA", Seleccionado.usuario_id.toString())
                //Toast.makeText(this,"mandar aprobado y el valor es: ${ValorCheckBox} y el id es ${Seleccionado.formulario_id}", Toast.LENGTH_SHORT).show()
            } else {
                if (checkBox2.isChecked) {
                    AccionTramites("AprobarTramitePorJefeDeArea",ValorCheckBox.toString(),Seleccionado.formulario_id.toString(),"APROBADO POR JEFE DE AREA", Seleccionado.usuario_id.toString())
                    //Toast.makeText(this,"mandar aprobado y el valor es: ${ValorCheckBox}", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this,"Indique si afecta o no el funcionamiento de su area", Toast.LENGTH_SHORT).show()
                }
            }
 */
        }


        Handler().postDelayed({
            // Acciones a realizar después de 22000 ms (22 segundos), si es necesario
        }, 22000)
    }



    fun AccionTramites(Accion:String,afectacion:String, id_fotmulario:String,estado:String, id_solicitante:String){
        val User = UsuarioClass.UsuarioClassManager.getUsuarioClass()
        dialog = Dialog(this)
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.setContentView(R.layout.progress_dialog)
        dialog!!.setCanceledOnTouchOutside(false)
        dialog!!.show()

        var request: StringRequest = object : StringRequest(
            Method.POST, EntornoDeDatos().URL,
            Response.Listener { response ->
                dialog?.dismiss()
                //Toast.makeText(this@AppActivity_ListadoTramites, ""+response, Toast.LENGTH_SHORT).show();
                try {
                    if (response == "AprobadoPorJefeDeArea") {
                        Toast.makeText(this@AppActivity_ListadoTramites,"ACCIÓN EJECUTADA CORRECTAMENTE", Toast.LENGTH_SHORT).show();
                        val intent = Intent(this, AppActivity_ListadoTramites::class.java)
                        finish()
                        startActivity(intent)
                    }
                } catch (e: JSONException) {
                    //Toast.makeText(activity,"algo esta mal",Toast.LENGTH_SHORT).show();
                    e.printStackTrace()
                }
                dialog?.dismiss()
            },
            Response.ErrorListener {
                dialog?.dismiss()
                Toast.makeText(this@AppActivity_ListadoTramites, "REVISE CONEXIÓN A INTERNET", Toast.LENGTH_LONG).show();
            }
        ) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String>? {
                val params: MutableMap<String, String> = java.util.HashMap()
                params["Accion"] = Accion
                params["usuario_id"] = User?.usuario_id.toString()
                params["id_fotmulario"] = id_fotmulario
                params["afectacion"] = afectacion
                params["estado"] = estado
                params["id_solicitante"] = id_solicitante

                return params
            }
        }
        val requestQueue = Volley.newRequestQueue(this@AppActivity_ListadoTramites)
        requestQueue.add(request)
    }


    fun TraerListadoMisTramites(Accion:String){
        EntornoDeDatos.VariablesGlobales.listado_tramites.clear()
        val tramitesAdapter = Tramites_Adapter(this, emptyList(), this)
        tramitesAdapter.updateList(emptyList())

        dialog = Dialog(this@AppActivity_ListadoTramites)
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.setContentView(R.layout.progress_dialog)
        dialog!!.setCanceledOnTouchOutside(false)
        dialog!!.show()


        var request: StringRequest = object : StringRequest(
            Method.POST, EntornoDeDatos().URL,
            Response.Listener { response ->
                dialog?.dismiss()
                //Toast.makeText(this@AppActivity_ListadoTramites, ""+response, Toast.LENGTH_SHORT).show();
                try {
                    val jsonArray = JSONArray(response)
                    if (jsonArray.length() > 0) {
                        //val gson = Gson()
                        val listado = parsearJSONArray(jsonArray)
                        val adapter = Tramites_Adapter(this@AppActivity_ListadoTramites, listado, this)
                        listView_ListadoTramites?.adapter = adapter
                       //Toast.makeText(this,"${listado.size}", Toast.LENGTH_SHORT).show();
                    }else{
                        val adapter = listView_ListadoTramites?.adapter as? Tramites_Adapter
                        adapter?.updateList(emptyList())
                        Toast.makeText(this,"NO HAY TRÁMITES POR EL MOMENTO", Toast.LENGTH_SHORT).show();
                    }
                } catch (e: JSONException) {
                    //Toast.makeText(getApplicationContext(),"VERIFIQUE SUS DATOS",Toast.LENGTH_SHORT).show();
                    e.printStackTrace()
                }
                dialog?.dismiss()
            },
            Response.ErrorListener {
                dialog?.dismiss()
                Toast.makeText(this@AppActivity_ListadoTramites, "REVISE CONEXIÓN A INTERNET", Toast.LENGTH_LONG).show();
            }
        ) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String>? {
                val params: MutableMap<String, String> = HashMap()
                params["Accion"] = Accion
                params["usuario_id"] = User?.usuario_id.toString()
                //esto para poder mostrar las solicitudes a aaprobar por parte del jefe
                params["area_id"] = User?.area_id.toString()
                return params
            }
        }
        val requestQueue = Volley.newRequestQueue(this@AppActivity_ListadoTramites)
        requestQueue.add(request)
    }

    private fun parsearJSONArray(jsonArray: JSONArray): List<TramitesClass> {
        EntornoDeDatos.VariablesGlobales.listado_tramites.clear()
        val lista = mutableListOf<TramitesClass>()

        try {
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val formulario_id= jsonObject.getLong("formulario_id")
                val formulario_licencias= jsonObject.getString("formulario_licencias")
                val formulario_dia_desde= jsonObject.getString("formulario_dia_desde")
                val formulario_dia_hasta= jsonObject.getString("formulario_dia_hasta")
                val formulario_explicacion= jsonObject.getString("formulario_explicacion")
                val usuario_id= jsonObject.getInt("usuario_id")
                val formulario_fecha_hora_registro= jsonObject.getString("formulario_fecha_hora_registro")
                val apr_des_por= jsonObject.getString("apr_des_por")
                val formulario_fecha_hora_aprobacion= jsonObject.getString("formulario_fecha_hora_aprobacion")
                val formulario_estado= jsonObject.getString("formulario_estado")
                val area_origen= jsonObject.getString("area_origen")
                val area_destino= jsonObject.getString("area_destino")
                val formulario_afectacion= jsonObject.getString("formulario_afectacion")
                val UserOrigen= jsonObject.getString("UserOrigen")
                val UserJefe= jsonObject.getString("UserJefe")
                val formulario_img_id= jsonObject.getString("formulario_img_id")
                val url_img= jsonObject.getString("url_img")
                val AreaOrigenDes= jsonObject.getString("AreaOrigenDes")
                val AreaDestinoDes= jsonObject.getString("AreaDestinoDes")

                val Tramites = TramitesClass(formulario_id, formulario_licencias, formulario_dia_desde, formulario_dia_hasta, formulario_explicacion, usuario_id,
                    formulario_fecha_hora_registro, apr_des_por, formulario_fecha_hora_aprobacion, formulario_estado, area_origen, area_destino, formulario_afectacion,
                UserOrigen,UserJefe, formulario_img_id,url_img,AreaOrigenDes,AreaDestinoDes)
                lista.add(Tramites)

                EntornoDeDatos.VariablesGlobales.listado_tramites.add(Tramites)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return lista
    }




}