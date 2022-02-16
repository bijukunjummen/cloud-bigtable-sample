package org.bk.notification.web

import org.assertj.core.api.Assertions
import org.bk.notification.model.ChatRoom
import org.bk.notification.service.ChatRoomRepository
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import org.springframework.web.reactive.function.BodyInserters
import reactor.core.publisher.Mono

@WebFluxTest(controllers = [ChatRoomController::class])
class ChatRoomControllerTest {

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @MockBean
    private lateinit var chatRoomRepository: ChatRoomRepository

    @Test
    fun `create and retrieve chatroom`() {
        val chatRoom = ChatRoom(id = "some-room", name = "some-room")
        whenever(chatRoomRepository.save(any()))
                .thenAnswer { invocation ->
                    Mono.just(invocation.arguments[0] as ChatRoom)
                }
        whenever(chatRoomRepository.getRoom(any()))
                .thenAnswer {
                    Mono.just(chatRoom)
                }

        webTestClient.post()
                .uri("/chatrooms")
                .body(BodyInserters.fromValue(ChatRoom(id = "some-room", name = "some-room")))
                .exchange()
                .expectBody()
                .jsonPath("$.id").isEqualTo("some-room")
                .jsonPath("$.name").isEqualTo("some-room")

        webTestClient.get()
                .uri("/chatrooms/some-room")
                .exchange()
                .expectHeader()
                .contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isEqualTo("some-room")
                .jsonPath("$.name").isEqualTo("some-room")
    }
}