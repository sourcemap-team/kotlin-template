package ru.sourcemap.connect.security

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import ru.sourcemap.connect.entity.user.User
import ru.sourcemap.connect.repository.UserRepository


@Service
class DatabaseUserDetailsService(
    private val userRepository: UserRepository
    ) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails? {
        val user: User = userRepository.findById(username.toLong()).orElseThrow()
        return user.toUserDetails()
    }
}

