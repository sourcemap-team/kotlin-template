package ru.sourcemap.connect.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import ru.sourcemap.connect.entity.user.User
import ru.sourcemap.connect.entity.user.UserProviderRelationship

interface UserProviderRelationshipRepository: JpaRepository<UserProviderRelationship, Long> {

    fun findAllByProvider(provider: User): Collection<UserProviderRelationship>

    fun findAllByUser(user: User): Collection<UserProviderRelationship>

    fun findByUserAndProvider(user: User, provider: User): UserProviderRelationship?

    fun findByUser_IdAndProvider(userId: Long, provider: User): UserProviderRelationship?

    fun deleteAllByUser_Id(userId: Long)

    fun findDistinctByProviderOrderByUser_UsernameAsc(
        provider: User,
        pageable: Pageable
    ): Page<UserProviderRelationship>

}
