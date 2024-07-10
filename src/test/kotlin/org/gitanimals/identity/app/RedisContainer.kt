package org.gitanimals.identity.app

import com.github.dockerjava.api.exception.InternalServerErrorException
import org.springframework.boot.test.context.TestConfiguration
import org.testcontainers.containers.GenericContainer
import org.testcontainers.utility.DockerImageName

@TestConfiguration
internal class RedisContainer {
    init {
        val redis: GenericContainer<*> = GenericContainer(DockerImageName.parse("redis:7.2.3"))
            .withExposedPorts(6379)

        runCatching {
            redis.start()
        }.onFailure {
            if (it is InternalServerErrorException) {
                redis.start()
            }
        }

        System.setProperty(
            "netx.port",
            redis.getMappedPort(6379).toString()
        )
    }
}
