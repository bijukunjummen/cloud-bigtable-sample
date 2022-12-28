package org.bk.notification.service

import org.bk.notification.model.ChatMessage
import org.bk.notification.model.Page
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface ChatMessageHandler {
    fun saveChatMessage(chatMessage: ChatMessage): Mono<ChatMessage>
    fun getOldChatMessages(chatRoomId: String): Flux<ChatMessage>
    fun getChatMessage(chatRoomId: String, chatMessageId: String): Mono<ChatMessage>

    /**
     * Get paginated notifications
     *
     * @param chatRoomId chat room id
     * @param offset from which row onwards - exclusive
     * @param count of recent notifications
     *
     * @return a page of chat messages
     */
    fun getPaginatedMessages(chatRoomId: String, offset: String = "", count: Long = 25): Page<ChatMessage>
}