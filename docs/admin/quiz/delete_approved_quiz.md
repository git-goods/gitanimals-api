# Delete approved quiz

승인된 퀴즈를 삭제합니다.

> [!WARN]
> 요청 시 `AdminCallDetected` 이벤트가 발행됩니다.

## Request
### HTTP METHOD : `DELETE`
### url : `https://api.gitanimals.org/admin/quizs/approved/{quizId}`
### RequestHeader
- Admin-Secret: `{발급받은 어드민 토큰을 넘겨주세요.}`
- Authorization: `{어드민 요청자의 인증토큰을 넘겨주세요.}`

### Path Variable
- quizId: `{삭제할 승인 퀴즈 ID}`

### Request Body
```json
{
  "reason": "policy violation"
}
```

## Response

200 OK
