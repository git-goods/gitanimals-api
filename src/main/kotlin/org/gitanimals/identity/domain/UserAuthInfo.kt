package org.gitanimals.identity.domain

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated

@Embeddable
class UserAuthInfo(
    @Enumerated(EnumType.STRING)
    @Column(name = "entry_point", columnDefinition = "VARCHAR(50) DEFAULT 'GITHUB'")
    val entryPoint: EntryPoint,

    @Column(name = "authentication_id", nullable = true)
    var authenticationId: String?,
)
