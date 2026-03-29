package org.gitanimals.quiz.controller.admin.request

import org.gitanimals.core.admin.AbstractAdminRequest

data class QuizAdminActionRequest(
    override val reason: String,
) : AbstractAdminRequest
