package org.gitanimals.auction

import com.ninjasquad.springmockk.MockkBean
import org.gitanimals.core.auth.InternalAuth
import org.gitanimals.core.auth.InternalAuthRequestInterceptor
import org.rooftop.netx.meta.EnableSaga
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.retry.annotation.EnableRetry

@EnableSaga
@EnableRetry
@EnableJpaAuditing
@SpringBootApplication
class AuctionTestRoot(
    @MockkBean(relaxed = true) private val internalAuth: InternalAuth,
    @MockkBean(relaxed = true) private val internalAuthRequestInterceptor: InternalAuthRequestInterceptor,
)
