# Get user by token

username에 해당하는 유저의 포인트를 감소시킵니다.

> [!WARN]   
> 요청하면 별도의 승인절차를 거친 후 승인이 되었을때, 요청이 처리됩니다.  

## Request
### HTTP METHOD : `POST`
### url : `https://api.gitanimals.org/admin/users/points/decrease/by-username/{username}`
### RequestHeader
- Admin-Secret: `{발급받은 어드민 토큰을 넘겨주세요.}`
- Authorization: `{어드민 요청자의 인증토큰을 넘겨주세요.}`

### Request Body
```json
{
  "point": 100000, // 감소 시킬 포인트
  "reason": "" // 요청을 처리하는 이유
}
```

