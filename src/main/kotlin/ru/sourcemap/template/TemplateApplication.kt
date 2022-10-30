package ru.sourcemap.template

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import ru.sourcemap.template.security.JwtConfiguration

@EnableJpaAuditing
@EnableConfigurationProperties(value = [JwtConfiguration::class])
@EnableJpaRepositories(basePackages = ["ru.sourcemap.template.repository"])
@SpringBootApplication
class TemplateApplication

fun main(args: Array<String>) {
    runApplication<TemplateApplication>(*args)
}


