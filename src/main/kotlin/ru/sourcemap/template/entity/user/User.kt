package ru.sourcemap.template.entity.user

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import ru.sourcemap.template.dto.user.RoleDto
import ru.sourcemap.template.dto.user.UserDto
import ru.sourcemap.template.entity.AuditingEntityWithLongKey
import javax.persistence.*

@Entity
@Table(name = "users")
class User(
    var username: String,
    var profilePictureUrl: String? = null,
    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val contacts: MutableCollection<Contact> = mutableListOf(),
    @OneToMany(mappedBy = "user", cascade = [CascadeType.PERSIST], fetch = FetchType.LAZY)
    val providedAt: MutableCollection<UserProviderRelationship> = mutableListOf(),
    @OneToMany(mappedBy = "provider", cascade = [CascadeType.PERSIST], fetch = FetchType.LAZY)
    val providerAt: MutableCollection<UserProviderRelationship> = mutableListOf(),
    @ManyToOne
    @JoinColumn(name = "role_id")
    var role: Role,
) : AuditingEntityWithLongKey() {

    fun toDto(): UserDto {
        return UserDto(
            id = this.id!!,
            username = this.username,
            profilePictureUrl = this.profilePictureUrl,
            roleInfo = RoleDto(
                name = this.role.roleName,
                displayName = this.role.roleDisplayName,
                authorities = this.role.getAuthorities()
            ),
            accountRegistrationDate = this.createdAt!!
        )
    }

    fun toUserDetails(): UserDetails {
        val user = this
        return object : UserDetails {

            override fun getAuthorities(): MutableCollection<out GrantedAuthority> = mutableListOf()

            override fun getPassword(): String? = null

            override fun getUsername(): String = user.id!!.toString()

            override fun isAccountNonExpired(): Boolean = true

            override fun isAccountNonLocked(): Boolean = true

            override fun isCredentialsNonExpired(): Boolean = true

            override fun isEnabled(): Boolean = true

        }
    }

}



