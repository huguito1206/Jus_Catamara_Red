package com.example.juscatamarared.Clases

data class SesionBiometrica(                        val sesion_biometrica_id :Int,
                                                    val token_dispositivo:String,
                                                    val usuario_dni:String,
                                                    val usuario_clave:String,
                                                    val usuario_id:Int,
                                                    var biometria_activada: Int,
){

    object SesionBiometricaManager{
        private var SesionBiometrica: SesionBiometrica? = null

        fun setSesionBiometrica(SesionBiometrica: SesionBiometrica?){
            this.SesionBiometrica = SesionBiometrica
        }

        fun getSesionBiometrica():SesionBiometrica?{
            return SesionBiometrica
        }

    }



}