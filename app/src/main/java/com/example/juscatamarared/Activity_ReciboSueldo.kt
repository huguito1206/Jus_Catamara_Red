package com.example.juscatamarared

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.media.SoundPool
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.view.WindowManager
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.juscatamarared.Clases.RecibirPdfStream
import com.example.juscatamarared.Clases.RecibosSueldoClass
import com.example.juscatamarared.Clases.UsuarioClass
import com.github.barteksc.pdfviewer.PDFView
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener

class Activity_ReciboSueldo : AppCompatActivity() {

    private lateinit var pdfView: PDFView
    private lateinit var progressBar: ProgressBar

    private lateinit var tv_DescargarPdf:TextView
    private lateinit var icono_DescargarPdf:ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recibo_sueldo_pdf)

        val window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = this.resources.getColor(R.color.ColorProgress)

        pdfView = findViewById(R.id.pdf_viewer)
        progressBar = findViewById(R.id.progressBar)

        tv_DescargarPdf = findViewById(R.id.tv_DescargarPdf)
        icono_DescargarPdf = findViewById(R.id.icono_DescargarPdf)

        val reciboSeleccionado = intent.getParcelableExtra<RecibosSueldoClass>("reciboSeleccionado")

        val pdfUrl = reciboSeleccionado?.pdf.toString()
        // Crea una instancia de la clase RecibirPdfStream y ejecuta AsyncTask
        val recibirPdfStream = RecibirPdfStream(pdfView, progressBar)
        recibirPdfStream.execute(pdfUrl)


        tv_DescargarPdf.setOnClickListener {
            iniciarDescarga(reciboSeleccionado?.pdf.toString())
        }

        icono_DescargarPdf.setOnClickListener {
            iniciarDescarga(reciboSeleccionado?.pdf.toString())
        }

    }


    private fun iniciarDescarga(UrlRecibo:String) {
        Toast.makeText(applicationContext, "Descarga Iniciada...", Toast.LENGTH_LONG).show()
        val nombreRecibo = "Recibo Sueldo ${UsuarioClass.UsuarioClassManager.getUsuarioClass()?.usuario_cuil}-${UrlRecibo}"
        val imageUrl = UrlRecibo

        val dm = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val request = DownloadManager.Request(Uri.parse(imageUrl))
        request.allowScanningByMediaScanner()
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "$nombreRecibo")
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        dm.enqueue(request)
    }



    override fun onBackPressed() {
        val intent = Intent(this, Fragment_Inicial::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        intent.putExtra("mostrar_noticias", true)
        startActivity(intent)
    }

}

