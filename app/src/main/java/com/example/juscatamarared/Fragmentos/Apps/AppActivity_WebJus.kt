package com.example.juscatamarared.Fragmentos.Apps

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.webkit.JsResult
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import com.example.juscatamarared.Clases.EntornoDeDatos
import com.example.juscatamarared.Clases.UsuarioClass
import com.example.juscatamarared.R

class AppActivity_WebJus : AppCompatActivity() {

    lateinit var url:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.app_activity_web_jus)

        val window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = this.resources.getColor(R.color.ColorProgress)

        val User = UsuarioClass.UsuarioClassManager.getUsuarioClass()

        val webView: WebView = findViewById(R.id.webview)

        webView.webChromeClient = object : WebChromeClient() {
            override fun onJsAlert(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean {
                // Aquí puedes manejar las alertas JavaScript y mostrarlas como quieras, por ejemplo, mediante un Toast
                Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
                result?.confirm() // Confirmar la alerta para que se cierre
                return true // Indica que la alerta ha sido manejada
            }
        }


        // Habilita JavaScript (opcional, dependiendo de tus necesidades)
        webView.settings.javaScriptEnabled = true

        // Agrega un WebViewClient para gestionar la navegación dentro del WebView
        webView.webViewClient = WebViewClient()

        // Carga una URL específica en el WebView

        //acciones para mostrar pantallas de acuerdo a accion previa
        if (intent.getBooleanExtra("mostrar_webJus", false)) { url = "https://juscatamarca.gob.ar/" }

        if (intent.getBooleanExtra("mostrar_CalendarioWeb", false)) { url = "https://juscatamarca.gob.ar/asuetosyferiados/public/" }

        if (intent.getBooleanExtra("mostrar_Encuestas", false)) { url = "${EntornoDeDatos().URL_General}/Encuesta/index.php?usuario_id=${User?.usuario_id.toString()}"}


        webView.loadUrl(url)

    }
}