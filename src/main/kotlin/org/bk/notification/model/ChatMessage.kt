package org.bk.notification.model

import java.time.Instant

data class ChatMessage(
        val id: String,
        val creationDate: Instant,
        val chatRoomId: String,
        val payload: String
)
