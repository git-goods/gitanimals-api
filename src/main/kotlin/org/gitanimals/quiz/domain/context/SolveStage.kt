package org.gitanimals.quiz.domain.context

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.time.Instant

@Embeddable
class SolveStage(
    @Column(name = "max_solve_stage")
    val maxSolveStage: Int,
    @Column(name = "current_solve_stage")
    val currentStage: Int,
    @Column(name = "current_stage_time_out", nullable = true)
    val currentStageTimeout: Instant?,
)
