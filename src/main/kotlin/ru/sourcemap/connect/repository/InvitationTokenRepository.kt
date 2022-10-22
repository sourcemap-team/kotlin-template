package ru.sourcemap.connect.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.sourcemap.connect.entity.user.InvitationToken
import ru.sourcemap.connect.entity.user.User

interface InvitationTokenRepository : JpaRepository<InvitationToken, String> {

    fun findAllByCreatedBy(createdBy: User): Collection<InvitationToken>

    fun deleteAllByCreatedBy(createdBy: User)

    fun deleteAllByUsedBy(usedBy: User)

}