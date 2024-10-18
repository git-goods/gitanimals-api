package org.gitanimals.gotcha.domain

import kotlin.math.max

enum class GotchaType(
    val point: Long,
) {

    DEFAULT(1000L) {

        override fun createGotcha(): Gotcha = Gotcha(this, this.point, getCapsules())

        private fun getCapsules(): List<Capsule> {
            val capsules = mutableListOf<Capsule>()
            capsuleAndRatios.forEach { capsule ->
                val count = max(1, (capsule.ratio * 1000).toInt())
                repeat(count) { capsules.add(capsule) }
            }
            return capsules
        }
    },
    ;

    abstract fun createGotcha(): Gotcha

    private companion object {
        private val capsuleAndRatios = listOf(
            Capsule.of("GOOSE", 1.0),
            Capsule.of("GOOSE_SUNGLASSES", 0.05),
            Capsule.of("GOOSE_KOTLIN", 0.01),
            Capsule.of("GOOSE_JAVA", 0.01),
            Capsule.of("GOOSE_JS", 0.01),
            Capsule.of("GOOSE_NODE", 0.01),
            Capsule.of("GOOSE_SWIFT", 0.01),
            Capsule.of("GOOSE_LINUX", 0.01),
            Capsule.of("GOOSE_SPRING", 0.01),
            Capsule.of("LITTLE_CHICK", 0.9),
            Capsule.of("LITTLE_CHICK_SUNGLASSES", 0.04),
            Capsule.of("LITTLE_CHICK_KOTLIN", 0.01),
            Capsule.of("LITTLE_CHICK_JAVA", 0.01),
            Capsule.of("LITTLE_CHICK_JS", 0.01),
            Capsule.of("LITTLE_CHICK_NODE", 0.01),
            Capsule.of("LITTLE_CHICK_SWIFT", 0.01),
            Capsule.of("LITTLE_CHICK_LINUX", 0.01),
            Capsule.of("LITTLE_CHICK_SPRING", 0.01),
            Capsule.of("PENGUIN", 0.5),
            Capsule.of("PENGUIN_SUNGLASSES", 0.2),
            Capsule.of("PENGUIN_KOTLIN", 0.01),
            Capsule.of("PENGUIN_JAVA", 0.01),
            Capsule.of("PENGUIN_JS", 0.01),
            Capsule.of("PENGUIN_NODE", 0.01),
            Capsule.of("PENGUIN_SWIFT", 0.01),
            Capsule.of("PENGUIN_LINUX", 0.01),
            Capsule.of("PENGUIN_SPRING", 0.01),
            Capsule.of("PIG", 0.2),
            Capsule.of("PIG_SUNGLASSES", 0.08),
            Capsule.of("PIG_KOTLIN", 0.01),
            Capsule.of("PIG_JAVA", 0.01),
            Capsule.of("PIG_JS", 0.01),
            Capsule.of("PIG_NODE", 0.01),
            Capsule.of("PIG_SWIFT", 0.01),
            Capsule.of("PIG_LINUX", 0.01),
            Capsule.of("PIG_SPRING", 0.01),
            Capsule.of("SLIME_RED", 0.1),
            Capsule.of("SLIME_RED_KOTLIN", 0.001),
            Capsule.of("SLIME_RED_JAVA", 0.001),
            Capsule.of("SLIME_RED_JS", 0.001),
            Capsule.of("SLIME_RED_NODE", 0.001),
            Capsule.of("SLIME_RED_SWIFT", 0.001),
            Capsule.of("SLIME_RED_LINUX", 0.001),
            Capsule.of("SLIME_BLUE", 0.1),
            Capsule.of("SLIME_GREEN", 0.1),
            Capsule.of("FLAMINGO", 0.05),
            Capsule.of("GOBLIN", 0.06),
            Capsule.of("GOBLIN_BAG", 0.03),
            Capsule.of("CAT", 0.1),
            Capsule.of("CHEESE_CAT", 0.04),
            Capsule.of("GALCHI_CAT", 0.06),
            Capsule.of("WHITE_CAT", 0.06),
            Capsule.of("FISH_MAN", 0.001),
            Capsule.of("FISH_MAN_GLASSES", 0.001),
            Capsule.of("QUOKKA", 0.3),
            Capsule.of("QUOKKA_LEAF", 0.1),
            Capsule.of("QUOKKA_SUNGLASSES", 0.05),
            Capsule.of("MOLE", 0.3),
            Capsule.of("MOLE_GRASS", 0.1),
            Capsule.of("RABBIT", 0.9),
            Capsule.of("DESSERT_FOX", 0.05),
            Capsule.of("SLOTH", 0.7),
            Capsule.of("SLOTH_KING", 0.05),
            Capsule.of("SLOTH_SUNGLASSES", 0.06),
            Capsule.of("TURTLE", 0.03),
            Capsule.of("GHOST", 0.05),
            Capsule.of("GHOST_KING", 0.01),
            Capsule.of("SCREAM", 0.005),
            Capsule.of("SCREAM_GHOST", 0.001),
        )
    }
}
