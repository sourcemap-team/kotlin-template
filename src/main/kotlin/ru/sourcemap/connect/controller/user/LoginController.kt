package ru.sourcemap.connect.controller.user

import io.swagger.v3.oas.annotations.Operation
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.codec.digest.HmacAlgorithms
import org.apache.commons.codec.digest.HmacUtils
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import ru.sourcemap.connect.controller.errorhandling.BadRequestException
import ru.sourcemap.connect.controller.errorhandling.ForbiddenException
import ru.sourcemap.connect.dto.authentication.JwtTokenResponse
import ru.sourcemap.connect.dto.authentication.RefreshTokenRequest
import ru.sourcemap.connect.dto.authentication.UserTgLoginRequest
import ru.sourcemap.connect.entity.user.User
import ru.sourcemap.connect.security.JwtUtils
import ru.sourcemap.connect.service.FeatureToggle
import ru.sourcemap.connect.service.FeatureTogglesService
import ru.sourcemap.connect.service.JwtUserExtractorService
import ru.sourcemap.connect.service.LoginService
import ru.sourcemap.connect.service.notification.telegram.TelegramConfiguration

@RestController
class LoginController(
    private val jwtUtils: JwtUtils,
    private val loginService: LoginService,
    private val telegramConfiguration: TelegramConfiguration,
    private val featureTogglesService: FeatureTogglesService,
    private val jwtUserExtractorService: JwtUserExtractorService
) {

    private val logger = LoggerFactory.getLogger(LoginController::class.java)

    @Operation(description = "Авторизация через телеграм")
    @PostMapping("/login-tg")
    fun loginTg(
        @RequestBody userTgLoginRequest: UserTgLoginRequest
    ): ResponseEntity<JwtTokenResponse> {
        logger.debug("userTgLoginRequest: $userTgLoginRequest")
        validateTgHash(userTgLoginRequest.telegramPayload)
        val user = getOrCreateUserByTg(userTgLoginRequest)
        val jwtSubject: String = user.id!!.toString()
        val jwtToken = jwtUtils.generateJwtToken(jwtSubject)
        val jwtRefreshToken = jwtUtils.generateRefreshToken(jwtSubject)
        return ResponseEntity.ok()
            .header(HttpHeaders.AUTHORIZATION, "Bearer $jwtToken")
            .body(JwtTokenResponse(jwtToken, jwtRefreshToken))
    }

    private fun getOrCreateUserByTg(
        userTgLoginRequest: UserTgLoginRequest
    ): User {
        val telegramPayload = userTgLoginRequest.telegramPayload //TODO convert map to data class after telegram hash is validated
        val telegramId = telegramPayload["id"] ?: throw BadRequestException("telegram payload does not contain user id")
        val user: User? = loginService.findUserByTelegramID(telegramId)
        if (user != null) {
            return user
        }
        val invitationTokenString = userTgLoginRequest.invitationToken
        val invitationToken = loginService.findAndValidateInvitationToken(invitationTokenString)
        return loginService.createTgUserByInvitationToken(invitationToken, telegramPayload)
    }

    private fun validateTgHash(telegramPayload: Map<String, String>) {
        val botToken = telegramConfiguration.botToken
        val hash = telegramPayload["hash"]
        val calculationFields = telegramPayload.minus("hash")
        val dataCheckString = calculationFields.entries
            .map { "${it.key}=${it.value}" }
            .sorted()
            .joinToString("\n") { it }

        val secretKeyByteArray = DigestUtils.sha256(botToken)
        val calculatedHash =
            HmacUtils(HmacAlgorithms.HMAC_SHA_256.getName(), secretKeyByteArray).hmacHex(dataCheckString)
        val telegramHashCheckValid = calculatedHash == hash
        logger.debug("Telegram hash check valid = $telegramHashCheckValid for request $telegramPayload")
        val hashCheckDisabled = featureTogglesService.isNonProdFeatureEnabled(FeatureToggle.FEATURE_TELEGRAM_HASH_CHECK_DISABLED)
        if (hashCheckDisabled) {
            logger.warn("Telegram hash check result is ignored for request: $telegramPayload")
        }
        if(!telegramHashCheckValid && !hashCheckDisabled) {
            throw ForbiddenException("telegram hash validation failed")
        }
    }

    @PostMapping("/refresh-token")
    fun refreshToken(
            @RequestBody refreshTokenRequest: RefreshTokenRequest
    ): ResponseEntity<JwtTokenResponse> {
        logger.debug("refreshTokenRequest: $refreshTokenRequest")
        val user = jwtUserExtractorService.getUserFromJwtToken(refreshTokenRequest.refreshToken)
        val jwtSubject: String = user.id!!.toString()
        val jwtToken = jwtUtils.generateJwtToken(jwtSubject)
        val jwtRefreshToken = jwtUtils.generateRefreshToken(jwtSubject)
        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer $jwtToken")
                .body(JwtTokenResponse(jwtToken, jwtRefreshToken))
    }

}
