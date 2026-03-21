package org.gitanimals.quiz.domain.not_approved

import org.gitanimals.quiz.domain.core.Category
import org.gitanimals.quiz.domain.core.Language
import org.gitanimals.quiz.domain.core.Level
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface NotApprovedQuizRepository : JpaRepository<NotApprovedQuiz, Long> {

    fun findAllByUserId(userId: Long): List<NotApprovedQuiz>

    @Query(
        """
        select q
        from NotApprovedQuiz q
        where q.id < :lastId
          and (:level is null or q.level = :level)
          and (:category is null or q.category = :category)
          and (:language is null or q.language = :language)
        order by q.id desc
        """
    )
    fun findAllByCursor(
        lastId: Long,
        level: Level?,
        category: Category?,
        language: Language?,
        pageable: Pageable,
    ): List<NotApprovedQuiz>
}
