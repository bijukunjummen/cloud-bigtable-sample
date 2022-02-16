package org.bk.notification.service

import com.google.api.core.ApiFuture
import com.google.cloud.bigtable.data.v2.BigtableDataClient
import com.google.cloud.bigtable.data.v2.models.Mutation
import com.google.cloud.bigtable.data.v2.models.Row
import com.google.cloud.bigtable.data.v2.models.RowMutation
import org.bk.notification.model.ChatRoom
import org.bk.notification.toMono
import reactor.core.publisher.Mono

class BigtableChatRoomRepository(private val bigtableDataClient: BigtableDataClient) : ChatRoomRepository {
    override fun save(chatRoom: ChatRoom): Mono<ChatRoom> {
        val key = "ROOM/R#${chatRoom.id}"
        val mutation: Mutation = Mutation.create()
                .setCell(CHAT_ROOM_DETAILS_FAMILY, NAME_QUALIFIER, chatRoom.name)
                .setCell(CHAT_ROOM_DETAILS_FAMILY, ID_QUALIFIER, chatRoom.id)
        val rowMutation: RowMutation = RowMutation.create(TABLE_ID, key, mutation)
        val result: ApiFuture<Void> = bigtableDataClient.mutateRowAsync(rowMutation)
        return result.toMono()
                .thenReturn(chatRoom)
    }

    override fun getRoom(chatRoomId: String): Mono<ChatRoom> {
        val key = "ROOM/R#${chatRoomId}"
        val rowFuture: ApiFuture<Row> = bigtableDataClient.readRowAsync(TABLE_ID, key)
        return rowFuture.toMono()
                .map { row ->
                    toRoom(row)
                }
    }

    private fun toRoom(row: Row): ChatRoom {
        val id = row.getCells(CHAT_ROOM_DETAILS_FAMILY, ID_QUALIFIER).first().value.toStringUtf8()
        val name = row.getCells(CHAT_ROOM_DETAILS_FAMILY, NAME_QUALIFIER).first().value.toStringUtf8()
        return ChatRoom(id = id, name = name)
    }

    companion object {
        private const val TABLE_ID = "chat_messages"
        private const val CHAT_ROOM_DETAILS_FAMILY = "chatRoomDetails"
        private const val NAME_QUALIFIER = "name"
        private const val ID_QUALIFIER = "id"
    }
}