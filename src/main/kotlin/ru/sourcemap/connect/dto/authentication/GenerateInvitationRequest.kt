package ru.sourcemap.connect.dto.authentication

import java.math.BigDecimal

data class GenerateInvitationRequest(
    val providerId: Long,
    val trustAmount: BigDecimal? = null
)