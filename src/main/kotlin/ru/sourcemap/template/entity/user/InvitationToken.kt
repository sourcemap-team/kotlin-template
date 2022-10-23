package ru.sourcemap.template.entity.user

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import ru.sourcemap.template.dto.user.InvitationTokenDto
import java.math.BigDecimal
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@EntityListeners(AuditingEntityListener::class)
class InvitationToken(

    @Id
    val token: String,
    @ManyToOne
    @JoinColumn(name = "creator_id")
    val createdBy: User,
    @OneToOne
    @JoinColumn(name = "used_by_user_id")
    var usedBy: User? = null,
    var usedAt: LocalDateTime? = null,
    var used: Boolean = false,
    val ttlSec: Long,
    @CreatedDate
    var createdAt: LocalDateTime? = null,
    val trustAmount: BigDecimal? = null,
    @ManyToOne
    @JoinColumn(name = "provider_id")
    val provider: User? = null,

) {

    fun toDto(): InvitationTokenDto {
        return InvitationTokenDto(
            token = token,
            createdBy = createdBy.toDto(),
            createdAt = createdAt!!,
            ttlSec = ttlSec,
            used = used,
            usedBy = usedBy?.toDto(),
            usedAt = usedAt,
        )
    }

}