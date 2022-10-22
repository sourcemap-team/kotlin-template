package ru.sourcemap.connect.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.sourcemap.connect.entity.user.Role
import ru.sourcemap.connect.entity.user.RoleName

interface RoleRepository  : JpaRepository<Role, Long>  {

    fun findByRoleName(roleName: RoleName): Role?

}