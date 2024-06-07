package com.example.juscatamarared.Clases

import android.os.Parcel
import android.os.Parcelable

class CompanerosClass(val usuario_id:Long,
                      val usuario_nombre:String,
                      val usuario_apellido:String,
                      val usuario_dni:String,
                      val usuario_cuil:String,
                      val usuario_legajo:String,
                      val usuario_provincia:String,
                      val usuario_depto:String,
                      val usuario_calle:String,
                      val usuario_casa_numero:String,
                      val usuario_fecha_nacimiento:String,
                      val usuario_email:String,
                      val usuario_biografia:String,
                      val usuario_telefono:String,
                      val usuario_profesion:String,
                      val usuario_clave:String,
                      val usuario_foto_perfil:String,
                      val usuario_facebook:String,
                      val usuario_instagram:String,
                      val cargo_id:Int,
                      val cargo_descripcion:String,
                      val cargo_comentario:String,
                      val tipo_usuario_id:Int,
                      val tipo_usuario_descripcion:String,
                      val entidad_id:Int,
                      val entidad_descripcion:String,
                      val area_id:Int,
                      val area_descripcion:String,
                      val estado_id:Int,
                      val estado_descripcion:String)
    : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(usuario_id)
        parcel.writeString(usuario_nombre)
        parcel.writeString(usuario_apellido)
        parcel.writeString(usuario_dni)
        parcel.writeString(usuario_cuil)
        parcel.writeString(usuario_legajo)
        parcel.writeString(usuario_provincia)
        parcel.writeString(usuario_depto)
        parcel.writeString(usuario_calle)
        parcel.writeString(usuario_casa_numero)
        parcel.writeString(usuario_fecha_nacimiento)
        parcel.writeString(usuario_email)
        parcel.writeString(usuario_biografia)
        parcel.writeString(usuario_telefono)
        parcel.writeString(usuario_profesion)
        parcel.writeString(usuario_clave)
        parcel.writeString(usuario_foto_perfil)
        parcel.writeString(usuario_facebook)
        parcel.writeString(usuario_instagram)
        parcel.writeInt(cargo_id)
        parcel.writeString(cargo_descripcion)
        parcel.writeString(cargo_comentario)
        parcel.writeInt(tipo_usuario_id)
        parcel.writeString(tipo_usuario_descripcion)
        parcel.writeInt(entidad_id)
        parcel.writeString(entidad_descripcion)
        parcel.writeInt(area_id)
        parcel.writeString(area_descripcion)
        parcel.writeInt(estado_id)
        parcel.writeString(estado_descripcion)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CompanerosClass> {
        override fun createFromParcel(parcel: Parcel): CompanerosClass {
            return CompanerosClass(parcel)
        }

        override fun newArray(size: Int): Array<CompanerosClass?> {
            return arrayOfNulls(size)
        }
    }
}