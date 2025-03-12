package org.gitanimals.quiz.app

import com.github.dockerjava.api.exception.InternalServerErrorException
import org.springframework.boot.test.context.TestConfiguration
import org.testcontainers.containers.GenericContainer
import org.testcontainers.utility.DockerImageName

@TestConfiguration
class ElasticSearchContainer {

    init {
        val elasticSearch: GenericContainer<*> = GenericContainer(DockerImageName.parse("redis:8.17.3"))
            .withExposedPorts(9200)

        runCatching {
            elasticSearch.start()
        }.onFailure {
            if (it is InternalServerErrorException) {
                elasticSearch.start()
            }
        }
    }
}
