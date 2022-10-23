package ru.sourcemap.template.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.sourcemap.template.entity.user.Contact
import ru.sourcemap.template.entity.user.ContactType
import ru.sourcemap.template.entity.user.User

interface ContactRepository : JpaRepository<Contact, Long> {

    fun findByUser(user: User): List<Contact>

    fun findByContactValueAndContactType(contactValue: String, contactType: ContactType): Contact?

}