package org.bk.notification.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "bigtable")
data class BigtableProperties(
    val instanceId: String = "bus-instance",
    val emulatorHost: String = "localhost",
    val emulatorPort: Int = 0,
    val projectId: String = "project-id"
)
