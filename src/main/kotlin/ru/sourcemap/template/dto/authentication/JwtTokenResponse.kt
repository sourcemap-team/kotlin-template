package ru.sourcemap.template.dto.authentication

data class JwtTokenResponse(
    val accessToken: String,
    val refreshToken: String
)