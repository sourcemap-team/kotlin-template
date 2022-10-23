package ru.sourcemap.template.dto.user

import java.time.LocalDateTime

data class InvitationTokenDto(
    val token: String,
    val createdBy: UserDto,
    val ttlSec: Long,
    val used: Boolean,
    val usedBy: UserDto?,
    val createdAt: LocalDateTime,
    val usedAt: LocalDateTime?,
)