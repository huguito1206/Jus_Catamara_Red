package com.example.juscatamarared.Clases

data class RecuperarcionClaveClass(val usuario_id:Int,
                                   val usuario_nombre:String,
                                   val usuario_apellido:String,
                                   val usuario_dni:String,
                                   val usuario_email:String,
                                   val usuario_clave:String,
                                   val codigo_recuperacion:String
){

    object RecuperacionClaveClassManager{
        private var RecuperarcionClaveClass: RecuperarcionClaveClass? = null

        fun setRecuperarcionClaveClass(RecuperarcionClaveClass: RecuperarcionClaveClass?){
            this.RecuperarcionClaveClass = RecuperarcionClaveClass
        }

        fun getRecuperarcionClaveClass():RecuperarcionClaveClass?{
            return RecuperarcionClaveClass
        }

    }
}
