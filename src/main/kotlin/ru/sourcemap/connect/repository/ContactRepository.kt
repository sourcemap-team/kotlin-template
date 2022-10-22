package ru.sourcemap.connect.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.sourcemap.connect.entity.user.Contact
import ru.sourcemap.connect.entity.user.ContactType
import ru.sourcemap.connect.entity.user.User

interface ContactRepository : JpaRepository<Contact, Long> {

    fun findByUser(user: User): List<Contact>

    fun findByContactValueAndContactType(contactValue: String, contactType: ContactType): Contact?

}