package org.gitanimals.identity.domain

import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class UserService(
    private val userRepository: UserRepository,
) {

    @Retryable(retryFor = [OptimisticLockingFailureException::class])
    @Transactional
    fun givePoint(username: String, contribution: Int) {
        val user = userRepository.findByName(username)
            ?: throw IllegalArgumentException("Cannot find exists user by username \"$username\"")

        user.givePoint(contribution)
    }
}
