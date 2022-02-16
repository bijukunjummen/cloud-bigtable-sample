package org.bk.notification.web

import org.bk.notification.model.ChatRoom
import org.bk.notification.service.ChatRoomRepository
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/chatrooms")
class ChatRoomController(
    private val chatRoomRepository: ChatRoomRepository
) {
    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun createChatRoom(@RequestBody chatRoomRequest: ChatRoom): Mono<ChatRoom> {
        return chatRoomRepository.save(chatRoomRequest)
    }

    @GetMapping(value = ["/{id}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getChatRoomDetails(@PathVariable("id") chatRoomId: String): Mono<ResponseEntity<ChatRoom>> {
        return chatRoomRepository.getRoom(chatRoomId)
            .map { room -> ResponseEntity.ok(room) }
            .switchIfEmpty(
                Mono.just(ResponseEntity.notFound().build())
            )
    }
}