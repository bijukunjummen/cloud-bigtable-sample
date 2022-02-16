package org.bk.notification.service

import org.bk.notification.model.ChatRoom
import reactor.core.publisher.Mono

interface ChatRoomRepository {
    fun save(chatRoom: ChatRoom): Mono<ChatRoom>
    fun getRoom(chatRoomId: String): Mono<ChatRoom>
}