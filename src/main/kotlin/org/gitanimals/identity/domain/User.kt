package org.gitanimals.identity.domain

import jakarta.persistence.*
import org.gitanimals.identity.core.AggregateRoot
import org.gitanimals.identity.domain.value.Contribution
import org.hibernate.annotations.BatchSize
import java.time.ZoneId
import java.time.ZonedDateTime

@AggregateRoot
@Table(
    name = "users", indexes = [
        Index(columnList = "username", unique = true)
    ]
)
@Entity(name = "users")
class User(
    @Id
    @Column(name = "id")
    val id: Long,

    @Column(name = "username", nullable = false)
    val name: String,

    @Column(name = "points", nullable = false, columnDefinition = "TEXT")
    private var points: Long,

    @BatchSize(size = 20)
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "users_contributions")
    private val contributions: MutableList<Contribution> = mutableListOf(),

    @OneToMany(
        mappedBy = "user",
        fetch = FetchType.LAZY,
        orphanRemoval = true,
        cascade = [CascadeType.ALL],
    )
    private val personas: MutableList<Persona>,

    @Version
    private val version: Long,
) : AbstractTime() {

    fun getPoints(): Long = points

    fun givePoint(contribution: Int) {
        val currentYearContribution = getCurrentYearContribution()

        val newContribution = contribution - currentYearContribution.contribution
        currentYearContribution.contribution += newContribution

        plusPoints(newContribution.toLong() * POINT_RATIO)
    }

    private fun getCurrentYearContribution(): Contribution {
        val currentYear = ZonedDateTime.now(ZoneId.of("UTC")).year
        return contributions.find { it.year == currentYear }
            ?: run {
                val newContribution = Contribution(currentYear, 0)
                contributions.add(newContribution)
                newContribution
            }
    }

    private fun plusPoints(points: Long) {
        this.points += points
    }


    private companion object {
        private const val POINT_RATIO = 100L
    }
}
