class ServicioSeleccionado private constructor() {
    var idServicio: String? = null
    var nombre: String? = null
    var descripcion: String? = null
    var contacto: String? = null
    var precioHora: String? = null
    var imagen: ByteArray? = null

    companion object {
        @Volatile
        private var INSTANCE: ServicioSeleccionado? = null

        fun getInstance(): ServicioSeleccionado {
            return INSTANCE ?: synchronized(this) {
                val instance = ServicioSeleccionado()
                INSTANCE = instance
                instance
            }
        }
    }

    fun setServicio(
        idServicio: String,
        nombre: String,
        descripcion: String,
        contacto: String,
        precioHora: String,
        imagen: ByteArray
    ) {
        this.idServicio = idServicio
        this.nombre = nombre
        this.descripcion = descripcion
        this.contacto = contacto
        this.precioHora = precioHora
        this.imagen = imagen
    }

    fun clear() {
        idServicio = null
        nombre = null
        descripcion = null
        contacto = null
        precioHora = null
        imagen = null
    }
}
