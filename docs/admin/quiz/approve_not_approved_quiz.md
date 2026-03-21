# Approve not approved quiz

미승인 퀴즈를 승인하여 승인 퀴즈로 반영합니다.

> [!WARN]
> 요청 시 `AdminCallDetected` 이벤트가 발행됩니다.

## Request
### HTTP METHOD : `POST`
### url : `https://api.gitanimals.org/admin/quizs/not-approved/{quizId}/approve`
### RequestHeader
- Admin-Secret: `{발급받은 어드민 토큰을 넘겨주세요.}`
- Authorization: `{어드민 요청자의 인증토큰을 넘겨주세요.}`

### Path Variable
- quizId: `{승인할 미승인 퀴즈 ID}`

### Request Body
```json
{
  "reason": "review completed"
}
```

## Response

200 OK
