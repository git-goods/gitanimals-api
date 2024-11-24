package org.gitanimals.gotcha.domain

fun interface DropRateClient {

    fun getDropRate(name: String): Double

}
