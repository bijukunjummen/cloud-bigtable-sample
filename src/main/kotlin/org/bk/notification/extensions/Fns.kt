package org.bk.notification.extensions

import org.slf4j.LoggerFactory

inline fun <reified T> loggerFor() = LoggerFactory.getLogger(T::class.java)