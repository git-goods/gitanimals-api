package org.gitanimals.identity.domain

import org.gitanimals.identity.core.IdGenerator
import org.springframework.orm.ObjectOptimisticLockingFailureException
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class UserService(
    private val userRepository: UserRepository,
) {

    fun existsUser(username: String): Boolean = userRepository.existsByName(username)

    @Retryable(retryFor = [ObjectOptimisticLockingFailureException::class])
    @Transactional
    fun givePoint(username: String, point: Long) {
        getUserByName(username).givePoint(point)
    }

    @Transactional
    fun newUser(username: String, contributionPerYears: Map<Int, Int>): User {
        if (existsUser(username)) {
            return getUserByName(username)
        }
        val user = User(
            id = IdGenerator.generate(),
            name = username,
            points = contributionPerYears.toPoint(),
        )
        return userRepository.save(user)
    }

    private fun Map<Int, Int>.toPoint(): Long {
        var point = 0L
        this.forEach {
            point += (it.value * CONTRIBUTION_POINT_RATIO)
        }
        return point
    }

    fun getUserByName(username: String): User = userRepository.findByName(username)
        ?: throw IllegalArgumentException("Cannot find exists user by username \"$username\"")

    private companion object {
        private const val CONTRIBUTION_POINT_RATIO = 100
    }
}
