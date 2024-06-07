package com.example.juscatamarared.Clases

data class NoticiasClass(
    var noticia_id: Int,
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
) {

    object NoticiasClassManager{
        private var NoticiasClass: NoticiasClass? = null

        fun setNoticiasClass(NoticiasClass: NoticiasClass?){
            this.NoticiasClass = NoticiasClass
        }

        fun getNoticiasClass():NoticiasClass?{
            return NoticiasClass
        }

    }
}
