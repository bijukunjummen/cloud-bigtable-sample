package org.bk.notification.service

import com.google.cloud.bigtable.admin.v2.BigtableTableAdminClient
import com.google.cloud.bigtable.admin.v2.BigtableTableAdminSettings
import com.google.cloud.bigtable.admin.v2.models.CreateTableRequest
import com.google.cloud.bigtable.data.v2.BigtableDataClient
import com.google.cloud.bigtable.data.v2.BigtableDataSettings
import com.google.cloud.bigtable.data.v2.models.Mutation
import com.google.cloud.bigtable.data.v2.models.Query
import com.google.cloud.bigtable.data.v2.models.Range
import com.google.cloud.bigtable.data.v2.models.Row
import com.google.cloud.bigtable.data.v2.models.RowMutation
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.testcontainers.containers.BigtableEmulatorContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

data class Person(val id: String, val name: String, val address: String, val country: String, val key: String)

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BigtablePaginationIntegrationTest {
    private lateinit var bigtableDataClient: BigtableDataClient
    private lateinit var bigtableAdminClient: BigtableTableAdminClient


    @BeforeAll
    fun beforeEach() {
        val dataSettings = BigtableDataSettings
            .newBuilderForEmulator(emulator.emulatorPort)
            .setProjectId("project-id")
            .setInstanceId("instance-id")
            .build()
        bigtableDataClient = BigtableDataClient
            .create(dataSettings)

        val dataAdminSettings: BigtableTableAdminSettings = BigtableTableAdminSettings
            .newBuilderForEmulator(emulator.emulatorPort)
            .setProjectId("project-id")
            .setInstanceId("instance-id")
            .build()
        bigtableAdminClient = BigtableTableAdminClient.create(dataAdminSettings)
        bigtableAdminClient.createTable(
            CreateTableRequest.of("people")
                .addFamily("details")
        )

    }

    @Test
    fun `save and paginate from bigtable`() {
        val people = listOf(
            Person("id-1", "name1", "address1", "USA", "person#id-001"),
            Person("id-2", "name2", "address2", "USA", "person#id-002"),
            Person("id-3", "name3", "address3", "USA", "person#id-003"),
            Person("id-4", "name4", "address3", "USA", "person#id-004"),
            Person("id-5", "name5", "address3", "USA", "person#id-005"),
            Person("id-6", "name6", "address6", "USA", "person#id-006"),
            Person("id-7", "name7", "address7", "USA", "person#id-007"),
            Person("id-8", "name8", "address8", "USA", "person#id-008"),
            Person("id-9", "name9", "address9", "USA", "person#id-009"),
            Person("id-10", "name10", "address10", "USA", "person#id-010")
        )
        people.forEach { p ->
            println("${p.key}\t${p.id}\t${p.name}\t${p.address}\t${p.country}\t")

        }
        people.forEach { p ->
            val mutation: Mutation = Mutation.create()
                .setCell("details", "id", p.id)
                .setCell("details", "name", p.name)
                .setCell("details", "address", p.address)
                .setCell("details", "country", p.country)

            val rowMutation = RowMutation.create("people", p.key, mutation)
            bigtableDataClient.mutateRow(rowMutation)
        }

        var offset = ""
        var rows: List<Person> = readRows(4, offset)
        while (rows.isNotEmpty()) {
            println("With offset $offset")
            rows.forEach { p ->
                println(p)
            }
            offset = if (rows.isNotEmpty()) rows.last().key else offset
            rows = readRows(4, offset)
        }
    }

    fun readRows(limit: Long, offset: String): List<Person> {
        val keyPrefix = "person#"
        val query: Query =
            if (offset.isNotEmpty()) {
                Query.create("people")
                    .limit(limit)
                    .range(Range.ByteStringRange.prefix(keyPrefix).startOpen(offset))
            } else Query.create("people").limit(limit).prefix(keyPrefix)

        val rows: List<Row> = bigtableDataClient.readRows(query).toList()
        val peopleListFromBigtable: List<Person> = rows.map { row ->
            val id = row.getCells("details", "id").first().value.toStringUtf8()
            val name = row.getCells("details", "name").first().value.toStringUtf8()
            val address = row.getCells("details", "address").first().value.toStringUtf8()
            val country = row.getCells("details", "country").first().value.toStringUtf8()
            val key = row.key.toStringUtf8()
            Person(id, name, address, country, key)
        }
        return peopleListFromBigtable
    }

    companion object {
        @JvmStatic
        @Container
        private val emulator = BigtableEmulatorContainer(
            DockerImageName.parse("gcr.io/google.com/cloudsdktool/cloud-sdk:373.0.0-emulators")
        )
    }
}