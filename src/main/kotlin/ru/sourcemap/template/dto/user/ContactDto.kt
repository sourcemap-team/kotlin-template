package ru.sourcemap.template.dto.user

import ru.sourcemap.template.entity.user.ContactType

data class ContactDto(
    val contactValue: String,
    val contactType: ContactType
)