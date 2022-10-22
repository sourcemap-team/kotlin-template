package ru.sourcemap.connect.service

import org.springframework.stereotype.Service
import ru.sourcemap.connect.controller.errorhandling.ForbiddenException
import ru.sourcemap.connect.controller.errorhandling.NotFoundException
import ru.sourcemap.connect.entity.user.User
import ru.sourcemap.connect.repository.UserRepository
import ru.sourcemap.connect.security.JwtUtils

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