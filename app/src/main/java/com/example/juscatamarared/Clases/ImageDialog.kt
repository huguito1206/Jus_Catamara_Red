package com.example.juscatamarared.Clases

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.juscatamarared.R
import com.github.chrisbanes.photoview.PhotoView
import de.hdodenhof.circleimageview.CircleImageView
import android.app.ProgressDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition

class ImageDialog(context: Context, private val imageUrl: String) : Dialog(context, R.style.TransparentDialog) {

    var dialog: Dialog?  = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        setContentView(R.layout.dialogo_image_full)

        val photoView: PhotoView = findViewById(R.id.photoView)

// Configura el diálogo de espera
        dialog = Dialog(context)
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.setContentView(R.layout.progress_dialog)
        dialog!!.setCanceledOnTouchOutside(false)
        dialog!!.show()

        //Glide.with(context).clear(photoView)

        Glide.with(context)
            .load(imageUrl)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .placeholder(R.drawable.ic_person)
            .into(object : SimpleTarget<Drawable>() {
                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?
                ) {
                    // Imagen cargada con éxito
                    dialog?.dismiss()
                    photoView.setImageDrawable(resource)
                }
            })



        // Cierra el diálogo al hacer clic en la imagen
        photoView.setOnClickListener {
            dismiss()

        }
    }
}