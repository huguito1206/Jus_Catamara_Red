package com.example.juscatamarared

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
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.juscatamarared.Clases.EntornoDeDatos
import com.example.juscatamarared.Clases.UsuarioClass
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okio.IOException
import org.json.JSONException
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


class Activity_NuevaNoticia : AppCompatActivity() {


    private var MAX_IMAGE_SIZE_KB = 1024 // 1 MB

    var dialog: Dialog?  = null

    var close_noti:ImageView? = null
    var camera_capture:ImageView? = null
    var btnBuscar:ImageView?=null
    var imageView:ImageView?= null
    var btnSubir:Button?=null

    var editTextDescripcion:EditText?=null

    private val REQUEST_IMAGE_CAPTURE = 1001
    private val CAMERA_PERMISSION_REQUEST_CODE = 101


    private var photoFile: File? = null
    private var imageUri: Uri? = null

    private val PICK_IMAGE_REQUEST = 1001// Puedes usar cualquier número que desees



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nueva_noticia)

        val window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = this.resources.getColor(R.color.ColorProgress)

        imageView=findViewById(R.id.imageView)



        close_noti = findViewById(R.id.close_noti)
        close_noti?.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                val intent = Intent(this@Activity_NuevaNoticia, Fragment_Inicial::class.java)
                intent.putExtra("accion", "mostrar_noticias")
                startActivity(intent)
                finish()
            }
        })

        camera_capture = findViewById(R.id.camera_capture)
        camera_capture?.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                requestCameraPermissions()
            }
        })

        btnBuscar = findViewById(R.id.btnBuscar)
        btnBuscar?.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST)

            }
        })



        btnSubir = findViewById(R.id.btnSubir)
        btnSubir!!.isEnabled = false


        editTextDescripcion= findViewById(R.id.editText)
        editTextDescripcion?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence,
                start: Int,
                i1: Int,
                i2: Int
            ) {
            }

            override fun onTextChanged(
                charSequence: CharSequence,
                i: Int,
                before: Int,
                count: Int
            ) {
                if (count > 0) { //count es cantidad de caracteres que tiene
                    btnSubir!!.isEnabled = true
                    btnSubir?.setBackground(resources.getDrawable(R.drawable.btn_fondo_azul))
                } else {
                    btnSubir!!.isEnabled = false
                    btnSubir?.setBackgroundColor(Color.parseColor("#22000000"))
                }
            }
            override fun afterTextChanged(editable: Editable) {}
        })

        btnSubir?.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                val drawable = imageView?.drawable
                if (drawable is BitmapDrawable) {
                    val imageBitmap = drawable.bitmap
                    // Aquí puedes usar imageBitmap para cargar la imagen al servidor
                    uploadImageToServer(imageBitmap, editTextDescripcion?.text.toString(),"NoticiaUpload")
                } else {
                    // El ImageView no contiene una imagen, toma medidas adicionales o muestra un mensaje de error
                    uploadSinImageToServer("SinImagenNoticiaUpload",editTextDescripcion?.text.toString())
                }

            }
        })

    }



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

    fun uploadSinImageToServer(Accion: String,Descripcion:String) {
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
                //Toast.makeText(this@Activity_NuevaNoticia, ""+response, Toast.LENGTH_SHORT).show();
                try {
                    if (response == "SinImagenExitoso") {
                        //Toast.makeText(this@Activity_NuevaNoticia,"", Toast.LENGTH_SHORT).show();
                        val intent = Intent(this@Activity_NuevaNoticia, Fragment_Inicial::class.java)
                        intent.putExtra("accion", "mostrar_noticias")
                        startActivity(intent)
                        finish()
                    }
                } catch (e: JSONException) {
                    //Toast.makeText(activity,"algo esta mal",Toast.LENGTH_SHORT).show();
                    e.printStackTrace()
                }
                dialog?.dismiss()
            },
            Response.ErrorListener {
                dialog?.dismiss()
                Toast.makeText(this@Activity_NuevaNoticia, "REVISE CONEXIÓN A INTERNET", Toast.LENGTH_LONG).show();
            }
        ) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String>? {
                val params: MutableMap<String, String> = HashMap()
                params["Accion"] = Accion
                params["descripcion"] = Descripcion
                params["usuario_id"] = User?.usuario_id.toString()
                return params
            }
        }
        val requestQueue = Volley.newRequestQueue(this@Activity_NuevaNoticia)
        requestQueue.add(request)
    }
    //aca funcion para subir img al server
    fun uploadImageToServer(imageBitmap: Bitmap?, description: String, action: String) {

        val dialog = Dialog(this@Activity_NuevaNoticia)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.progress_dialog)
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()

        val user = UsuarioClass.UsuarioClassManager.getUsuarioClass()
        val url = EntornoDeDatos().URL

        imageBitmap?.let { bitmap ->
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()
            val encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT)


            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", "image.jpg", RequestBody.create("image/jpeg".toMediaTypeOrNull(), byteArray))
                .addFormDataPart("descripcion", description)
                .addFormDataPart("Accion", action)
                .addFormDataPart("usuario_id", user?.usuario_id.toString())
                .build()

            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val client = OkHttpClient()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    dialog.dismiss()
                    runOnUiThread {
                        Toast.makeText(this@Activity_NuevaNoticia, "REVISE CONEXIÓN A INTERNET", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onResponse(call: Call, response: okhttp3.Response) {
                    dialog.dismiss()
                    val responseData = response.body?.string()
                    runOnUiThread {
                        if (response.isSuccessful) {
                            //Toast.makeText(this@Activity_NuevaNoticia, responseData, Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@Activity_NuevaNoticia, Fragment_Inicial::class.java)
                            intent.putExtra("accion", "mostrar_noticias")
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this@Activity_NuevaNoticia, "ERROR AL CARGAR, REINTENTE", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            })
        } ?: run {
            dialog.dismiss()
            Toast.makeText(this@Activity_NuevaNoticia, "La imagen es nula", Toast.LENGTH_LONG).show()
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this, Fragment_Inicial::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        intent.putExtra("mostrar_noticias", true)
        startActivity(intent)
    }


















}