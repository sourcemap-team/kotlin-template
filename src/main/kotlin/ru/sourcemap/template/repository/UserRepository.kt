package ru.sourcemap.template.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.sourcemap.template.entity.user.User

interface UserRepository : JpaRepository<User, Long> {
}