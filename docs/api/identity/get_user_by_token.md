# Get user by token

토큰에 해당하는 유저를 조회합니다.

## Request
### HTTP METHOD : `GET`
### url : `https://api.gitanimals.org/users`
### RequestHeader
- Authorization: `{token}`

## Response

```json
{
  "id": "1",
  "username": "devxb",
  "points": "491128",
  "profileImage": "https://avatars.githubusercontent.com/u/62425964?v=4",
  "entryPoint": "GITHUB" // GITHUB OR APPLE
}
```
