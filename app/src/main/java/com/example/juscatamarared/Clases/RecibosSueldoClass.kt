package com.example.juscatamarared.Clases

import android.os.Parcel
import android.os.Parcelable

class RecibosSueldoClass(var recibo_id:Long,
                         var pdf:String,
                         var descripcion_pdf:String,
                         var usuario_id:Int)
    : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(recibo_id)
        parcel.writeString(pdf)
        parcel.writeString(descripcion_pdf)
        parcel.writeInt(usuario_id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RecibosSueldoClass> {
        override fun createFromParcel(parcel: Parcel): RecibosSueldoClass {
            return RecibosSueldoClass(parcel)
        }

        override fun newArray(size: Int): Array<RecibosSueldoClass?> {
            return arrayOfNulls(size)
        }
    }
}