package org.bk.notification.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConfigurationProperties(prefix = "bigtable")
@ConstructorBinding
class BigtableProperties(
    val instanceId: String = "bus-instance",
    val emulatorHost: String = "localhost",
    val emulatorPort: Int = 0,
    val projectId: String = "project-id"
)
