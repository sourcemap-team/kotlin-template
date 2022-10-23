package ru.sourcemap.template.dto.authentication

import ru.sourcemap.template.dto.user.UserDto

data class InvitationInfoDto(
    val invitedBy: UserDto,
    val provider: UserDto?
)