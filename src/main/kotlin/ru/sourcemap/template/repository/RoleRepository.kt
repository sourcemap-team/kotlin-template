package ru.sourcemap.template.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.sourcemap.template.entity.user.Role
import ru.sourcemap.template.entity.user.RoleName

interface RoleRepository  : JpaRepository<Role, Long>  {

    fun findByRoleName(roleName: RoleName): Role?

}