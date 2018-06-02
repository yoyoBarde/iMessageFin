package com.example.kent.imessage.model

import com.example.kent.imessage.ChatScreen
import java.util.*

class TextMessage(val text: String, override val time: Date, override val senderId: String,
                   override val recipientId: String, override val senderName: String,
                   override val type: String = ChatScreen.KEY_TEXT) : Message {
    constructor() : this("", Date(0), "", "", "")
}