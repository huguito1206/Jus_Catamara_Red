package com.example.juscatamarared.Fragmentos.Apps

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
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
import android.util.Base64
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.juscatamarared.Clases.EntornoDeDatos
import com.example.juscatamarared.Clases.SignatureView
import com.example.juscatamarared.Clases.UsuarioClass
import com.example.juscatamarared.Fragment_Inicial
import com.example.juscatamarared.R
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONException
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class AppActivity_TramiteFormulario : AppCompatActivity() {

    var dialog: Dialog?  = null

    var ValorTramiteFormulario:String ?= null
    var ValorFechaCheckBox:String ?= null

    var Explicacion:EditText?=null


    var imageView:ImageView?= null

    private var photoFile: File? = null
    private var imageUri: Uri? = null

    private val REQUEST_IMAGE_CAPTURE = 1
    private val CAMERA_PERMISSION_REQUEST_CODE = 101

    private val PICK_IMAGE_REQUEST = 1 // Puedes usar cualquier número que desees


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.app_activity_tramite_formulario)

        val window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = this.resources.getColor(R.color.ColorProgress)





        val FeriaEnero = findViewById<RadioButton>(R.id.RadioButton_FeriaEnero)
        val FeriaJulio= findViewById<RadioButton>(R.id.RadioButton_FeriaJulio)
        val Lao= findViewById<RadioButton>(R.id.RadioButton_Lao)

        val Maternidad= findViewById<RadioButton>(R.id.RadioButton_Maternidad)
        val Enfermedad= findViewById<RadioButton>(R.id.RadioButton_Enfermedad)
        val AtencionFamiliar= findViewById<RadioButton>(R.id.RadioButton_AtencionFamiliar)
        val Matrimonio= findViewById<RadioButton>(R.id.RadioButton_Matrimonio)
        val ActCientif= findViewById<RadioButton>(R.id.RadioButton_ActCientif)
        val Examen= findViewById<RadioButton>(R.id.RadioButton_Examen)

        val NacVaron= findViewById<RadioButton>(R.id.RadioButton_NacVaron)
        val CasamientoHijo= findViewById<RadioButton>(R.id.RadioButton_CasamientoHijo)
        val FallecimientoPariente= findViewById<RadioButton>(R.id.RadioButton_FallecimientoPariente)
        val RazonParticular= findViewById<RadioButton>(R.id.RadioButton_RazonParticular)
        val RazonEspecial= findViewById<RadioButton>(R.id.RadioButton_RazonEspecial)
        val Adopcion= findViewById<RadioButton>(R.id.RadioButton_Adopcion)
        val ParticipacionComicios= findViewById<RadioButton>(R.id.RadioButton_ParticipacionComicios)
        val DonacionSangre= findViewById<RadioButton>(R.id.RadioButton_DonacionSangre)
        val FuerzaMayor= findViewById<RadioButton>(R.id.RadioButton_FuerzaMayor)
        val Tardanza= findViewById<RadioButton>(R.id.RadioButton_Tardanza)
        val LeyCultos= findViewById<RadioButton>(R.id.RadioButton_LeyCultos)
        val FrancoCompensatorio= findViewById<RadioButton>(R.id.RadioButton_FrancoCompensatorio)

        val ProblemasReloj= findViewById<RadioButton>(R.id.RadioButton_ProblemasReloj)
        val RetiroTrabajo= findViewById<RadioButton>(R.id.RadioButton_RetiroTrabajo)
        val PresentacionDocumentacion= findViewById<RadioButton>(R.id.RadioButton_PresentacionDocumentacion)
        val ComisionServicio= findViewById<RadioButton>(R.id.RadioButton_ComisionServicio)
        val PresentacionInforme= findViewById<RadioButton>(R.id.RadioButton_PresentacionInforme)
        val SolicitudInforme= findViewById<RadioButton>(R.id.RadioButton_SolicitudInforme)
        val SuspencionLicencia= findViewById<RadioButton>(R.id.RadioButton_SuspencionLicencia)
        val ProrrogaLicencia= findViewById<RadioButton>(R.id.RadioButton_ProrrogaLicencia)
        val Lactancia= findViewById<RadioButton>(R.id.RadioButton_Lactancia)

        val FechaDesde: EditText = findViewById(R.id.Edt_FechaDesde)
        val FechaHasta: EditText = findViewById(R.id.Edt_FechaHasta)
        val checkBox_fechaHoy:CheckBox = findViewById(R.id.checkBox_fechaHoy)

        Explicacion = findViewById(R.id.Edt_Explicacion)

        val camera = findViewById<ImageView>(R.id.camera_capture_document)
        imageView =findViewById<ImageView>(R.id.imageView_document)

        val Btn_EnviarFormulario = findViewById<Button>(R.id.Btn_EnviarFormulario)


        val myCalendar = Calendar.getInstance()
        val formatoDeFecha = "dd/MM/yyyy"
        var sdf = SimpleDateFormat(formatoDeFecha, Locale.US)

        val listenerFechaDesde = View.OnClickListener {
            if (checkBox_fechaHoy.isChecked) {
                FechaDesde.setOnClickListener(null)
                Toast.makeText(this, "Seleccionó la fecha de HOY", Toast.LENGTH_SHORT).show()
            }else{
                val datePickerDialog = DatePickerDialog(
                    this@AppActivity_TramiteFormulario,
                    { view, year, monthOfYear, dayOfMonth ->
                        myCalendar.set(Calendar.YEAR, year)
                        myCalendar.set(Calendar.MONTH, monthOfYear)
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                        val fechaFormateada = sdf.format(myCalendar.time)
                        FechaDesde.setText(fechaFormateada)

                        if (FechaHasta.text.isNotEmpty()) {
                            val fechaDesdeString = FechaDesde.text.toString()
                            val fechaHastaString = FechaHasta.text.toString()

                            if (fechaDesdeString.isNotEmpty() && fechaHastaString.isNotEmpty()) {
                                val fechaDesde = sdf.parse(fechaDesdeString)
                                val fechaHasta = sdf.parse(fechaHastaString)
                                if (fechaDesde != null && fechaHasta != null) {
                                    calcularDiferenciaDeFechas(fechaDesde, fechaHasta)
                                } else {
                                    Toast.makeText(this, "Error al parsear las fechas", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(this, "Debe seleccionar ambas fechas", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    myCalendar.get(Calendar.YEAR),
                    myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)
                )

                datePickerDialog.datePicker.minDate = System.currentTimeMillis()
                datePickerDialog.show()
            }
        }


        val listenerFechaHasta = View.OnClickListener {
            if (checkBox_fechaHoy.isChecked) {
                FechaHasta.setOnClickListener(null)
                Toast.makeText(this, "Seleccionó la fecha de HOY", Toast.LENGTH_SHORT).show()
            }else{
                val datePickerDialog = DatePickerDialog(
                    this@AppActivity_TramiteFormulario,
                    { view, year, monthOfYear, dayOfMonth ->
                        myCalendar.set(Calendar.YEAR, year)
                        myCalendar.set(Calendar.MONTH, monthOfYear)
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                        val fechaFormateada = sdf.format(myCalendar.time)
                        FechaHasta.setText(fechaFormateada)

                        if (FechaHasta.text.isNotEmpty()) {
                            val fechaDesdeString = FechaDesde.text.toString()
                            val fechaHastaString = FechaHasta.text.toString()

                            if (fechaDesdeString.isNotEmpty() && fechaHastaString.isNotEmpty()) {
                                val fechaDesde = sdf.parse(fechaDesdeString)
                                val fechaHasta = sdf.parse(fechaHastaString)
                                if (fechaDesde != null && fechaHasta != null) {
                                    calcularDiferenciaDeFechas(fechaDesde, fechaHasta)
                                } else {
                                    Toast.makeText(this, "Error al parsear las fechas", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(this, "Debe seleccionar ambas fechas", Toast.LENGTH_SHORT).show()
                            }
                        }

                    },
                    myCalendar.get(Calendar.YEAR),
                    myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)
                )
                datePickerDialog.datePicker.minDate = System.currentTimeMillis()
                datePickerDialog.show()
            }
        }


        FechaDesde.setOnClickListener(listenerFechaDesde)
        FechaHasta.setOnClickListener(listenerFechaHasta)

        checkBox_fechaHoy.setOnCheckedChangeListener { _, isChecked ->
            if (checkBox_fechaHoy.isChecked) {
                FechaDesde.setText("")
                FechaHasta.setText("")
                FechaDesde.error = null
                FechaHasta.error = null
                // Desactivar los OnClickListener si el CheckBox está marcado
                //FechaDesde.setOnClickListener(null)
                //FechaHasta.setOnClickListener(null)

                val añoActual = myCalendar.get(Calendar.YEAR)
                val mesActual = myCalendar.get(Calendar.MONTH)
                val diaActual = myCalendar.get(Calendar.DAY_OF_MONTH)
                val fechaFormateada = "${diaActual}/${mesActual}/${añoActual}"
                //Toast.makeText(this, fechaFormateada, Toast.LENGTH_SHORT).show()
                ValorFechaCheckBox=fechaFormateada

                Toast.makeText(this, "Dias seleccionados: 1", Toast.LENGTH_SHORT).show()

            } else {
                // Volver a activar los OnClickListener si el CheckBox no está marcado
                FechaDesde.setOnClickListener(listenerFechaDesde)
                FechaHasta.setOnClickListener(listenerFechaHasta)
            }
        }


        camera?.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                requestCameraPermissions()
            }
        })


        val signatureView: SignatureView = findViewById(R.id.signatureViewAgente)
                    Btn_EnviarFormulario.setOnClickListener(object : View.OnClickListener {
                        override fun onClick(v: View?) {

                            val User = UsuarioClass.UsuarioClassManager.getUsuarioClass()

                            if (FeriaEnero.isChecked == true) {
                                ValorTramiteFormulario = FeriaEnero.text.toString()
                            }
                            if (FeriaJulio.isChecked == true) {
                                ValorTramiteFormulario = FeriaJulio.text.toString()
                            }
                            if (Lao.isChecked == true) {
                                ValorTramiteFormulario = Lao.text.toString()
                            }

                            if (Maternidad.isChecked == true) {
                                ValorTramiteFormulario = Maternidad.text.toString()
                            }
                            if (Enfermedad.isChecked == true) {
                                ValorTramiteFormulario = Enfermedad.text.toString()
                            }
                            if (AtencionFamiliar.isChecked == true) {
                                ValorTramiteFormulario = AtencionFamiliar.text.toString()
                            }
                            if (Matrimonio.isChecked == true) {
                                ValorTramiteFormulario = Matrimonio.text.toString()
                            }
                            if (ActCientif.isChecked == true) {
                                ValorTramiteFormulario = ActCientif.text.toString()
                            }
                            if (Examen.isChecked == true) {
                                ValorTramiteFormulario = Examen.text.toString()
                            }

                            if (NacVaron.isChecked == true) {
                                ValorTramiteFormulario = NacVaron.text.toString()
                            }
                            if (CasamientoHijo.isChecked == true) {
                                ValorTramiteFormulario = CasamientoHijo.text.toString()
                            }
                            if (FallecimientoPariente.isChecked == true) {
                                ValorTramiteFormulario = FallecimientoPariente.text.toString()
                            }
                            if (RazonParticular.isChecked == true) {
                                ValorTramiteFormulario = RazonParticular.text.toString()
                            }
                            if (RazonEspecial.isChecked == true) {
                                ValorTramiteFormulario = RazonEspecial.text.toString()
                            }
                            if (Adopcion.isChecked == true) {
                                ValorTramiteFormulario = Adopcion.text.toString()
                            }
                            if (ParticipacionComicios.isChecked == true) {
                                ValorTramiteFormulario = ParticipacionComicios.text.toString()
                            }
                            if (DonacionSangre.isChecked == true) {
                                ValorTramiteFormulario = DonacionSangre.text.toString()
                            }
                            if (FuerzaMayor.isChecked == true) {
                                ValorTramiteFormulario = FuerzaMayor.text.toString()
                            }
                            if (Tardanza.isChecked == true) {
                                ValorTramiteFormulario = Tardanza.text.toString()
                            }
                            if (LeyCultos.isChecked == true) {
                                ValorTramiteFormulario = LeyCultos.text.toString()
                            }
                            if (FrancoCompensatorio.isChecked == true) {
                                ValorTramiteFormulario = FrancoCompensatorio.text.toString()
                            }

                            if (ProblemasReloj.isChecked == true) {
                                ValorTramiteFormulario = ProblemasReloj.text.toString()
                            }
                            if (RetiroTrabajo.isChecked == true) {
                                ValorTramiteFormulario = RetiroTrabajo.text.toString()
                            }
                            if (PresentacionDocumentacion.isChecked == true) {
                                ValorTramiteFormulario = PresentacionDocumentacion.text.toString()
                            }
                            if (ComisionServicio.isChecked == true) {
                                ValorTramiteFormulario = ComisionServicio.text.toString()
                            }
                            if (PresentacionInforme.isChecked == true) {
                                ValorTramiteFormulario = PresentacionInforme.text.toString()
                            }
                            if (SolicitudInforme.isChecked == true) {
                                ValorTramiteFormulario = SolicitudInforme.text.toString()
                            }
                            if (SuspencionLicencia.isChecked == true) {
                                ValorTramiteFormulario = SuspencionLicencia.text.toString()
                            }
                            if (ProrrogaLicencia.isChecked == true) {
                                ValorTramiteFormulario = ProrrogaLicencia.text.toString()
                            }
                            if (Lactancia.isChecked == true) {
                                ValorTramiteFormulario = Lactancia.text.toString()
                            }



                            if (ValorTramiteFormulario == null) {
                                Toast.makeText(this@AppActivity_TramiteFormulario, "Seleccione un tipo de trámite", Toast.LENGTH_SHORT).show()
                            }else{
                                if (checkBox_fechaHoy.isChecked){

                                    val drawable = imageView?.drawable

                                    if (drawable is BitmapDrawable) {

                                        val imageBitmap = drawable.bitmap //imagen de la documentacion
                                        if (signatureView.hasSignature()) {
                                            val signatureBitmap = signatureView.getSignatureBitmap() //Aca la imagen de la firma del agente
                                            // Aquí puedes usar imageBitmap para cargar la imagen al servidor
                                            //Toast.makeText(this@AppActivity_TramiteFormulario, "HAY FIRMA DEL AGENTE", Toast.LENGTH_SHORT).show()
                                            CargaTramiteConImagenTramite(imageBitmap,signatureBitmap,"ConImagenTramiteFirmas",ValorTramiteFormulario.toString(),ValorFechaCheckBox.toString(),ValorFechaCheckBox.toString(),Explicacion?.text.toString())
                                        } else {
                                            // No se ha dibujado una firma, muestra un mensaje o realiza alguna acción
                                            Toast.makeText(this@AppActivity_TramiteFormulario, "Por favor, dibuje una firma", Toast.LENGTH_SHORT).show()
                                        }

                                    } else {
                                        //Toast.makeText(this@AppActivity_TramiteFormulario, "NO HAY IMAGEN DE DOCUMENTACION", Toast.LENGTH_SHORT).show()
                                        // El ImageView no contiene una imagen, toma medidas adicionales o muestra un mensaje de error

                                        if (signatureView.hasSignature()) {
                                            val signatureBitmap = signatureView.getSignatureBitmap() //Aca la imagen de la firma del agente
                                            // Aquí puedes usar imageBitmap para cargar la imagen al servidor
                                            //Toast.makeText(this@AppActivity_TramiteFormulario, "HAY FIRMA DEL AGENTE", Toast.LENGTH_SHORT).show()
                                            CargaTramiteSinImagenConFirma(signatureBitmap,"SinImgYfirmasTramite",ValorTramiteFormulario.toString(),ValorFechaCheckBox.toString(),ValorFechaCheckBox.toString(),Explicacion?.text.toString())
                                        } else {
                                            // No se ha dibujado una firma, muestra un mensaje o realiza alguna acción
                                            Toast.makeText(this@AppActivity_TramiteFormulario, "Por favor, dibuje una firma", Toast.LENGTH_SHORT).show()
                                        }

                                    }


                                }else{
                                    if (FechaDesde.text.toString() == "") {
                                        Toast.makeText(this@AppActivity_TramiteFormulario, "Indique los detalles de dias", Toast.LENGTH_SHORT).show()
                                        FechaDesde.error = ""
                                    } else {
                                        if (FechaHasta.text.toString() == "") {
                                            Toast.makeText(this@AppActivity_TramiteFormulario, "Indique los detalles de dias", Toast.LENGTH_SHORT).show()
                                            FechaHasta.error = ""
                                        } else {
                                            FechaDesde.error = null
                                            FechaHasta.error = null



                                            val drawable = imageView?.drawable
                                            if (drawable is BitmapDrawable) {
                                                val imageBitmap = drawable.bitmap

                                                if (signatureView.hasSignature()) {
                                                    val signatureBitmap = signatureView.getSignatureBitmap() //Aca la imagen de la firma del agente
                                                    // Aquí puedes usar imageBitmap para cargar la imagen al servidor
                                                    CargaTramiteConImagenTramite(imageBitmap,signatureBitmap,"ConImagenTramiteFirmas",ValorTramiteFormulario.toString(),FechaDesde?.text.toString(),FechaHasta?.text.toString(),Explicacion?.text.toString())
                                                } else {
                                                    // No se ha dibujado una firma, muestra un mensaje o realiza alguna acción
                                                    Toast.makeText(this@AppActivity_TramiteFormulario, "Por favor, dibuje una firma", Toast.LENGTH_SHORT).show()
                                                }
                                            } else {
                                                // El ImageView no contiene una imagen, toma medidas adicionales o muestra un mensaje de error

                                                if (signatureView.hasSignature()) {
                                                    val signatureBitmap = signatureView.getSignatureBitmap() //Aca la imagen de la firma del agente
                                                    // Aquí puedes usar imageBitmap para cargar la imagen al servidor
                                                    //Toast.makeText(this@AppActivity_TramiteFormulario, "HAY FIRMA DEL AGENTE", Toast.LENGTH_SHORT).show()
                                                    CargaTramiteSinImagenConFirma(signatureBitmap,"SinImgYfirmasTramite",ValorTramiteFormulario.toString(),FechaDesde?.text.toString(),FechaHasta?.text.toString(),Explicacion?.text.toString())
                                                } else {
                                                    // No se ha dibujado una firma, muestra un mensaje o realiza alguna acción
                                                    Toast.makeText(this@AppActivity_TramiteFormulario, "Por favor, dibuje una firma", Toast.LENGTH_SHORT).show()
                                                }

                                                //CargaTramiteSinImagenConFirma("SinImagenTramite",ValorTramiteFormulario.toString(),FechaDesde?.text.toString(),FechaHasta?.text.toString(),Explicacion?.text.toString())
                                            }

                                        }
                                    }

                                }

                            }

                        }
                    })


        }


    fun calcularDiferenciaDeFechas(fechaDesde: Date, fechaHasta: Date) {
        val diferenciaEnMilisegundos = fechaHasta.time - fechaDesde.time
        val diferenciaEnDias = TimeUnit.MILLISECONDS.toDays(diferenciaEnMilisegundos)
        val total = diferenciaEnDias +1
        Toast.makeText(this, "Dias seleccionados: $total", Toast.LENGTH_SHORT).show()
    }



    private fun requestCameraPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE)
        } else {
            // Los permisos de la cámara ya están concedidos, puedes iniciar la captura de la foto.
            //dispatchTakePictureIntent()

            //aca abre la galeria para seleccionar una imagen
            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST)
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
                if (imageUri != null) {
                   //aca por si necesito hacer algo al cargar una imagen o no
                }
            }
        }


        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            // La URI de la imagen seleccionada
            val selectedImageUri: Uri = data.data!!

            // Aquí puedes realizar las operaciones que desees con la URI de la imagen seleccionada
            // Por ejemplo, mostrar la imagen en un ImageView
            imageView?.setImageURI(selectedImageUri)
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


    fun CargaTramiteSinImagenConFirma(imageBitmap2: Bitmap?,Accion:String, Descripcion:String,Fecha1:String,Fecha2:String, Explicacion:String) {

        dialog = Dialog(this)
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.setContentView(R.layout.progress_dialog)
        dialog!!.setCanceledOnTouchOutside(false)
        dialog!!.show()


        val User = UsuarioClass.UsuarioClassManager.getUsuarioClass()
        val url = EntornoDeDatos().URL
        val byteArrayOutputStream = ByteArrayOutputStream()

        val byteArrayOutputStream2 = ByteArrayOutputStream()
        imageBitmap2?.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream2)
        val byteArray2 = byteArrayOutputStream2.toByteArray()

        val byteArray = byteArrayOutputStream.toByteArray()
        val encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT)

        val client = OkHttpClient()

        val formBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("image", "image.jpg", RequestBody.create("image/jpeg".toMediaTypeOrNull(), byteArray2))


            .addFormDataPart("Accion", Accion)
            .addFormDataPart("descripcion", Descripcion)
            .addFormDataPart("usuario_id",User?.usuario_id.toString())
            .addFormDataPart("fecha_desde", Fecha1)
            .addFormDataPart("fecha_hasta", Fecha2)
            .addFormDataPart("Explicacion", Explicacion)
            .build()

        val request = Request.Builder()
            .url(url)
            .post(formBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                dialog?.dismiss()
                Toast.makeText(this@AppActivity_TramiteFormulario, "REVISE CONEXIÓN A INTERNET", Toast.LENGTH_LONG).show();
            }

            override fun onResponse(call: Call, response: okhttp3.Response) {
                runOnUiThread {
                    dialog?.dismiss()

                    if (response.isSuccessful) {
                        // La solicitud fue exitosa, puedes manejar la respuesta del servidor aquí
                        //Toast.makeText(this@Activity_NuevaNoticia, "Exito ${response.message}", Toast.LENGTH_SHORT).show()
                        val responseData = response.body?.string()
                        //Toast.makeText(this@AppActivity_TramiteFormulario, responseData, Toast.LENGTH_SHORT).show()
                        // Realiza acciones con la respuesta del servidor

                        //Aca tengo que limitar de acuerdo a la respuesta del server

                        Toast.makeText(this@AppActivity_TramiteFormulario, "Enviado con éxito", Toast.LENGTH_LONG).show();
                        val intent = Intent(this@AppActivity_TramiteFormulario, Fragment_Inicial::class.java)
                        intent.putExtra("accion", "mostrar_apps")
                        intent.putExtra("mostrar_apps", true)
                        startActivity(intent)
                        finish()

                    } else {
                        // La solicitud no fue exitosa, maneja el error aquí
                        Toast.makeText(this@AppActivity_TramiteFormulario, "OCURRIO UN ERROR, REINTENTE", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }








    fun CargaTramiteConImagenTramite(imageBitmap1: Bitmap?, imageBitmap2: Bitmap?,Accion:String, Descripcion:String,Fecha1:String,Fecha2:String, Explicacion:String) {

        dialog = Dialog(this)
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.setContentView(R.layout.progress_dialog)
        dialog!!.setCanceledOnTouchOutside(false)
        dialog!!.show()


        val User = UsuarioClass.UsuarioClassManager.getUsuarioClass()
        val url = EntornoDeDatos().URL
        val byteArrayOutputStream = ByteArrayOutputStream()

        val byteArrayOutputStream1 = ByteArrayOutputStream()
        imageBitmap1?.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream1)
        val byteArray1 = byteArrayOutputStream1.toByteArray()

        val byteArrayOutputStream2 = ByteArrayOutputStream()
        imageBitmap2?.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream2)
        val byteArray2 = byteArrayOutputStream2.toByteArray()

        val byteArray = byteArrayOutputStream.toByteArray()
        val encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT)

        val client = OkHttpClient()

        val formBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            //.addFormDataPart("image", "image.jpg", RequestBody.create("image/jpeg".toMediaTypeOrNull(), byteArray))
            .addFormDataPart("image1", "image1.jpg", RequestBody.create("image/jpeg".toMediaTypeOrNull(), byteArray1))
            .addFormDataPart("image2", "image2.jpg", RequestBody.create("image/jpeg".toMediaTypeOrNull(), byteArray2))

            .addFormDataPart("Accion", Accion)
            .addFormDataPart("descripcion", Descripcion)
            .addFormDataPart("usuario_id",User?.usuario_id.toString())
            .addFormDataPart("fecha_desde", Fecha1)
            .addFormDataPart("fecha_hasta", Fecha2)
            .addFormDataPart("Explicacion", Explicacion)
            .build()

        val request = Request.Builder()
            .url(url)
            .post(formBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                dialog?.dismiss()
                Toast.makeText(this@AppActivity_TramiteFormulario, "REVISE CONEXIÓN A INTERNET", Toast.LENGTH_LONG).show();
            }

            override fun onResponse(call: Call, response: okhttp3.Response) {
                runOnUiThread {
                    dialog?.dismiss()

                    if (response.isSuccessful) {
                        // La solicitud fue exitosa, puedes manejar la respuesta del servidor aquí
                        //Toast.makeText(this@Activity_NuevaNoticia, "Exito ${response.message}", Toast.LENGTH_SHORT).show()
                        val responseData = response.body?.string()
                        //Toast.makeText(this@AppActivity_TramiteFormulario, responseData, Toast.LENGTH_SHORT).show()
                        // Realiza acciones con la respuesta del servidor
                        Toast.makeText(this@AppActivity_TramiteFormulario, "Enviado con éxito", Toast.LENGTH_LONG).show();
                        val intent = Intent(this@AppActivity_TramiteFormulario, Fragment_Inicial::class.java)
                        intent.putExtra("accion", "mostrar_apps")
                        intent.putExtra("mostrar_apps", true)
                        startActivity(intent)
                        finish()

                    } else {
                        // La solicitud no fue exitosa, maneja el error aquí
                        Toast.makeText(this@AppActivity_TramiteFormulario, "OCURRIO UN ERROR, REINTENTE", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }



    fun CargaTramiteSinImagen(Accion:String, Descripcion:String,Fecha1:String,Fecha2:String, Explicacion:String){
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
                //Toast.makeText(this@AppActivity_TramiteFormulario, ""+response, Toast.LENGTH_SHORT).show();
                try {
                    if (response == "SinImagenExitoso") {
                        Toast.makeText(this@AppActivity_TramiteFormulario,"SOLICITUD ENVIADA CORRECTAMENTE", Toast.LENGTH_SHORT).show();

                        val intent = Intent(this@AppActivity_TramiteFormulario, Fragment_Inicial::class.java)
                        intent.putExtra("accion", "mostrar_apps")
                        intent.putExtra("mostrar_apps", true)
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
                Toast.makeText(this@AppActivity_TramiteFormulario, "REVISE CONEXIÓN A INTERNET", Toast.LENGTH_LONG).show();
            }
        ) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String>? {
                val params: MutableMap<String, String> = HashMap()
                params["Accion"] = Accion
                params["descripcion"] = Descripcion
                params["usuario_id"] = User?.usuario_id.toString()
                params["fecha_desde"]= Fecha1
                params["fecha_hasta"]= Fecha2
                params["Explicacion"]= Explicacion
                return params
            }
        }
        val requestQueue = Volley.newRequestQueue(this@AppActivity_TramiteFormulario)
        requestQueue.add(request)
    }


}

