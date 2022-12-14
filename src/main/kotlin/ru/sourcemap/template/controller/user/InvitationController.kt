package ru.sourcemap.template.controller.user

import io.swagger.v3.oas.annotations.Operation
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.sourcemap.template.dto.authentication.GenerateInvitationRequest
import ru.sourcemap.template.dto.authentication.GenerateInvitationResponse
import ru.sourcemap.template.dto.authentication.InvitationInfoDto
import ru.sourcemap.template.entity.user.User
import ru.sourcemap.template.service.JwtUserExtractorService
import ru.sourcemap.template.service.LoginService

@RestController
class InvitationController(
    private val jwtUserExtractorService: JwtUserExtractorService,
    private val loginService: LoginService
) {

    private val logger = LoggerFactory.getLogger(InvitationController::class.java)

    @Operation(description = "Сгенерировать инвайт-код от имени текущего пользователя")
    @PostMapping("/invitation/generate")
    fun generateInvitation(
        @RequestHeader(HttpHeaders.AUTHORIZATION) authHeader: String?,
        @RequestBody(required = false) generateInvitationRequest: GenerateInvitationRequest?
    ): ResponseEntity<GenerateInvitationResponse> {
        val user: User = jwtUserExtractorService.getUserFromAuthorizationHeader(authHeader)
        val invitationTokenString = loginService.generateInvitationTokenByUser(user, generateInvitationRequest)
        return ResponseEntity.ok(
            GenerateInvitationResponse(
                invitationToken = invitationTokenString
            )
        )
    }

    @Operation(description = "Проверить валидность инвайт-кода")
    @GetMapping("/invitation/check")
    fun check(
        @RequestParam shareToken: String
    ): ResponseEntity<InvitationInfoDto> {
        logger.debug("/invitation/check request with shareToken: $shareToken")
        val invitationToken = loginService.findAndValidateInvitationToken(shareToken)
        val invitationCreator = invitationToken.createdBy
        val invitationProvider: User? = invitationToken.provider ?: LoginService.bfsSearchForProvider(invitationCreator)
        val responseBody = if (invitationProvider?.id == invitationCreator.id) {
            InvitationInfoDto(
                invitedBy = invitationCreator.toDto(),
                provider = null
            )
        } else {
            InvitationInfoDto(
                invitedBy = invitationCreator.toDto(),
                provider = invitationProvider?.toDto()
            )
        }
        return ResponseEntity.status(HttpStatus.OK).body(responseBody)
    }

}