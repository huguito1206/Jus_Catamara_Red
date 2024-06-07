package com.example.juscatamarared

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.graphics.SurfaceTexture
import android.hardware.Camera
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CaptureRequest
import android.media.ImageReader
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.TextureView
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Nueva_Noticia_pruebas : AppCompatActivity() {

    private lateinit var textureView: TextureView
    private lateinit var camera: Camera
    private lateinit var imageView: ImageView

    private var camera1: Camera? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.aaaa)

        textureView = findViewById(R.id.textureView)
        imageView = findViewById(R.id.imageView)

        // Solicitar permiso de cámara si no está otorgado
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE)
        } else {
            startCamera()
        }

        val btnCapture = findViewById<Button>(R.id.btnCapture)
        btnCapture.setOnClickListener { capturarImagen() }
    }

    private fun startCamera() {
        try {
            camera = Camera.open()
            textureView.surfaceTextureListener = surfaceTextureListener
            camera.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private val surfaceTextureListener = object : TextureView.SurfaceTextureListener {
        override fun onSurfaceTextureAvailable(surface: android.graphics.SurfaceTexture, width: Int, height: Int) {
            try {
                camera.setPreviewTexture(surface)
                camera.startPreview()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        override fun onSurfaceTextureSizeChanged(surface: android.graphics.SurfaceTexture, width: Int, height: Int) {}
        override fun onSurfaceTextureDestroyed(surface: android.graphics.SurfaceTexture): Boolean = false
        override fun onSurfaceTextureUpdated(surface: android.graphics.SurfaceTexture) {}
    }

    private fun capturarImagen() {
        camera?.takePicture(null, null, { data, camera ->
            // Maneja la imagen capturada
            // Liberar la cámara después de capturar la imagen
            camera.release()
        })
    }

    private val pictureCallback = Camera.PictureCallback { data, _ ->
        val bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
        imageView.setImageBitmap(bitmap)
        imageView.visibility = View.VISIBLE

        // Detener la vista previa de la cámara después de capturar la imagen
        camera.stopPreview()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Liberar la cámara cuando se cierre la actividad
        camera.release()
    }

    companion object {
        private const val CAMERA_PERMISSION_CODE = 100
    }



}




