package com.example.juscatamarared

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.juscatamarared.Clases.EntornoDeDatos
import com.example.juscatamarared.Clases.RecuperarcionClaveClass
import com.example.juscatamarared.Clases.UsuarioClass
import org.json.JSONException

class Activity_RecuperarClave : AppCompatActivity() {

    var dialog: Dialog?  = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recuperar_clave)

        val window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = this.resources.getColor(R.color.ColorProgress)


        val User = RecuperarcionClaveClass.RecuperacionClaveClassManager.getRecuperarcionClaveClass()
        val Titulo = findViewById<TextView>(R.id.TV_descripcionCorreoRC)

        Titulo.text= "Se envió un codigo de verificacion a ${User?.usuario_email.toString()}, " +
                "complete los campos.(revise correos SPAM, en caso de no encotrar el mensaje)"

        val Siguiente = findViewById<TextView>(R.id.TvSiguienteRC)

        val num1=findViewById<EditText>(R.id.editText1)
        val num2=findViewById<EditText>(R.id.editText2)
        val num3=findViewById<EditText>(R.id.editText3)
        val num4=findViewById<EditText>(R.id.editText4)
        val num5=findViewById<EditText>(R.id.editText5)
        val num6=findViewById<EditText>(R.id.editText6)


        num1.addTextChangedListener(createTextWatcher(num1, num2));
        num2.addTextChangedListener(createTextWatcher(num2, num3));
        num3.addTextChangedListener(createTextWatcher(num3, num4));
        num4.addTextChangedListener(createTextWatcher(num4, num5));
        num5.addTextChangedListener(createTextWatcher(num5, num6));


        Siguiente.setOnClickListener {
            if (num1.text.toString().isEmpty() || num2.text.toString().isEmpty()|| num3.text.toString().isEmpty()|| num4.text.toString().isEmpty()|| num5.text.toString().isEmpty()|| num6.text.toString().isEmpty()) {
                Toast.makeText(this@Activity_RecuperarClave, "Complete todos los campos", Toast.LENGTH_SHORT).show()
            }else{
                val CodigoCompleto = "${num1.text.toString()}${num2.text.toString()}${num3.text.toString()}${num4.text.toString()}${num5.text.toString()}${num6.text.toString()}"

                if (CodigoCompleto.equals(User?.codigo_recuperacion.toString())){
                    //Toast.makeText(this@Activity_RecuperarClave, "Son iguales", Toast.LENGTH_SHORT).show()
                    DialogoOpcional()
                }else{
                    Toast.makeText(this@Activity_RecuperarClave, "CÓDIGO INCORRECTO", Toast.LENGTH_SHORT).show()
                }

            }
        }


    }


    private fun createTextWatcher(currentEditText: EditText, nextEditText: EditText): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {
                // No necesitas implementar este método
            }

            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                // No necesitas implementar este método
            }

            override fun afterTextChanged(editable: Editable?) {
                if (!editable.isNullOrEmpty()) {
                    nextEditText.requestFocus()
                }
            }
        }
    }


    fun DialogoOpcional() {
        val builder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.dialogo_cambio_clave_primer_inicio, null)
        builder.setView(view)
        val dialog = builder.create()
        dialog.window?.setGravity(Gravity.CENTER)
        dialog.show()

        val editTextPassword1:EditText? = view.findViewById(R.id.edt_PrimeraClaveDialog)
        val editTextPassword2:EditText?= view.findViewById(R.id.edt_SegundaClaveDialog)
        val CambiarClave = view.findViewById<Button>(R.id.BtnCambiarClave)

        CambiarClave.setOnClickListener {
            val password1 = editTextPassword1?.text.toString()
            val password2 = editTextPassword2?.text.toString()

            // Verifica que los campos no estén vacíos
            if (password1.isNotEmpty() && password2.isNotEmpty()) {
                if (password1 == password2) {
                    //Toast.makeText(this, "Las contraseñas coinciden", Toast.LENGTH_SHORT).show()
                    cambiarClave("CambiarClaveAlIniciar",password2)
                    //dialog.dismiss()

                } else {
                    // Las contraseñas no coinciden, muestra un mensaje de error
                    Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Al menos uno de los campos está vacío, muestra un mensaje de error
                Toast.makeText(this, "Por favor, completa ambos campos", Toast.LENGTH_SHORT).show()
            }

        }

        Handler().postDelayed({
            // Acciones a realizar después de 22000 ms (22 segundos), si es necesario
        }, 22000)
    }


    fun cambiarClave (Accion: String,NuevaClave:String) {
        val User = RecuperarcionClaveClass.RecuperacionClaveClassManager.getRecuperarcionClaveClass()
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
                //Toast.makeText(this@Activity_NuevaNoticia, ""+response, Toast.LENGTH_SHORT).show();
                try {
                    if (response == "CambioExitoso") {
                        Toast.makeText(this@Activity_RecuperarClave,"Inicie Sesión con su Nueva Clave", Toast.LENGTH_SHORT).show();
                        val intent = Intent(this@Activity_RecuperarClave, Login::class.java)
                        // intent.putExtra("accion", "mostrar_noticias")
                        startActivity(intent)
                        finish()
                    }
                } catch (e: JSONException) {
                    Toast.makeText(this,"algo salió mal, reintente",Toast.LENGTH_SHORT).show();
                    e.printStackTrace()
                }
                dialog?.dismiss()
            },
            Response.ErrorListener {
                dialog?.dismiss()
                Toast.makeText(this@Activity_RecuperarClave, "REVISE CONEXIÓN A INTERNET", Toast.LENGTH_LONG).show();
            }
        ) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String>? {
                val params: MutableMap<String, String> = java.util.HashMap()
                params["Accion"] = Accion
                params["NuevaClave"] = NuevaClave
                params["usuario_id"] = User?.usuario_id.toString()
                return params
            }
        }
        val requestQueue = Volley.newRequestQueue(this@Activity_RecuperarClave)
        requestQueue.add(request)
    }



}