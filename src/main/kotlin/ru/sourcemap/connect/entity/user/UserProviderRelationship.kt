package ru.sourcemap.connect.entity.user

import ru.sourcemap.connect.entity.AuditingEntityWithLongKey
import java.math.BigDecimal
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Entity
class UserProviderRelationship(
    @ManyToOne
    @JoinColumn(name = "provider_id")
    var provider: User,
    @ManyToOne
    @JoinColumn(name = "user_id")
    var user: User,
    @Column(scale = 8)
    var feeRate: BigDecimal,
    val trustAmount: BigDecimal? = null,
    @ManyToOne
    @JoinColumn(name = "recommended_by_id")
    val recommendedBy: User? = null,
): AuditingEntityWithLongKey()