package ru.sourcemap.connect.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.sourcemap.connect.entity.user.User

interface UserRepository : JpaRepository<User, Long> {
}