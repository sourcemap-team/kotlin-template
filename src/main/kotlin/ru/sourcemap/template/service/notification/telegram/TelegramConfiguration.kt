package ru.sourcemap.template.service.notification.telegram

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "telegram")
data class TelegramConfiguration(
    val apiUrl: String,
    val botToken: String,
    val chatId: String
)
