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
        bigtableAdminClient.createTable(CreateTableRequest.of("chat_messages")
                .addFamily("chatRoomDetails")
                .addFamily("chatMessageDetails"))

        chatMessageRepository = BigtableChatMessageRepository(bigtableDataClient)
        chatRoomRepository = BigtableChatRoomRepository(bigtableDataClient)
    }

    @Test
    fun `save and retrieve from bigtable`() {
        val notification = sampleNotification("id-1", "some-channel")
        val chatRoom = ChatRoom("some-channel", "some-channel")
        StepVerifier.create(chatRoomRepository.save(chatRoom))
                .assertNext { savedRoom ->
                    assertThat(savedRoom).isEqualTo(chatRoom)
                }
                .verifyComplete()

        StepVerifier.create(chatMessageRepository.save(notification))
                .assertNext { savedNotification ->
                    assertThat(savedNotification).isEqualTo(notification)
                }
                .verifyComplete()

        StepVerifier.create(chatMessageRepository.getLatestSavedChatMessages(chatRoomId = "some-channel"))
                .assertNext { n ->
                    assertThat(n).isEqualTo(notification)
                }
                .verifyComplete()
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