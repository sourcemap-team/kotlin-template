package ru.sourcemap.template.dto.user

import java.time.LocalDateTime

data class UserDto(
    val id: Long,
    val username: String,
    val profilePictureUrl: String? = null,
    val roleInfo: RoleDto,
    val accountRegistrationDate: LocalDateTime
)