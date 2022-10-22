package ru.sourcemap.connect.security

import io.jsonwebtoken.*
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.*

@Component
class JwtUtils(
    private val jwtConfiguration: JwtConfiguration
) {

    fun generateRefreshToken(jwtSubject: String): String {
        return generateJwtToken(jwtSubject, jwtConfiguration.refreshTokenExpirationSec)
    }
    fun generateJwtToken(jwtSubject: String, ttlSec: Long = jwtConfiguration.expirationSec): String {
        val now = Date()
        return Jwts.builder()
            .setSubject(jwtSubject)
            .setIssuedAt(now)
            .setExpiration(Date(now.time + ttlSec * 1000))
            .signWith(SignatureAlgorithm.HS512, jwtConfiguration.secret)
            .compact()
    }

    fun extractTokenSubject(token: String): String {
        return Jwts.parser().setSigningKey(jwtConfiguration.secret).parseClaimsJws(token).body.subject
    }

    fun validateJwtToken(authToken: String): Boolean {
        try {
            Jwts.parser().setSigningKey(jwtConfiguration.secret).parseClaimsJws(authToken)
            return true
        } catch (e: SignatureException) {
            logger.error("Invalid JWT signature: {}", e.message)
        } catch (e: MalformedJwtException) {
            logger.error("Invalid JWT token: {}", e.message)
        } catch (e: ExpiredJwtException) {
            logger.error("JWT token is expired: {}", e.message)
        } catch (e: UnsupportedJwtException) {
            logger.error("JWT token is unsupported: {}", e.message)
        } catch (e: IllegalArgumentException) {
            logger.error("JWT claims string is empty: {}", e.message)
        }
        return false
    }

    companion object {

        private val logger = LoggerFactory.getLogger(JwtUtils::class.java)

        fun extractTokenFromAuthHeader(authHeader: String?): String? {
            if (authHeader.isNullOrEmpty() || !authHeader.startsWith("Bearer ")) {
                return null
            }
            return authHeader.split(" ")[1].trim { it == ' ' }
        }

    }
}