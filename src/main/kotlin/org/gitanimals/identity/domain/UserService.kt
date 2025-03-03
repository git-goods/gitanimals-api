package org.gitanimals.identity.domain

import org.springframework.data.repository.findByIdOrNull
import org.springframework.orm.ObjectOptimisticLockingFailureException
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class UserService(
    private val userRepository: UserRepository,
    private val userIdempotencyRepository: UserIdempotencyRepository,
) {

    fun existsUser(username: String): Boolean = userRepository.existsByName(username)

    @Retryable(retryFor = [ObjectOptimisticLockingFailureException::class])
    @Transactional
    fun givePoint(username: String, point: Long, reason: String) {
        userRepository.findByName(username)?.givePoint(point, reason)
    }

    @Transactional
    fun newUser(username: String, profileImage: String, contributionPerYears: Map<Int, Int>): User {
        if (existsUser(username)) {
            return getUserByName(username)
        }
        val user = User.newUser(
            name = username,
            points = contributionPerYears.toPoint(),
            profileImage = profileImage,
        )
        return userRepository.save(user)
    }

    @Transactional
    @Retryable(ObjectOptimisticLockingFailureException::class)
    fun decreasePoint(userId: Long, idempotencyKey: String, point: Long): User {
        requireIdempotency("decreasePoint:$userId:$idempotencyKey")

        val user = getUserById(userId)
        user.decreasePoint(point)
        return user
    }

    @Transactional
    @Retryable(ObjectOptimisticLockingFailureException::class)
    fun increasePoint(userId: Long, idempotencyKey: String, point: Long): User {
        requireIdempotency("increasePoint:$userId:$idempotencyKey")

        val user = getUserById(userId)
        user.increasePoint(point)
        return user
    }

    @Transactional
    @Retryable(ObjectOptimisticLockingFailureException::class)
    fun increasePointByUsername(username: String, idempotencyKey: String, point: Long): User {
        val user = getUserByName(username)
        requireIdempotency("increasePoint:${user.id}:$idempotencyKey")

        user.increasePoint(point)
        return user
    }

    private fun requireIdempotency(idempotencyKey: String) {
        val userIdempotency =
            userIdempotencyRepository.findByIdOrNull(idempotencyKey)

        require(userIdempotency == null) { "Duplicated request by idempotency key \"$idempotencyKey\"" }

        userIdempotencyRepository.save(UserIdempotency(idempotencyKey))
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

    fun getUserById(userId: Long): User = userRepository.findByIdOrNull(userId)
        ?: throw IllegalArgumentException("Cannot find exists user by id \"$userId\"")

    private companion object {
        private const val CONTRIBUTION_POINT_RATIO = 100
    }
}
