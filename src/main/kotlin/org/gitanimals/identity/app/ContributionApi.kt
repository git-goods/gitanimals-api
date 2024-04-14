package org.gitanimals.identity.app

interface ContributionApi {

    fun getContributionCountWithToken(
        username: String,
        years: List<Int>
    ): Map<Int, Int>

    fun getAllContributionYearsWithToken(username: String): List<Int>
}
