package ru.sourcemap.connect

import org.apache.commons.lang3.SystemUtils
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import ru.sourcemap.connect.security.JwtConfiguration
import ru.sourcemap.connect.service.notification.telegram.TelegramConfiguration

//TODO add indexes to database
@EnableJpaAuditing
@EnableConfigurationProperties(value = [JwtConfiguration::class, TelegramConfiguration::class])
@EnableJpaRepositories(basePackages = ["ru.sourcemap.connect.repository"])
@SpringBootApplication
class ConnectApplication

private val logger = LoggerFactory.getLogger(ConnectApplication::class.java)

fun main(args: Array<String>) {
    loadTdJniLibrary()
    runApplication<ConnectApplication>(*args)
}


