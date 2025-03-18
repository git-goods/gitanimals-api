package org.gitanimals

import org.rooftop.netx.meta.EnableSaga
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.retry.annotation.EnableRetry
import org.springframework.scheduling.annotation.EnableScheduling

@EnableSaga
@EnableRetry
@EnableCaching
@EnableScheduling
@EnableJpaAuditing
@EnableElasticsearchRepositories
@SpringBootApplication
class Application {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(Application::class.java, *args)
        }

    }
}
