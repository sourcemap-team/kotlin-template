package ru.sourcemap.template.service

import org.springframework.stereotype.Service
import ru.sourcemap.template.controller.errorhandling.ForbiddenException
import ru.sourcemap.template.controller.errorhandling.NotFoundException
import ru.sourcemap.template.entity.user.User
import ru.sourcemap.template.repository.UserRepository
import ru.sourcemap.template.security.JwtUtils

@Service
class JwtUserExtractorService(
    private val userRepository: UserRepository,
    private val jwtUtils: JwtUtils
) {

    fun getUserFromAuthorizationHeader(authHeader: String?): User {
        val jwtToken = JwtUtils.extractTokenFromAuthHeader(authHeader)
            ?: throw ForbiddenException("jwt token is invalid: $authHeader")
        return getUserFromJwtToken(jwtToken)
    }

    fun getUserFromJwtToken(jwtToken: String): User {
        val jwtSubject = jwtUtils.extractTokenSubject(jwtToken)
        return userRepository.findById(jwtSubject.toLong()).orElse(null)
                ?: throw NotFoundException("jwt token was valid, but could not find user")
    }

}