package ru.sourcemap.template.dto.user

import java.math.BigDecimal

data class ProviderDto(
    val provider: UserDto,
    val inviter: UserDto?,
    val trustAmount: BigDecimal?
)