package com.example.juscatamarared.Clases

import android.os.Parcel
import android.os.Parcelable

class PublicacionesUserClass (

    var noticia_id: Long,
    var imagen: String? ,
    var descripcion: String? ,
    var usuario_id: Int ,
    var noticia_fecha: String? ,
    var noticia_hora: String?,
    var nombre_apellido: String? ,
    var usuario_foto_perfil: String?,
    var usuario_dio_megusta:String,
    var total_megusta:String,

    var reaccionMeGusta: Boolean = false

) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(noticia_id)
        parcel.writeString(imagen)
        parcel.writeString(descripcion)
        parcel.writeInt(usuario_id)
        parcel.writeString(noticia_fecha)
        parcel.writeString(noticia_hora)
        parcel.writeString(nombre_apellido)
        parcel.writeString(usuario_foto_perfil)
        parcel.writeString(usuario_dio_megusta)
        parcel.writeString(total_megusta)
        parcel.writeString(reaccionMeGusta.toString())

    }


    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TramitesClass> {
        override fun createFromParcel(parcel: Parcel): TramitesClass {
            return TramitesClass(parcel)
        }

        override fun newArray(size: Int): Array<TramitesClass?> {
            return arrayOfNulls(size)
        }
    }
}

