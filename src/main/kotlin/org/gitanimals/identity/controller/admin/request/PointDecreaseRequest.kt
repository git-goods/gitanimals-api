package org.gitanimals.identity.controller.admin.request

import org.gitanimals.core.admin.AbstractAdminRequest

data class PointDecreaseRequest(
    val point: Long,
    override val reason: String,
) : AbstractAdminRequest
