package org.gitanimals.identity.domain

import jakarta.persistence.*

@Table(
    name = "users", indexes = [
        Index(columnList = "username", unique = true)
    ]
)
@Entity(name = "users")
class User(
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    val id: Long? = null,

    @Column(name = "username", nullable = false)
    val name: String,

    @OneToMany(mappedBy = "id")
    private val personas: List<Persona>,
) : AbstractTime() {


}
