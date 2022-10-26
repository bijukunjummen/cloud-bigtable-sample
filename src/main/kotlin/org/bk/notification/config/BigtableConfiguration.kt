package org.bk.notification.config

import com.google.api.gax.core.NoCredentialsProvider
import com.google.api.gax.grpc.ChannelPrimer
import com.google.api.gax.retrying.RetrySettings
import com.google.api.gax.rpc.StatusCode
import com.google.cloud.ServiceOptions
import com.google.cloud.bigtable.common.Status.Code
import com.google.cloud.bigtable.data.v2.BigtableDataClient
import com.google.cloud.bigtable.data.v2.BigtableDataSettings
import com.google.common.collect.ImmutableSet
import org.bk.notification.service.BigtableChatMessageRepository
import org.bk.notification.service.BigtableChatRoomRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.threeten.bp.Duration
import java.util.Collections

@Configuration
class BigtableConfiguration {
    @Bean
    fun bigtableDataClient(bigtableProperties: BigtableProperties): BigtableDataClient {
        val settingsBuilder: BigtableDataSettings.Builder =
            if (bigtableProperties.emulatorPort != 0) {
                BigtableDataSettings
                    .newBuilderForEmulator(
                        bigtableProperties.emulatorHost,
                        bigtableProperties.emulatorPort
                    )
                    .setProjectId(bigtableProperties.projectId)
                    .setInstanceId(bigtableProperties.instanceId)
                    .setCredentialsProvider(NoCredentialsProvider.create())

            } else {
                BigtableDataSettings
                    .newBuilder()
                    .setInstanceId(bigtableProperties.instanceId)
                    .setProjectId(ServiceOptions.getDefaultProjectId())
            }


//        settingsBuilder.stubSettings().readRowSettings()
//            .setRetryableCodes(StatusCode.Code.DEADLINE_EXCEEDED, StatusCode.Code.UNAVAILABLE)
//            .setRetrySettings(
//                RetrySettings.newBuilder()
//                    .setMaxAttempts(5)
//                    .setInitialRetryDelay(Duration.ofSeconds(5))
//                    .setRetryDelayMultiplier(2.0)
//                    .setMaxRetryDelay(Duration.ofSeconds(10))
//                    .setInitialRpcTimeout(Duration.ofSeconds(5))
//                    .setRpcTimeoutMultiplier(2.0)
//                    .setMaxRpcTimeout(Duration.ofSeconds(60))
//                    .setTotalTimeout(Duration.ofSeconds(60))
//                    .build()
//            )
//        settingsBuilder.stubSettings().readRowsSettings()
//            .setRetryableCodes(StatusCode.Code.DEADLINE_EXCEEDED, StatusCode.Code.UNAVAILABLE)
//            .setRetrySettings(
//                RetrySettings.newBuilder()
//                    .setMaxAttempts(5)
//                    .setInitialRetryDelay(Duration.ofSeconds(5))
//                    .setRetryDelayMultiplier(2.0)
//                    .setMaxRetryDelay(Duration.ofSeconds(10))
//                    .setInitialRpcTimeout(Duration.ofSeconds(5))
//                    .setRpcTimeoutMultiplier(2.0)
//                    .setMaxRpcTimeout(Duration.ofSeconds(60))
//                    .setTotalTimeout(Duration.ofSeconds(60))
//                    .build()
//            )
//
//        settingsBuilder.stubSettings().bulkReadRowsSettings()
//            .setRetryableCodes(StatusCode.Code.DEADLINE_EXCEEDED, StatusCode.Code.UNAVAILABLE)
//            .setRetrySettings(
//                RetrySettings.newBuilder()
//                    .setMaxAttempts(5)
//                    .setInitialRetryDelay(Duration.ofSeconds(5))
//                    .setRetryDelayMultiplier(2.0)
//                    .setMaxRetryDelay(Duration.ofSeconds(10))
//                    .setInitialRpcTimeout(Duration.ofSeconds(5))
//                    .setRpcTimeoutMultiplier(2.0)
//                    .setMaxRpcTimeout(Duration.ofSeconds(60))
//                    .setTotalTimeout(Duration.ofSeconds(60))
//                    .build()
//            )

        return BigtableDataClient
            .create(settingsBuilder.build())
    }

    @Bean
    fun bigtableChatRoomRepository(bigtableDataClient: BigtableDataClient): BigtableChatRoomRepository {
        return BigtableChatRoomRepository(bigtableDataClient)
    }

    @Bean
    fun bigtableChatMessageRepository(bigtableDataClient: BigtableDataClient): BigtableChatMessageRepository {
        return BigtableChatMessageRepository(bigtableDataClient)
    }
}
