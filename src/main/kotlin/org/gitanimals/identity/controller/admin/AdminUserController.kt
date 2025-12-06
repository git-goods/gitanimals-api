package org.gitanimals.identity.controller.admin

import org.gitanimals.core.admin.AdminCallDetected
import org.gitanimals.core.admin.AdminConst.ADMIN_SECRET_KEY
import org.gitanimals.identity.app.Token
import org.gitanimals.identity.app.UserFacade
import org.gitanimals.identity.controller.admin.request.PointDecreaseRequest
import org.gitanimals.identity.controller.admin.request.PointIncreaseRequest
import org.gitanimals.identity.domain.EntryPoint
import org.gitanimals.identity.domain.UserService
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationEventPublisher
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
class AdminUserController(
    private val userService: UserService,
    private val userFacade: UserFacade,
    private val eventPublisher: ApplicationEventPublisher,
    @Value("\${gitanimals.admin.token}") private val adminToken: String,
) {

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/admin/users/points/increase/by-username/{username}")
    fun increaseUserPointsByUsername(
        @PathVariable("username") username: String,
        @RequestParam(
            name = "entryPoint",
            defaultValue = "GITHUB",
        )
        entryPoint: EntryPoint = EntryPoint.GITHUB,
        @RequestHeader(ADMIN_SECRET_KEY) adminSecret: String,
        @RequestHeader(HttpHeaders.AUTHORIZATION) authorization: String,
        @RequestBody pointIncreaseRequest: PointIncreaseRequest,
    ) {
        require(adminSecret == adminToken) {
            "WRONG TOKEN"
        }

        val user = userFacade.getUserByToken(Token.from(authorization))

        userService.increasePointByUsernameWithoutIdempotency(username, entryPoint, pointIncreaseRequest.point)
        eventPublisher.publishEvent(
            AdminCallDetected(
                username = user.getName(),
                reason = pointIncreaseRequest.reason,
                path = "/admin/users/points/increase/by-username/{username}",
                description = "$username 유저에게 ${pointIncreaseRequest.point} 지급",
            )
        )
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/admin/users/points/decrease/by-username/{username}")
    fun decreaseUserPointsByUsername(
        @PathVariable("username") username: String,
        @RequestParam(
            name = "entryPoint",
            defaultValue = "GITHUB",
        )
        entryPoint: EntryPoint = EntryPoint.GITHUB,
        @RequestHeader(ADMIN_SECRET_KEY) adminSecret: String,
        @RequestHeader(HttpHeaders.AUTHORIZATION) authorization: String,
        @RequestBody pointDecreaseRequest: PointDecreaseRequest,
    ) {
        require(adminSecret == adminToken) {
            "WRONG TOKEN"
        }

        val user = userFacade.getUserByToken(Token.from(authorization))

        userService.decreasePointByUsernameWithoutIdempotency(username, entryPoint, pointDecreaseRequest.point)
        eventPublisher.publishEvent(
            AdminCallDetected(
                username = user.getName(),
                reason = pointDecreaseRequest.reason,
                path = "/admin/users/points/decrease/by-username/{username}",
                description = "$username 유저에게 ${pointDecreaseRequest.point} 차감",
            )
        )
    }

}
