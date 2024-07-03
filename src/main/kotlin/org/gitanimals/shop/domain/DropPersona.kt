package org.gitanimals.shop.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.gitanimals.identity.core.AggregateRoot
import org.gitanimals.identity.core.IdGenerator

@Entity
@AggregateRoot
@Table(name = "drop_persona")
class DropPersona(
    @Id
    @Column(name = "id")
    val id: Long,

    @Column(name = "persona_id", nullable = false)
    val personaId: Long,

    @Column(name = "dropped_user_id", nullable = false)
    val droppedUserId: Long,

    @Column(name = "given_point", nullable = false)
    val given_point: Long

) {

    companion object {
        private const val DEFAULT_GIVEN_POINT = 100L

        fun of(personaId: Long, droppedUserId: Long): DropPersona {
            return DropPersona(
                id = IdGenerator.generate(),
                personaId = personaId,
                droppedUserId = droppedUserId,
                given_point = DEFAULT_GIVEN_POINT
            )
        }
    }
}
