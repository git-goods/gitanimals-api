package org.gitanimals.quiz.domain.context

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import org.gitanimals.core.instant
import java.time.Instant
import kotlin.time.Duration.Companion.seconds

@Embeddable
class SolveStage(
    @Column(name = "max_solve_stage")
    val maxSolveStage: Int,
    @Column(name = "current_solve_stage")
    private var currentStage: Int,
    @Column(name = "current_stage_time_out", nullable = true)
    private var currentStageTimeout: Instant?,
) {

    fun getCurrentStage() = currentStage

    fun getCurrentStageTimeout() = currentStageTimeout

    fun setNextStage() {
        check(currentStage + 1 <= maxSolveStage) {
            "Cannot start solve cause already solve all quiz. currentStage: \"$currentStage\", maxSolveStage: \"$maxSolveStage\""
        }

        currentStage += 1
        currentStageTimeout = instant()
            .plusMillis((10).seconds.inWholeMilliseconds + NETWORK_LATENCY)
    }

    companion object {
        private const val NETWORK_LATENCY = 500
    }
}
