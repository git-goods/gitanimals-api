package org.gitanimals.identity.controller

import org.gitanimals.identity.controller.response.TotalUserResponse
import org.gitanimals.identity.domain.UserStatisticService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
class UserStatisticController(
    private val userStatisticService: UserStatisticService,
) {

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/users/statistics/total")
    fun totalUsers(): TotalUserResponse =
        TotalUserResponse.from(userStatisticService.getTotalUserCount())

}
