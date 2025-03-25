package org.gitanimals.quiz.domain.context

enum class QuizSolveContextStatus {
    NOT_STARTED,
    SOLVING,
    SUCCESS,
    FAIL,
    DONE,
    ;

    companion object {
        val solveTransferableStatus = setOf(SUCCESS, NOT_STARTED)
    }
}
