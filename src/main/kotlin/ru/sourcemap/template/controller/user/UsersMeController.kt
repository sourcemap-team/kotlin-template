package ru.sourcemap.template.controller.user

import io.swagger.v3.oas.annotations.Operation
import org.springframework.core.env.Environment
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import ru.sourcemap.template.dto.user.InvitationTokenDto
import ru.sourcemap.template.dto.user.UserDto
import ru.sourcemap.template.dto.user.UserModificationRequest
import ru.sourcemap.template.entity.user.User
import ru.sourcemap.template.repository.*
import ru.sourcemap.template.service.JwtUserExtractorService

@RestController
@RequestMapping("/users/me")
class UsersMeController(
    private val jwtUserExtractorService: JwtUserExtractorService,
    private val userRepository: UserRepository,
    private val invitationTokenRepository: InvitationTokenRepository,
    private val userProviderRelationshipRepository: UserProviderRelationshipRepository,
    private val environment: Environment
) {

    @GetMapping
    fun usersMe(
        @RequestHeader(HttpHeaders.AUTHORIZATION) authHeader: String
    ): ResponseEntity<UserDto> {
        val user = jwtUserExtractorService.getUserFromAuthorizationHeader(authHeader)
        return ResponseEntity.ok(user.toDto())
    }

    @Operation(description = "Изменить имя или фото текущего юзера")
    @PostMapping
    fun modifyUser(
        @RequestHeader(HttpHeaders.AUTHORIZATION) authHeader: String,
        @RequestBody userModificationRequest: UserModificationRequest
    ): ResponseEntity<UserDto> {
        val user = jwtUserExtractorService.getUserFromAuthorizationHeader(authHeader)
        userModificationRequest.username?.let { user.username = it }
        userModificationRequest.profilePictureUrl?.let { user.profilePictureUrl = it }
        userRepository.save(user)
        return ResponseEntity.ok(user.toDto())
    }

    @Operation(description = "Получить информацию о всех приглашениях, созданных текущим пользователем")
    @GetMapping("/invitations")
    fun getMyInvitations(
        @RequestHeader(HttpHeaders.AUTHORIZATION) authHeader: String
    ): ResponseEntity<Collection<InvitationTokenDto>> {
        val user = jwtUserExtractorService.getUserFromAuthorizationHeader(authHeader)
        val invitationTokens = invitationTokenRepository.findAllByCreatedBy(user).map { it.toDto() }
        return ResponseEntity.ok(invitationTokens)
    }

    @DeleteMapping
    @Transactional
    fun deleteCurrentUser(
        @RequestHeader(HttpHeaders.AUTHORIZATION) authHeader: String
    ): ResponseEntity<Any> {
        if(environment.activeProfiles.contains("prod")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
        val user: User = jwtUserExtractorService.getUserFromAuthorizationHeader(authHeader)
        val userProvidesAt = user.providerAt
        val providedAt = user.providedAt.first()
        userProvidesAt.forEach {
            it.provider = providedAt.provider
        }
        userProviderRelationshipRepository.saveAll(userProvidesAt)
        user.providedAt.remove(providedAt)
        userProviderRelationshipRepository.deleteById(providedAt.id!!)
        invitationTokenRepository.deleteAllByUsedBy(user)
        invitationTokenRepository.deleteAllByCreatedBy(user)
        userRepository.delete(user)
        return ResponseEntity.ok().build()
    }

}