package com.example.juscatamarared.Clases

import android.os.AsyncTask
import android.view.View
import android.widget.ProgressBar
import com.github.barteksc.pdfviewer.PDFView
import java.io.BufferedInputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class RecibirPdfStream(private val pdfView: PDFView, private val progressBar: ProgressBar) :
    AsyncTask<String, Void, InputStream>() {

    override fun doInBackground(vararg strings: String?): InputStream? {
        var inputStream: InputStream? = null
        try {
            val url = URL(strings[0])
            val urlConnection = url.openConnection() as HttpURLConnection
            if (urlConnection.responseCode == 200) {
                inputStream = BufferedInputStream(urlConnection.inputStream)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return inputStream
    }

    override fun onPostExecute(inputStream: InputStream?) {
        // Cargar y mostrar el PDF
        inputStream?.let {
            pdfView.fromStream(it).load()
        }
        // Ocultar la barra de progreso
        progressBar.visibility = View.GONE
    }
}
