package org.gitanimals.identity

import org.rooftop.netx.meta.EnableSaga
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.retry.annotation.EnableRetry

@EnableSaga
@EnableRetry
@EnableJpaAuditing
@SpringBootApplication
class IdentityTestRoot
