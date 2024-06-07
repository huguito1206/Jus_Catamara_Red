package com.example.juscatamarared.Clases

import android.os.Parcel
import android.os.Parcelable

class TramitesClass(

    var formulario_id:Long,
    var formulario_licencias: String? ,
    var formulario_dia_desde: String? ,
    var formulario_dia_hasta: String? ,
    var formulario_explicacion: String? ,
    var usuario_id: Int,
    var formulario_fecha_hora_registro: String? ,
    var apr_des_por:String?,
    var formulario_fecha_hora_aprobacion: String? ,
    var formulario_estado: String?,
    var area_origen: String? ,
    var area_destino: String? ,
    var formulario_afectacion: String?,
    var UserOrigen:String?,
    var UserJefe:String?,
    var formulario_img_id: String?,
    var url_img:String?,
    var AreaOrigenDes:String,
    var AreaDestinoDes:String
   )

    : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt(),
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

    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(formulario_id)
        parcel.writeString(formulario_licencias)
        parcel.writeString(formulario_dia_desde)
        parcel.writeString(formulario_dia_hasta)
        parcel.writeString(formulario_explicacion)
        parcel.writeInt(usuario_id)
        parcel.writeString(formulario_fecha_hora_registro)
        parcel.writeString(apr_des_por)
        parcel.writeString(formulario_fecha_hora_aprobacion)
        parcel.writeString(formulario_estado)
        parcel.writeString(area_origen)
        parcel.writeString(area_destino)
        parcel.writeString(formulario_afectacion)
        parcel.writeString(UserOrigen)
        parcel.writeString(UserJefe)
        parcel.writeString(formulario_img_id)
        parcel.writeString(AreaOrigenDes)
        parcel.writeString(AreaDestinoDes)
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


