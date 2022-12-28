package org.bk.notification.service

import com.google.cloud.bigtable.admin.v2.BigtableTableAdminClient
import com.google.cloud.bigtable.admin.v2.BigtableTableAdminSettings
import com.google.cloud.bigtable.admin.v2.models.CreateTableRequest
import com.google.cloud.bigtable.data.v2.BigtableDataClient
import com.google.cloud.bigtable.data.v2.BigtableDataSettings
import org.assertj.core.api.Assertions.assertThat
import org.bk.notification.model.ChatMessage
import org.bk.notification.model.ChatRoom
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.boot.test.autoconfigure.json.JsonTest
import org.testcontainers.containers.BigtableEmulatorContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import reactor.core.publisher.Flux
import reactor.test.StepVerifier
import java.time.Instant

@JsonTest
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BigtableNotificationsIntegrationTest {
    private lateinit var chatMessageRepository: BigtableChatMessageRepository
    private lateinit var chatRoomRepository: BigtableChatRoomRepository

    @BeforeAll
    fun beforeEach() {
        val dataSettings = BigtableDataSettings
            .newBuilderForEmulator(emulator.emulatorPort)
            .setProjectId("project-id")
            .setInstanceId("instance-id")
            .build()
        val bigtableDataClient = BigtableDataClient
            .create(dataSettings)

        val dataAdminSettings: BigtableTableAdminSettings = BigtableTableAdminSettings
            .newBuilderForEmulator(emulator.emulatorPort)
            .setProjectId("project-id")
            .setInstanceId("instance-id")
            .build()
        val bigtableAdminClient: BigtableTableAdminClient = BigtableTableAdminClient.create(dataAdminSettings)
        bigtableAdminClient.createTable(
            CreateTableRequest.of("chat_messages")
                .addFamily("chatRoomDetails")
                .addFamily("chatMessageDetails")
        )

        chatMessageRepository = BigtableChatMessageRepository(bigtableDataClient)
        chatRoomRepository = BigtableChatRoomRepository(bigtableDataClient)
    }

    @Test
    fun `save and retrieve from bigtable`() {
        val not1 = sampleNotification("id-1", "some-channel")
        val not2 = sampleNotification("id-2", "some-channel")
        val not3 = sampleNotification("id-3", "some-channel")
        val not4 = sampleNotification("id-4", "some-channel")
        val chatRoom = ChatRoom("some-channel", "some-channel")
        StepVerifier.create(chatRoomRepository.save(chatRoom))
            .assertNext { savedRoom ->
                assertThat(savedRoom).isEqualTo(chatRoom)
            }.verifyComplete()

        StepVerifier.create(
            Flux.concat(
                chatMessageRepository.save(not1),
                chatMessageRepository.save(not2),
                chatMessageRepository.save(not3),
                chatMessageRepository.save(not4)
            )
        ).assertNext { n1 ->
            assertThat(n1).isEqualTo(not1)
        }.assertNext { n2 ->
            assertThat(n2).isEqualTo(not2)
        }.assertNext { n3 ->
            assertThat(n3).isEqualTo(not3)
        }.assertNext { n4 ->
            assertThat(n4).isEqualTo(not4)
        }.verifyComplete()

        StepVerifier.create(chatMessageRepository.getLatestSavedChatMessages(chatRoomId = "some-channel"))
            .assertNext { n ->
                assertThat(n).isEqualTo(not1)
            }.assertNext { n ->
                assertThat(n).isEqualTo(not2)
            }.assertNext { n ->
                assertThat(n).isEqualTo(not3)
            }.assertNext { n ->
                assertThat(n).isEqualTo(not4)
            }.verifyComplete()

        val p1 = chatMessageRepository.getPaginatedMessages(chatRoomId = "some-channel", count = 1)
        assertThat(p1.data).isEqualTo(listOf(not1))

        val p2 = chatMessageRepository.getPaginatedMessages(
            chatRoomId = "some-channel",
            offset = p1.nextOffset,
            count = 1
        )
        assertThat(p2.data).isEqualTo(listOf(not2))
        val p3 = chatMessageRepository.getPaginatedMessages(
            chatRoomId = "some-channel",
            offset = p2.nextOffset,
            count = 2
        )
        assertThat(p3.data).isEqualTo(listOf(not3, not4))
    }


    private fun sampleNotification(id: String, channelId: String): ChatMessage =
        ChatMessage(
            id = id,
            chatRoomId = channelId,
            creationDate = Instant.now(),
            payload = "some payload"
        )

    companion object {
        @JvmStatic
        @Container
        private val emulator = BigtableEmulatorContainer(
            DockerImageName.parse("gcr.io/google.com/cloudsdktool/cloud-sdk:373.0.0-emulators")
        )
    }
}