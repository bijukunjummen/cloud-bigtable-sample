package org.bk.notification.web.model

enum class ErrorLevel {
    INFO, WARN, ERROR
}

data class ErrorData(
    val level: ErrorLevel,
    val msg: String
)

data class ErrorMessage(
    val msg: String,
    val errors: List<ErrorData>
)