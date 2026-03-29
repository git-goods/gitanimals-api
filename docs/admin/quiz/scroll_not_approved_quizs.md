# Scroll not approved quizs

미승인 퀴즈를 `id` 기반 no-offset 방식으로 조회합니다.

> [!WARN]
> 조회는 항상 `id` 커서를 기준으로 내려갑니다.

## Request
### HTTP METHOD : `GET`
### url : `https://api.gitanimals.org/admin/quizs/not-approved`
### RequestHeader
- Admin-Secret: `{발급받은 어드민 토큰을 넘겨주세요.}`
- Authorization: `{어드민 요청자의 인증토큰을 넘겨주세요.}`

### Query Parameter
- lastId: `{optional, 다음 페이지 조회를 위한 커서}`
- level: `{optional, EASY | MEDIUM | DIFFICULT}`
- category: `{optional, FRONTEND | BACKEND}`
- language: `{optional, KOREA | ENGLISH}`

## Response

200 OK

```json
{
  "quizs": [
    {
      "id": "712345678901234567",
      "userId": "1234",
      "level": "MEDIUM",
      "category": "FRONTEND",
      "language": "KOREA",
      "problem": "브라우저의 reflow는 layout 계산과 관련이 있다.",
      "expectedAnswer": "YES",
      "createdAt": "2026-03-21 01:00:00",
      "modifiedAt": "2026-03-21 01:00:00"
    }
  ],
  "nextId": "712345678901234567"
}
```

### Response Field
- quizs: 최대 20개의 미승인 퀴즈 목록
- nextId: 다음 페이지가 없으면 `null`
