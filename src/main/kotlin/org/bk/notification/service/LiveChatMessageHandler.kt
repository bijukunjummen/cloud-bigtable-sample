package org.bk.notification.service

import org.bk.notification.exception.ChatRoomNotFoundException
import org.bk.notification.model.ChatMessage
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty

@Service
class LiveChatMessageHandler(
    private val chatRoomRepository: ChatRoomRepository,
    private val chatMessageRepository: ChatMessageRepository
) : ChatMessageHandler {
    override fun saveChatMessage(chatMessage: ChatMessage): Mono<ChatMessage> {
        val roomId = chatMessage.chatRoomId
        return chatRoomRepository.getRoom(roomId)
            .switchIfEmpty { Mono.error(ChatRoomNotFoundException("Chat room $roomId missing")) }
            .flatMap {
                chatMessageRepository.save(chatMessage)
                    .thenReturn(chatMessage)
            }
    }

    override fun getOldChatMessages(chatRoomId: String): Flux<ChatMessage> {
        return chatMessageRepository.getLatestSavedChatMessages(
            chatRoomId = chatRoomId,
            latestFirst = false
        )
    }

    override fun getChatMessage(chatRoomId: String, chatMessageId: String): Mono<ChatMessage> {
        return chatMessageRepository.getChatMessage(chatRoomId, chatMessageId)
    }
}
