package org.bk.notification.service

import org.bk.notification.model.ChatMessage
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface ChatMessageHandler {
    fun saveChatMessage(chatMessage: ChatMessage): Mono<ChatMessage>
    fun getOldChatMessages(chatRoomId: String): Flux<ChatMessage>
    fun getChatMessage(chatRoomId: String, chatMessageId: String): Mono<ChatMessage>
}