package ru.sourcemap.connect.dto.authentication

data class JwtTokenResponse(
    val accessToken: String,
    val refreshToken: String
)