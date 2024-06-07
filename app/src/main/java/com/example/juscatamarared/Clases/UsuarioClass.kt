package com.example.juscatamarared.Clases

data class UsuarioClass(val usuario_id:Int,
                        val usuario_nombre:String,
                        val usuario_apellido:String,
                        var usuario_dni:String,
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
                        var usuario_clave:String,
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
                        val estado_descripcion:String
){

    object UsuarioClassManager{
        private var UsuarioClass: UsuarioClass? = null

        fun setUsuarioClass(UsuarioClass: UsuarioClass?){
            this.UsuarioClass = UsuarioClass
        }

        fun getUsuarioClass():UsuarioClass?{
            return UsuarioClass
        }

    }

    fun limpiarDatos() {
        usuario_clave = ""
    }
}
