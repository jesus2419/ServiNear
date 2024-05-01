package models

class Service {


    data class Service(
        val id: String, // Identificador único del servicio
        val name: String,
        val category: String,
        val imageUrl: String,
        val chatMessages: List<ChatMessage> = emptyList() // Lista de mensajes de chat del servicio
    )

}