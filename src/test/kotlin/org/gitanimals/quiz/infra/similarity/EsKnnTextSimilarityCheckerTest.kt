package org.gitanimals.quiz.infra.similarity

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.springframework.data.elasticsearch.client.elc.NativeQuery
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.data.elasticsearch.core.SearchHit
import org.springframework.data.elasticsearch.core.SearchHits

internal class EsKnnTextSimilarityCheckerTest : DescribeSpec({

    describe("getSimilarity 메소드는") {
        context("Elasticsearch 조회에 성공하면") {
            val elasticSearchOperations = mockk<ElasticsearchOperations>()
            val tokenizer = mockk<Tokenizer>()
            val checker = EsKnnTextSimilarityChecker(elasticSearchOperations, tokenizer)
            val quizSimilarity = QuizSimilarity(
                id = 1L,
                quizId = 10L,
                vector = listOf(0.1f, 0.2f),
            )
            val searchHits = mockk<SearchHits<QuizSimilarity>>()

            every { tokenizer.embed(any()) } returns embeddingResponse()
            every {
                elasticSearchOperations.search(any<NativeQuery>(), QuizSimilarity::class.java)
            } returns searchHits
            every { searchHits.searchHits } returns listOf(
                SearchHit(
                    null,
                    null,
                    null,
                    1.0f,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    quizSimilarity,
                )
            )

            it("유사한 퀴즈 아이디를 반환한다.") {
                val result = checker.getSimilarity("quiz")

                result.similarityQuizIds shouldBe listOf(10L)
            }
        }

        context("Elasticsearch 조회에 실패하면") {
            val elasticSearchOperations = mockk<ElasticsearchOperations>()
            val tokenizer = mockk<Tokenizer>()
            val checker = EsKnnTextSimilarityChecker(elasticSearchOperations, tokenizer)

            every { tokenizer.embed(any()) } returns embeddingResponse()
            every {
                elasticSearchOperations.search(any<NativeQuery>(), QuizSimilarity::class.java)
            } throws IllegalStateException("Elastic down")

            it("빈 결과를 반환한다.") {
                val result = checker.getSimilarity("quiz")

                result.similarityQuizIds shouldBe emptyList()
            }
        }
    }
}) {

    companion object {
        private fun embeddingResponse(): Tokenizer.Response {
            return Tokenizer.Response(
                usage = Tokenizer.Response.Usage(
                    promptToken = 1,
                    totalToken = 1,
                ),
                model = "text-embedding-3-small",
                data = listOf(
                    Tokenizer.Response.Data(
                        `object` = "embedding",
                        embedding = listOf(0.1f, 0.2f),
                    )
                ),
            )
        }
    }
}
