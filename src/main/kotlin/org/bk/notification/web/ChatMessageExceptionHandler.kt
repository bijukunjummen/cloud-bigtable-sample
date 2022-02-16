package org.bk.notification.web

import org.bk.notification.exception.ChatRoomNotFoundException
import org.bk.notification.web.model.ErrorMessage
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.reactive.handler.WebFluxResponseStatusExceptionHandler
import reactor.core.publisher.Mono

@ControllerAdvice
class ChatMessageExceptionHandler : WebFluxResponseStatusExceptionHandler() {
    @ExceptionHandler(ChatRoomNotFoundException::class)
    fun handleChatRoomNotFoundException(chatRoomNotFoundException: ChatRoomNotFoundException): Mono<ResponseEntity<ErrorMessage>> {
        return Mono.just(
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorMessage(msg = chatRoomNotFoundException.message ?: "", errors = emptyList()))
        )
    }
}