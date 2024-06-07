package com.example.juscatamarared.Fragmentos.Apps

import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.TextKeyListener.clear
import android.util.Base64
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.core.view.drawToBitmap
import com.example.juscatamarared.Clases.EntornoDeDatos
import com.example.juscatamarared.Clases.SignatureView
import com.example.juscatamarared.Clases.SlowLinearSmoothScroller
import com.example.juscatamarared.Clases.UsuarioClass
import com.example.juscatamarared.Fragment_Inicial
import com.example.juscatamarared.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.core.RepoManager.clear
import de.hdodenhof.circleimageview.CircleImageView
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.ByteArrayOutputStream
import java.io.IOException


class AppActivity_FirmarJefe : AppCompatActivity() {

    var dialog: Dialog?  = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.app_activity_firmar_jefe)

        val window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = this.resources.getColor(R.color.ColorProgress)


        //Recibo los valores para aplicar al metodo de carga
        val intent = intent
        val ValorCheck = intent.getStringExtra("ValorCheck")
        val IdFormulario = intent.getStringExtra("IdFormulario")
        val IdUsuarioSeleccionado = intent.getStringExtra("IdUsuarioSeleccionado")


        val signatureView: SignatureView = findViewById(R.id.signatureView)


        val clearSignatureButton = findViewById<FloatingActionButton>(R.id.clearSignatureButton)
        clearSignatureButton.setOnClickListener {
            signatureView.clear()
            }




        val btnAprobar_tramite:Button = findViewById(R.id.btnAprobar_tramite_firma)
        btnAprobar_tramite.setOnClickListener {
            //Toast.makeText(this@AppActivity_FirmarJefe, "valor check es ${ValorCheck} el id formula ${IdFormulario} y id User Form ${IdUsuarioSeleccionado}", Toast.LENGTH_LONG).show();

            if (signatureView.hasSignature()) {
                val signatureBitmap = signatureView.getSignatureBitmap()
                //Toast.makeText(this@AppActivity_FirmarJefe, "firma valida", Toast.LENGTH_LONG).show();
                uploadImageToServer(signatureBitmap,IdFormulario.toString(),ValorCheck.toString(),"APROBADO POR JEFE DE AREA",IdUsuarioSeleccionado.toString(),"AprobarTramitePorJefeDeArea")
            } else {
                // No se ha dibujado una firma, muestra un mensaje o realiza alguna acción
                Toast.makeText(this, "Por favor, dibuje una firma", Toast.LENGTH_SHORT).show()
            }
        }

        val btnRechazar_tramite:Button = findViewById(R.id.btnRechazar_tramite)
        btnRechazar_tramite.setOnClickListener {
            if (signatureView.hasSignature()) {
                val signatureBitmap = signatureView.getSignatureBitmap()
                //Toast.makeText(this@AppActivity_FirmarJefe, "firma valida", Toast.LENGTH_LONG).show();
                uploadImageToServer(signatureBitmap,IdFormulario.toString(),ValorCheck.toString(),"RECHAZADO POR JEFE DE AREA",IdUsuarioSeleccionado.toString(),"AprobarTramitePorJefeDeArea")
            } else {
                // No se ha dibujado una firma, muestra un mensaje o realiza alguna acción
                Toast.makeText(this, "Por favor, dibuje una firma", Toast.LENGTH_SHORT).show()
            }
        }

    }


    fun hasSignature(bitmap: Bitmap): Boolean {
        for (x in 0 until bitmap.width) {
            for (y in 0 until bitmap.height) {
                if (bitmap.getPixel(x, y) != Color.TRANSPARENT) {
                    return true
                }
            }
        }
        return false
    }





    fun uploadImageToServer(imageBitmap: Bitmap?,FormularioId:String,Afectacion:String,Estado:String,SolicitanteId:String, Accion:String) {

        dialog = Dialog(this)
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.setContentView(R.layout.progress_dialog)
        dialog!!.setCanceledOnTouchOutside(false)
        dialog!!.show()

        val User = UsuarioClass.UsuarioClassManager.getUsuarioClass()
        val url = EntornoDeDatos().URL
        val byteArrayOutputStream = ByteArrayOutputStream()
        imageBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        val encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT)

        val client = OkHttpClient()

        val formBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("image", "image.jpg", RequestBody.create("image/jpeg".toMediaTypeOrNull(), byteArray))
            .addFormDataPart("id_fotmulario", FormularioId)
            .addFormDataPart("afectacion", Afectacion)
            .addFormDataPart("estado", Estado)
            .addFormDataPart("id_solicitante", SolicitanteId)
            .addFormDataPart("Accion", Accion)
            .addFormDataPart("usuario_id",User?.usuario_id.toString())
            .build()

        val request = Request.Builder()
            .url(url)
            .post(formBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Maneja la falla en la solicitud
                dialog?.dismiss()
                Toast.makeText(this@AppActivity_FirmarJefe, "REVISE CONEXIÓN A INTERNET", Toast.LENGTH_LONG).show();
            }

            override fun onResponse(call: Call, response: okhttp3.Response) {
                runOnUiThread {

                    dialog?.dismiss()

                    if (response.isSuccessful) {
                        // La solicitud fue exitosa, puedes manejar la respuesta del servidor aquí
                        //Toast.makeText(this@Activity_NuevaNoticia, "Exito ${response.message}", Toast.LENGTH_SHORT).show()
                        val responseData = response.body?.string()
                        Toast.makeText(this@AppActivity_FirmarJefe, "cargado con exito", Toast.LENGTH_SHORT).show()


                        val intent = Intent(this@AppActivity_FirmarJefe, Fragment_Inicial::class.java)
                        intent.putExtra("accion", "mostrar_apps")
                        startActivity(intent)
                        finish()

                        // Realiza acciones con la respuesta del servidor
                    } else {
                        // La solicitud no fue exitosa, maneja el error aquí
                        val responseData = response.body?.string()
                        //Toast.makeText(this@Activity_NuevaNoticia, responseData, Toast.LENGTH_SHORT).show()

                        Toast.makeText(this@AppActivity_FirmarJefe, "ERROR AL CARGAR, REINTENTE", Toast.LENGTH_LONG).show();

                    }
                }
            }
        })
    }



}
