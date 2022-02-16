package org.bk.notification.web

import org.bk.notification.exception.ChatRoomNotFoundException
import org.bk.notification.model.ChatMessage
import org.bk.notification.service.ChatMessageHandler
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBodyList
import reactor.core.publisher.Flux
import java.time.Instant

@WebFluxTest(controllers = [ChatMessageController::class, ChatMessageExceptionHandler::class])
class ChatMessagesControllerTest {

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @MockBean
    private lateinit var chatMessageHandler: ChatMessageHandler

    @Test
    fun `get a notifications stream`() {
        val chatMessage1 = sampleChatMessage("id-old-1", "some-channel")
        val chatMessage2 = sampleChatMessage("id-old-2", "some-channel")
        whenever(chatMessageHandler.getOldChatMessages(any()))
            .thenReturn(Flux.just(chatMessage1, chatMessage2))

        webTestClient.get()
            .uri("/messages/some-channel")
            .exchange()
            .expectHeader()
            .contentType("application/json")
            .expectBodyList<ChatMessage>()
            .contains(chatMessage1, chatMessage2)


    }

    @Test
    fun `get a notifications stream in a invalid room`() {
        whenever(chatMessageHandler.getOldChatMessages(any()))
            .thenReturn(
                Flux.error(ChatRoomNotFoundException(msg = "Room not found"))
            )

        webTestClient.get()
            .uri("/messages/some-channel")
            .exchange()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.msg").isEqualTo("Room not found")
    }

    private fun sampleChatMessage(id: String, chatRoomId: String): ChatMessage =
        ChatMessage(
            id = id,
            chatRoomId = chatRoomId,
            creationDate = Instant.now(),
            payload = ""
        )
}