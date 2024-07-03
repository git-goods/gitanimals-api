package org.gitanimals.gotcha.domain

import org.gitanimals.gotcha.domain.response.GotchaResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class GotchaService {

    fun gotcha(point: Long, type: GotchaType): GotchaResponse = defaultGotcha.random(point)

    private companion object {
        val defaultGotcha = GotchaType.DEFAULT.createGotcha()
    }
}
