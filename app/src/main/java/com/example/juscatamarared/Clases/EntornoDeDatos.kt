package com.example.juscatamarared.Clases

import android.os.Build
import com.example.juscatamarared.Adaptadores.Companeros_Adapter

class EntornoDeDatos {

    //val URL:String = "http://192.168.123.159/RedJus/consult.php" //trabajo
    //val URL_General:String = "http://192.168.123.159/RedJus/" //trabajo

    val URL:String = "https://justabot.juscatamarca.gob.ar/PruebasMobile/consult.php" //trabajo
    val URL_General:String = "https://justabot.juscatamarca.gob.ar/PruebasMobile/" //trabajo

    //val URL:String = "http://192.168.1.123/RedJus/consult.php" //mi casa
    //val URL:String = "http://192.168.1.123/RedJus/consult.php" //trabajo
    //val URL_General:String = "http://192.168.1.123/RedJus/" //trabajo

    object VariablesGlobales {

        var VariableToken: String = ""
            private set // Esto restringe el acceso al setter fuera del objeto

        fun setVariableToken(nuevoValor: String) {
            VariableToken = nuevoValor
        }


        var VariableUbicacion:String = ""
            private set

        fun setVariableUbicacion(nuevoValor:String){
            VariableUbicacion=nuevoValor
        }


        var fabricanteYmodelo:String = Build.MANUFACTURER +" "+ Build.MODEL
            private set

        fun setfabricanteYmodelo(nuevoValor:String){
            fabricanteYmodelo = nuevoValor
        }


        var MostraOcultarDelete:String = "Mostrar"
            private set

        fun setMostraOcultarDelete(nuevoValor:String){
            MostraOcultarDelete = nuevoValor
        }

        var isBiometricAuthenticated: Boolean = false



        val listado_noticia: MutableList<NoticiasClass> = mutableListOf()

        val listado_recibo: MutableList<RecibosSueldoClass> = mutableListOf()

        val listado_tramites: MutableList<TramitesClass> = mutableListOf()

        val listado_companeros: MutableList<CompanerosClass> = mutableListOf()

        val ListadoPublicacionesUser: MutableList<PublicacionesUserClass> = mutableListOf()

    }



}