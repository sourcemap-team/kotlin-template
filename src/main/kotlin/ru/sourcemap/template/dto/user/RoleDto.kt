package ru.sourcemap.template.dto.user

import ru.sourcemap.template.entity.user.Authority
import ru.sourcemap.template.entity.user.RoleName

data class RoleDto(
    val name: RoleName,
    val displayName: String,
    val authorities: Collection<Authority>
)