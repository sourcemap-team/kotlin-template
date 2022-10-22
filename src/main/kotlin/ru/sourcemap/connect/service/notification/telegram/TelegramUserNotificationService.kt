package ru.sourcemap.connect.service.notification.telegram

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import ru.sourcemap.connect.service.notification.UserNotificationService

@Service
class TelegramUserNotificationService(
    private val telegramConfiguration: TelegramConfiguration,
    private val telegramWebClient: WebClient
): UserNotificationService {

    private val logger = LoggerFactory.getLogger(TelegramConfiguration::class.java)

    override fun sendMessage(messageText: String, recipientId: String) {
        logger.debug("Sending telegram notification for user: $recipientId")
        val response = telegramWebClient
            .post()
            .uri {
                it
                    .path("/bot{httpToken}/{method}")
                    .queryParam("chat_id", telegramConfiguration.chatId)
                    .queryParam("text", "$messageText for phone: $recipientId")
                    .build(telegramConfiguration.botToken, "sendMessage")
            }
            .retrieve()
            .bodyToMono(String::class.java)
            .block()
        logger.debug(response)
    }

}