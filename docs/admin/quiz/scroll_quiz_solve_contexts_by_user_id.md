# Scroll quiz solve contexts by user id

특정 `userId`의 `QuizSolveContext`를 `id` 기반 no-offset 방식으로 조회합니다.

> [!WARN]
> `userId`는 반드시 입력해야 하며, 한 번에 최대 20개를 응답합니다.

## Request
### HTTP METHOD : `GET`
### url : `https://api.gitanimals.org/admin/quizs/contexts`
### RequestHeader
- Admin-Secret: `{발급받은 어드민 토큰을 넘겨주세요.}`
- Authorization: `{어드민 요청자의 인증토큰을 넘겨주세요.}`

### Query Parameter
- userId: `{required, 조회할 유저 ID}`
- lastId: `{optional, 다음 페이지 조회를 위한 커서}`

## Response

200 OK

```json
{
  "quizSolveContexts": [
    {
      "id": "812345678901234567",
      "userId": "1234",
      "category": "BACKEND",
      "round": {
        "total": 3,
        "current": 1,
        "timeoutAt": "2026-03-21 01:00:10"
      },
      "prize": 2000,
      "solvedAt": "2026-03-21",
      "status": "SUCCESS",
      "createdAt": "2026-03-21 01:00:00",
      "modifiedAt": "2026-03-21 01:00:05"
    }
  ],
  "nextId": "812345678901234567"
}
```

### Response Field
- quizSolveContexts: 최대 20개의 풀이 컨텍스트 목록
- round.total: 전체 문제 수
- round.current: 현재 라운드
- round.timeoutAt: 현재 라운드 제한시간, 없으면 `null`
- status: `NOT_STARTED | SOLVING | SUCCESS | FAIL | DONE`
- nextId: 다음 페이지가 없으면 `null`
