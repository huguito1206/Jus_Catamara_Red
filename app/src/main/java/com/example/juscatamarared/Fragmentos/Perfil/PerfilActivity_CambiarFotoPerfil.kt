package com.example.juscatamarared.Fragmentos.Perfil

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.media.ExifInterface
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.util.Base64
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.example.juscatamarared.Clases.EntornoDeDatos
import com.example.juscatamarared.Clases.UsuarioClass
import com.example.juscatamarared.Fragment_Inicial
import com.example.juscatamarared.R
import com.example.juscatamarared.Servicios.ActualizarPerfilServices
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PerfilActivity_CambiarFotoPerfil : AppCompatActivity() {

    var dialog: Dialog?  = null

    var TomarFoto:ImageView? = null
    var BuscarEnGaleria:ImageView?=null
    var imageView:ImageView?= null
    var btnSubir:Button?=null

    private val REQUEST_IMAGE_CAPTURE = 1
    private val CAMERA_PERMISSION_REQUEST_CODE = 101

    private var photoFile: File? = null

    private val PICK_IMAGE_REQUEST = 1 // Puedes usar cualquier número que desees


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nueva_noticia)

        val window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = this.resources.getColor(R.color.ColorProgress)

        btnSubir = findViewById(R.id.btnSubir)
        btnSubir!!.isEnabled = false
        btnSubir!!.text= "Cambiar Foto"

        val close_noti = findViewById<ImageView>(R.id.close_noti)
        close_noti.setVisibility(View.GONE) //se puede ocultar asi

        val editText = findViewById<EditText>(R.id.editText)
        editText.visibility = View.GONE // Asi tambien se puede ocultar

        imageView = findViewById(R.id.imageView)

        BuscarEnGaleria = findViewById(R.id.btnBuscar)
        TomarFoto = findViewById(R.id.camera_capture)




        BuscarEnGaleria?.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                //aca abre la galeria para seleccionar una imagen
                val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST)

            }
        })


        TomarFoto?.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                requestCameraPermissions()
            }
        })


        btnSubir?.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                val drawable = imageView?.drawable
                if (drawable is BitmapDrawable) {
                    val imageBitmap = drawable.bitmap
                    // Aquí puedes usar imageBitmap para cargar la imagen al servidor
                    uploadImageToServer(imageBitmap, "CambiarFotoPerfil")
                } else {
                    // El ImageView no contiene una imagen, toma medidas adicionales o muestra un mensaje de error
                }

            }
        })



    }



    fun uploadImageToServer(imageBitmap: Bitmap?,Accion:String) {

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
                Toast.makeText(this@PerfilActivity_CambiarFotoPerfil, "REVISE CONEXIÓN A INTERNET", Toast.LENGTH_LONG).show();
            }

            override fun onResponse(call: Call, response: okhttp3.Response) {
                runOnUiThread {

                    val intentNoti = Intent(this@PerfilActivity_CambiarFotoPerfil, ActualizarPerfilServices::class.java)
                    startService(intentNoti)

                    if (response.isSuccessful) {

                        //arracon servicio de actualizar perfil

                        // La solicitud fue exitosa, puedes manejar la respuesta del servidor aquí
                        //Toast.makeText(this@Activity_NuevaNoticia, "Exito ${response.message}", Toast.LENGTH_SHORT).show()
                        val responseData = response.body?.string()
                        //Toast.makeText(this@Activity_NuevaNoticia, responseData, Toast.LENGTH_SHORT).show()

                        val handler = Handler()

                        // Define el tiempo de retardo en milisegundos (por ejemplo, 3000 ms = 3 segundos)
                        val delayMillis: Long = 4000

                        handler.postDelayed({
                            val intent = Intent(this@PerfilActivity_CambiarFotoPerfil, Fragment_Inicial::class.java)
                            intent.putExtra("accion", "mostrar_MiPerfil")
                            startActivity(intent)
                            dialog?.dismiss()
                            finish()
                        }, delayMillis)


                        // Realiza acciones con la respuesta del servidor
                    } else {
                        // La solicitud no fue exitosa, maneja el error aquí
                        val responseData = response.body?.string()
                        //Toast.makeText(this@Activity_NuevaNoticia, responseData, Toast.LENGTH_SHORT).show()

                        Toast.makeText(this@PerfilActivity_CambiarFotoPerfil, "ERROR AL CARGAR, REINTENTE", Toast.LENGTH_LONG).show();

                    }
                }
            }
        })
    }



    override fun onBackPressed() {
        val intent = Intent(this, Fragment_Inicial::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        intent.putExtra("mostrar_MiPerfil", true)
        startActivity(intent)
    }




    //Aca va todo para la captura desde la camara

    private fun requestCameraPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE)
        } else {
            // Los permisos de la cámara ya están concedidos, puedes iniciar la captura de la foto.
            dispatchTakePictureIntent()


        }
    }


    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            // Crear un archivo temporal para guardar la imagen en su máxima resolución
            photoFile = createImageFile()
            photoFile?.let {
                val photoUri = FileProvider.getUriForFile(this, "com.example.juscatamarared.fileprovider", it)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    private fun createImageFile(): File {
        // Crea un archivo temporal en el almacenamiento externo
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            // Cargar la imagen desde el archivo temporal en el ImageView
            photoFile?.let { file ->
                val imageBitmap = BitmapFactory.decodeFile(file.absolutePath)
                val correctedImage = rotateImageIfRequired(imageBitmap, file.absolutePath)
                imageView?.setImageBitmap(correctedImage)
                // Ahora que tienes la imagen capturada, puedes enviarla al servidor
                enableUploadButton()
            }
        }


        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            // La URI de la imagen seleccionada
            val selectedImageUri: Uri = data.data!!

            // Aquí puedes realizar las operaciones que desees con la URI de la imagen seleccionada
            // Por ejemplo, mostrar la imagen en un ImageView
            imageView?.setImageURI(selectedImageUri)
            enableUploadButton()
        }


    }


    private fun rotateImageIfRequired(bitmap: Bitmap, photoPath: String): Bitmap {
        val ei = ExifInterface(photoPath)
        val orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)

        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateBitmap(bitmap, 90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateBitmap(bitmap, 180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateBitmap(bitmap, 270f)
            else -> bitmap
        }
    }

    private fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degrees)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }


    private fun enableUploadButton() {
        btnSubir?.isEnabled = true
        btnSubir?.background = resources.getDrawable(R.drawable.btn_fondo_azul)
    }





}