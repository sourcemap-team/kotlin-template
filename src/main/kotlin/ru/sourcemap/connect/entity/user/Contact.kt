package ru.sourcemap.connect.entity.user

import ru.sourcemap.connect.entity.AuditingEntityWithLongKey
import javax.persistence.*

@Entity
class Contact(
    @ManyToOne
    @JoinColumn(name = "user_id")
    val user: User,
    val contactValue: String,
    @Enumerated(EnumType.STRING)
    val contactType: ContactType
): AuditingEntityWithLongKey()