# Delete not approved quiz

미승인 퀴즈를 삭제합니다. 내부적으로 포인트 회수 로직이 함께 수행될 수 있습니다.

> [!WARN]
> 요청 시 `AdminCallDetected` 이벤트가 발행됩니다.

## Request
### HTTP METHOD : `DELETE`
### url : `https://api.gitanimals.org/admin/quizs/not-approved/{quizId}`
### RequestHeader
- Admin-Secret: `{발급받은 어드민 토큰을 넘겨주세요.}`
- Authorization: `{어드민 요청자의 인증토큰을 넘겨주세요.}`

### Path Variable
- quizId: `{삭제할 미승인 퀴즈 ID}`

### Request Body
```json
{
  "reason": "duplicate quiz"
}
```

## Response

200 OK
