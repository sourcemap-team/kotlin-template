package ru.sourcemap.template.controller.user

import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import ru.sourcemap.template.dto.authentication.JwtTokenResponse
import ru.sourcemap.template.dto.authentication.RefreshTokenRequest
import ru.sourcemap.template.dto.user.UserRegistrationRequest
import ru.sourcemap.template.security.JwtUtils
import ru.sourcemap.template.service.JwtUserExtractorService
import ru.sourcemap.template.service.LoginService

@RestController
class LoginController(
    private val jwtUtils: JwtUtils,
    private val loginService: LoginService,
    private val jwtUserExtractorService: JwtUserExtractorService
) {

    private val logger = LoggerFactory.getLogger(LoginController::class.java)

    @PostMapping("/register")
    fun register(@RequestBody userRegistrationRequest: UserRegistrationRequest): ResponseEntity<JwtTokenResponse> {
        loginService.registerUser(userRegistrationRequest)
        val jwtTokenResponse =
            loginService.authenticateByOtp(userRegistrationRequest.username, userRegistrationRequest.password)
        return ResponseEntity.ok()
            .header(HttpHeaders.AUTHORIZATION, "Bearer ${jwtTokenResponse.accessToken}")
            .body(jwtTokenResponse)
    }

    @PostMapping("/login")
    fun login(@RequestBody userRegistrationRequest: UserRegistrationRequest): ResponseEntity<JwtTokenResponse> {
        val jwtTokenResponse =
            loginService.authenticateByOtp(userRegistrationRequest.username, userRegistrationRequest.password)
        return ResponseEntity.ok()
            .header(HttpHeaders.AUTHORIZATION, "Bearer ${jwtTokenResponse.accessToken}")
            .body(jwtTokenResponse)
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
