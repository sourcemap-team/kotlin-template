package ru.sourcemap.template

import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import ru.sourcemap.template.security.JwtConfiguration
import ru.sourcemap.template.service.notification.telegram.TelegramConfiguration

//TODO add indexes to database
@EnableJpaAuditing
@EnableConfigurationProperties(value = [JwtConfiguration::class, TelegramConfiguration::class])
@EnableJpaRepositories(basePackages = ["ru.sourcemap.template.repository"])
@SpringBootApplication
class ConnectApplication

fun main(args: Array<String>) {
    runApplication<ConnectApplication>(*args)
}


