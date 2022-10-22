package ru.sourcemap.connect.entity.user

import ru.sourcemap.connect.entity.AuditingEntityWithLongKey
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated

//TODO cache roles: @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
class Role(
    @Enumerated(EnumType.STRING)
    val roleName: RoleName,
    val roleDisplayName: String,
    val invite: Boolean,
    val provide: Boolean,
    val modifyRates: Boolean,
    val maintain: Boolean
): AuditingEntityWithLongKey() {

    fun getAuthorities(): Collection<Authority> {
        val authorities: MutableCollection<Authority> = mutableListOf()
        if(invite) {
            authorities.add(Authority.INVITE)
        }
        if(provide) {
            authorities.add(Authority.PROVIDE)
        }
        if(modifyRates) {
            authorities.add(Authority.MODIFY_RATES)
        }
        if(maintain) {
            authorities.add(Authority.MAINTAIN)
        }
        return authorities
    }

}