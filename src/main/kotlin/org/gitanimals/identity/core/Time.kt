package org.gitanimals.identity.core

import java.time.Clock
import java.time.Instant
import java.time.ZonedDateTime

var clock: Clock = Clock.systemUTC()

fun instant() = Instant.now(clock)

fun Instant.toZonedDateTime() = ZonedDateTime.ofInstant(this, clock.zone)
