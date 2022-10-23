package ru.sourcemap.template.security

import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import ru.sourcemap.template.entity.user.User
import ru.sourcemap.template.repository.UserRepository
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@Component
class JwtTokenFilter(
    private val jwtUtils: JwtUtils,
    private val userRepository: UserRepository
) : OncePerRequestFilter() {

    private val logger = LoggerFactory.getLogger(JwtTokenFilter::class.java)

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain
    ) {
        val cookiesToLog: String? = request.cookies?.joinToString("\n") { "name=${it.name} value=${it.value}" }

        logger.debug("Cookies:\n$cookiesToLog")

        val authHeader: String? = request.getHeader(HttpHeaders.AUTHORIZATION)
        val token = JwtUtils.extractTokenFromAuthHeader(authHeader)
        if (token == null || !jwtUtils.validateJwtToken(token)) {
            chain.doFilter(request, response)
            return
        }
        val jwtSubject = jwtUtils.extractTokenSubject(token)
        val user: User? = userRepository.findById(jwtSubject.toLong()).orElse(null)
        if(user == null) {
            logger.warn("JWT token is valid, but could not find user $jwtSubject")
            chain.doFilter(request, response)
            return
        }
        val userDetails: UserDetails = user.toUserDetails()
        val authentication = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
        authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
        SecurityContextHolder.getContext().authentication = authentication
        chain.doFilter(request, response)
    }
}