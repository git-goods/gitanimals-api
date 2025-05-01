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

    fun existsUser(username: String, entryPoint: EntryPoint): Boolean =
        userRepository.existsByNameAndEntryPoint(username, entryPoint)

    @Retryable(retryFor = [ObjectOptimisticLockingFailureException::class])
    @Transactional
    fun givePoint(username: String, entryPoint: EntryPoint, point: Long, reason: String) {
        userRepository.findByNameAndEntryPoint(username, entryPoint)?.givePoint(point, reason)
    }

    @Transactional
    fun newUser(
        username: String,
        entryPoint: EntryPoint,
        profileImage: String,
        contributionPerYears: Map<Int, Int>
    ): User {
        if (existsUser(username, entryPoint)) {
            return getUserByNameAndEntryPoint(username, entryPoint)
        }
        val user = User.newUser(
            name = username,
            points = contributionPerYears.toPoint(),
            profileImage = profileImage,
            entryPoint = entryPoint,
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
    fun increasePointByUsername(
        username: String,
        entryPoint: EntryPoint,
        idempotencyKey: String,
        point: Long,
    ): User {
        val user = getUserByNameAndEntryPoint(username, entryPoint)
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

    fun getUserByNameAndEntryPoint(username: String, entryPoint: EntryPoint): User =
        userRepository.findByNameAndEntryPoint(username, entryPoint)
            ?: throw IllegalArgumentException("Cannot find exists user by username \"$username\" and entryPoint: \"$entryPoint\"")

    fun getUserById(userId: Long): User = userRepository.findByIdOrNull(userId)
        ?: throw IllegalArgumentException("Cannot find exists user by id \"$userId\"")

    private companion object {
        private const val CONTRIBUTION_POINT_RATIO = 100
    }
}
