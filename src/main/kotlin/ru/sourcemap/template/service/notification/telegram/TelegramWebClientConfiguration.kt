package ru.sourcemap.template.service.notification.telegram

import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Configuration
class TelegramWebClientConfiguration {

    private val logger = LoggerFactory.getLogger(TelegramWebClientConfiguration::class.java)

    @Bean
    fun telegramWebClient(telegramConfiguration: TelegramConfiguration): WebClient {
        return WebClient
            .builder()
            .baseUrl(telegramConfiguration.apiUrl)
            .filters { exchangeFilterFunctions ->
                exchangeFilterFunctions.add(logRequest())
                exchangeFilterFunctions.add(logResponse())
            }
            .build()
    }

    fun logRequest(): ExchangeFilterFunction =
        ExchangeFilterFunction.ofRequestProcessor {
            logger.debug("making request ${it.method().name}: ${it.url()}")
            Mono.just(it)
        }

    fun logResponse(): ExchangeFilterFunction =
        ExchangeFilterFunction.ofResponseProcessor {
            logger.debug("received response with status code: ${it.statusCode()}")
            Mono.just(it)
        }
}