package org.gitanimals.core.event

interface EventLogger {

    /**
     * 분석 이벤트를 트래킹한다. 절대 예외를 throw 하지 않으며, 실패는 구현체 내부에서 로깅으로 처리된다.
     */
    fun track(eventName: String, distinctId: String, properties: Map<String, Any?> = emptyMap())
}
