package ru.sourcemap.connect.dto.authentication

import ru.sourcemap.connect.dto.user.UserDto
import java.math.BigDecimal

data class InvitationInfoDto(
    val invitedBy: UserDto,
    val provider: UserDto?
)