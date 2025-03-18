package org.gitanimals.quiz.app.response

data class CreateQuizResponse(
    val result: String,
    val point: Long,
    val message: String,
) {

    companion object {
        fun success(point: Long, message: String): CreateQuizResponse {
            return CreateQuizResponse(
                result = "SUCCESS",
                point = point,
                message = message,
            )
        }

        fun fail(point: Long, message: String): CreateQuizResponse {
            return CreateQuizResponse(
                result = "FAIL",
                point = point,
                message = message,
            )
        }
    }
}
