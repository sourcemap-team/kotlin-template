package ru.sourcemap.template.entity.user

import ru.sourcemap.template.entity.AuditingEntityWithLongKey
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated

//TODO cache roles: @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
class Role(
    @Enumerated(EnumType.STRING)
    val roleName: RoleName,
    val roleDisplayName: String,
    val invite: Boolean
): AuditingEntityWithLongKey() {

    fun getAuthorities(): Collection<Authority> {
        val authorities: MutableCollection<Authority> = mutableListOf()
        if(invite) {
            authorities.add(Authority.INVITE)
        }
        return authorities
    }

}