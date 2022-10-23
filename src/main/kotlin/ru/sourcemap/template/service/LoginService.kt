package ru.sourcemap.template.service

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import ru.sourcemap.template.controller.errorhandling.BadRequestException
import ru.sourcemap.template.controller.errorhandling.ForbiddenException
import ru.sourcemap.template.controller.errorhandling.NotFoundException
import ru.sourcemap.template.dto.authentication.GenerateInvitationRequest
import ru.sourcemap.template.dto.authentication.JwtTokenResponse
import ru.sourcemap.template.dto.user.UserRegistrationRequest
import ru.sourcemap.template.entity.user.*
import ru.sourcemap.template.repository.ContactRepository
import ru.sourcemap.template.repository.InvitationTokenRepository
import ru.sourcemap.template.repository.RoleRepository
import ru.sourcemap.template.repository.UserRepository
import ru.sourcemap.template.security.JwtUtils
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*


@Service
class LoginService(
    private val invitationTokenRepository: InvitationTokenRepository,
    @Value("\${security.invitation-link.expiration-sec}")
    private val invitationTokenExpirationSec: Long,
    private val featureTogglesService: FeatureTogglesService,
    private val roleRepository: RoleRepository,
    private val passwordEncoder: PasswordEncoder,
    private val authenticationManager: AuthenticationManager,
    private val userRepository: UserRepository,
    private val jwtUtils: JwtUtils
) {

    private val logger = LoggerFactory.getLogger(LoginService::class.java)

    fun registerUser(userRegistrationRequest: UserRegistrationRequest) {
        val defaultRole = roleRepository.findByRoleName(RoleName.GUEST)!!
        userRepository.save(
            User(
                username = userRegistrationRequest.username,
                password = passwordEncoder.encode(userRegistrationRequest.password),
                role = defaultRole
            )
        )
    }

    fun authenticateByOtp(username: String, password: String): JwtTokenResponse {
        val user = userRepository.findByUsername(username) ?: throw ForbiddenException("invalid username or password")
        try {
            authenticationManager.authenticate(UsernamePasswordAuthenticationToken(user.id!!, password))
        } catch (ex: BadCredentialsException) {
            throw ForbiddenException("invalid username or password")
        }
        val jwtSubject = user.id!!.toString()
        return JwtTokenResponse(
            accessToken = jwtUtils.generateJwtToken(jwtSubject),
            refreshToken = jwtUtils.generateRefreshToken(jwtSubject)
        )
    }

    fun generateInvitationTokenByUser(user: User, generateInvitationRequest: GenerateInvitationRequest?): String {
        val invitationTokenString = UUID.randomUUID().toString()
        val recommendedProvider: User? = generateInvitationRequest?.providerId
            ?.let {
                userRepository.findById(it)
                    .orElseThrow { NotFoundException("Could not find provider to supply with invitation. Provider with id=$it not found.") }
            }
        val invitationToken = InvitationToken(
            token = invitationTokenString,
            createdBy = user,
            ttlSec = invitationTokenExpirationSec,
            trustAmount = generateInvitationRequest?.trustAmount,
            provider = recommendedProvider
        )
        invitationTokenRepository.save(invitationToken)
        return invitationTokenString
    }

    fun findAndValidateInvitationToken(invitationTokenString: String?): InvitationToken {
        if (invitationTokenString == null || invitationTokenString == "") {
            throw ForbiddenException("To login for the first time invitation link must be used")
        }
        val invitationToken: InvitationToken = invitationTokenRepository.findById(invitationTokenString).orElse(null)
            ?: throw ForbiddenException("Invalid invitation: $invitationTokenString")
        if (invitationToken.used) {
            throw ForbiddenException("Invitation is already used: $invitationTokenString")
        }
        if (invitationToken.createdAt!!.plusSeconds(invitationToken.ttlSec) < LocalDateTime.now()) {
            throw ForbiddenException("Invitation expired: $invitationTokenString")
        }
        return invitationToken
    }

    fun setInvitationTokenUsed(invitationToken: InvitationToken, usedBy: User) {
        if (featureTogglesService.isNonProdFeatureEnabled(FeatureToggle.FEATURE_INFINITE_USE_INVITATION_TOKENS)) {
            logger.warn("infinite invitation tokens enabled, invitation token: ${invitationToken.token} is not used")
            return
        }
        invitationToken.used = true
        invitationToken.usedAt = LocalDateTime.now()
        invitationToken.usedBy = usedBy
        invitationTokenRepository.save(invitationToken)
    }

    private fun determineProviderForNewUser(invitationToken: InvitationToken): User {
        val provider = invitationToken.provider ?: bfsSearchForProvider(invitationToken.createdBy)
        if (provider == null) {
            val errorText = "Could not find provider for new user, login with invitation: ${invitationToken.token}"
            logger.error(errorText)
            throw BadRequestException(errorText)
        }
        if (!provider.role.getAuthorities().contains(Authority.PROVIDE)) {
            val errorText = "Recommended provider has no PROVIDE authority, " +
                    "login with invitation: ${invitationToken.token}, " +
                    "attempted provider user id: ${provider.id}"
            logger.error(errorText)
            throw BadRequestException(errorText)
        }
        return provider
    }

    companion object {

        fun bfsSearchForProvider(root: User): User? {
            val queue: Queue<User> = LinkedList()
            queue.add(root)
            while (!queue.isEmpty()) {
                val checkNode: User = queue.poll()
                if (checkNode.role.getAuthorities().contains(Authority.PROVIDE)) {
                    return checkNode
                }
                queue.addAll(checkNode.providedAt.map { it.provider })
            }
            return null
        }
    }
}
