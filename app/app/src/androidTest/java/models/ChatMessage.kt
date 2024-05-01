package models

class ChatMessage {


    data class ChatMessage(
        val sender: String,
        val message: String,
        val timestamp: Long
    )

}