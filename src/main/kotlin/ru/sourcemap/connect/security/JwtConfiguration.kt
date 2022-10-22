package ru.sourcemap.connect.security

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "security.jwt")
data class JwtConfiguration(
    val secret: String,
    val expirationSec: Long,
    val refreshTokenExpirationSec: Long
)