package org.gitanimals.quiz.domain.core

enum class Language {
    KOREA,
    ENGLISH,
    ;

    companion object {

        fun String.containsKorean(): Boolean {
            val koreanRegex = Regex("[가-힣]")
            return this.contains(koreanRegex)
        }
    }
}
