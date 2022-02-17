package org.bk.notification.web

import org.bk.notification.model.ChatMessage
import org.bk.notification.service.ChatMessageHandler
import org.bk.notification.web.model.ChatMessageRequest
import org.springframework.http.ResponseEntity
import org.springframework.util.StopWatch
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Instant
import java.util.*

@RestController
@RequestMapping("/messages")
class ChatMessageController(private val chatMessageHandler: ChatMessageHandler) {

    @PostMapping("/{chatRoomId}")
    fun addMessagesToRoom(
        @PathVariable("chatRoomId") chatRoomId: String,
        @RequestBody request: ChatMessageRequest
    ): Mono<ChatMessage> {
        return chatMessageHandler.saveChatMessage(
            ChatMessage(
                id = UUID.randomUUID().toString(),
                chatRoomId = chatRoomId,
                creationDate = Instant.now(),
                payload = request.payload
            )
        )
    }

    @GetMapping(path = ["/{chatRoomId}"])
    fun getNotifications(@PathVariable("chatRoomId") chatRoomId: String): Flux<ChatMessage> {
        return chatMessageHandler.getOldChatMessages(chatRoomId)
    }

    @GetMapping(path = ["/{chatRoomId}/{chatMessageId}"])
    fun getNotifications(
        @PathVariable("chatRoomId") chatRoomId: String,
        @PathVariable("chatMessageId") chatMessageId: String
    ): Mono<ResponseEntity<WithTimeWrapper<ChatMessage>>> {
        return chatMessageHandler
            .getChatMessage(chatRoomId, chatMessageId)
            .flatMap { chatMessage ->
                Mono.deferContextual { context ->
                    val stopWatch = context.get<StopWatch>(STOPWATCH_KEY)
                    stopWatch.stop()
                    Mono.just(ResponseEntity.ok(WithTimeWrapper(chatMessage, stopWatch.totalTimeNanos)))
                }
            }
            .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()))
            .contextWrite { context ->
                val stopWatch = StopWatch()
                stopWatch.start()
                context.put(STOPWATCH_KEY, stopWatch)
            }
    }

    companion object {
        private const val STOPWATCH_KEY = "stopWatchKey"
    }
}

data class WithTimeWrapper<T>(
    val data: T,
    val timeElapsedNanos:Long
)