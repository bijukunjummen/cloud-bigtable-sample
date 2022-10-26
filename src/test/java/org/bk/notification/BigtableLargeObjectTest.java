package org.bk.notification;

import com.google.cloud.bigtable.data.v2.BigtableDataClient;
import com.google.cloud.bigtable.data.v2.BigtableDataSettings;
import com.google.cloud.bigtable.data.v2.models.Mutation;
import com.google.cloud.bigtable.data.v2.models.RowMutation;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
class BigtableLargeObjectTest {
    @Test
    void testSavingALargeObject() throws Exception {
        BigtableDataSettings.Builder settingsBuilder = BigtableDataSettings.newBuilder()
                .setInstanceId("messages-instance");

        BigtableDataClient bigtableClient = BigtableDataClient
                .create(settingsBuilder.build());

        String id = "12345654321";
        String chatRoomId = "12345678987654321";
        var key = "MESSAGES/R#" + chatRoomId + "/M#" + id;
        Mutation mutation = Mutation.create()
                .setCell("chatMessageDetails", "id", id)
                .setCell("chatMessageDetails", "chatRoomId", chatRoomId)
                .setCell("chatMessageDetails", "payload", "hello world");
        RowMutation rowMutationReversedTime = RowMutation.create("chat_messages", key, mutation);

        bigtableClient.mutateRow(rowMutationReversedTime);
    }

    @Test
    void deleteKey() throws Exception{
        BigtableDataSettings.Builder settingsBuilder = BigtableDataSettings.newBuilder()
                .setInstanceId("messages-instance")
                .setProjectId("biju-altostrat-demo");

        BigtableDataClient bigtableClient = BigtableDataClient
                .create(settingsBuilder.build());
        String id = "12345654321";
        String chatRoomId = "12345678987654321";
        var key = "MESSAGES/R#" + chatRoomId + "/M#" + id;
        Mutation mutation = Mutation.create().deleteRow();
        RowMutation deleteMutation = RowMutation.create("chat_messages", key, mutation);
        bigtableClient.mutateRow(deleteMutation);
    }
}
